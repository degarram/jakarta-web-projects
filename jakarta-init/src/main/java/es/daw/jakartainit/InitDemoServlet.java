package es.daw.jakartainit;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Demonstrates that a servlet instance is initialized once and then reused
 * for subsequent requests.
 *
 * <p>The page displays the initialization timestamp, the servlet instance
 * identity, and both a regular and an atomic request counter.</p>
 */
@WebServlet("/init-demo")
public class InitDemoServlet extends HttpServlet {

    private final AtomicInteger atomicRequestCount = new AtomicInteger(0);
    private String initializationTime;
    private int requestCount = 0;

    /**
     * Stores the timestamp for this servlet instance.
     */
    @Override
    public void init() {
        initializationTime = LocalDateTime.now().toString();
    }

    /**
     * Increments the request counters and forwards the current values to the
     * result page.
     *
     * @param request the incoming HTTP request
     * @param response the HTTP response
     * @throws ServletException if the result page cannot be forwarded
     * @throws IOException if an input/output error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        requestCount++;

        request.setAttribute("initializationTime", initializationTime);
        request.setAttribute("requestCount", requestCount);
        request.setAttribute("instanceId", this.hashCode());
        request.setAttribute("atomicRequestCount", atomicRequestCount.incrementAndGet());

        request.getRequestDispatcher("/result.jsp").forward(request, response);
    }
}