/*
 * Copyright 2018 The OpenTracing Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
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
