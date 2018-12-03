# Choerodon Starter Swagger

The toolkit of swagger, configures ``Docket`` of swagger, and customizes some swagger plugins for scanning annotations, etc., and adds scanned data to swagger's json.

- Configure swagger's Docket.
- Exclude swagger's built-in ``ApiResourceController``. The controller's interface is directly invoked by the jss of the swagger-ui front end, because the implementation of the interface needs to be customized in the ``manager-service``, to prevent the built-in ``ApiResourceController`` from conflicting with the custom interface, so the built-in ``ApiResourceController`` is excluded.

- Customize ``CustomPageRequest`` of the swagger api.
- Customize swagger plugins, scan annotations such as ``@Permission`` and ``@Label``, and add notes of swagger json to the information of annotation.
- Through the ``@ChoerodonExtraData`` annotation, implement the ``ExtraData`` Manager interface class, custom scan to the class and call the method of getData, add the acquired ``ExtraData`` to swagger json.
- After the service starts, the ``manager-service`` will automatically pull the swagger json of the service, and perform the response parsing method according to custom data of different types. For example, the data of ``@Permission`` will be parsed into the data of the interface for service authentication.


## Requirements
- The toolkit is spring boot's starter project, only for spring boot project.

## Usage
- Add a dependency (Docket and plugin will automatically take effect after addition):

  ```xml
    <dependency>
      <groupId>io.choerodon</groupId>
      <artifactId>choerodon-starter-swagger</artifactId>
       <version>0.8.1.RELEASE</version>
    </dependency>
  ```
- Using the paged interface, add the ``@CustomPageRequest`` annotation so that swagger ui displays the paging request correctly:

  ```java
    @GetMapping
    @CustomPageRequest
    public ResponseEntity<Page<ProjectDTO>> list(@ApiIgnore
                                                 @SortDefault(value = "id", direction = Sort.Direction.DESC)PageRequest pageRequest) {
       //...
    }
  ```
- Insert custom information extra information into the swagger by using ``ChoerodonExtraData``:

  ```java
   @ChoerodonExtraData
	public class CustomExtraDataManager implements ExtraDataManager {
	    @Override
	    public ExtraData getData() {
	        ChoerodonRouteData choerodonRouteData = new ChoerodonRouteData();
	        choerodonRouteData.setName("manager");
	        choerodonRouteData.setPath("/manager/**");
	        choerodonRouteData.setServiceId("manager-service");
	        extraData.put(ExtraData.ZUUL_ROUTE_DATA, choerodonRouteData);
	        return extraData;
	    }
	}
  ```
  
## Reporting Issues

If you find any shortcomings or bugs, please describe them in the [Issue](https://github.com/choerodon/choerodon/issues/new?template=issue_template.md).
    
## How to Contribute
Pull requests are welcome! Follow [this link](https://github.com/choerodon/choerodon/blob/master/CONTRIBUTING.md) for more information on how to contribute.
