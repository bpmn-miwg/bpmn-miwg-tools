. ~/.bash_profile
mvn package site site:stage post-site
bees app:deploy -a bpmn-miwg/tools target/tools.war

