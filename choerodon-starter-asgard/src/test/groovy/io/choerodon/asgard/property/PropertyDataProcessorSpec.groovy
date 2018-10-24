package io.choerodon.asgard.property

import io.choerodon.asgard.saga.SagaDefinition
import io.choerodon.asgard.saga.annotation.Saga
import io.choerodon.asgard.saga.annotation.SagaTask
import io.choerodon.asgard.schedule.ParamType
import io.choerodon.asgard.schedule.annotation.JobParam
import io.choerodon.asgard.schedule.annotation.JobTask
import org.springframework.transaction.annotation.Isolation
import spock.lang.Specification

@Saga(code = "sagaTwo", description = "sagaTwo", inputSchema = "{\"username\":\"aa\", \"age\":0}")
class PropertyDataProcessorSpec extends Specification {

    def '测试 postProcessAfterInitialization'() {
        given: '创建一个PropertyDataProcessor'
        def propertyData = new PropertyData()
        def propertyDataProcessor = new PropertyDataProcessor(propertyData)
        propertyData.setService("test-service")

        and: '创建测试所用的bean'
        def definition = new PropertyDataProcessorSpec()

        when: '调用postProcessAfterInitialization方法'
        propertyDataProcessor.postProcessAfterInitialization(definition, "definition")

        then: '验证扫描的propertyData'
        propertyData.getService() == 'test-service'
        def jobTasks = propertyData.getJobTasks()
        def sagas = propertyData.getSagas()
        def sagaTasks = propertyData.getSagaTasks()
        jobTasks.size() == 1
        sagas.size() == 2
        sagaTasks.size() == 3

        jobTasks.get(0).code == 'test'
        jobTasks.get(0).method == this.getClass().getName() + '.test'
        jobTasks.get(0).maxRetryCount == 2
        jobTasks.get(0).description == 'test'
        jobTasks.get(0).params.size() == 3
        jobTasks.get(0).params.get(0).name == 'isInstantly'
        jobTasks.get(0).params.get(0).description == 'isInstantly'
        jobTasks.get(0).params.get(0).defaultValue == true
        jobTasks.get(0).params.get(0).type == ParamType.BOOLEAN.value
        jobTasks.get(0).params.get(1).type == ParamType.STRING.value
        jobTasks.get(0).params.get(2).type == ParamType.INTEGER.value

        selectSagaByCode(sagas, 'sagaOne').inputSchemaSource == SagaDefinition.SagaInputSchemaSource.INPUT_SCHEMA_CLASS.name()
        selectSagaByCode(sagas, 'sagaOne').description == 'sagaOne'
        selectSagaByCode(sagas, 'sagaTwo').inputSchemaSource == SagaDefinition.SagaInputSchemaSource.INPUT_SCHEMA.name()
        selectSagaByCode(sagas, 'sagaTwo').inputSchema != null

        def sagaTaskOne = selectSagaTaskByCode(sagaTasks, 'sagaTaskOne')
        sagaTaskOne.code == 'sagaTaskOne'
        sagaTaskOne.sagaCode == 'sagaOne'
        sagaTaskOne.seq == 1
        sagaTaskOne.concurrentLimitNum == 2
        sagaTaskOne.outputSchemaSource == SagaDefinition.SagaTaskOutputSchemaSource.OUTPUT_SCHEMA_CLASS.name()
        selectSagaTaskByCode(sagaTasks, 'sagaTaskTwo').outputSchemaSource == SagaDefinition.SagaTaskOutputSchemaSource.OUTPUT_SCHEMA.name()
        selectSagaTaskByCode(sagaTasks, 'sagaTaskThree').outputSchemaSource == SagaDefinition.SagaTaskOutputSchemaSource.METHOD_RETURN_TYPE.name()

    }

    def selectSagaByCode(List<PropertySaga> sagas, String code) {
        for (PropertySaga saga : sagas) {
            if (saga.getCode() == code) return saga
        }
        return null
    }

    def selectSagaTaskByCode(List<PropertySagaTask> sagaTasks, String code) {
        for (PropertySagaTask sagaTask : sagaTasks) {
            if (sagaTask.getCode() == code) return sagaTask
        }
        return null
    }


    @JobTask(code = "test",
            description = "test",
            maxRetryCount = 2, params = [
                    @JobParam(name = "isInstantly", defaultValue = "true", type = Boolean.class, description = "isInstantly"),
                    @JobParam(name = "name", defaultValue = "zh"),
                    @JobParam(name = "age", type = Integer.class)],
            transactionTimeout = 10,
            transactionReadOnly = false,
            transactionIsolation = Isolation.DEFAULT,
            transactionManager = "manager"
    )
    Map<String, Object> test(Map<String, Object> data) {
        return data
    }

    @Saga(code = "sagaOne", description = "sagaOne", inputSchemaClass = User.class)
    private void sagaOne() {
    }

    @SagaTask(code = "sagaTaskOne",
            sagaCode = "sagaOne",
            description = "sagaTaskOne",
            maxRetryCount = 10,
            timeoutSeconds = 10,
            timeoutPolicy = SagaDefinition.TimeoutPolicy.ALERT_ONLY,
            seq = 1,
            concurrentLimitNum = 2,
            concurrentLimitPolicy = SagaDefinition.ConcurrentLimitPolicy.TYPE,
            outputSchemaClass = User.class)
    private void sagaTaskOne() {

    }

    @SagaTask(code = "sagaTaskTwo", sagaCode = "sagaTwo",
            description = "sagaTaskTwo", seq = 10,
            concurrentLimitNum = 5, outputSchema = "{\"username\":\"aa\", \"age\":0}")
    private void sagaTaskTwo() {

    }

    @SagaTask(code = "sagaTaskThree", sagaCode = "sagaTwo",
            description = "sagaTaskThree", seq = 20,
            concurrentLimitNum = 15)
    private User sagaTaskThree() {

    }


    static class User {
        private String username
        private Integer age

        String getUsername() {
            return username
        }

        void setUsername(String username) {
            this.username = username
        }

        Integer getAge() {
            return age
        }

        void setAge(Integer age) {
            this.age = age
        }
    }

}
