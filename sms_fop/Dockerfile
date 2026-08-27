# Global build arguments
# BASE_DOCKER_IMAGE_WITH_UTILS: Located in the Template Pipeline as GLOBAL_BASE_DOCKER_IMAGE_WITH_UTILS.
ARG BASE_DOCKER_IMAGE_WITH_UTILS
# BASE_DOCKER_IMAGE: Located in the Template Pipeline as GLOBAL_BASE_DOCKER_IMAGE.
ARG BASE_DOCKER_IMAGE

# =====================================================================
# STAGE: Config Cloner (Using the utilities image)
# =====================================================================
FROM ${BASE_DOCKER_IMAGE_WITH_UTILS} AS config-cloner

# Accept pipeline credentials and variables inside this stage
ARG DEPLOY_ENV
ARG CONFIG_REPO_URL
ARG RESOURCE_REPO_URL

WORKDIR /tmp

# Clone the target branch directly using the single URL argument
RUN git clone --branch ${DEPLOY_ENV} ${CONFIG_REPO_URL} /tmp/config-repo

# Clone the resource repository to access common resources (e.g., OTEL Collector Artifact, Certificates, etc.)
RUN git clone --branch main ${RESOURCE_REPO_URL} /tmp/resource-repo

# =====================================================================
# STAGE: Secure Runtime
# =====================================================================
FROM ${BASE_DOCKER_IMAGE}

# Accept pipeline variables inside this stage
ARG OTEL_PROTOCOL
ARG OTEL_ENDPOINT
ARG OTEL_SERVICE_NAMESPACE
ARG OTEL_SERVICE_NAME
ARG OTEL_ENVIRONMENT

WORKDIR /opt/app/

# Copy the compiled Java application jar from runner workspace
COPY build/libs/psql-inventory-integration-service-1.0.0.jar /opt/app/psql-inventory-integration-service.jar

# Copy the sftp config files directly from the temporary cloner stage
COPY --from=config-cloner /tmp/config-repo/sftp-configs/psql-inventory-integration-service-${OTEL_SERVICE_NAMESPACE}-id_rsa.pvt /opt/app/sftp/id_rsa
COPY --from=config-cloner /tmp/config-repo/sftp-configs/psql-inventory-integration-service-${OTEL_SERVICE_NAMESPACE}-known_hosts /opt/app/sftp/known_hosts
# Setup permissions for the sftp configs
USER root
RUN chown 10000:10000 /opt/app/sftp/id_rsa /opt/app/sftp/known_hosts \
    && chmod 400 /opt/app/sftp/id_rsa \
    && chmod 444 /opt/app/sftp/known_hosts
USER 10000

# Copy the OTEL Collector Artifact & Certificates
COPY --from=config-cloner /tmp/resource-repo/resources/opentelemetry-javaagent.jar /opt/app/opentelemetry-javaagent.jar
COPY --from=config-cloner /tmp/resource-repo/resources/*.crt /opt/app/certificates/

# Runtime env (helm-values may override any of these)
ENV CONFIG_DIR="/opt/app/config"

# Set OTEL environment variables
ENV JAVA_OTEL_OPTS="-javaagent:opentelemetry-javaagent.jar \
    -Dotel.logs.exporter=otlp \
    -Dotel.metrics.exporter=otlp \
    -Dotel.traces.exporter=otlp \
    -Dotel.exporter.otlp.protocol=${OTEL_PROTOCOL} \
    -Dotel.exporter.otlp.endpoint=${OTEL_ENDPOINT} \
    -Dotel.resource.attributes=service.namespace=${OTEL_SERVICE_NAMESPACE},service.name=${OTEL_SERVICE_NAME},environment=${OTEL_ENVIRONMENT} \
    -Dotel.exporter.otlp.certificate=/opt/app/certificates/Michelin_PKI_SubCA16_SHA256_chain.crt \
    -Dotel.javaagent.debug=false"

# Startup command to run
CMD ["sh", "-c", "java $JAVA_OTEL_OPTS \
-jar psql-inventory-integration-service.jar \
--spring.config.name=psql-inventory-integration-service \
--spring.config.additional-location=optional:file:config/"]