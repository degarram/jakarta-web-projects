package es.daw.jakartahelloworld;

import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Proxy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HelloServletTest {

    @Test
    void servletIsMappedToHelloServlet() {
        assertEquals("/hello-servlet",
                HelloServlet.class.getAnnotation(jakarta.servlet.annotation.WebServlet.class).value()[0]);
    }

    @Test
    void initSetsGreetingAndGetRendersHtmlResponse() throws Exception {
        HelloServlet servlet = new HelloServlet();
        servlet.init();

        StringWriter body = new StringWriter();
        String[] contentType = new String[1];
        HttpServletResponse response = responseProxy(body, contentType);

        servlet.doGet(null, response);

        assertEquals("text/html", contentType[0]);
        assertEquals(
                "<html><body>" + System.lineSeparator()
                        + "<h1>Hello World!</h1>" + System.lineSeparator()
                        + "</body></html>" + System.lineSeparator(),
                body.toString());
    }

    private static HttpServletResponse responseProxy(StringWriter body, String[] contentType) {
        PrintWriter writer = new PrintWriter(body);
        return (HttpServletResponse) Proxy.newProxyInstance(
                HelloServletTest.class.getClassLoader(),
                new Class<?>[]{HttpServletResponse.class},
                (proxy, method, args) -> {
                    if ("setContentType".equals(method.getName())) {
                        contentType[0] = (String) args[0];
                        return null;
                    }
                    if ("getWriter".equals(method.getName())) {
                        return writer;
                    }
                    if ("toString".equals(method.getName())) {
                        return "HttpServletResponse test double";
                    }
                    if ("hashCode".equals(method.getName())) {
                        return System.identityHashCode(proxy);
                    }
                    if ("equals".equals(method.getName())) {
                        return proxy == args[0];
                    }
                    if (method.getReturnType() == boolean.class) {
                        return false;
                    }
                    if (method.getReturnType() == int.class) {
                        return 0;
                    }
                    if (method.getReturnType() == long.class) {
                        return 0L;
                    }
                    return null;
                });
    }
}
