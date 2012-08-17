export MAVEN_OPTS="-Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,address=8000,server=y,suspend=n"
nohup mvn  -Dlogback.configurationFile=logback.xml jetty:run &
