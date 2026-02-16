FROM eclipse-temurin:21-jre-alpine

ADD ./build/libs/transaction-*-SNAPSHOT.jar /

RUN mv transaction-* transaction.jar && \
    chown -R 65000:65000 transaction.jar


EXPOSE 8080
USER 65000

# Entry point
ENTRYPOINT ["java", "-jar", "/transaction.jar"]