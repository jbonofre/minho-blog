package net.nanthrax.blog.minho.rest;

import net.nanthrax.blog.minho.jpa.BlogJpaService;
import org.apache.karaf.minho.boot.Minho;
import org.apache.karaf.minho.boot.service.ConfigService;
import org.apache.karaf.minho.boot.service.LifeCycleService;
import org.apache.karaf.minho.jpa.openjpa.OpenJPAService;
import org.apache.karaf.minho.rest.jersey.JerseyRestService;
import org.apache.karaf.minho.web.jetty.JettyWebContainerService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class BlogApiTest {

    @Test
    public void simpleTest() throws Exception {
        String blog = "{ \"title\": \"Foo\", \"content\": \"Bar\" }";

        URL url = new URL("http://localhost:8080/blog");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoInput(true);
        connection.setDoOutput(true);

        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()))) {
            writer.write(blog);
            writer.flush();
        }

        Assertions.assertEquals("OK", connection.getResponseMessage());
        Assertions.assertEquals(200, connection.getResponseCode());

        connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setDoOutput(true);

        String line;
        StringBuilder builder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
        }

        Assertions.assertEquals("[{\"title\":\"Foo\",\"content\":\"Bar\"}]", builder.toString());
    }

    @BeforeEach
    protected void setup() throws Exception {
        ConfigService configService = new ConfigService();
        Map<String, String> properties = new HashMap<>();
        properties.put("rest.packages", "net.nanthrax.blog.minho.rest");
        properties.put("rest.path", "/blog/*");
        configService.setProperties(properties);

        Minho minho = Minho.builder().loader(() -> Stream.of(
                configService,
                new LifeCycleService(),
                new OpenJPAService(),
                new BlogJpaService(),
                new JettyWebContainerService(),
                new JerseyRestService()
        )).build().start();
    }

    @AfterEach
    protected void teardown() throws Exception {
        Minho.getInstance().close();
    }

}
