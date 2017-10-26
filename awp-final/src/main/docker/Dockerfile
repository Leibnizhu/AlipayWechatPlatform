FROM java:8-jre

ENV VERTICLE_FILE awp-0.0.1-SNAPSHOT-fat.jar
ENV VERTICLE_NAME com.turingdi.awp.verticle.MainVerticle
ENV VERTICLE_HOME /usr/verticles

EXPOSE 8080

# Copy your fat jar to the container
COPY verticles/* $VERTICLE_HOME/

# Launch the verticle
WORKDIR $VERTICLE_HOME
ENTRYPOINT ["sh", "-c"]
CMD ["exec java -jar $VERTICLE_FILE run $VERTICLE_NAME -conf $VERTICLE_HOME/classes/config.json"]