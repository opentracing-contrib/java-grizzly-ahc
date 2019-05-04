package io.opentracing.contrib.grizzly.ahc;

import java.util.Iterator;
import java.util.Map;

import com.ning.http.client.RequestBuilderBase;

import io.opentracing.propagation.TextMap;

/**
 * @author Jose Montoya
 */
public class RequestBuilderInjectAdapter implements TextMap {
	private final RequestBuilderBase<?> builder;

	public RequestBuilderInjectAdapter(RequestBuilderBase<?> builder) {
		this.builder = builder;
	}

	@Override
	public Iterator<Map.Entry<String, String>> iterator() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void put(String key, String value) {
		builder.addHeader(key, value);
	}
}