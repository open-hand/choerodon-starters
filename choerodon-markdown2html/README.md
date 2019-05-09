# choerodon-markdown2html
Java library for parsing and rendering Markdown text according to the CommonMark specification (and some extensions).


It is modify from a open source repository([commonmark-java](https://github.com/atlassian/commonmark-java.git)) and add some custom features.

## Feature
- Small (core has no dependencies, extensions in separate artifacts)
- Fast (10-20 times faster than pegdown, see benchmarks in repo)
- Flexible (manipulate the AST after parsing, customize HTML rendering)
- Extensible (tables, strikethrough, autolinking and more, see below)

## Installation and Getting Started

Simply add the following dependency to your project's build file:


**Maven: pom.xml**
```xml
<dependency>
   <groupId>io.choerodon</groupId>
   	<artifactId>choerodon-markdown2html</artifactId>
   	<version>0.11.0.RELEASE</version>
</dependency>
```

## Reporting Issues
If you find any shortcomings or bugs, please describe them in the  [issue](https://github.com/choerodon/choerodon/issues/new?template=issue_template.md).

## How to Contribute
Pull requests are welcome! [Follow](https://github.com/choerodon/choerodon/blob/master/CONTRIBUTING.md) to know for more information on how to contribute.
