<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!--<script type="text/javascript" src="${pageContext.request.contextPath}/js/jsxml.js"></script>-->
<div class="alert alert-warning alert-dismissible fade show" role="alert">
    <div class="container">
        <strong>${mensajeRegistro}</strong>
    </div>
</div>

<form:form method="POST" action="${pageContext.request.contextPath}/${controller}/procesar" enctype="multipart/form-data">
    <div class="container my-5">
        <div class="input-group mb-3">
            <div class="input-group-prepend">
                <input class="btn btn-outline-secondary" type="submit" value="${etiquetaBoton}"/>
            </div>
            <div class="custom-file">
                <input type="file" name="archivosxml" accept=".xml" multiple="true" class="custom-file-input" id="inputGroupFile03" aria-describedby="inputGroupFileAddon03" required>
                <label class="custom-file-label" for="inputGroupFile03">Seleccione sus archivos</label>
            </div>
        </div>
    </div>
</form:form>



<!-- Optional JavaScript -->
<!-- jQuery first, then Popper.js, then Bootstrap JS -->
<script src="https://code.jquery.com/jquery-3.3.1.slim.min.js" integrity="sha384-q8i/X+965DzO0rT7abK41JStQIAqVgRVzpbzo5smXKp4YfRvH+8abtTE1Pi6jizo" crossorigin="anonymous"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.7/umd/popper.min.js" integrity="sha384-UO2eT0CpHqdSJQ6hJty5KVphtPhzWj9WO1clHTMGa3JDZwrnQq4sF86dIHNDz0W1" crossorigin="anonymous"></script>
<script src="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/js/bootstrap.min.js" integrity="sha384-JjSmVgyd0p3pXB1rRibZUAYoIIy6OrQ6VrjIEaFf/nJGzIxFDsf4x0xIM+B07jRM" crossorigin="anonymous"></script>

<script>
    $("#btnSubmit").click(function () {
        $(this).prop("disabled", true); //deshabilitamos el botón
        $(this).html(
                `<span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span>  ${etiquetaBoton}` //añadimos el spinner
                );
        document.getElementById("myForm").submit();
    });
</script>