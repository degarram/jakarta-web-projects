package es.daw.jakartahelloworld;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * Servlet that returns a small HTML page containing a greeting.
 *
 * <p>The servlet is available at the {@code /hello-servlet} URL and is
 * initialized by the servlet container before it handles requests.</p>
 */
@WebServlet("/hello-servlet")
public class HelloServlet extends HttpServlet {
    /**
     * Greeting displayed in the generated HTML response.
     */
    private String message;

    /**
     * Initializes the greeting used by this servlet.
     *
     * <p>This lifecycle method is called by the servlet container once before
     * the servlet begins processing requests.</p>
     */
    @Override
    public void init() {
        message = "Hello World!";
    }

    /**
     * Handles a GET request by returning the configured greeting as HTML.
     *
     * @param request  HTTP request received from the client
     * @param response HTTP response to be populated with the greeting page
     * @throws IOException if the response writer cannot be created or written
     */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");

        PrintWriter out = response.getWriter();
        out.println("<html><body>");
        out.println("<h1>" + message + "</h1>");
        out.println("</body></html>");
    }
}