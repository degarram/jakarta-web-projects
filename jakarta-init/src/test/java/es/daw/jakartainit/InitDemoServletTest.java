package es.daw.jakartainit;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.Proxy;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InitDemoServletTest {

    @Test
    void servletIsMappedToInitDemoPath() {
        var annotation = InitDemoServlet.class.getAnnotation(
                jakarta.servlet.annotation.WebServlet.class);

        assertNotNull(annotation);
        assertEquals("/init-demo", annotation.value()[0]);
    }

    @Test
    void initCreatesAnIso8601InitializationTimestamp() throws ServletException, IOException {
        var servlet = new InitDemoServlet();
        var attributes = new HashMap<String, Object>();
        var forwardedPath = new AtomicReference<String>();

        servlet.init();
        invokeGet(servlet, attributes, forwardedPath);

        var initializationTime = assertInstanceOf(
                String.class, attributes.get("initializationTime"));
        assertTrue(initializationTime.contains("T"));
        assertDoesNotThrow(() -> LocalDateTime.parse(initializationTime));
        assertEquals("/result.jsp", forwardedPath.get());
    }

    @Test
    void getExposesExpectedAttributesAndForwardsToResultPage()
            throws ServletException, IOException {
        var servlet = new InitDemoServlet();
        var attributes = new HashMap<String, Object>();
        var forwardedPath = new AtomicReference<String>();

        servlet.init();
        invokeGet(servlet, attributes, forwardedPath);

        assertInstanceOf(String.class, attributes.get("initializationTime"));
        assertEquals(1, attributes.get("requestCount"));
        assertEquals(servlet.hashCode(), attributes.get("instanceId"));
        assertEquals(1, attributes.get("atomicRequestCount"));
        assertEquals("/result.jsp", forwardedPath.get());
    }

    @Test
    void repeatedRequestsReuseInitializationDataAndIncrementCounters()
            throws ServletException, IOException {
        var servlet = new InitDemoServlet();
        var firstAttributes = new HashMap<String, Object>();
        var secondAttributes = new HashMap<String, Object>();
        var firstForwardedPath = new AtomicReference<String>();
        var secondForwardedPath = new AtomicReference<String>();

        servlet.init();
        invokeGet(servlet, firstAttributes, firstForwardedPath);
        invokeGet(servlet, secondAttributes, secondForwardedPath);

        assertEquals(firstAttributes.get("initializationTime"),
                secondAttributes.get("initializationTime"));
        assertEquals(firstAttributes.get("instanceId"),
                secondAttributes.get("instanceId"));
        assertEquals(1, firstAttributes.get("requestCount"));
        assertEquals(2, secondAttributes.get("requestCount"));
        assertEquals(1, firstAttributes.get("atomicRequestCount"));
        assertEquals(2, secondAttributes.get("atomicRequestCount"));
        assertEquals("/result.jsp", firstForwardedPath.get());
        assertEquals("/result.jsp", secondForwardedPath.get());
    }

    @Test
    void atomicCounterProducesUniqueValuesForConcurrentRequests()
            throws Exception {
        var servlet = new InitDemoServlet();
        servlet.init();
        int requestTotal = 100;

        try (ExecutorService executor = Executors.newFixedThreadPool(8)) {
            var futures = IntStream.range(0, requestTotal)
                    .mapToObj(index -> executor.submit(() -> {
                        var attributes = new ConcurrentHashMap<String, Object>();
                        invokeGet(servlet, attributes, new AtomicReference<>());
                        return attributes;
                    }))
                    .toList();

            var atomicValues = futures.stream()
                    .map(future -> {
                        try {
                            return future.get();
                        } catch (Exception exception) {
                            throw new AssertionError("Concurrent request failed", exception);
                        }
                    })
                    .map(attributes -> (Integer) attributes.get("atomicRequestCount"))
                    .sorted()
                    .toList();

            assertEquals(IntStream.rangeClosed(1, requestTotal).boxed().toList(),
                    atomicValues);
        }
    }

    private static void invokeGet(
            InitDemoServlet servlet,
            Map<String, Object> attributes,
            AtomicReference<String> forwardedPath)
            throws ServletException, IOException {
        var request = proxy(HttpServletRequest.class, (proxy, method, args) -> {
            if (method.getName().equals("setAttribute")) {
                attributes.put((String) args[0], args[1]);
                return null;
            }
            if (method.getName().equals("getRequestDispatcher")) {
                forwardedPath.set((String) args[0]);
                return proxy(RequestDispatcher.class, (dispatcher, dispatchMethod, dispatchArgs) -> null);
            }
            return defaultValue(method.getReturnType());
        });
        var response = proxy(HttpServletResponse.class,
                (proxy, method, args) -> defaultValue(method.getReturnType()));

        servlet.doGet(request, response);
    }

    @SuppressWarnings("unchecked")
    private static <T> T proxy(Class<T> type, Invocation invocation) {
        return (T) Proxy.newProxyInstance(
                type.getClassLoader(),
                new Class<?>[]{type},
                (proxy, method, args) -> invocation.invoke(proxy, method, args));
    }

    private static Object defaultValue(Class<?> returnType) {
        if (!returnType.isPrimitive()) {
            return null;
        }
        if (returnType == boolean.class) {
            return false;
        }
        if (returnType == char.class) {
            return '\0';
        }
        if (returnType == byte.class || returnType == short.class
                || returnType == int.class || returnType == long.class
                || returnType == float.class || returnType == double.class) {
            return 0;
        }
        return null;
    }

    @FunctionalInterface
    private interface Invocation {
        Object invoke(Object proxy, java.lang.reflect.Method method, Object[] args)
                throws Throwable;
    }
}
