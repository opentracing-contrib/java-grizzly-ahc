# OpenTracing Grizzly Async HTTP Client Instrumentation
OpenTracing instrumentation for Grizzly Async HTTP Client.

## OpenTracing Agents
When using a runtime agent like [java-agent](https://github.com/opentracing-contrib/java-agent) or [java-specialagent](https://github.com/opentracing-contrib/java-specialagent) `AsyncHttpClient`s will be automatically instrumented by injecting a `TracingRequestFilter` into its `AsyncHttpClientConfig`. This is the case with the plain `AsyncHttpClient` or `SimpleAsyncHttpClient`:

```java
AsyncHttpClient client = new AsyncHttpClient();
Response response = client.prepareGet("http://localhost:8080/root").execute().get();
```
or
```java
SimpleAsyncHttpClient client = new SimpleAsyncHttpClient.Builder()
 .setUrl("http://localhost:8080/root")
 .build();

Response respose = client.get().get();
```
Refer to the agents' documentation for how to include this library as an instrumentation plugin.

## Non-Agent Configuration
When not using any of the OpenTracing Agents the `TracingRequestFiler` must be added directly to the `AsyncHttpClientConfig`.

```java
AsyncHttpClientConfig config = new AsyncHttpClientConfig.Builder()
        .addRequestFilter(new TracingRequestFilter())
        .build();

AsyncHttpClient client = new AsyncHttpClient(config);
```