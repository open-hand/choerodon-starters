# choerodon-starter-config-client
> The package modifies spring cloud config client 1.3.x to achieve the purpose of zuul route hot updating. After modifying the route through the manager-service page, the api-gateway and gateway-helper's services do not need to be restarted, the routing takes effect dynamically, and the route for the HelperZuulRoutesProperties is hot-updated. The properties is used to store custom gateway helpers that the service passes through. 

- Implement zuul's routing hot update.
- Implements routing thermal updates of HelperZuulRoutesProperties. The route is used to determine whether to request the default gateway-helper or the customized gateway-helper to authenticate and limit the flow.

- Principle of realization：

  Then，the api-gateway and gateway-helper will call the package's ConfigServicePropertySourceLocator's locate method to pull the configuration, modify the implementation of the locate method to add operations on updating the zuul route and the HelperZuulRoutesProperties route, and send a RoutesRefreshedEvent event to implement the route's hot update .
  

## Requirements
- The service must be a zuul project using the spring cloud of bus.
- Currently, manager-service only supports api-gateway and gateway-helper's services to obtain routing information.

## To get the code

```
git clone https://github.com/choerodon/choerodon-starters.git
```

## Usage
- Add dependencies (replace zuul's original spring-cloud-config-client):

  ```xml
    <dependency>
      <groupId>io.choerodon</groupId>
      <artifactId>choerodon-starter-config-client</artifactId>
       <version>0.5.0.RELEASE</version>
    </dependency>
  ```
- Add a bean using the service to make it effective:
   
 ```java
 @Bean
 public RouteLocator memoryRouterOperator() {
     return new MemoryRouteLocator(this.server.getServletPrefix(), this.zuulProperties);
 }

 @Bean
 public RouterOperator routerOperator(ApplicationEventPublisher publisher,
                                      RouteLocator routeLocator) {
     return new RouterOperator(publisher, routeLocator);
 }
 ```

## Reporting Issues

If you find any shortcomings or bugs, please describe them in the Issue.
    
## How to Contribute
Pull requests are welcome! Follow this link for more information on how to contribute.

## Note
- Currently, manager-service only supports api-gateway and gateway-helper services to obtain routing information. To add additional services to pull routing information, configure the choerodon.gateway.names attribute in manager-service.