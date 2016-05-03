FROM vertx/vertx3
MAINTAINER Krish <kyechcha@cisco.com>

#Environment variables
ENV VERTICLE_PATH target
ENV VERTIFLE_FILE blogger-services-1.0.0-SNAPSHOT-fat.jar

ENV CONTAINER_VERTICLE_HOME /usr/verticles

#Expose port of the docker image
EXPOSE 8443

#Create User / group cmaduser and change permissions for home folder
RUN groupadd -r cmaduser -g 433 && \
    useradd -u 431 -r -g cmaduser -d $CONTAINER_VERTICLE_HOME -s /sbin/nologin -c "Docker image user" cmaduser && \
    chown -R cmaduser:cmaduser $CONTAINER_VERTICLE_HOME

#Switch to non-root user as a best practice
USER cmaduser

#Copy your verticle to the container
COPY $VERTICLE_PATH/$VERTICLE_FILE CONTAINER_VERTICLE_HOME/

#Launch the verticle
WORKDIR $CONTAINER_VERTICLE_HOME
ENTRYPOINT ["sh", "-c"]
CMD ["java -jar  $VERTICLE_FILE"]
