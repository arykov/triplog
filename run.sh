MAVEN_OPTS='-agentlib:jdwp=transport=dt_socket,address=8000,server=y,suspend=n'
mvn  -Dlogback.configurationFile=logback.xml jetty:run
