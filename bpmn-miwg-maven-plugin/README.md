Objective
=========

This Maven plugin will run the BPMN MIWG test tools for contributions from all vendors' BPMN contained in the project. It may also be configured to run tests for just one vendor or a subset. 

For more information on the tests run please see the individual test tool READMEs at: 

- [xml-compare](https://github.com/bpmn-miwg/bpmn-miwg-tools/tree/master/BPMN%202.0%20XML%20Compare)
- [xpath](https://github.com/bpmn-miwg/bpmn-miwg-tools/tree/master/XPathTestRunner)

Running
=======

1. Ensure you have Maven installed, for further information see [here](http://maven.apache.org/)
1. Put your .bpmn files in the project's resources directory (default: src/main/resources) in the following sub-directory: 
  - Toolname Versionnumber/
  
  Note the 'Tool Version' folder must match either the property at step 6 below or one of the tools listed in [tools JSON file](https://github.com/bpmn-miwg/bpmn-miwg-test-suite/blob/master/tools-tested-by-miwg.json)
1. Add the BPMN-MIWG repository to your project's pom.xml: 

  ```xml
    <pluginRepositories>
      <pluginRepository>
        <id>bpmn-miwg-snapshot</id>
        <name>BPMN-MIWG Snapshot Repository</name>
        <url>http://repository-bpmn-miwg.forge.cloudbees.com/snapshot/</url>
      </pluginRepository>
    </pluginRepositories>
  ```
1. Add the BPMN-MIWG plugin to your project's pom.xml: 
  
  ```xml
    <plugins>
      ...
      <plugin>
        <groupId>org.omg.bpmn.miwg</groupId>
        <artifactId>bpmn-miwg-maven-plugin</artifactId>
        <version>${bpmn-miwg-version}</version>
      </plugin>
    </plugins>
  ```
1. Add the test-suite as a dependency; this is where the Reference models may be found. 

  ```xml
    <dependencies>
      ...
      <dependency>
        <groupId>org.omg.bpmn.miwg</groupId>
        <artifactId>test-suite</artifactId>
        <version>${bpmn-miwg-version}</version>
      </dependency>
    </dependencies>
  ```
1. If you want to restrict the tests to just one tool, add this property to your pom.xml: 

  ```xml
    <properties>
      <project.bpmn.application>Tool folder including version</project.bpmn.application>
      ...
    </properties>
  ```
  For example: 

  ```xml
    <properties>
      <project.bpmn.application>Activiti 5.14.1</project.bpmn.application>
      ...
    </properties>
  ```
1. Run tests with the following Maven command: 
  ```
     mvn bpmn-miwg:test
  ```
4. review test output in target/site/ folder 
5. (If part of the BPMN-MIWG test automation team). Copy target/site content to https://github.com/bpmn-miwg/bpmn-miwg-tools branch gh-pages and commit it to the Github repository. 

Known Issues 
============

1. If both export and roundtrip tests have been supplied then for now only the roundtrip are reported in the overview.html. All output files are however created in the xpath and xml-compare sub-folders. 

