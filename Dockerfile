# Amazon Corretto 8 — misma distribución que el .sdkmanrc (8.0.472-amzn)
# "corretto" en la imagen de Tomcat ES Amazon Corretto, mismo JDK distinto empaquetado
FROM tomcat:9.0-jdk8-corretto

# Eliminar las webapps por defecto de Tomcat
RUN rm -rf /usr/local/tomcat/webapps/*

# Crear la estructura de directorios de trabajo que usa la aplicación
# Equivalente a /resources/Peajes/* en el servidor WebLogic
# La ruta completa se construye como: System.getProperty("user.dir") + path.yml
# En Tomcat: user.dir = /usr/local/tomcat
RUN mkdir -p \
    /usr/local/tomcat/resources/Peajes/Procesados/RemesaPago \
    /usr/local/tomcat/resources/Peajes/Procesados/ArchivarFactura \
    /usr/local/tomcat/resources/Peajes/ftp \
    /usr/local/tomcat/resources/Peajes/Backups/restauraciones \
    /usr/local/tomcat/resources/scripts

# Copiar el WAR con nombre sistemagestion.war → context path /sistemagestion
COPY target/SistemaGestion.war /usr/local/tomcat/webapps/sistemagestion.war

EXPOSE 8080

