Objective
=========

This test tool runs the following tests on the BPMN files:

(1) Validate the BPMN XSD Schema.
(2) Test selected aspects of the B.1 test using XPath expressions.
(3) Test selected aspects of the B.2 tests using XPath expressions.

Running
=======

The application expects two parameters:

(1) A folder containing the rest results
   (e.g., ```B - Validate that tool covers conformance class set``` in the test case repository).
 
(2) A folder where the results are stored. Existing files will be overwritten.

An optional third parameter limits which applications can be run. An application is designated
by the folder containing the BPMN files (e.g., ```MID Innovator 11.5.1.30223```).
This is particularly useful when debugging the program.


Constraints
===========

Completeness
------------
Only parts of the model are tested. Therefore, BPMN files which are successfully tested,
may still contain semantic errors. Syntactic errors are unlikely, as the XSD validation
is quite strict.

Attributes
----------
The test engine is quite strict and expects the tests in a correct format.
In particular, the names have to be correct on a byte-to-byte base.

E.g., the engine won't find the following tag:

```<messageFlow name="MessageFlow 1" ... ```

The correct tag is:
```<messageFlow name="Message Flow 1" ... ```

There are only the following exceptions:
- During a normalization of the DOM, trailing and leading white spaces are removed.
- Double white spaces are reduced to a single whitespace. 


Technical details
=================

The engine searches the repository for BPMN files that are named according the naming conventions

For each application and BPM file, the available tests (currently only B.1.0) are executed and
a log file is created. The log file is stored in a separate directory in order to avoid merge
conflicts. There is one exception to the naming convention: For testing the test engine, the
reference BPMN files are tested as well.

The test procedure currently consists of the following steps:

1. The engine uses XSD schema validation
2. The engine issues a large number of XPath queries on a DOM that is built from the BPMN XML.
   A stack ensures that the test can traverse down and up the DOM.
   Tests are specified in the following format:

```	loadFile(fileName);

	selectElement("Collaboration", "//bpmn:collaboration");

	navigateElement("Message Flow 1",
			"bpmn:messageFlow[@name='Message Flow 1']");
	navigateElement("Message Flow 2",
			"bpmn:messageFlow[@name='Message Flow 2']");

	{
		selectProcess("Process of 'Participant'",
				"//bpmn:process[@id=//bpmn:participant[@name='Participant']/@processRef]");
		navigateElement("Start Event timer",
				"bpmn:startEvent[@name='Start Event Timer']",
				"timerStartEvent");
		navigateElement("User Task 2",
				"bpmn:userTask[@name='User Task 2']");
		navigateElement("Service Task 3",
				"bpmn:serviceTask[@name='Service Task 3']");
		navigateElement("End Event None 1",
				"bpmn:endEvent[@name='End Event None 1']");
		pop();
	}```


Results
=======

Reference
---------
The test indicates no issues. This is expected.

Camunda Modeler
---------------
The test indicates no issues.

MID Innovator
-------------
The tests confirms the already reported issues.

Signavio
--------
Signavio serializes models containing UTF-8 line breaks (```&#10;```). This irritates the XPath
engine which cannot find the corresponding attributes.

Yaoqiang
--------
There is no participant with the name "Pool". Actually, there is only one participant
("Participant"). Therefore, all assertions for the second pool return issues..


Requests
========
Values for the name attribute should be unique at least at the process level. Otherwise,
defining tests will be more time-consuming. Currently, the models appear to have sufficiently
unique name attributes.