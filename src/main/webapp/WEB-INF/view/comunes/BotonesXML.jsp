<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<div class="col-3">
    <a href="${pageContext.request.contextPath}/clasificar" class="btn btn-primary btn-block"><i class="fas fa-plus"></i> Clasificar</a>
</div>
<div class="col-3">
    <a href="${pageContext.request.contextPath}/procesar" class="btn btn-primary btn-block"><i class="fas fa-plus"></i> Procesar</a>
</div>
<c:choose>
    <c:when test="${botonVisible == 'no' }">

    </c:when>
    <c:otherwise>
        <div class="col-5">
            <form:form action="${pageContext.request.contextPath}/${controller}/busqueda" method="post" id="myForm">
                <div class="input-group mb-2">
                    <div class="input-group-prepend">
                        <button class="btn btn-primary" type="button" id="btnSubmit" onclick="loadDetails();">
                            <i class="fas fa-search" id="searchIcon"></i>
                        </button>
                    </div>
                    <input type="text"  name="valor" class="form-control" id="searchValue" placeholder="Buscar" value="${ultimaBusqueda}" required>
                    <select class="form-select fa" name="filtro">
                        <option class="fa" value="cliente" ${filtro eq 'cliente' ? 'selected' : ''}>&#xf007;</option>
                        <option class="fa" value="remesa" ${filtro eq 'remesa' ? 'selected' : ''}>&#xf621;</option>
                        <option class="fa" value="codFisFac" ${filtro eq 'codFisFac' ? 'selected' : ''}>&#xf15c;</option>
                    </select>
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
        if(searchValue.value !== ""){
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