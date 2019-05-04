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
package io.opentracing.contrib.specialagent.grizzly.ahc;

import io.opentracing.contrib.grizzly.ahc.AbstractAhcTest;
import io.opentracing.contrib.specialagent.AgentRunner;
import org.glassfish.grizzly.http.server.HttpServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.Response;
import com.ning.http.client.SimpleAsyncHttpClient;

import io.opentracing.Scope;
import io.opentracing.mock.MockTracer;

/**
 * @author Jose Montoya
 */
@RunWith(AgentRunner.class)
public class Ahc2Test extends AbstractAhcTest {

	@Before
	public void before(MockTracer tracer) throws Exception {
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
	public void basicAhcTest(MockTracer tracer) throws Throwable {
		Response response;

		try (AsyncHttpClient client = new AsyncHttpClient();
			 Scope scope = tracer.buildSpan("parent").startActive(true)) {
			response = client.prepareGet("http://localhost:8080/root").execute().get();
		}

		doTest(response, tracer);
	}

	@Test
	public void basicSimpleAhcTest(MockTracer tracer) throws Throwable {
		Response response;

		try (SimpleAsyncHttpClient client = new SimpleAsyncHttpClient.Builder()
				.setUrl("http://localhost:8080/root")
				.build();
			 Scope scope = tracer.buildSpan("parent").startActive(true)) {
			response = client.get().get();

		}

		doTest(response, tracer);
	}
}
