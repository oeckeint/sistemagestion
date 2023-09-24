<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@taglib prefix="security" uri="http://www.springframework.org/security/tags" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="cli" scope="request" value="cliente"/>
<c:set var="nom" scope="request" value="nombre"/>
<c:set var="cups" scope="request" value="cups"/>
<c:set var="sel" scope="request" value="selected"/>
<fmt:message var="busqueda" key="customers.search"/>
<c:set var="existendatos" value="${param.existendatos}"></c:set>

<security:authorize access="hasRole('ADMIN')">
    <c:choose>
        <c:when test="${existendatos}">
            <c:set var="addbuttonsize" value="col-3 col-md-3"></c:set>
        </c:when>
        <c:otherwise>
            <c:set var="addbuttonsize" value="col-7 col-md-5"></c:set>
        </c:otherwise>
    </c:choose>
    <div class="${addbuttonsize}">
        <a href="${pageContext.request.contextPath}/clientes/formulario" class="btn btn-primary btn-block"><i class="fas fa-plus"></i> <fmt:message key="customers.add"/></a>
    </div>
</security:authorize>
<c:if test="${existendatos}">
    <div class="col-8 col-md-9">
        <form:form action="${pageContext.request.contextPath}/clientes/busqueda" modelAttribute="busquedaCliente" method="post" id="myForm">
            <div class="input-group">
                <button class="btn btn-primary btn-block" type="button" id="btnSubmit" onclick="buscarCliente();">
                    <i class="fas fa-search" id="searchIcon"></i>
                </button>
                <form:input path="valor" cssClass="form-control" id="searchValue" placeholder="${busqueda}"/>
                <div class="input-group-prepend">
                    <form:select path="filtro" cssClass="btn-primary form-select text-left" id="filterDropdown">
                        <form:options cssClass="bg-white text-dark" items="${busquedaCliente.filtros}"></form:options>
                    </form:select>
                </div>
                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
            </div>
            <form:errors path="valor" cssClass="error"/>
        </form:form>
    </div>
</c:if>

<script>
    let searchValue = document.querySelector("#searchValue");
    let button = document.querySelector("#btnSubmit");
    button.disabled = true;
    searchValue.addEventListener("input", stateHandle);

    document.addEventListener("DOMContentLoaded", function () {
        if (searchValue.value.trim() !== "") {
            button.disabled = false;
        }
    });

    function stateHandle() {
        if (document.querySelector("#searchValue").value.trim() === "") {
            button.disabled = true;
        } else {
            button.disabled = false;
        }
    }

    function buscarCliente() {
        let span = document.createElement("span");
        span.className = "spinner-border spinner-border-sm";
        button.disabled = true;
        button.removeChild(document.getElementById("searchIcon"));
        button.appendChild(span);
        document.getElementById("myForm").submit();
    }
</script>