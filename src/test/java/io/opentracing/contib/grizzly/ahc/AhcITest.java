package io.opentracing.contib.grizzly.ahc;

import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.Response;
import com.ning.http.client.SimpleAsyncHttpClient;
import io.opentracing.Scope;
import io.opentracing.contrib.specialagent.AgentRunner;
import io.opentracing.mock.MockSpan;
import io.opentracing.mock.MockTracer;
import io.opentracing.util.GlobalTracer;
import org.glassfish.grizzly.http.server.HttpHandler;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.Request;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * @author Jose Montoya
 */
@RunWith(AgentRunner.class)
@AgentRunner.Config(debug = true, verbose = true)
public class AhcITest extends AbstractAhcTest {

	@BeforeClass
	public static void beforeClass(MockTracer tracer) throws Exception {
		GlobalTracer.register(tracer);
	}

	@Before
	public void before(MockTracer tracer) throws Exception {
		// clear traces
		tracer.reset();

		httpServer = HttpServer.createSimpleServer();
		httpServer.start();
		setupServer(httpServer);
	}

	@After
	public void after(MockTracer tracer) throws Exception {
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
