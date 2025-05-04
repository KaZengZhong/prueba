# Usar una imagen base más segura como Amazon Corretto con la última versión
FROM amazoncorretto:17.0.15
ARG JAR_FILE=target/prestabanco-backend.jar
COPY ${JAR_FILE} prestabanco-backend.jar
EXPOSE 8090
ENTRYPOINT ["java","-jar","/prestabanco-backend.jar"]