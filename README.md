# BPMN MIWG Tools

This is a set of tools for validating and comparing the [test results](https://github.com/bpmn-miwg/bpmn-miwg-test-suite) collected by the BPMN Model Interchange Workging Group (BPMN MIWG) at the OMG.


# Test Reports

One of the outputs of the BPMN MIWG tools is a set of [Test Reports](http://bpmn-miwg.github.io/bpmn-miwg-tools):

[<img height="400" src="http://bpmn-miwg.github.io/bpmn-miwg-tools/bpmn-tools-tested-for-model-interchange-screenshot.png">](http://bpmn-miwg.github.io/bpmn-miwg-tools/)

This work is licensed under the MIT License (see LICENSE.txt).



# Setting up the development environment

The development environment is being set up as follows:

1. First, clone the following repositories bmpm-miwig-test-suite and bpmn-miwg-tools
2. In `bmpm-miwig-test-suite`:
	1. Run `mvn clean install`
3. In `bpmn-miwg-tools`:
	1. Run `mvn clean install`
	2. Run `mvn eclipse:eclipse`
4. Open the projects in eclipse


## Personal access token

Some tests in `bpmn-miwg-tools` require a [GitHub personal access token](https://help.github.com/articles/creating-a-personal-access-token-for-the-command-line/).

This can be passed as follows:
`-DgithubUser=USERNAME -DgithubToken=GITHUBTOKEN`

