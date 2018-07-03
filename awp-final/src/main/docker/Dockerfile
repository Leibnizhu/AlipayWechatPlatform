FROM java:8u111-jre-alpine

ENV VERTICLE_FILE awp-0.0.1-SNAPSHOT-fat.jar
ENV VERTICLE_HOME /usr/verticles

EXPOSE 8080

# Copy your fat jar to the container
COPY verticles/* $VERTICLE_HOME/

# Launch the verticle
WORKDIR $VERTICLE_HOME
ENTRYPOINT ["sh", "-c"]
CMD ["exec java -jar $VERTICLE_FILE" $VERTICLE_HOME/config.json"]