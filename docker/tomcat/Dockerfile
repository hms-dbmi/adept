FROM tomcat:7-jre8

RUN rm -rf /usr/local/tomcat/webapps/*
COPY note-services.war /usr/local/tomcat/webapps
RUN mv /usr/local/tomcat/webapps/note-services.war /usr/local/tomcat/webapps/ROOT.war
COPY web.xml /usr/local/tomcat/conf

EXPOSE 8080

CMD ["catalina.sh", "jpda", "run"]
