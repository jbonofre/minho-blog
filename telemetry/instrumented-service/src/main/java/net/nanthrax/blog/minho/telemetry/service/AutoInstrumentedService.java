package net.nanthrax.blog.minho.telemetry.service;

import io.opentelemetry.instrumentation.annotations.SpanAttribute;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import org.apache.karaf.minho.boot.spi.Service;

public class AutoInstrumentedService implements Service {

    @WithSpan
    public String processing(@SpanAttribute("message") String message) {
        return inner(message);
    }

    @WithSpan
    public String inner(@SpanAttribute("message") String message) {
        return message.toUpperCase();
    }

}
