<%@ page import="java.util.LinkedHashMap" %>
<%@taglib prefix="security" uri="http://www.springframework.org/security/tags" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%
    request.setCharacterEncoding("UTF-8");
    response.setContentType("text/html;charset=UTF-8");
%>
<security:authorize access="hasRole('ADMIN')">
    <div class="col-3">
        <a href="${pageContext.request.contextPath}/procesar" class="btn btn-primary btn-block"><i class="fas fa-plus"></i> Agregar</a>
    </div>
</security:authorize>
<c:choose>
    <c:when test="${botonVisible == 'no' }">

    </c:when>
    <c:otherwise>
        <div class="col-7">
            <form:form action="${pageContext.request.contextPath}/clientes/reclamaciones/busqueda" modelAttribute="busqueda" method="post" id="myForm">
                <div class="input-group">
                    <button class="btn btn-primary" type="button" id="btnSubmit" onclick="loadDetails();">
                        <i class="fas fa-search" id="searchIcon"></i>
                    </button>
                    <form:input path="valorActual" cssClass="form-control" id="searchValue" placeholder="Buscar"/>
                    <div class="input-group-prepend">
                        <form:select path="filtroActual" cssClass="btn-primary form-select text-left" id="filterDropdown">
                            <form:options cssClass="bg-white text-dark" items="${busqueda.filtros}"></form:options>
                        </form:select>
                    </div>
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                    <form:hidden path="filtros" />
                </div>
                <form:errors path="valorActual" cssClass="error"/>
                <form:errors path="filtroActual" cssClass="error"/>
                <form:errors path="filtros" cssClass="error"/>
            </form:form>
        </div>
    </c:otherwise>
</c:choose>

<script>
    let searchValue = document.querySelector("#searchValue");
    let button = document.querySelector("#btnSubmit");
    button.disabled = true;
    searchValue.addEventListener("input", stateHandle);

    document.addEventListener("DOMContentLoaded", function () {
        if (searchValue.value !== "") {
            button.disabled = false;
        }
    });

    function stateHandle() {
        if (document.querySelector("#searchValue").value === "") {
            button.disabled = true;
        } else {
            button.disabled = false;
        }
    }

    function loadDetails() {
        let span = document.createElement("span");
        span.className = "spinner-border spinner-border-sm";
        button.disabled = true;
        button.removeChild(document.getElementById("searchIcon"));
        button.appendChild(span);
        document.getElementById("myForm").submit();
    }
</script>