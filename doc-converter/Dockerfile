# Document Converter Dockerfile
# Based on JODConverter runtime image but customized for our application

ARG JAVA_VERSION=17
ARG TARGET_BASE_IMAGE=debian:bookworm

############ BUILD STAGE ############
FROM eclipse-temurin:${JAVA_VERSION}-jdk-bookworm as builder

# Set working directory
WORKDIR /app

# Copy Maven wrapper and pom.xml first for better Docker layer caching
COPY mvnw ./
COPY .mvn .mvn/
COPY pom.xml ./

# Download dependencies
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application
RUN ./mvnw clean package -DskipTests -B

############ RUNTIME STAGE ############
FROM $TARGET_BASE_IMAGE as runtime

ARG JAVA_VERSION

# Set environment variables
ENV JAVA_HOME=/opt/java/openjdk
ENV PATH="${JAVA_HOME}/bin:${PATH}"
ENV LANG=C.UTF-8
ENV LC_ALL=C.UTF-8

# Create application user
ENV APP_USER=docconverter
ENV APP_GROUP=docconverter
ENV APP_HOME=/app

# Install JDK and LibreOffice
RUN echo 'deb http://deb.debian.org/debian bookworm-backports main' > /etc/apt/sources.list.d/backports.list \
  && apt-get update && apt-get -y install \
  # Basic system packages
  apt-transport-https locales-all curl wget \
  # Graphics and font libraries for LibreOffice
  libpng16-16 libxinerama1 libgl1-mesa-glx libfontconfig1 libfreetype6 libxrender1 \
  libxcb-shm0 libxcb-render0 adduser cpio findutils gosu \
  # Process management utilities
  procps \
  # Install LibreOffice from backports (24.x version)
  && apt-get -y install -t bookworm-backports \
  libreoffice libreoffice-base libreoffice-common libreoffice-base-core \
  libreoffice-writer libreoffice-calc libreoffice-impress libreoffice-draw \
  # Install JDK 17
  && apt-get -y install openjdk-17-jdk \
  # Create application user and group
  && groupadd $APP_GROUP \
  && useradd -m -s /bin/bash $APP_USER -g $APP_GROUP \
  # Clean up
  && apt-get autoclean -y && apt-get clean -y \
  && rm -rf /var/lib/apt/lists/*

# Set JAVA_HOME for OpenJDK
ENV JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64

# Create font cache for application user
USER $APP_USER
RUN fc-cache -fr
USER root

# Create application directory
RUN mkdir -p $APP_HOME && chown -R $APP_USER:$APP_GROUP $APP_HOME

# Copy the built JAR file from builder stage
COPY --from=builder /app/target/*.jar $APP_HOME/app.jar
COPY --from=builder /app/target/classes $APP_HOME/classes

# Set proper permissions
RUN chown -R $APP_USER:$APP_GROUP $APP_HOME

# Create directories for temporary files and conversions
RUN mkdir -p /tmp/doc-converter && chown -R $APP_USER:$APP_GROUP /tmp/doc-converter

# Switch to application user
USER $APP_USER

# Set working directory
WORKDIR $APP_HOME

# Expose application port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/api/convert/health || exit 1

# Start the application
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-Dspring.profiles.active=docker", "-jar", "app.jar"]