package io.opentracing.contrib.grizzly.ahc;

import com.ning.http.client.*;
import com.ning.http.client.filter.FilterContext;
import com.ning.http.client.filter.FilterException;
import com.ning.http.client.filter.RequestFilter;
import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.propagation.Format;
import io.opentracing.tag.Tags;
import io.opentracing.util.GlobalTracer;

/**
 * @author Jose Montoya
 */
public class TracingRequestFilter implements RequestFilter {
	private final Tracer tracer;

	public TracingRequestFilter(Tracer tracer) {
		this.tracer = tracer;
	}

	public TracingRequestFilter() {
		this.tracer = GlobalTracer.get();
	}

	@Override
	@SuppressWarnings({"rawtypes", "unchecked"})
	public <T> FilterContext<T> filter(FilterContext<T> ctx) throws FilterException {
		Request request = ctx.getRequest();

		Span span = tracer.buildSpan("HTTP::" + request.getMethod())
				.withTag(Tags.SPAN_KIND.getKey(), Tags.SPAN_KIND_CLIENT)
				.start();

		Tags.COMPONENT.set(span, "java-grizzly-ahc");
		Tags.HTTP_METHOD.set(span, request.getMethod());
		Tags.HTTP_URL.set(span, request.getUrl());

		tracer.inject(span.context(), Format.Builtin.HTTP_HEADERS, new RequestBuilderInjectAdapter(new RequestBuilder(request)));

		return new FilterContext.FilterContextBuilder(ctx)
				.asyncHandler(new AsyncHandlerWrapper(ctx.getAsyncHandler(), span))
				.build();
	}

	private class AsyncHandlerWrapper<T> implements AsyncHandler<T> {
		private final AsyncHandler<T> asyncHandler;
		private final Span span;

		public AsyncHandlerWrapper(AsyncHandler<T> asyncHandler, Span span) {
			this.asyncHandler = asyncHandler;
			this.span = span;
		}

		@Override
		public STATE onStatusReceived(HttpResponseStatus httpResponseStatus) throws Exception {
			Tags.HTTP_STATUS.set(span, httpResponseStatus.getStatusCode());
			return asyncHandler.onStatusReceived(httpResponseStatus);
		}

		@Override
		public T onCompleted() throws Exception {
			span.finish();
			return asyncHandler.onCompleted();
		}

		@Override
		public void onThrowable(Throwable t) {
			asyncHandler.onThrowable(t);
		}

		@Override
		public STATE onBodyPartReceived(HttpResponseBodyPart bodyPart) throws Exception {
			return asyncHandler.onBodyPartReceived(bodyPart);
		}

		@Override
		public STATE onHeadersReceived(HttpResponseHeaders headers) throws Exception {
			return asyncHandler.onHeadersReceived(headers);
		}
	}
}
