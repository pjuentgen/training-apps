# MDC Attributes Reference

This document lists all the MDC (Mapped Diagnostic Context) attributes that are automatically added to your logs and exported via OTLP to your observability platform.

## Request Context Attributes

| Attribute | Description | Example Value |
|-----------|-------------|---------------|
| `request.id` | Unique identifier for each request | `550e8400-e29b-41d4-a716-446655440000` |
| `request.method` | HTTP method used | `GET`, `POST`, `PUT`, `DELETE` |
| `request.uri` | Request URI/endpoint | `/api/normal` |
| `request.query_string` | Query parameters | `param1=value1&param2=value2` |
| `request.remote_addr` | Client IP address | `192.168.1.100` |
| `request.user_agent` | Client user agent | `Mozilla/5.0...` |
| `request.content_type` | Request content type | `application/json` |

## API Operation Attributes

| Attribute | Description | Example Value |
|-----------|-------------|---------------|
| `api.endpoint` | API endpoint path | `/api/normal` |
| `api.type` | Type of API operation | `NORMAL`, `ERROR`, `SLOW` |
| `api.operation` | Specific operation name | `normal_call`, `error_call` |
| `api.version` | API version | `v1` |

## Service Context Attributes

| Attribute | Description | Example Value |
|-----------|-------------|---------------|
| `service.name` | Service name | `spring-boot-demo` |
| `service.version` | Service version | `1.0.0` |
| `service.environment` | Deployment environment | `development`, `production` |
| `service.component` | Service component | `rest-controller`, `api-call-service` |
| `service.layer` | Application layer | `presentation`, `business` |
| `service.method` | Specific method being executed | `saveApiCall`, `getAllApiCalls` |

## Operation Timing Attributes

| Attribute | Description | Example Value |
|-----------|-------------|---------------|
| `operation.start_time` | ISO timestamp when operation started | `2025-10-31T19:30:15.123Z` |
| `operation.start_time_ms` | Epoch milliseconds when started | `1698781815123` |
| `operation.duration_ms` | Total execution time | `245` |
| `operation.end_time` | ISO timestamp when operation completed | `2025-10-31T19:30:15.368Z` |
| `operation.status` | Operation result status | `success`, `error`, `expected_error` |
| `operation.simulated_delay_ms` | Simulated delay for testing | `150` |

## Response Attributes

| Attribute | Description | Example Value |
|-----------|-------------|---------------|
| `response.status` | Response status | `success`, `error`, `simulated_error` |
| `response.status_code` | HTTP status code | `200`, `500`, `404` |
| `response.size_bytes` | Response size in bytes | `1024` |

## Performance Attributes

| Attribute | Description | Example Value |
|-----------|-------------|---------------|
| `performance.category` | Performance classification | `fast`, `normal`, `slow`, `very_slow` |
| `sla.compliant` | Whether operation meets SLA | `true`, `false` |
| `sla.threshold_ms` | SLA threshold in milliseconds | `2000` |

## Error Attributes

| Attribute | Description | Example Value |
|-----------|-------------|---------------|
| `error.type` | Exception class name | `InterruptedException`, `RuntimeException` |
| `error.message` | Error message | `Operation was interrupted` |
| `error.category` | Error category | `business_logic`, `interruption`, `unexpected` |
| `error.severity` | Error severity level | `low`, `medium`, `high` |
| `error.stack_trace` | First stack trace element | `com.demo.controller.DemoController.normalCall(DemoController.java:42)` |
| `error.occurred` | Whether an error occurred | `true`, `false` |
| `error.simulated` | Whether error was simulated | `true`, `false` |

## Business Context Attributes

| Attribute | Description | Example Value |
|-----------|-------------|---------------|
| `business.domain` | Business domain | `demo-api` |
| `business.transaction_type` | Type of business transaction | `api_call` |
| `business.operation_type` | Business operation type | `data_retrieval`, `error_simulation` |
| `business.complexity` | Operation complexity | `low`, `medium`, `high` |
| `business.expected_duration_ms` | Expected operation duration | `200` |
| `business.expected_outcome` | Expected result | `success`, `error` |
| `business.test_scenario` | Test scenario type | `failure_path`, `success_path` |

## Database Operation Attributes

| Attribute | Description | Example Value |
|-----------|-------------|---------------|
| `db.operation` | Database operation type | `insert`, `select`, `count` |
| `db.operation.type` | Specific operation | `create`, `query_all`, `query_by_type` |
| `db.table` | Database table | `api_calls` |
| `db.system` | Database system | `h2` |
| `db.connection_string` | Connection string | `jdbc:h2:mem:testdb` |
| `db.user` | Database user | `sa` |
| `db.query.type` | Query method | `findAll`, `findByCallType`, `countByCallType` |
| `db.query.result_count` | Number of results | `25` |
| `db.query.estimated_size_bytes` | Estimated result size | `5000` |
| `db.query.filter.call_type` | Filter applied | `NORMAL`, `ERROR` |
| `db.record.id` | Database record ID | `123` |
| `db.record.created_at` | Record creation timestamp | `2025-10-31T19:30:15.123` |
| `db.record.size_estimate` | Estimated record size | `256` |
| `db.operation.duration_ms` | Database operation time | `45` |
| `db.operation.status` | Database operation status | `success`, `error` |
| `db.error.type` | Database error type | `SQLException` |
| `db.error.message` | Database error message | `Connection timeout` |

## Infrastructure Attributes

| Attribute | Description | Example Value |
|-----------|-------------|---------------|
| `host.name` | Host/user name | `demo-server` |
| `process.pid` | Process ID | `12345` |
| `thread.name` | Thread name | `http-nio-8080-exec-1` |

## Simulation Attributes

| Attribute | Description | Example Value |
|-----------|-------------|---------------|
| `simulation.type` | Type of simulation | `slow_operation`, `fast_operation` |
| `simulation.component` | Simulated component | `database`, `cache` |
| `simulation.delay_ms` | Simulated delay | `3500` |
| `simulation.delay_category` | Delay category | `fast`, `slow` |
| `simulation.expected_range` | Expected delay range | `10-50ms`, `2000-5000ms` |
| `simulation.status` | Simulation status | `completed`, `interrupted` |
| `simulation.error` | Simulation error | `Thread interrupted` |

## Response Generation Attributes

| Attribute | Description | Example Value |
|-----------|-------------|---------------|
| `response.generation.type` | Generation type | `random` |
| `response.generation.component` | Generator component | `response-generator` |
| `response.generation.selected_index` | Selected response index | `2` |
| `response.generation.total_options` | Total available options | `5` |
| `response.generation.response_length` | Generated response length | `34` |
| `response.generation.status` | Generation status | `success` |

## Usage in Observability

All these attributes are automatically:

1. **Included in JSON logs** via Logstash encoder
2. **Exported to OTLP** via OpenTelemetry auto-instrumentation
3. **Correlated with traces and spans** for complete observability
4. **Searchable and filterable** in your observability platform

## Example Log Entry

```json
{
  "@timestamp": "2025-10-31T19:30:15.123Z",
  "level": "INFO",
  "message": "Normal API call completed successfully in 245ms",
  "logger": "com.demo.controller.DemoController",
  "thread": "http-nio-8080-exec-1",
  "request.id": "550e8400-e29b-41d4-a716-446655440000",
  "api.endpoint": "/api/normal",
  "api.type": "NORMAL",
  "operation.duration_ms": "245",
  "response.status": "success",
  "performance.category": "normal",
  "sla.compliant": "true",
  "service.name": "spring-boot-demo",
  "service.environment": "development"
}
```

These comprehensive attributes provide deep visibility into your application's behavior, performance, and any issues that occur.