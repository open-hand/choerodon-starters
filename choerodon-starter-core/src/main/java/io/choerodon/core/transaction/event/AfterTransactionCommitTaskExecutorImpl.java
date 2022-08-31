package io.choerodon.core.transaction.event;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.Assert;

import io.choerodon.core.exception.CommonException;

import org.hzero.core.base.BaseConstants;
import org.hzero.core.util.Pair;

/**
 * 数据库事务提交后任务注册机实现类
 * @author 立风ukyo@csdn Greco_Dai 2021-06-04
 */
@Component
public class AfterTransactionCommitTaskExecutorImpl implements AfterTransactionCommitTaskExecutor, TransactionSynchronization {

    private static final Logger LOGGER = LoggerFactory.getLogger(AfterTransactionCommitTaskExecutorImpl.class);
    private static final ThreadLocal<List<Pair<Runnable, ExecuteType>>> TASK_LIST = new ThreadLocal<>();

    private final ThreadPoolTaskExecutor applicationTaskExecutor;

    @Autowired
    public AfterTransactionCommitTaskExecutorImpl(ThreadPoolTaskExecutor applicationTaskExecutor) {
        this.applicationTaskExecutor = applicationTaskExecutor;
    }

    @Override
    public void registerTask(
            Runnable task
    ) {
        this.registerTask(task, ExecuteType.THREAD_POOL);
    }

    @Override
    public void registerTask(
            Runnable task,
            ExecuteType executeType
    ) {
        this.registerTask(task, executeType, true);
    }

    @Override
    public void registerTask(
            Runnable task,
            ExecuteType executeType,
            boolean executeImmediatelyWhenSynchronizationNotActive
    ) {
        Assert.notNull(task, BaseConstants.ErrorCode.NOT_NULL);
        if(executeType == null) {
            executeType = ExecuteType.THREAD_POOL;
        }
        LOGGER.debug("Submitting new AfterTransactionCommitTask {} to execute after current Transaction is committed", task);

        // check if synchronization is active
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            LOGGER.debug("Transaction synchronization is NOT ACTIVE");
            if(executeImmediatelyWhenSynchronizationNotActive) {
                LOGGER.debug("Executing AfterTransactionCommitTask {} immediately due to config", task);
                task.run();
            } else {
                throw new CommonException("Transaction synchronization is NOT ACTIVE when register AfterTransactionCommitTask {}", task);
            }
            return;
        }

        // wrap this to a Synchronization and register to TransactionSynchronizationManager
        List<Pair<Runnable, ExecuteType>> taskList = TASK_LIST.get();
        if (taskList == null) {
            taskList = new ArrayList<>();
            TASK_LIST.set(taskList);
            TransactionSynchronizationManager.registerSynchronization(this);
        }

        // add to task list, task list will be fired after current Transaction is committed
        taskList.add(new Pair<>(task, executeType));
    }

    @Override
    public void afterCommit() {
        List<Pair<Runnable, ExecuteType>> threadRunnableList = TASK_LIST.get();
        if(threadRunnableList == null || threadRunnableList.isEmpty()) {
            return;
        }

        LOGGER.debug("Current Transaction successfully committed, executing {} tas", threadRunnableList.size());
        for (Pair<Runnable, ExecuteType> runnableExecuteTypePair : threadRunnableList) {
            Runnable task = runnableExecuteTypePair.getFirst();
            ExecuteType executeType = runnableExecuteTypePair.getSecond();
            if(LOGGER.isInfoEnabled()) {
                LOGGER.debug("Executing task {} in {}", task, executeType.name());
            }
            try {
                if(executeType == ExecuteType.CURRENT_THREAD) {
                    task.run();
                } else {
                    this.applicationTaskExecutor.execute(task);
                }
            } catch (Exception ex) {
                LOGGER.error("Failed to Execute task " + task, ex);
            }
        }
    }

    @Override
    public void afterCompletion(int status) {
        LOGGER.debug("Transaction completed with status {}", status == STATUS_COMMITTED ? "COMMITTED" : "ROLLED_BACK");
        TASK_LIST.remove();
    }

}
