package net.nanthrax.blog.minho.telemetry;

import net.nanthrax.blog.minho.telemetry.service.AutoInstrumentedService;
import net.nanthrax.blog.minho.telemetry.servlet.OpenTelemetryLoader;
import net.nanthrax.blog.minho.telemetry.servlet.InstrumentedServlet;
import org.apache.karaf.minho.boot.Minho;
import org.apache.karaf.minho.boot.service.ConfigService;
import org.apache.karaf.minho.boot.service.LifeCycleService;
import org.apache.karaf.minho.web.jetty.JettyWebContainerService;
import org.junit.jupiter.api.Test;

import java.util.stream.Stream;

public class    SimpleTest {

    @Test
    public void simple() throws Exception {
        // to use autoconfiguration (adding our service with @WithSpan and Jetty for instance), we need to have
        // the opentelemetry agent
        // -javaagent:/path/to/.m2/repository/io/opentelemetry/javaagent/opentelemetry-javaagent/1.22.1/opentelemetry-javaagent-1.22.1.jar -Dotel.service.name=minho-blog -Dotel.traces.exporter=otlp
        Minho minho = Minho.builder()
                .loader(() -> Stream.of(
                        new ConfigService(),
                        new LifeCycleService(),
                        new OpenTelemetryLoader(),
                        new AutoInstrumentedService(),
                        new InstrumentedServlet(),
                        new JettyWebContainerService()
                ))
                .build().start();

        while (true) {
            Thread.sleep(200);
        }

    }

}
