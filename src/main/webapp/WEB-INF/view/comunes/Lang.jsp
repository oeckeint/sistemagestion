<%@page import="java.util.logging.Level"%>
<%@page import="java.util.logging.Logger"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%
    Logger logger = Logger.getLogger(getClass().getName());
    String language = request.getParameter("language");
    if (language == null) {
            logger.log(Level.INFO, ">>> Buscando lenguage en session scope");
            language = session.getAttribute("language").toString();
        }
    if (language == null) {
        logger.log(Level.INFO, ">>> Definiendo lenguage por default \"ES\"");
        language = "es";
    } else if (language != null) {
        if (!language.equals("en") && !language.equals("es")) {
            logger.log(Level.INFO, ">>> Definiendo lenguage por default \"ES\"");
            language = "es";
        }
    }
    session.setAttribute("language", language);

%>
<c:set var="language" value="${param.language}" scope="session"/>
<fmt:setLocale value="${language}" scope="session"/>
<fmt:setBundle basename="labels" scope="session"/>