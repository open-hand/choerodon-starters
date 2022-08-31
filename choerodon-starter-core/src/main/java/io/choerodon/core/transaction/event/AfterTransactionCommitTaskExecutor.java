package io.choerodon.core.transaction.event;

/**
 * 数据库事务提交后任务注册机
 * @author 立风ukyo@csdn gaokuo.dai@zknow.com 2022-08-16
 */
public interface AfterTransactionCommitTaskExecutor {

    /**
     * 任务执行策略
     */
    enum ExecuteType {
        /**
         * 在当前线程同步执行
         */
        CURRENT_THREAD,
        /**
         * 在线程池中异步执行
         */
        THREAD_POOL
    }

    /**
     * 注册任务, 任务将在当前事务提交之后异步执行, 如果当前无事务, 则在当前线程立即执行任务
     * @param task 任务
     */
    void registerTask(
            Runnable task
    );

    /**
     * 注册任务, 任务将在当前事务提交之后执行, 如果当前无事务, 则在当前线程立即执行任务
     * @param task 任务
     * @param executeType 执行策略
     */
    void registerTask(
            Runnable task,
            ExecuteType executeType
    );

    /**
     * 注册任务, 任务将在当前事务提交之后执行, 如果当前无事务, 则将抛出异常
     * @param task 任务
     * @param executeType 执行策略
     * @param executeImmediatelyWhenSynchronizationNotActive 如果当前无事务时处理策略, true -> 当前线程立即执行任务, false -> 抛出异常
     */
    void registerTask(
            Runnable task,
            ExecuteType executeType,
            boolean executeImmediatelyWhenSynchronizationNotActive
    );

}
