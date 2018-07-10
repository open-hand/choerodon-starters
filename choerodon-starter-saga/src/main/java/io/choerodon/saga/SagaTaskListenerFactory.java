package io.choerodon.saga;

import java.util.Set;
import java.util.concurrent.ExecutorService;

public class SagaTaskListenerFactory {

   private ExecutorService executorService;

   public SagaTaskListenerFactory(ExecutorService executorService) {
      this.executorService = executorService;
   }

   public void createListeners(final Set<SagaTaskInvokeBean> invokeBeans) {

   }

}
