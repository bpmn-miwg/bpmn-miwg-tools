#!/bin/sh
wget 'http://raw.github.com/bpmn-miwg/bpmn-miwg-test-suite/master/tools-tested-by-miwg.json'  --output-document=tools-tested-by-miwg.json
wget "https://api.github.com/repos/bpmn-miwg/bpmn-miwg-test-suite/issues?state=open&per_page=100"   --output-document=issues-open.json
wget "https://api.github.com/repos/bpmn-miwg/bpmn-miwg-test-suite/issues?state=closed&per_page=100" --output-document=issues-closed.json
#wget 'http://open.dapper.net/transform.php?dappName=BPMNToolsfromBPMNorg&transformer=JSON&applyToUrl=http%3A%2F%2Fwww.businessprocessincubator.com%2Ftag%2Fproduct%2Flist%2FtagId%2F170%2F%3Fbpi-embedded%3D1%26bpi-feed%3D1%26dir%3Dasc%26layout%5B0%5D%3Dnew-window%26limit%3Dall%26mode%3Dgrid%26order%3Dname%26utm_campaign%3DFeed%26utm_medium%3Dweb%26utm_source%3DOMG' -O - | python -mjson.tool > bpmn-tools.json
echo 'If one of the following numbers equals to 100, we are not displaying all issues:'
grep -o '"body":' issues-open.json | wc -l
grep -o '"body":' issues-closed.json | wc -l
