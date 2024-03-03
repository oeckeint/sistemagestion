<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@taglib prefix="security" uri="http://www.springframework.org/security/tags" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="cli" scope="request" value="cliente"/>
<c:set var="nom" scope="request" value="nombre"/>
<c:set var="cups" scope="request" value="cups"/>
<c:set var="sel" scope="request" value="selected"/>
<fmt:message var="busqueda" key="customers.search"/>
<c:set var="existendatos" value="${param.existendatos}"/>

<c:if test="${existendatos}"></c:if>
    <div class="col-8 col-md-9">
        <form:form action="${pageContext.request.contextPath}/medidas/validar" modelAttribute="medidaValidatorForm" method="post" id="myForm">
            <div class="input-group">
                <button class="btn btn-primary btn-block" type="button" id="btnSubmit" onclick="buscar();">
                    <i class="fas fa-search" id="searchIcon"></i>
                </button>
                <form:input path="valorSeleccionado" cssClass="form-control" id="searchValue" placeholder="${busqueda}"/>
                <div class="input-group-prepend">
                    <form:select path="filtro" cssClass="btn-primary form-select text-left" id="filterDropdown">
                        <form:options cssClass="bg-white text-dark" items="${medidaValidatorForm.filtros}"/>
                    </form:select>
                </div>
                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
            </div>
            <form:errors path="valorSeleccionado" cssClass="error"/>
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

    function buscar() {
        //let span = document.createElement("span");
        //span.className = "spinner-border spinner-border-sm";
        //button.disabled = true;
        //button.removeChild(document.getElementById("searchIcon"));
        //button.appendChild(span);
        document.getElementById("myForm").submit();
    }
</script>