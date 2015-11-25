#!/bin/sh
cd ../bpmn-miwg-tools/submission-counter/
mvn clean install
cd -
cp ../bpmn-miwg-tools/submission-counter/target/submissions.json .
./update-json-data.sh