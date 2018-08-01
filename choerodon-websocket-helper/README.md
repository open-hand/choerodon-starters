## Choerodon WebSocket Helper
A tool to route msg by using redis from one WebSocket Client to another.This tool is
mainly designed for supporting the interacting of devops service with choerodon agent. And also support
simple WebSocket msg route and dispatch tool.


## Usage


Add maven dependency.
```xml
<dependency>
     <artifactId>choerodon-websocket-helper</artifactId>
     <groupId>io.choerodon</groupId>
     <version>0.6.1.RELEASE</version>
 </dependency>

```

Add this config to spring boot application yml config file.
```yaml
choerodon:
  websocket:
    // whether to open security check ,default true
    security: 
    // endpoint adderess for web, defalut is/ws/**
    front: 
    //the oauth token check url
    oauth-url: http://your.oauth-server/oauth/api/user
    //endpoint address for choerodon-agent ,default is /agent/**
    agent: 
    //max concurrency of redis msg listener container
    max-redis-msg-listener-concurrency: 500
    //self register interval milliseconds
    register-interval: 

```

## Dependencies

* Redis

##  Reporting Issues

If you find any shortcomings or bugs, please describe them in the [Issue](https://github.com/choerodon/choerodon/issues/new?template=issue_template.md).
    
## How to Contribute
Pull requests are welcome! Follow [this link](https://github.com/choerodon/choerodon/blob/master/CONTRIBUTING.md) for more information on how to contribute.
