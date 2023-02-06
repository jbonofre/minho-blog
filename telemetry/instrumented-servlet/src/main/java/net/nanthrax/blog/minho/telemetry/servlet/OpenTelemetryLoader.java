package net.nanthrax.blog.minho.telemetry.servlet;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.trace.propagation.W3CTraceContextPropagator;
import io.opentelemetry.context.propagation.ContextPropagators;
import io.opentelemetry.exporter.otlp.http.trace.OtlpHttpSpanExporter;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.SimpleSpanProcessor;
import org.apache.karaf.minho.boot.service.ServiceRegistry;
import org.apache.karaf.minho.boot.spi.Service;

public class OpenTelemetryLoader implements Service {

    private OpenTelemetry openTelemetry;

    @Override
    public void onRegister(ServiceRegistry serviceRegistry) {
        SdkTracerProvider sdkTracerProvider =
                SdkTracerProvider.builder()
                        .setResource(Resource.create(Attributes.of(AttributeKey.stringKey("service.name"), "minho-blog")))
                        .addSpanProcessor(SimpleSpanProcessor.create(OtlpHttpSpanExporter.getDefault()))
                        .build();

        OpenTelemetrySdk sdk =
                OpenTelemetrySdk.builder()
                        .setTracerProvider(sdkTracerProvider)
                        .setPropagators(ContextPropagators.create(W3CTraceContextPropagator.getInstance()))
                        .build();

        Runtime.getRuntime().addShutdownHook(new Thread(sdkTracerProvider::close));

        openTelemetry = sdk;
    }

    public OpenTelemetry getOpenTelemetry() {
        return openTelemetry;
    }

}
