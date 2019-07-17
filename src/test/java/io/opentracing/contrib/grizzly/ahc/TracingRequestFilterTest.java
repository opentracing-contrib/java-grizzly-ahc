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

import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClientConfig;
import com.ning.http.client.Response;
import io.opentracing.Scope;
import io.opentracing.mock.MockTracer;
import io.opentracing.util.GlobalTracer;
import io.opentracing.util.ThreadLocalScopeManager;
import org.glassfish.grizzly.http.server.HttpServer;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Jose Montoya
 */
public class TracingRequestFilterTest extends AbstractAhcTest {
	protected static final MockTracer tracer = new MockTracer(new ThreadLocalScopeManager());

	@BeforeClass
	public static void beforeClass() throws Exception {
		GlobalTracer.registerIfAbsent(tracer);
	}

	@Before
	public void before() throws Exception {
		// clear traces
		tracer.reset();

		httpServer = HttpServer.createSimpleServer();
		httpServer.start();
		setupServer(httpServer);
	}

	@After
	public void after() throws Exception {
		if (httpServer != null) {
			httpServer.shutdownNow();
		}
	}

	@Test
	public void testBasicRequest() throws Throwable {
		AsyncHttpClientConfig config = new AsyncHttpClientConfig.Builder()
				.addRequestFilter(new TracingRequestFilter())
				.build();

		Response response;

		try (AsyncHttpClient client = new AsyncHttpClient(config);
			 Scope scope = tracer.buildSpan("parent").startActive(true)) {
			response = client.prepareGet("http://localhost:8080/root").execute().get();
		}

		doTest(response, tracer);
	}
}
