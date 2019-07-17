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

import com.ning.http.client.RequestBuilder;
import io.opentracing.Scope;
import io.opentracing.mock.MockTracer;
import io.opentracing.propagation.Format;
import io.opentracing.util.ThreadLocalScopeManager;
import org.junit.Test;

import static org.junit.Assert.assertFalse;

public class RequestBuilderInjectAdapterTest {
    protected static final MockTracer tracer = new MockTracer(new ThreadLocalScopeManager());

    @Test
    public void injectedRequest() {
        RequestBuilderInjectAdapter injectAdapter = new RequestBuilderInjectAdapter(new RequestBuilder().setUrl("http://foo"));

        try (Scope scope = tracer.buildSpan("foo").startActive(true)) {
            tracer.inject(tracer.activeSpan().context(), Format.Builtin.HTTP_HEADERS, injectAdapter);
        }

        assertFalse(injectAdapter.injectedRequest().getHeaders().isEmpty());
    }
}
