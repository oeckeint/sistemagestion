<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@taglib prefix="security" uri="http://www.springframework.org/security/tags" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <jsp:include page="/WEB-INF/paginas/comunes/contenidoHead.jsp"></jsp:include>
        </head>
        <body>
        <jsp:include page="/WEB-INF/paginas/comunes/cabecero.jsp"></jsp:include>

        <%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!--<script type="text/javascript" src="${pageContext.request.contextPath}/js/jsxml.js"></script>-->
        <div class="alert alert-warning alert-dismissible fade show" role="alert">
            <div class="container">
                <h5 class="m-0"><a href="${pageContext.request.contextPath}/"><i class="fas fa-arrow-circle-left text-success"></i></a> <strong>${mensajeRegistro}</strong></h5>
            </div>
        </div>

        <form:form method="POST" action="${pageContext.request.contextPath}/ftp/subir?${_csrf.parameterName}=${_csrf.token}" enctype="multipart/form-data" id="filesInput">
            <div class="container my-5">
                <div class="input-group mb-3">
                    <div class="input-group-prepend" id="espacioBoton">
                        <button class="btn btn-outline-secondary" type="button" id="btnSubmit" onclick="enviarArchivos();">
                        	${etiquetaBoton}
                        </button>                         
                    </div>
                    <div class="custom-file" id="archivosAEnviar">
                        <input type="file" name="archivos" accept=".pdf" multiple="true" class="form-control" aria-describedby="inputGroupFileAddon03" id="xmlFiles" required>
                    </div>
                </div>
                <div id="nombreArchivos"></div>
            </div>
        </form:form>



        <!-- Optional JavaScript -->
        <!-- jQuery first, then Popper.js, then Bootstrap JS -->
        <script src="https://code.jquery.com/jquery-3.3.1.slim.min.js" integrity="sha384-q8i/X+965DzO0rT7abK41JStQIAqVgRVzpbzo5smXKp4YfRvH+8abtTE1Pi6jizo" crossorigin="anonymous"></script>
        <script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.7/umd/popper.min.js" integrity="sha384-UO2eT0CpHqdSJQ6hJty5KVphtPhzWj9WO1clHTMGa3JDZwrnQq4sF86dIHNDz0W1" crossorigin="anonymous"></script>
        <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/js/bootstrap.min.js" integrity="sha384-JjSmVgyd0p3pXB1rRibZUAYoIIy6OrQ6VrjIEaFf/nJGzIxFDsf4x0xIM+B07jRM" crossorigin="anonymous"></script>

        <script>
        let filesInput = document.querySelector("#archivosAEnviar");
        let button = document.querySelector("#btnSubmit");
        let xmlFiles = document.querySelector("#xmlFiles");
        filesInput.addEventListener("change", stateHandle);
        button.disabled = true;

        function stateHandle() {
            if (document.querySelector("#archivosAEnviar").value === "") {
                button.disabled = true;
            } else {
                button.disabled = false;
            }
            document.getElementById("nombreArchivos").innerHTML = "<hr/><h5>Archivos seleccionados <span class='badge badge-success'>" + xmlFiles.files.length + "</span></h5><hr/>";
            for (var i = 0, max = xmlFiles.files.length; i < max; i++) {
                if(i + 1 === max) {
                    document.getElementById("nombreArchivos").innerHTML += (i + 1) + ".- " + xmlFiles.files[i].name;
                } else if(i >= 30) {
                    document.getElementById("nombreArchivos").innerHTML += "<Strong>" + (max - 30 + " archivos más ...</Strong>");
                    break;
                } else {
                    document.getElementById("nombreArchivos").innerHTML += (i + 1) + ".- " + xmlFiles.files[i].name + "<br/>";
                }
            }
            document.getElementById("nombreArchivos").innerHTML += "<hr/>";
            
        }

        function enviarArchivos() {
            filesInput.style.display = "none";
            let span = document.createElement("span");
            span.className = "spinner-border spinner-border-sm";
            button.disabled = true;
            button.className = "btn btn-primary";
            button.firstChild.data = "Subiendo ";
            button.appendChild(span);
            document.getElementById("filesInput").submit();
        }
        </script>

        <c:forEach var="archivoErroneo" items="${archivosErroneos}">
            <div class="alert alert-danger" role="alert">
                <div class="container">${archivoErroneo}</div>
            </div>
        </c:forEach>

        <jsp:include page="/WEB-INF/paginas/comunes/piePagina.jsp"></jsp:include>
    </body>
</html>