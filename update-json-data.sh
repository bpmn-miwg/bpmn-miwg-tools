#!/bin/sh

#wget 'http://open.dapper.net/transform.php?dappName=BPMNToolsfromBPMNorg&transformer=JSON&applyToUrl=http%3A%2F%2Fwww.businessprocessincubator.com%2Ftag%2Fproduct%2Flist%2FtagId%2F170%2F%3Fbpi-embedded%3D1%26bpi-feed%3D1%26dir%3Dasc%26layout%5B0%5D%3Dnew-window%26limit%3Dall%26mode%3Dgrid%26order%3Dname%26utm_campaign%3DFeed%26utm_medium%3Dweb%26utm_source%3DOMG' -O - | python -mjson.tool > bpmn-tools.json

wget 'http://raw.github.com/bpmn-miwg/bpmn-miwg-test-suite/master/tools-tested-by-miwg.json'  --output-document=tools-tested-by-miwg.json
wget 'http://raw.github.com/bpmn-miwg/bpmn-miwg-test-suite/master/test-case-structure.json'  --output-document=test-case-structure.json


# open issues
wget "https://api.github.com/repos/bpmn-miwg/bpmn-miwg-test-suite/issues?state=open&per_page=100&page=1"   --output-document=issues-open-01.json
wget "https://api.github.com/repos/bpmn-miwg/bpmn-miwg-test-suite/issues?state=open&per_page=100&page=2"   --output-document=issues-open-02.json
wget "https://api.github.com/repos/bpmn-miwg/bpmn-miwg-test-suite/issues?state=open&per_page=100&page=3"   --output-document=issues-open-03.json
wget "https://api.github.com/repos/bpmn-miwg/bpmn-miwg-test-suite/issues?state=open&per_page=100&page=4"   --output-document=issues-open-04.json
head -n -2 issues-open-01.json > issues-open.json
echo "  }," >> issues-open.json
tail -n +2 issues-open-02.json | head -n -2 >> issues-open.json
echo "  }," >> issues-open.json
tail -n +2 issues-open-03.json | head -n -2 >> issues-open.json
echo "  }," >> issues-open.json
tail -n +2 issues-open-04.json >> issues-open.json
rm issues-open-*.json


# closed issues
wget "https://api.github.com/repos/bpmn-miwg/bpmn-miwg-test-suite/issues?state=closed&per_page=100&page=1" --output-document=issues-closed-01.json
wget "https://api.github.com/repos/bpmn-miwg/bpmn-miwg-test-suite/issues?state=closed&per_page=100&page=2" --output-document=issues-closed-02.json
head -n -2 issues-closed-01.json > issues-closed.json
echo "  }," >> issues-closed.json
tail -n +2 issues-closed-02.json >> issues-closed.json
rm issues-closed-*.json


# validate completeness
echo 'If one of the following numbers equals to a multitude of 100, we are not displaying all issues:'
grep -o '"body":' issues-open.json | wc -l
grep -o '"body":' issues-closed.json | wc -l
# once the limit is reached read: http://developer.github.com/guides/traversing-with-pagination/
