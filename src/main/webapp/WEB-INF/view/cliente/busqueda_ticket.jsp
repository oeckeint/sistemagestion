<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@taglib prefix="security" uri="http://www.springframework.org/security/tags" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<security:authorize access="hasRole('ADMIN')">
    <div class="col-3">
        <a href="${pageContext.request.contextPath}/clientes/tickets/agregar" class="btn btn-primary btn-block"><i class="fas fa-plus"></i> Agregar</a>
    </div>
</security:authorize>
<div class="col-7">
    <form:form action="${pageContext.request.contextPath}/clientes/tickets/busqueda" modelAttribute="busquedaTicket" method="post" id="myForm">
        <div class="input-group">
            <button class="btn btn-primary btn-block" type="button" id="btnSubmit" onclick="buscarTicket();">
                <i class="fas fa-search" id="searchIcon"></i>
            </button>
            <form:input path="valor" cssClass="form-control" id="searchValue" placeholder="${busqueda}"/>
            <div class="input-group-prepend">
                <form:select path="filtro" cssClass="btn-primary form-select text-left" id="filterDropdown">
                    <form:options cssClass="bg-white text-dark" items="${busquedaTicket.filtros}"></form:options>
                </form:select>
            </div>
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
        </div>
        <form:errors path="valor" cssClass="error"/>
    </form:form>
</div>

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

function buscarTicket() {
    let span = document.createElement("span");
    span.className = "spinner-border spinner-border-sm";
    button.disabled = true;
    button.removeChild(document.getElementById("searchIcon"));
    button.appendChild(span);
    document.getElementById("myForm").submit();
}
</script>