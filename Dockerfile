FROM vertx/vertx3

#Environment variables
ENV VERTICLE_FILE target/blogger-services-1.0.0-SNAPSHOT-fat.jar

ENV VERTICLE_HOME /usr/verticles
#Expose port of the docker image
EXPOSE 8443

#Copy your verticle to the container
COPY $VERTICLE_FILE $VERTICLE_HOME/

#Launch the verticle
WORKDIR $VERTICLE_HOME
ENTRYPOINT ["sh", "-c"]
CMD ["java -jar $VERTICLE_FILE"]
