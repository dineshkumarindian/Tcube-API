# Build Image Base
FROM maven:3.8.1-amazoncorretto-8 AS BUILD

# Copy required files
COPY src /home/app/src
COPY pom.xml /home/app

# Run copy & build commands
RUN cp home/app/src/main/resources/application-dev.properties /home/app/src/main/resources/application.properties
RUN mvn -f /home/app/pom.xml -T 2C clean install -Dmaven.test.skip -DskipTests

# Deploy Image Base
FROM tomcat:9.0-jdk8-corretto

# Copy war file to tomcat
COPY --from=build /home/app/target/tcube-0.0.1-SNAPSHOT.war /usr/local/tomcat/webapps/ROOT.war

