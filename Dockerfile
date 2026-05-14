FROM exclipse-temurin:21-jre
WORKDIR /app
COPY target/moneymanagerr.jar moneymanager-v1.0.jar
LABEL authors="chandra"
EXPOSE 9090
ENTRYPOINT ["java", "-jar","moneymanager-v1.0.jar`"]