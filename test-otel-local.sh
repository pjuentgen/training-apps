#!/bin/bash

# Download OpenTelemetry Java agent if it doesn't exist
AGENT_JAR="otel/opentelemetry-javaagent.jar"
if [ ! -f "$AGENT_JAR" ]; then
    echo "Downloading OpenTelemetry Java agent..."
    mkdir -p otel
    curl -L "https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/latest/download/opentelemetry-javaagent.jar" \
        -o "$AGENT_JAR"
fi

# Test with local console output instead of OTLP endpoint
java -javaagent:$AGENT_JAR \
    -Dotel.service.name=spring-boot-demo \
    -Dotel.service.version=1.0.0 \
    -Dotel.resource.attributes=service.name=spring-boot-demo,service.version=1.0.0,deployment.environment=development \
    -Dotel.logs.exporter=logging \
    -Dotel.metrics.exporter=logging \
    -Dotel.traces.exporter=logging \
    -Dotel.propagators=tracecontext,baggage \
    -Dotel.instrumentation.logback-appender.experimental-log-attributes=true \
    -Dotel.instrumentation.logback-appender.experimental.capture-code-attributes=true \
    -Dotel.instrumentation.logback-appender.experimental.capture-marker-attribute=true \
    -Dotel.instrumentation.logback-appender.experimental.capture-key-value-pair-attributes=true \
    -Dotel.instrumentation.logback-appender.experimental.capture-logger-context=true \
    -Dotel.instrumentation.logback-appender.experimental.capture-mdc-attributes=* \
    -jar target/spring-boot-demo-0.0.1-SNAPSHOT.jar