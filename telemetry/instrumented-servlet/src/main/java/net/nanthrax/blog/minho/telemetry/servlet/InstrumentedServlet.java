package net.nanthrax.blog.minho.telemetry.servlet;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import io.opentelemetry.context.propagation.TextMapGetter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.nanthrax.blog.minho.telemetry.service.AutoInstrumentedService;
import org.apache.karaf.minho.boot.Minho;
import org.apache.karaf.minho.boot.spi.Service;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Collections;
import java.util.Properties;

public class InstrumentedServlet extends HttpServlet implements Service {

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

        OpenTelemetry openTelemetry = Minho.getInstance().getServiceRegistry().get(OpenTelemetryLoader.class).getOpenTelemetry();
        Tracer tracer = openTelemetry.getTracer(InstrumentedServlet.class.getName());
        AutoInstrumentedService innerService = Minho.getInstance().getServiceRegistry().get(AutoInstrumentedService.class);

        Context context = openTelemetry.getPropagators().getTextMapPropagator().extract(Context.current(), request, new TextMapGetter<HttpServletRequest>() {
            @Override
            public Iterable<String> keys(HttpServletRequest carrier) {
                return Collections.list(carrier.getHeaderNames());
            }

            @Override
            public String get(HttpServletRequest carrier, String key) {
                return carrier.getHeader(key);
            }
        });

        Span span = tracer.spanBuilder("InstrumentedServlet.GET").setParent(context).setSpanKind(SpanKind.SERVER).startSpan();

        try (Scope scope = span.makeCurrent()) {
            span.setAttribute("service", "instrumented-servlet");
            span.setAttribute("http.method", "GET");
            span.setAttribute("http.scheme", "http");
            span.setAttribute("http.host", "localhost:8080");
            span.setAttribute("http.target", "/observed");

            span.addEvent("Call inner service");

            String message = innerService.processing("Hello World");

            span.addEvent("Inner service responded", Attributes.of(AttributeKey.stringKey("message"), message));

            span.addEvent("Create HTTP response");

            response.setStatus(200);
            try (Writer writer = new OutputStreamWriter(response.getOutputStream())) {
                writer.write(message);
            }

            span.addEvent("HTTP response sent", Attributes.of(AttributeKey.stringKey("message"), message));

        } finally {
            span.end();
        }
    }

    @Override
    public Properties properties() {
        Properties properties = new Properties();
        properties.put("contextPath", "/observed");
        return properties;
    }

}
