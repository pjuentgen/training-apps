#!/bin/bash

# Download OpenTelemetry Java agent if it doesn't exist
AGENT_JAR="otel/opentelemetry-javaagent.jar"
if [ ! -f "$AGENT_JAR" ]; then
    echo "Downloading OpenTelemetry Java agent..."
    mkdir -p otel
    curl -L "https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/latest/download/opentelemetry-javaagent.jar" \
        -o "$AGENT_JAR"
fi


# Run the application with OpenTelemetry Java agent
java -javaagent:$AGENT_JAR \
    -Dotel.service.name=spring-boot-demo \
    -Dotel.service.version=1.0.0 \
    -Dotel.resource.attributes=service.name=spring-boot-demo,service.version=1.0.0,deployment.environment=development \
    -Dotel.exporter.otlp.endpoint="${OTEL_EXPORTER_OTLP_ENDPOINT}" \
    -Dotel.exporter.otlp.headers="Authorization=Bearer ${DASH0_AUTHORIZATION_TOKEN}" \
    -Dotel.logs.exporter=otlp \
    -Dotel.metrics.exporter=otlp \
    -Dotel.traces.exporter=otlp \
    -Dotel.propagators=tracecontext,baggage \
    -Dotel.instrumentation.logback-appender.experimental-log-attributes=true \
    -Dotel.instrumentation.logback-appender.experimental.capture-code-attributes=true \
    -Dotel.instrumentation.logback-appender.experimental.capture-marker-attribute=true \
    -Dotel.instrumentation.logback-appender.experimental.capture-key-value-pair-attributes=true \
    -Dotel.instrumentation.logback-appender.experimental.capture-logger-context=true \
    -Dotel.instrumentation.logback-appender.experimental.capture-mdc-attributes=* \
    -jar target/spring-boot-demo-0.0.1-SNAPSHOT.jar