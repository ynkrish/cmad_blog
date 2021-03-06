FROM ynkrish/ubuntu1510_jdk8:v1
MAINTAINER Krish <kyechcha@cisco.com>

#Environment variables
ENV VERTICLE_PATH target
ENV VERTICLE_FILE blogger-services-1.0.0-SNAPSHOT-fat.jar

ENV CONTAINER_VERTICLE_HOME /opt/verticles

#Expose port of the docker image
EXPOSE 9443

#Copy your verticle to the container
COPY $VERTICLE_PATH/$VERTICLE_FILE $CONTAINER_VERTICLE_HOME/

#Launch the verticle
WORKDIR $CONTAINER_VERTICLE_HOME
ENTRYPOINT ["sh", "-c"]
CMD ["java -jar $VERTICLE_FILE"]

#TODO: Currently using ROOT user, need to create local user and use that
# filename to be inferred ?
