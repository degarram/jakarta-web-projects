package es.daw.jakartahelloworld;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertTrue;

class WebResourcesTest {

    @Test
    void homePageContainsWelcomeHeadingAndServletLink() throws IOException {
        String page = readResource("/index.jsp");

        assertTrue(page.contains("contentType=\"text/html; charset=UTF-8\""));
        assertTrue(page.contains("<h1><%= \"Hello World!\" %></h1>"));
        assertTrue(page.contains("<a href=\"hello-servlet\">Hello Servlet</a>"));
    }

    @Test
    void deploymentDescriptorDeclaresJakartaWebApplication() throws IOException {
        String descriptor = readResource("/WEB-INF/web.xml");

        assertTrue(descriptor.contains("xmlns=\"https://jakarta.ee/xml/ns/jakartaee\""));
        assertTrue(descriptor.contains("web-app_6_1.xsd"));
        assertTrue(descriptor.contains("version=\"6.1\""));
    }

    private static String readResource(String path) throws IOException {
        Path file = Paths.get("src", "main", "webapp", path.substring(1));
        assertTrue(Files.exists(file), "Missing web resource: " + file);
        return new String(Files.readAllBytes(file), StandardCharsets.UTF_8);
    }
}
