<%@taglib prefix="security" uri="http://www.springframework.org/security/tags" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<security:authorize access="hasRole('ADMIN')">
    <div class="col-2">
        <a href="${pageContext.request.contextPath}/clasificar" class="btn btn-primary btn-block">Clasificar</a>
    </div>
    <div class="col-2">
        <a href="${pageContext.request.contextPath}/procesar" class="btn btn-primary btn-block">Procesar</a>
    </div>
</security:authorize>
<c:choose>
    <c:when test="${botonVisible == 'no' }">

    </c:when>
    <c:otherwise>
        <div class="col-7">
            <form:form action="${pageContext.request.contextPath}/${controller}/busqueda" method="post" id="myForm">
                <div class="input-group">
                    <button class="btn btn-primary" type="button" id="btnSubmit" onclick="loadDetails();">
                        <i class="fas fa-search" id="searchIcon"></i>
                    </button>
                    <input type="text"  name="valor" class="form-control" id="searchValue" placeholder="Buscar" value="${ultimaBusqueda}" required>
                    <div class="input-group-prepend">
                        <select class="btn-primary form-select text-left" name="filtro">
                            <option class="bg-white text-dark" value="cliente" ${filtro eq 'cliente' ? 'selected' : ''}>Cliente</option>
                            <option class="bg-white text-dark" value="remesa" ${filtro eq 'remesa' ? 'selected' : ''}>Remesa</option>
                            <option class="bg-white text-dark" value="codFisFac" ${filtro eq 'codFisFac' ? 'selected' : ''}>CodFiscal</option>
                        </select>
                    </div>
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                </div>
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