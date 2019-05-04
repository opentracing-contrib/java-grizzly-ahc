package io.opentracing.contrib.grizzly.ahc;

import static org.junit.Assert.*;

import java.util.List;

import org.glassfish.grizzly.http.server.HttpHandler;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.Request;

import com.ning.http.client.Response;

import io.opentracing.mock.MockSpan;
import io.opentracing.mock.MockTracer;

/**
 * @author Jose Montoya
 */
public abstract class AbstractAhcTest {
	protected HttpServer httpServer;

	protected void doTest(Response response, MockTracer tracer) throws Exception {
		assertEquals(response.getStatusCode(), 201);

		List<MockSpan> spans = tracer.finishedSpans();
		assertEquals(2, spans.size());

		assertEquals(spans.get(0).context().traceId(), spans.get(1).context().traceId());
		assertEquals(spans.get(0).parentId(), spans.get(1).context().spanId());
	}

	protected void setupServer(HttpServer httpServer) {
		httpServer.getServerConfiguration().addHttpHandler(
				new HttpHandler() {
					@Override
					public void service(Request request, org.glassfish.grizzly.http.server.Response response) throws Exception {
						response.setStatus(201);
					}
				},
				"/root");
	}
}
