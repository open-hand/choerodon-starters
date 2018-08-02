# Choerodon Starter Bus
This service comes from ``spring cloud bus 1.3.x``, and add version and the information of service instance, also dynamically pull the configuration with version.

- Modify ``RemoteApplicationEvent``

    Added fields for ``destinationInstance`` and ``configVersion`` to indicate the service instance ID and the version of config.
- The ``onApplicationEvent`` method of ``RefreshListener`` was modified. When the event has ``configVersion``, dynamically modify the label in the environment variable.
  
  ```java
  public void onApplicationEvent(RefreshRemoteApplicationEvent event) {
      if (!StringUtils.isEmpty(event.getConfigVersion())) {
          environmentManager.setProperty(CONFIG_LABEL, event.getConfigVersion());
      }
      Set<String> keys = contextRefresher.refresh();
      log.info("Received remote refresh request. Keys refreshed " + keys);  
  }
  ```
  
## Requirements
- The service used must be a spring cloud project.

## To get the code

```
git clone https://github.com/choerodon/choerodon-starters.git
```

## Usage
- Replace spring-cloud-bus

 ```xml
  <dependency>
	    <groupId>org.springframework.cloud</groupId>
	    <artifactId>spring-cloud-starter-bus-kafka</artifactI>
	    <exclusions>
	        <exclusion>
	            <groupId>org.springframework.cloud</groupId>
	            <artifactId>spring-cloud-bus</artifactId>
	        </exclusion>
	    </exclusions>
  </dependency>
  <dependency>
	    <groupId>io.choerodon</groupId>
	    <artifactId>choerodon-starter-bus</artifactId>
	    <version>0.6.0.RELEASE</version>
  </dependency>
 ```

## Reporting Issues

If you find any shortcomings or bugs, please describe them in the [Issue](https://github.com/choerodon/choerodon/issues/new?template=issue_template.md).
    
## How to Contribute
Pull requests are welcome! Follow [this link](https://github.com/choerodon/choerodon/blob/master/CONTRIBUTING.md) for more information on how to contribute.

## Note
- All services that use bus must replace ``bus`` with ``choerodon-starter-bus. Otherwise``, otherwise some error will be caused by incompatible message entity format.
