BPMN Model Interchange Results Site 
===================================

These GitHub pages store the results of the work created by the BPMN Model Interchange Working Group (BPMN MIWG) at the OMG.

For more information see: http://www.omgwiki.org/bpmn-miwg
For the results themselves see: http://bpmn-miwg.github.io/bpmn-miwg-tools/

[<img height="350" src="http://bpmn-miwg.github.io/bpmn-miwg-tools/bpmn-tools-tested-for-model-interchange-screenshot.png">](http://bpmn-miwg.github.io/bpmn-miwg-tools/)

<a rel="license" href="http://creativecommons.org/licenses/by/3.0/deed.en_CA"><img alt="Creative Commons Licence" style="border-width:0" src="http://i.creativecommons.org/l/by/3.0/88x31.png" /></a><br />This work is licensed under a <a rel="license" href="http://creativecommons.org/licenses/by/3.0/deed.en_CA">Creative Commons Attribution 3.0 Unported License</a>.

Information for anyone wanting to publish the latest results
============================================================

Where is everything kept? 
-------------------------

1. The tests themselves are in the repository at: https://github.com/bpmn-miwg/bpmn-miwg-test-suite. You will need to clone it to your machine to get the very latest content or to add new results. Approximately once a week an archive is published to the MIWG Maven repository, more of this later. 

1. The tools that analyse the tests are in this repository: https://github.com/bpmn-miwg/bpmn-miwg-tools in the master branch. They are also compiled and published to the Maven repo. 

1. To run the tests in a single command you will need to have Maven installed. From the bpmn-miwg-test-suite folder that you cloned you can run 
```
mvn clean bpmn-miwg:test site site:jar install
```
This will take several minutes. 

1. To update the results site, switch to the folder where you have cloned _this_ repository. There run:
```
mvn process-resources 
```

1. You now have a local copy of the new site, please take a moment to check the contents are as expected. If you have python installed you can do this conveniently by running: 
```
python -m SimpleHTTPServer 8888
```
and then navigating to this address in your browser: 
```
http://localhost:8888/
```

1. Once you are satisfied the results are all present, simple commit and push to GitHub. 

In case of any issues please contact miwg(at)trisotech.com. 

