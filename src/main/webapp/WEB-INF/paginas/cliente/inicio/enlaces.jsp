<%@taglib prefix="security" uri="http://www.springframework.org/security/tags" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
        <div class="container">
            <div class="row my-5">
                <div class="col-6 col-lg-3 d-grid gap-2 my-2">
                    <a class="btn btn-success" href="${pageContext.request.contextPath}/peajes"><fmt:message key="header.tolls"/></a>
                </div>
                <div class="col-6 col-lg-3 d-grid gap-3 my-2">
                    <a class="btn btn-success" href="${pageContext.request.contextPath}/facturas"><fmt:message key="header.bills"/></a>
                </div>
                <div class="col-6 col-lg-3 d-grid gap-3 my-2">
                    <a class="btn btn-success" href="${pageContext.request.contextPath}/otrasfacturas"><fmt:message key="header.otherbills"/></a>
                </div>
                <div class="col-6 col-lg-3 d-grid gap-3 my-2">
                    <a class="btn btn-danger" href="${pageContext.request.contextPath}/clientes"><fmt:message key="header.customers"/></a>
                </div>
                <security:authorize access="hasRole('ADMIN')">
                    <div class="col-6 col-lg-3 d-grid gap-3 my-2">
                        <a class="btn btn-warning" href="${pageContext.request.contextPath}/clasificar"><fmt:message key="header.sort"/></a>
                    </div>
                    <div class="col-6 col-lg-3 d-grid gap-3 my-2">
                        <a class="btn btn-warning" href="${pageContext.request.contextPath}/procesar"><fmt:message key="header.process"/></a>
                    </div>
                    <div class="col-6 col-lg-3 d-grid gap-3 my-2">
                        <a class="btn btn-secondary" href="${pageContext.request.contextPath}/configuraciones"><fmt:message key="header.config"/></a>
                    </div>
                    <div class="col-6 col-lg-3 d-grid gap-3 my-2">
                        <a class="btn btn-primary" href="${pageContext.request.contextPath}/ftp"><fmt:message key="header.ftp"/></a>
                    </div>
                </security:authorize>
            </div>
        </div>

        
        