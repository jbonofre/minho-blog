package net.nanthrax.blog.minho.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.karaf.minho.boot.spi.Service;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Properties;

public class HelloServlet extends HttpServlet implements Service {

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(response.getOutputStream()))) {
            writer.write("Hello world!");
        }
    }

    @Override
    public Properties properties() {
        Properties properties = new Properties();
        properties.put("contextPath", "/hello");
        return properties;
    }

}
