Objective
=========

This test tool performs XML comparisons of the reference BPMN files and those 
imported and re-exported by each tool. It aims to eliminate differences that 
are not significant to the semantic equivalence of the files. 

Running
=======

The easiest way to run the test tool is via Maven as it manages all dependencies
for you. 

1. Install Maven as described here: 
2. Clone the tools and test suite to the same directory as the tools, for example: 
    /Users/timstephenson/git/bpmn-miwg-tools/BPMN 2.0 XML Compare
    /Users/timstephenson/git/bpmn-miwg-test-suite
3. Run mvn test from the XML Compare directory.

Constraints
===========

This tool is still under development and it is possible that non-significant 
differences are reported. It is less likely (though possible I suppose) that 
significant differences are omitted. 

For now no assertions are made in the test (asserting that no differences exist
would simply ensure all tests were reported as failures). One future direction 
could be to create a baseline that asserted if the number of differences 
increased between different versions of a given tool or different versions of 
the reference models but this is not decided. 

Results
=======

An early sample of summary output is as follows. This will be published in 
summary and complete form shortly. 

Looking for MIWG resources in: /Users/timstephenson/git/bpmn-miwg-tools/BPMN 2.0 XML Compare/../../bpmn-miwg-test-suite
Issues found in implementation of A.1.0 by Yaoqiang 2.1.33: 38
Issues found in implementation of A.1.0 by Yaoqiang 2.1.28: 32
Issues found in implementation of A.1.0 by camunda modeler 2.0.12: 55
Issues found in implementation of A.1.0 by Signavio Process Editor 6.7.5: 34
Issues found in implementation of A.1.0 by Yaoqiang 2.1.24: 32
Issues found in implementation of A.1.0 by MID Innovator 11.5.1.30223: 1
Issues found in implementation of A.1.0 by IBM Process Designer 8.0.1: 10
Issues found in implementation of A.1.0 by camunda modeler 2.0.11: 26
Issues found in implementation of A.2.0 by Yaoqiang 2.1.33: 59
Issues found in implementation of A.2.0 by Yaoqiang 2.1.28: 48
Issues found in implementation of A.2.0 by camunda modeler 2.0.12: 113
Issues found in implementation of A.2.0 by Signavio Process Editor 6.7.5: 50
Issues found in implementation of A.2.0 by Yaoqiang 2.1.24: 48
Issues found in implementation of A.2.0 by MID Innovator 11.5.1.30223: 1
Issues found in implementation of A.2.0 by IBM Process Designer 8.0.1: 10
Issues found in implementation of A.2.0 by camunda modeler 2.0.11: 42
Issues found in implementation of A.3.0 by Yaoqiang 2.1.33: 64
Issues found in implementation of A.3.0 by Yaoqiang 2.1.28: 53
Issues found in implementation of A.3.0 by camunda modeler 2.0.12: 111
Issues found in implementation of A.3.0 by Signavio Process Editor 6.7.5: 55
Issues found in implementation of A.3.0 by Yaoqiang 2.1.24: 53
Issues found in implementation of A.3.0 by MID Innovator 11.5.1.30223: 1
Issues found in implementation of A.3.0 by IBM Process Designer 8.0.1: 10
Issues found in implementation of A.3.0 by camunda modeler 2.0.11: 47
Issues found in implementation of A.4.0 by Yaoqiang 2.1.33: 118
Issues found in implementation of A.4.0 by Yaoqiang 2.1.28: 139
Issues found in implementation of A.4.0 by camunda modeler 2.0.12: 191
Issues found in implementation of A.4.0 by Signavio Process Editor 6.7.5: 121
Issues found in implementation of A.4.0 by Yaoqiang 2.1.24: 1
Issues found in implementation of A.4.0 by MID Innovator 11.5.1.30223: 1
Issues found in implementation of A.4.0 by IBM Process Designer 8.0.1: 1
Issues found in implementation of A.4.0 by camunda modeler 2.0.11: 1
Issues found in implementation of B.1.0 by Yaoqiang 2.1.33: 237
Issues found in implementation of B.1.0 by Yaoqiang 2.1.28: 280
Issues found in implementation of B.1.0 by camunda modeler 2.0.12: 394
Issues found in implementation of B.1.0 by Signavio Process Editor 6.7.5: 159
Issues found in implementation of B.1.0 by Yaoqiang 2.1.24: 247
Issues found in implementation of B.1.0 by MID Innovator 11.5.1.30223: 1
Issues found in implementation of B.1.0 by IBM Process Designer 8.0.1: 1
Issues found in implementation of B.1.0 by camunda modeler 2.0.11: 238
Issues found in implementation of B.2.0 by Yaoqiang 2.1.33: 619
Issues found in implementation of B.2.0 by Yaoqiang 2.1.28: 741
Issues found in implementation of B.2.0 by camunda modeler 2.0.12: 1145
Issues found in implementation of B.2.0 by Signavio Process Editor 6.7.5: 422
Issues found in implementation of B.2.0 by Yaoqiang 2.1.24: 702
Issues found in implementation of B.2.0 by MID Innovator 11.5.1.30223: 1
Issues found in implementation of B.2.0 by IBM Process Designer 8.0.1: 1
Issues found in implementation of B.2.0 by camunda modeler 2.0.11: 706


