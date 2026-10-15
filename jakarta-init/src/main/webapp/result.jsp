<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head><title>Servlet Initialization Demo</title></head>
<body>
    <h1>How many times is init() called?</h1>

    <p><b>Servlet initialization time:</b> ${initializationTime}</p>
    <p><b>Request count:</b> ${requestCount}</p>
    <p><b>Atomic request count:</b> ${atomicRequestCount}</p>
    <p><b>Instance identifier (hashCode):</b> ${instanceId}</p>

    <hr>
    <p>Reload this page (F5) several times:</p>
    <ul>
        <li>The <b>initialization time</b> does not change.</li>
        <li>The <b>instance identifier</b> does not change either.</li>
        <li>The <b>request count</b> increases after each reload.</li>
    </ul>
</body>
</html>
