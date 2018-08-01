# Choerodon Starter Oauth Resource

This project is a jar package. It is a basic repository of Choerodon. It provides resource services to check `JWT_TOKEN` when accepting http requests, and captures the exceptions of `controller` uniformly, and converts it into user language corresponding description information.

## Usage

The dependencies of pom:

```xml
<dependency>
    <groupId>io.choerodon</groupId>
    <artifactId>choerodon-starter-oauth-resource</artifactId>
     <version>0.6.1.RELEASE</version>
</dependency>
```

1.Check the legality of the `access token` that is carried when accessing the service.

* Block all http requests except `HttpMethod` for `OPTIONS`
* Get the permission check of `JWT_TOKEN` in the head information of the HTTP.
* `JWT_TOKEN` uses a custom conversion method to resolve.

Usage：

* The repository configures the type of interfaces that need to validate `token`. After referencing the repository, you need to add the annotation `@EnableChoerodonResourceServer` to the `application` startup class. The service will automatically verify the `JWT_TOKEN`.

* The verified url defaults to `/v1/*`. You can also customize the path type of the request to be verified. Add `choerodon.resource.pattern=/v2/*,/v3/*` in the spring-boot configuration file. 

* When testing locally, you need to add `{"Jwt_Token" : "Bearer jwt_token"}` to the header.

2.Unified capture and processing of service control class exceptions.

* Exception Information returns object `Exception Response`
* Indicates that the caught controller exceptions include `CommonException`, `NotFoundException`, `MethodArgumentNotValidException`, `DuplicateKeyException`, `MultipartException`, `BadSqlGrammarException`.
* According to the user's language, return the description of captured exception.

Usage:

In the business development of the service, throwing the above exceptions into the `controller` layer can be captured and returned with custom description information.
```java
throw new CommonException("error message");
```
 
3.Method for serializing and deserializing time based on time zone of user.


##  Reporting Issues

If you find any shortcomings or bugs, please describe them in the Issue.
    
## How to Contribute
Pull requests are welcome! Follow this link for more information on how to contribute.

