<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@taglib prefix="security" uri="http://www.springframework.org/security/tags" %>
<security:authorize access="hasRole('ADMIN')">
    <div class="col-3">
        <a href="${pageContext.request.contextPath}/clientes/formulario" class="btn btn-primary btn-block"><i class="fas fa-plus"></i> Agregar</a>
    </div>
</security:authorize>
<div class="col-5">
    <form:form action="${pageContext.request.contextPath}/clientes/busqueda" method="post" id="myForm">
        <div class="input-group mb-2">
            <div class="input-group-prepend">
                <button class="btn btn-primary" type="button" id="btnSubmit" onclick="buscarCliente();">
                    <i class="fas fa-search" id="searchIcon"></i>
                </button>
            </div>
            <input type="text"  name="valor" class="form-control" id="searchValue" placeholder="Buscar id cliente" value="${ultimaBusqueda}" required>
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
        </div>
    </form:form>
</div>

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

    function buscarCliente() {
        let span = document.createElement("span");
        span.className = "spinner-border spinner-border-sm";
        button.disabled = true;
        button.removeChild(document.getElementById("searchIcon"));
        button.appendChild(span);
        document.getElementById("myForm").submit();
    }
</script>