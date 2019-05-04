package io.opentracing.contib.grizzly.ahc;

import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClientConfig;
import com.ning.http.client.Response;
import io.opentracing.Scope;
import io.opentracing.contrib.grizzly.ahc.TracingRequestFilter;
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
public class Ahc1Test extends AbstractAhcTest {
	protected static final MockTracer tracer = new MockTracer(new ThreadLocalScopeManager());

	@BeforeClass
	public static void beforeClass() throws Exception {
		GlobalTracer.register(tracer);
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
	public void basicAhcTest() throws Throwable {
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
