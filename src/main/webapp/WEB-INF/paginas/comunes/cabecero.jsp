<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@taglib prefix="security" uri="http://www.springframework.org/security/tags" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<header id="main-header" class="py-2 bg-info text-white">
    <div class="container">
        <nav class="navbar navbar-expand-lg navbar-dark">
            <div class="container-fluid">
                <h3 id="pageHeader">
                    <c:choose>
                        <c:when test="${not empty param.tituloDinamico}">${param.tituloDinamico}</c:when>
                        <c:otherwise>${titulo}</c:otherwise>
                    </c:choose>
                </h3>
                <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNavDarkDropdown" aria-controls="navbarNavDarkDropdown" aria-expanded="false" aria-label="Toggle navigation">
                    <span class="navbar-toggler-icon"></span>
                </button>
                <div class="row justify-content-end">
                    <div class="collapse navbar-collapse" id="navbarNavDarkDropdown">
                        <ul class="navbar-nav">
                            <li class="nav-item dropdown">
                                <a class="nav-link dropdown-toggle text-white" href="#" id="navbarDarkDropdownMenuLink" role="button" data-bs-toggle="dropdown" aria-expanded="false"><fmt:message key="header.customers"/></a>
                                <ul class="dropdown-menu dropdown-menu-dark" aria-labelledby="navbarDarkDropdownMenuLink">
                                    <li><a class="dropdown-item" href="${pageContext.request.contextPath}/clientes"><fmt:message key="customers.list"/></a></li>
                                    <security:authorize access="hasRole('ADMIN')">
                                        <li><a class="dropdown-item" href="${pageContext.request.contextPath}/clientes/formulario"><fmt:message key="customers.add"/></a></li>
                                        <li><a class="dropdown-item" href="${pageContext.request.contextPath}/clientes/tickets"><fmt:message key="customers.tickets"/></a></li>
                                        <li><a class="dropdown-item" href="${pageContext.request.contextPath}/clientes/reclamaciones"><fmt:message key="customers.reclamaciones.tittle"/></a></li>
                                    </security:authorize>
                                    <!--<li><a class="dropdown-item" href="#">B�squeda Avanzada</a></li>-->
                                </ul>
                            </li>
                            <li class="nav-item dropdown">
                                <a class="nav-link dropdown-toggle text-white" href="#" id="navbarDarkDropdownMenuLink" role="button" data-bs-toggle="dropdown" aria-expanded="false"><fmt:message key="header.xml"/></a>
                                <ul class="dropdown-menu dropdown-menu-dark" aria-labelledby="navbarDarkDropdownMenuLink">
                                    <li><a class="dropdown-item" href="${pageContext.request.contextPath}/peajes"><fmt:message key="header.tolls"/></a></li>
                                    <li><a class="dropdown-item" href="${pageContext.request.contextPath}/facturas"><fmt:message key="header.bills"/></a></li>
                                    <li><a class="dropdown-item" href="${pageContext.request.contextPath}/otrasfacturas"><fmt:message key="header.otherbills"/></a></li>
                                    <security:authorize access="hasRole('ADMIN')">
                                        <li><a class="dropdown-item" href="${pageContext.request.contextPath}/clasificar"><fmt:message key="header.sort"/></a></li>
                                        <li><a class="dropdown-item" href="${pageContext.request.contextPath}/procesar"><fmt:message key="header.process"/></a></li>
                                    </security:authorize>
                                </ul>
                            </li>
                            <li class="nav-item dropdown">
                                <a class="nav-link dropdown-toggle text-white" href="#" id="navbarDarkDropdownMenuLink" role="button" data-bs-toggle="dropdown" aria-expanded="false"><i class="fas fa-user"></i> <security:authentication property="principal.username" /></a>
                                <ul class="dropdown-menu dropdown-menu-dark" aria-labelledby="navbarDarkDropdownMenuLink">
                                    <form:form method="POST" action="${pageContext.request.contextPath}/logout">
                                        <input type="submit" value="<fmt:message key='login.logout'/>" class="dropdown-item"/>
                                    </form:form>
                                </ul>
                            </li>
                        </ul>
                        <a class="nav-link text-white p-0" href="${pageContext.request.contextPath}/"><i class="fas fa-home"></i></a>
                    </div>
                </div>
            </div>
        </nav>
    </div>
</header>
