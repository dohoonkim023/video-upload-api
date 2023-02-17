FROM openjdk:11-jre-slim
COPY build/libs/video-upload-api-0.0.1-SNAPSHOT.jar video.jar
RUN  apt-get update -y && apt-get install -y ffmpeg

ENTRYPOINT ["java", "-DSpring.profiles.active=prod", "-jar", "/video.jar"]