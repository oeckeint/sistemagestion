<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:forEach var="archivoErroneo" items="${archivosErroneos}" varStatus="id">
    <div class="alert alert-danger" role="alert" id="mensajeError${id.count}">
        <div class="container">
            <button type="button" class="btn btn-danger btn-sm" onclick="eliminarNotificacion(${id.count})"> x </button>
            ${archivoErroneo}
        </div>
    </div>
</c:forEach>

<script>
    function eliminarNotificacion(id) {
        document.getElementById("mensajeError" + id).style.display = "none";
    }
</script>