NOTIFICATIONS JSP ---
<br>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:forEach var="n" items="${notifications}">
    <div class="alert alert-${n.type.name().toLowerCase()} alert-dismissible fade show">
        ${n.message}
        <button type="button" class="close" data-dismiss="alert">&times;</button>
    </div>
</c:forEach>

<br>
NOTIFICATIONS JSP --- |