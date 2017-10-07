FROM polyhx/java-seed

ADD . .

EXPOSE 8080

RUN mvn clean package

CMD ["java", "-jar", "target/lhgames-java-server-1.0.0.jar"]
