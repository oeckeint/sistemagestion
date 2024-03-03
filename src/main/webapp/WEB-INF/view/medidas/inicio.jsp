<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="security" uri="http://www.springframework.org/security/tags" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<jsp:include page="/WEB-INF/view/comunes/Lang.jsp"/>

<!DOCTYPE html>
  <html lang="${language}">
  <head>
    <jsp:include page="/WEB-INF/paginas/comunes/contenidoHead.jsp"/>
  </head>
  <body>
  <!--Cabecero-->
  <jsp:include page="/WEB-INF/paginas/comunes/cabecero.jsp"/>
  <div class="alert alert-warning alert-dismissible fade show" role="alert">
    <div class="container">
      <c:choose>
        <c:when test="${info eq 'errorDatosEnviados'}">
          Por favor revise su solicitud, los datos enviados no son correctos. Recuerde que solo se aceptan valores enteros.
        </c:when>
        <c:when test="${info eq 'sinMedidas'}">
            No se han encontrado medidas para el cliente seleccionado.
        </c:when>
        <c:otherwise>
          Desde aqui puedes descargar las medidas de calidad de los clientes, puedes usar los filtros para buscar
        </c:otherwise>
      </c:choose>
    </div>
  </div>

  <div class="container">
    <hr>
    <div class="row">
      <div class="col-12">
        <div class="row justify-content-between p-0">
          <div class="col-12 col-lg-5">
            <h2 class="m-0"><a href="${pageContext.request.contextPath}/clientes"><i class="fas fa-arrow-circle-left text-success"></i></a>Medidas</h2>
          </div>
          <div class="row col-12 col-lg-6 justify-content-end mt-3 mt-md-0">
            <jsp:include page="/WEB-INF/view/medidas/common/formulario_descargar_medidas_csv.jsp"/>
          </div>
        </div>
      </div>
    </div>
    <hr/>
  </div>

  <script>
    function loadData(id, url) {
      window.location = url;
    }

    function editData(url){
      window.location = url;
    }
  </script>

  <!--Footer-->
  <jsp:include page="/WEB-INF/paginas/comunes/piePagina.jsp"></jsp:include>
  </body>
</html>