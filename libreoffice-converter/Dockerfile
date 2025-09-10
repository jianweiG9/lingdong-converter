# Use OpenJDK 17 as base image
FROM openjdk:17-jdk-slim

# Set maintainer information
LABEL maintainer="LibreOffice Converter Team"
LABEL description="LibreOffice-based document conversion service"

# Install system dependencies
RUN apt-get update && \
    apt-get install -y \
    libreoffice \
    fonts-liberation \
    fonts-dejavu-core \
    fonts-noto-core \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

# Create application user
RUN groupadd -r appuser && useradd -r -g appuser appuser

# Set working directory
WORKDIR /app

# Copy the application JAR file
COPY target/libreoffice-converter-1.0.0.jar app.jar

# Create directories for temporary files
RUN mkdir -p /tmp/conversion && \
    chown -R appuser:appuser /tmp/conversion && \
    chown -R appuser:appuser /app

# Switch to non-root user
USER appuser

# Expose the application port
EXPOSE 8080

# Set environment variables
ENV JAVA_OPTS="-Xmx512m -Xms256m"
ENV CONVERTER_OFFICE_HOME="/usr/lib/libreoffice"
ENV CONVERTER_KILL_EXISTING_PROCESS="true"

# Health check (commented out - requires curl)
# HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
#     CMD curl -f http://localhost:8080/api/v1/conversion/health || exit 1

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]