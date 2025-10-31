```
mkdir -p ./otelif [ ! -f "./otel/opentelemetry-javaagent.jar" ]; then                                              
    echo "Downloading OpenTelemetry Java agent..."
    curl -L -o ./otel/opentelemetry-javaagent.jar \
        "https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/latest/download/opentelemetry-javaagent.jar"
fi
```

```
docker run -p 8080:8080 \
    -v "$(pwd)/otel/opentelemetry-javaagent.jar:/opt/opentelemetry/opentelemetry-javaagent.jar:ro" \
    -e JAVA_TOOL_OPTIONS="-javaagent:/opt/opentelemetry/opentelemetry-javaagent.jar" \
    -e OTEL_SERVICE_NAME="spring-boot-demo" \
    -e OTEL_SERVICE_VERSION="1.0.0" \
    -e OTEL_ENVIRONMENT="development" \
    -e OTEL_EXPORTER_OTLP_ENDPOINT="${OTEL_EXPORTER_OTLP_ENDPOINT=}" \
    -e OTEL_EXPORTER_OTLP_HEADERS="Authorization=Bearer ${DASH0_AUTHORIZATION_TOKEN}" \
    -e OTEL_EXPORTER_OTLP_PROTOCOL="grpc" \
    -e OTEL_INSTRUMENTATION_HTTP_ENABLED="true" \
    -e OTEL_INSTRUMENTATION_JDBC_ENABLED="true" \
    -e OTEL_INSTRUMENTATION_JPA_ENABLED="true" \
    -e OTEL_INSTRUMENTATION_SPRING_WEB_ENABLED="true" \
    -e OTEL_INSTRUMENTATION_SPRING_WEBMVC_ENABLED="true" \
    -e OTEL_RESOURCE_ATTRIBUTES="service.name=spring-boot-demo,service.version=1.0.0,deployment.environment=development" \
    -e OTEL_TRACES_SAMPLER="traceidratio" \
    -e OTEL_TRACES_SAMPLER_ARG="1.0" \
    -e OTEL_METRICS_EXPORTER="otlp" \
    -e OTEL_LOGS_EXPORTER="otlp" \
    -e OTEL_EXPORTER_OTLP_PROTOCOL=http/protobuf \
    spring-boot-demo:latest
```