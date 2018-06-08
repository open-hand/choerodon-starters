# Choerodon Starter Config Monitor

``choerodon-starter-config-monitor`` comes from the ``spring cloud config monitor 1.3.x``, the message sent to the bus adds the instance information of configured version and service.

```java
@RequestMapping(method = RequestMethod.POST)
    public Set<String> notifyByPath(@RequestHeader HttpHeaders headers,
                                    @RequestBody Map<String, Object> request) {
        PropertyPathNotification notification = this.extractor.extract(headers, request);
        if (notification != null) {
            String destinationInstance = null;
            Object instance = request.get(KEY_INSTANCE);
            if (instance != null && instance instanceof String) {
                destinationInstance = (String) instance;
            }
            String configVersion = null;
            Object config = request.get(KEY_CONFIG_VERSION);
            if (config != null && config instanceof String) {
                configVersion = (String) config;
            }
            Set<String> services = new LinkedHashSet<>();

            for (String path : notification.getPaths()) {
                services.addAll(guessServiceName(path));
            }
            if (this.applicationEventPublisher != null) {
                for (String service : services) {
                    log.info("Refresh for: " + service);
                    this.applicationEventPublisher
                            .publishEvent(new RefreshRemoteApplicationEvent(this,
                                    this.contextId, service, destinationInstance, configVersion));
                }
                return services;
            }

        }
        return Collections.emptySet();
    }
```

## Feature
- In the future, the package may be removed and modified into a ``config-server`` custom interface.

## Requirements
- Used only for the service of ``config-server``.

## To get the code

```
git clone https://github.com/choerodon/choerodon-starters.git
```

## Usage
- Used in config server (replaces the original      ``spring-cloud-config-monitor``)

 ```xml
 <dependency>
     <groupId>io.choerodon</groupId>
     <artifactId>choerodon-starter-config-monitor</artifactId>
    <version>0.5.1.RELEASE</version>
 </dependency>
 ```
 
## Reporting Issues

If you find any shortcomings or bugs, please describe them in the [Issue](https://github.com/choerodon/choerodon/issues/new?template=issue_template.md).
    
## How to Contribute
Pull requests are welcome! Follow [this link](https://github.com/choerodon/choerodon/blob/master/CONTRIBUTING.md) for more information on how to contribute.

