#!/bin/sh
str=$"\n"
nohup /usr/local/jdk1.8.0_171/bin/java -jar apollo-portal-0.11.0-SNAPSHOT.jar  -Dapollo_profile=cas > /usr/local/apollo/portal/log.log 2>&1 &
sstr=$(echo -e $str)
echo $sstr
exit