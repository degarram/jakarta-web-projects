package es.daw.jakartainit;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.HTML;
import javax.swing.text.html.parser.ParserDelegator;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WebResourcesTest {

    @Test
    void landingPageUsesEnglishTextAndLinksToTheServlet() throws IOException {
        var page = read("index.html");
        var document = parse(page);

        assertTrue(document.htmlLanguage.equals("en"));
        assertTrue(document.title.equals("Servlet Initialization Demo"));
        assertTrue(document.heading
                .contains("Want to verify that init() runs only once?"));
        assertTrue(document.linkText
                .equals("Open the initialization demo"));
        assertTrue(document.linkHref.equals("init-demo"));
        assertFalse(containsSpanishCharacters(page));
    }

    @Test
    void resultPageDisplaysEveryServletAttribute() throws IOException {
        var page = read("result.jsp");
        var document = parse(page);

        assertTrue(document.title.equals("Servlet Initialization Demo"));
        assertTrue(page.contains("${initializationTime}"));
        assertTrue(page.contains("${requestCount}"));
        assertTrue(page.contains("${atomicRequestCount}"));
        assertTrue(page.contains("${instanceId}"));
        assertTrue(page.contains("Reload this page (F5) several times:"));
        assertFalse(containsSpanishCharacters(page));
    }

    @Test
    void deploymentDescriptorUsesJakartaServletVersion61() throws IOException {
        var descriptor = read("WEB-INF/web.xml");

        assertTrue(descriptor.contains("https://jakarta.ee/xml/ns/jakartaee"));
        assertTrue(descriptor.contains("web-app_6_1.xsd"));
        assertTrue(descriptor.contains("version=\"6.1\""));
    }

    private static String read(String relativePath) throws IOException {
        try (InputStream stream = WebResourcesTest.class.getClassLoader()
                .getResourceAsStream(relativePath)) {
            assertTrue(stream != null, "Missing web resource: " + relativePath);
            return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private static boolean containsSpanishCharacters(String text) {
        return text.matches("(?s).*[\u00e1\u00e9\u00ed\u00f3\u00fa\u00f1\u00c1\u00c9\u00cd\u00d3\u00da\u00d1].*");
    }

    private static ParsedHtml parse(String html) throws IOException {
        var parsed = new ParsedHtml();
        new ParserDelegator().parse(new StringReader(html), parsed.callback, true);
        return parsed;
    }

    private static final class ParsedHtml {
        private String htmlLanguage;
        private String title;
        private String heading;
        private String linkHref;
        private String linkText;
        private final HTMLEditorKit.ParserCallback callback = new HTMLEditorKit.ParserCallback() {
            private HTML.Tag activeTag;
            private final StringBuilder text = new StringBuilder();

            @Override
            public void handleStartTag(HTML.Tag tag, javax.swing.text.MutableAttributeSet attributes,
                                       int position) {
                activeTag = tag;
                if (tag == HTML.Tag.HTML) {
                    htmlLanguage = (String) attributes.getAttribute(HTML.Attribute.LANG);
                }
                if (tag == HTML.Tag.A) {
                    linkHref = (String) attributes.getAttribute(HTML.Attribute.HREF);
                }
            }

            @Override
            public void handleText(char[] data, int position) {
                if (activeTag == HTML.Tag.TITLE || activeTag == HTML.Tag.H1
                        || activeTag == HTML.Tag.A) {
                    text.append(data);
                }
            }

            @Override
            public void handleEndTag(HTML.Tag tag, int position) {
                if (tag == HTML.Tag.TITLE) {
                    title = text.toString().trim();
                    text.setLength(0);
                } else if (tag == HTML.Tag.H1) {
                    heading = text.toString().trim();
                    text.setLength(0);
                } else if (tag == HTML.Tag.A) {
                    linkText = text.toString().trim();
                    text.setLength(0);
                }
                activeTag = null;
            }
        };
    }
}
