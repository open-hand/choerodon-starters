# Choerodon Starter Event Consumer

**The consumption side of the message about data consistency**，At present, support for consumption queues**kafka** requires cooperating with ``choerodon-starter-event-producer`` (data consistency message elimination sender) and ``event-store-service`` (data consistency event middleware service) to achieve data consistency. Compared to clients such as ``spring-kafka``, the "**exactly-once**" message consumes semantics at once. That is, a message will be consumed and consumed only once. Use the method of manually submitting the displacement to ensure that the message is was consumed correctly. the use of memory and database tables to duplicate the way to ensure that the message will not be repeated consumption, and built-in **retry** function.

The consumer side of the event used to implement data consistency.

## Feature

- After the message failure policy is set to send_back_event_store, it will be passed back to event-store-service. Retry is manually clicked on the page to complete the resumption of the failed message, but the front-end page has not been completed, so currently only the message failure policy is supported as nothing.
- Add a interface of message failure policy to allow users to customize message failure policies.
- Other message queues such as pub/sub for rabbitmq, rocketmq, and redis are supported.

## Requirements

- This toolkit is spring boot's starter project, only for the project of spring boot.
- If you enable deduplication, that is, ``choerodon.event.consumer.enable-duplicate-remove=true``, need to add the mybatis dependency:

  ```xml
   <dependency>
     	<groupId>mysql</groupId>
     	<artifactId>mysql-connector-java</artifactId>
  </dependency>
  <dependency> 
    	<groupId>io.choerodon</groupId>
   		 <artifactId>choerodon-starter-mybatis-mapper</artifactId>
  </dependency>
  ```
  And in the service used, add the msg_record table by the following sql or groovy:
  
  ```sql
  DROP TABLE IF EXISTS `msg_record`;
  CREATE TABLE `msg_record` (
	  `uuid` varchar(50) NOT NULL,
	  `create_time` datetime DEFAULT NULL,
	  PRIMARY KEY (`uuid`)
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8;
  ```
  ```groovy
  package db

	databaseChangeLog(logicalFilePath:'msg_record.groovy') {
	    changeSet(id: '2018-02-06-add-table-msg_record', author: 'flyleft') {
	        createTable(tableName: "msg_record") {
	            column(name: 'uuid', type: 'VARCHAR(50)', autoIncrement: false, remarks: 'uuid') {
	                constraints(primaryKey: true)
	            }
	            column(name: 'create_time', type: 'DATETIME', remarks: '创建时间')
	        }
	    }
	}
  ```

- After the message failure policy is set to send_back_event_store, feign is called back to event-store-service. At this time, the service is required to contain the spring cloud cloud-cloud-netflix-core-dependent spring cloud project.

## Usage

- Configuration：

  ```xml
   <dependency>
     	<groupId>io.choerodon</groupId>
     	<artifactId>choerodon-starter-event-consumer</artifactId>
  <version>0.6.1.RELEASE</version>
  </dependency>
  <dependency>
      <groupId>org.apache.kafka</groupId>
      <artifactId>kafka-clients</artifactId>
      <version>0.11.0.1</version>
  </dependency>
  ```
- File configuration：

  ```yaml
  choerodon:
     event:
       consumer:
         enabled: true # Whether to start up, do not set the default start up
         queue-type: kafka # Message Queuing type currently only supports kafka.
         enable-duplicate-remove: true # Whether to start up the heavy function
         failed-strategy: nothing # Message failure strategy
         retry:
           enabled: true # Whether to enable the function of retry.
         kafka:
           bootstrap-seevent_storervers: localhost:9092 # Kafka's address
           client-id: event-consume-helper-test-kafka-1
           session-timeout-ms: 30000
           max-poll-records: 500
           heartbeat-interval-ms: 3000
           fetch-max-bytes: 52428800
           fetch-max-wait-ms: 500
           max-poll-interval-ms: 300000
           auto-offset-reset: earliest
           fetch-min-bytes: 1
           send-buffer-bytes: 131072
           receive-buffer-bytes: 65536
           reconnect-backoff-ms: 50
           reconnect-backoff-max-ms: 1000
           retry-backoff-ms: 100
           metrics-sample-window-ms: 3000
           metrics-num-sample: 2
           metrics-recording-level: INFO
           security-protocol: PLAINTEXT
           connections-max-idle-ms: 54000
           request-timeout-ms: 305000
           check-crcs: true
           exclude-internal-topics: true
           isolation-level: read_uncommitted
           partition-assignment-strategy: org.apache.kafka.clients.consumer.RangeAssignor
  ```
   
- Used in code：
 
	 ```java
	  @EventListener(topic = "event-producer-demo",//The topic name of the subscription
	            businessType = "reduceStock",//Business type
	            retryTimes = 3, //Number of retries
	            firstInterval = 30000, //First the interval of retry(milliseconds)
	            retryInterval = 10000) //The interval of retry(milliseconds)
	    public void messgae(EventPayload<RepertoryPayload> payload) {
	        RepertoryPayload data = payload.getData();
	        LOGGER.info("data: {}", data);
	        //...
	    }
	 ```

## Dependencies

- ``quartz``：Used for message retrying
- ``reflections``：Used to scan the annotations of @EventListener 
- ``kafka-clients``： Kafkad: Native client
- ``choerodon-starter-mybatis-mapper``：Mybatis's package (joined when start up to heavy)
- ``mysql-connector-java``: The JDBC driver of Mysql  (joined when enabled to remove deduplication)
- ``spring-cloud-netflix-core``：The basic package of spring cloud  (joined when the message failure policy is set to send_back_event_store)

## Reporting Issues

If you find any shortcomings or bugs, please describe them in the [Issue](https://github.com/choerodon/choerodon/issues/new?template=issue_template.md).
    
## How to Contribute
Pull requests are welcome! Follow [this link](https://github.com/choerodon/choerodon/blob/master/CONTRIBUTING.md) for more information on how to contribute.

## Note
- In order to ensure that the message will not be re-used, please turn on the *Remove deduplication* function, which is ``choerodon.event.consumer.enable-duplicate-remove=true``.
- If manual retrying on the page has not been completed yet, after completion, set the message failure policy** to send_back_event_store to ensure that the message is consumed.
- The retry function of the message is only used for non-code exceptions such as temporarily connecting to the database. If there is no the retry of non-code exceptions, there is no effect.