# Choerodon Starter Event Producer

**The sender of the message about data consistency**，Data consistency needs to be achieved with ``choerodon-starter-event-consumer`` and ``event-store-service``.

## Feature
- The toolkit of the data consistency sender uses the execute method of ``EventExeProducerTemplate`` or ``EventProducerTemplate`` to complete the event sending. If an event is not sent to confirm or cancel the event after the event is created due to a crash or the like, the ``event-store-service`` will call back to the interface to confirm the event status, so the service used needs to add a **Back to the interface**.

## Requirements
- This toolkit is spring boot starter project, only for the project of spring boot.
- Because using feign, the needed project is the project of spring cloud.

## Usage
- Add dependency

  ```xml
    <dependency>
      <groupId>io.choerodon</groupId>
      <artifactId>choerodon-starter-event-producer</artifactId>
      <version>0.5.3.RELEASE</version>
    </dependency>
  ```
- Add the back check interface (implement ``EventBackCheckControllerInter`` interface)

  ```java
  @RestController
  public class EventBackCheckController implements EventBackCheckControllerInter {
      @Autowired
      private OrderService orderService;
      @Override
      public EventBackCheckRecord queryEventStatus(String uuid, String type) {
          boolean exist = false;
          if (EVENT_TYPE_ORDER.equals(type)) {
              exist = orderService.exist(uuid);
          }
          //...
          if (exist) {
              return new EventBackCheckRecord(uuid, EventStatus.CONFIRMED);
          }
          return new EventBackCheckRecord(uuid, EventStatus.CANCELED);
      }
  }
  ```
- Use ``EventExeProducerTemplate`` to complete the call of the event, such as creating an order requires the inventory service to reduce the inventory synchronously.

  ```java
  private void createOrderOne(Order order) {
        RepertoryPayload payload = new RepertoryPayload("apple", 3);
        /**
         * producerType: Used for checking interface
         * consumerType: Used to distinguish different business types when consuming
         * topic：The message queue sent to can be named with the service name of the current service.
         * If the producerType and consumerType are the same, you can call execute(String type, String topic, Object payload, EventExecuter executer)
         */
        Exception exception = producerExeTemplate.execute("order", "reduceStock" ,
                "event-producer-demo", payload,
                (String uuid) -> {
                    order.setUuid(uuid);
                    order.setName(uuid);
                    if (orderMapper.insert(order) != 1) {
                        throw new CommonException("error.order.create.insert");
                    }
                    //Reset the value of payload according to the business process result.
                    payload.setOrderId(order.getId());
                });

        if (exception != null) {
            throw exception;
        }
    }
    
    //For example, the event consumer is as follows. The consumer type of the sender corresponds to the businessType of the consumer.
    @EventListener(topic = "event-producer-demo", businessType = "reduceStock")
    public void messgae(EventPayload<RepertoryPayload> payload) {
        RepertoryPayload data = payload.getData();
        LOGGER.info("data: {}", data);
        //...
     }
  ```

- The back-check logic should match the logic of the service execution at the sender of the corresponding event.
    - For example **Create order**：

    ```java
      Exception exception = producerExeTemplate.execute("createOrder", "reduceStock" ,
                "event-producer-demo", payload,
                (String uuid) -> {
                    order.setUuid(uuid);
                    order.setName(uuid);
                    if (orderMapper.insert(order) != 1) {
                        throw new CommonException("error.order.create.insert");
                    }
                });
      //Create an order, insert a piece of data, the table has the field corresponding to the uuid, back to check the logic of the interface should be successful if the uuid field exists, otherwise it fails.
       public EventBackCheckRecord queryEventStatus(String uuid, String type) {
          boolean exist = false;
          if ("createOrder".equals(type)) {
              exist = orderService.exist(uuid);
          }
          //...
          if (exist) {
              return new EventBackCheckRecord(uuid, EventStatus.CONFIRMED);
          }
          return new EventBackCheckRecord(uuid, EventStatus.CANCELED);
      }
    ```
    - For example, **Cancel orders**：

    ```java
        Order order = selectByPrimaryKey(orderId);
        String selectUuid = order.getUuid();
        boolean result = producerTemplate.execute("cancelOrder", "addStock" ,
                    "event-producer-demo", payload,
                    (String uuid) -> {
                        if (orderMapper.delete(order) != 1) {
                            throw new CommonException("error.order.create.insert");
                        }
                    }, selectUuid);//The uuid queried in the incoming orders table here
        //Delete the order, delete the data, the table will no longer have the uuid corresponding field, the logic of the checkback interface should be failed if there is the uuid field, otherwise it is successful.
        public EventBackCheckRecord queryEventStatus(String uuid, String type) {;
            if ("cancelOrder".equals(type)) {
                if (orderService.exist(uuid)) {
                return new EventBackCheckRecord(uuid, EventStatus.CONFIRMED);
                }
            }
            //...
            return new EventBackCheckRecord(uuid, EventStatus.CANCELED);
        }
     ```

## Dependencies
- ``choerodon-starter-core``: Rely on the package of core defined ``EventPayoad`` etc.
- ``spring-cloud-starter-feign``：Complete the call to ``event-store-service`` with feign.

## Reporting Issues

If you find any shortcomings or bugs, please describe them in the [Issue](https://github.com/choerodon/choerodon/issues/new?template=issue_template.md).
    
## How to Contribute
Pull requests are welcome! Follow [this link](https://github.com/choerodon/choerodon/blob/master/CONTRIBUTING.md) for more information on how to contribute.

## Note
- A check-back interface must be implemented for ``event-store-service`` callbacks when the event state is indeterminate, and the callback logic should match the business logic.