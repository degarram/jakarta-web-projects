package es.daw.jakartainit;

import org.apache.catalina.startup.Tomcat;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EmbeddedTomcatIT {

    @Test
    void deployedApplicationServesInitializationDemo() throws Exception {
        var baseDirectory = Files.createTempDirectory("jakarta-init-tomcat");
        var webappDirectory = createWebappDirectory(baseDirectory);
        var tomcat = new Tomcat();
        tomcat.setBaseDir(baseDirectory.toString());
        tomcat.setPort(0);
        tomcat.addWebapp("", webappDirectory.toString());

        try {
            tomcat.start();
            var port = tomcat.getConnector().getLocalPort();
            var response = HttpClient.newHttpClient().send(
                    HttpRequest.newBuilder(URI.create(
                            "http://localhost:" + port + "/init-demo")).GET().build(),
                    HttpResponse.BodyHandlers.ofString());

            assertEquals(200, response.statusCode());
            assertTrue(response.body().contains("Servlet initialization time:"));
            assertTrue(response.body().contains("Atomic request count:"));
        } finally {
            tomcat.stop();
            tomcat.destroy();
        }
    }

    private static Path createWebappDirectory(Path baseDirectory) throws Exception {
        var webappDirectory = baseDirectory.resolve("webapp");
        copyDirectory(Path.of("src", "main", "webapp"), webappDirectory);
        copyDirectory(Path.of("target", "classes"),
                webappDirectory.resolve("WEB-INF").resolve("classes"));
        return webappDirectory;
    }

    private static void copyDirectory(Path source, Path target) throws Exception {
        try (Stream<Path> paths = Files.walk(source, FileVisitOption.FOLLOW_LINKS)) {
            for (Path path : paths.toList()) {
                var destination = target.resolve(source.relativize(path));
                if (Files.isDirectory(path)) {
                    Files.createDirectories(destination);
                } else {
                    Files.copy(path, destination);
                }
            }
        }
    }
}
