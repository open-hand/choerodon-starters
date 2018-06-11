# Choerodon Starter Core

The is the core toolkit developed by Choerodon and provides some basic classes for use in the development process. Contains entity classes, exception classes, object conversion tools, custom validation tools, etc. that are commonly used in the development of microservices using the choerodon framework.

## Feature

The follow-up will continue to be refactored, moving some basic and generic classes to the`` choerodon-starter-core`` package.

## Requirements

- The toolkit is spring boot's starter project, only for spring boot project.

## To get the code

```
git clone https://github.com/choerodon/choerodon-starters.git
```

## Installation and Getting Started

If the project uses the toolkit, it only needs to introduce the following the dependencies of maven.

```xml
<dependency>
    <groupId>io.choerodon</groupId>
    <artifactId>choerodon-starter-core</artifactId>
    <version>0.5.2.RELEASE</version>
</dependency>
```

## Usage


### `BaseController`

Inherited from the controller of this class, using the get method to request parameters, equivalent to doing a `.trim()` operation.

### Object Conversion Tools

Used for mutual conversion of entity, datao bject, and dto. The parameter types of `ConvertorI` are ordered, followed by entity, data object, and dto. If you only customize the conversion of two types, the other type writes `Object`.
Specific conversion implementation requires rewriting method：

```java
@Component
public class LabelConverter implements ConvertorI<LabelE, LabelDO, LabelDTO> {

    @Override
    public LabelE dtoToEntity(LabelDTO dto) {
        return new LabelE(dto.getId(), dto.getName(), dto.getType(),
                dto.getObjectVersionNumber());
    }

    @Override
    public LabelDTO entityToDto(LabelE entity) {
        LabelDTO labelDTO = new LabelDTO();
        BeanUtils.copyProperties(entity, labelDTO);
        return labelDTO;
    }

    @Override
    public LabelE doToEntity(LabelDO dataObject) {
        return new LabelE(dataObject.getId(), dataObject.getName(), dataObject.getType(),
                dataObject.getObjectVersionNumber());
    }

    @Override
    public LabelDO entityToDo(LabelE entity) {
        LabelDO labelDO = new LabelDO();
        BeanUtils.copyProperties(entity, labelDO);
        return labelDO;
    }

    @Override
    public LabelDTO doToDto(LabelDO dataObject) {
        LabelDTO labelDTO = new LabelDTO();
        BeanUtils.copyProperties(dataObject, labelDTO);
        return labelDTO;
    }

    @Override
    public LabelDO dtoToDo(LabelDTO dto) {
        LabelDO labelDO = new LabelDO();
        BeanUtils.copyProperties(dto, labelDO);
        return labelDO;
    }

}
```

The class of conversion tool will give all the converters to the spring management, custom converters need to add `@Component` annotations, call `ConvertHelper` when converting:

```java
//Turn entity into dto object
ClientE client = new ClientE();
ConvertHelper.convert(client, ClientDTO.class);
//Turn list entity into list dto object
List<ClientE> clients = new ArrayList<>();
List<ClientDTO> clientList = ConvertHelper.convertList(clients, ClientDTO.class);
```

The `ConvertPageHelper` method is provided for the `Page` object of `PageHelper`:

```java
Page<ClientDO> clientDOPage
                = PageHelper.doPageAndSort(pageRequest, () -> clientMapper.fulltextSearch(clientDO, params));
Page<ClientE> clientEPage = ConvertPageHelper.convertPage(clientDOPage, ClientE.class);
```

### Common exception class

`CommonException` is a common exception class that uses:

```java
throw new CommonException("error.message");
```

Exception message is used in conjunction with spring message. If you need to display parameters, the usage is as follows:

```java
throw new CommonException("error.message",1,2);
```

```properties
error.message=error message {0}, {1}
```

### Oauth

Get current login user information and client information. Usage is as follows:

```java
CustomUserDetails details = DetailsHelper.getUserDetails();
```

```java
CustomClientDetails customClientDetails = DetailsHelper.getClientDetails();
```

### Swagger

`PermissionData` is used to store swagger, capture interface information, and send to iam service to resolve permissions.
`SwaggerExtraData` is an extended entity: added LabelData object.
`ChoerodonRouteData`: Microservice routing entity.

### Verification tools

`@Valid` only validates a single object, `ValidList` implements the check on the list collection. Usage is as follows:

```java
public ResponseEntity<List<OrganizationDTO>> create(@RequestBody @Valid ValidList<OrganizationDTO> organizations) {}
```

## Dependencies

- ``org.springframework.security.oauth``: Oauth module reference dependencies
- ``com.fasterxml.jackson.core``: Date serialization and deserialization of oauth module
- ``javax.validation``: Class of verification tools of verifying relation

## Reporting Issues

If you find any shortcomings or bugs, please describe them in the [Issue](https://github.com/choerodon/choerodon/issues/new?template=issue_template.md).

## How to Contribute

Pull requests are welcome! Follow [this link](https://github.com/choerodon/choerodon/blob/master/CONTRIBUTING.md) for more information on how to contribute.