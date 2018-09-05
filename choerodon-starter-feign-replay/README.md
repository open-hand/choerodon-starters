# Choerodon Starter Fegin Replay

``choerodon-starter-feign-replay`` is an extension toolkit based on spring cloud ribbon, feign, and other components. The repository mainly solves the transfer of services through feign. The mutual transfer between various microservices in Choerodon are through feign, so each resource of Choerodon needs to add this dependency package.

## Feature

Implement the passing of token based on the latest ``shareSecurityContext``

## Requirements
Add the following configuration before joining the microservice that depends on the toolkit.

```
hystrix:
  shareSecurityContext: true
```

## To get the code

```
git clone https://github.com/choerodon/choerodon-starters.git	
```
## Installation and Getting Started

Add the following dependencies of maven to the project

```
<dependency>
    <groupId>io.choerodon</groupId>
    <artifactId>choerodon-starter-feign-replay</artifactId>
    <version>0.6.3.RELEASE</version>
</dependency>
```
## Dependencies
- spring cloud eureka
- spring cloud feign

## Reporting Issues
If you find any shortcomings or bugs, please describe them in the [Issue](https://github.com/choerodon/choerodon/issues/new?template=issue_template.md).
    
## How to Contribute
Pull requests are welcome! Follow [this link](https://github.com/choerodon/choerodon/blob/master/CONTRIBUTING.md) for more information on how to contribute.

## Note
This tool only passes tokens for oauth2 in Choerodon, and calls feign. If there is no security context, a token is generated based on the configured serviceAccount.