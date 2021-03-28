<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <jsp:include page="/WEB-INF/paginas/comunes/contenidoHead.jsp"></jsp:include>
    </head>
    <body>
        <jsp:include page="/WEB-INF/paginas/comunes/cabecero.jsp"></jsp:include>
        <div class="alert alert-warning alert-dismissible fade show" role="alert">
            <div class="container">
                <strong>${mensaje}</strong>
            </div>
        </div>

        <c:forEach var="ResConsulta" items="${resultadosArchivoXML}" varStatus="indice">
         **********Datos del archivo ${indice.index}*************
        <li>idCliente ${ResConsulta.idClente}</li>
        <li>Compañia ${ResConsulta.compania}</li>
        <li>Tarifa ${ResConsulta.tarifa}</li>
        <li>Fecha Factura ${ResConsulta.fechaFactura}</li>
        <li>Fecha Inicio ${ResConsulta.fechaInicio}</li>
        <li>Fecha Fin ${ResConsulta.fechaFin}</li>
        <li>Número de Dias ${ResConsulta.numeroDias}</li>
        <li>CUPS ${ResConsulta.cups}</li>
        <li>Cliente ${ResConsulta.cliente}</li>
        <li>Importe Total Termino Potencia ${ResConsulta.importeTotalTerminoPotencia}</li>
        <li>Importe Importe Total Energia Activa ${ResConsulta.importeTotalEnergiaActiva}</li>
        <li>Importe Importe Total Energia Reactiva ${ResConsulta.importeTotalEnergiaReactiva}</li>
        <li>Importe Importe Total Excesos ${ResConsulta.importeTotalExcesos}</li>
        <li>Importe Importe Facturación Alquileres ${ResConsulta.importeFacturacionAlquileres}</li>
        ****************Datos de Periodo***************<br>
        Datos P# kW Fact|#]
        <li>Potencia a facturar (1) ${ResConsulta.potenciaAFacturar1}</li>
        <li>Potencia a facturar (2) ${ResConsulta.potenciaAFacturar2}</li>
        <li>Potencia a facturar (3) ${ResConsulta.potenciaAFacturar3}</li>
        <li>Potencia a facturar (4) ${ResConsulta.potenciaAFacturar4}</li>
        <li>Potencia a facturar (5) ${ResConsulta.potenciaAFacturar5}</li>
        <li>Potencia a facturar (6) ${ResConsulta.potenciaAFacturar6}</li>
        Datos P# kW Cons
        <li>Potencia Max Deman (1) ${ResConsulta.potenciaMaxDemandada1}</li>
        <li>Potencia Max Deman (2) ${ResConsulta.potenciaMaxDemandada2}</li>
        <li>Potencia Max Deman (3) ${ResConsulta.potenciaMaxDemandada3}</li>
        <li>Potencia Max Deman (4) ${ResConsulta.potenciaMaxDemandada4}</li>
        <li>Potencia Max Deman (5) ${ResConsulta.potenciaMaxDemandada5}</li>
        <li>Potencia Max Deman (6) ${ResConsulta.potenciaMaxDemandada6}</li>
        Precio Potencia
        <li>Precio Potencia (1) ${ResConsulta.precioPotencia1}</li>
        <li>Precio Potencia (2) ${ResConsulta.precioPotencia2}</li>
        <li>Precio Potencia (3) ${ResConsulta.precioPotencia3}</li>
        <li>Precio Potencia (4) ${ResConsulta.precioPotencia4}</li>
        <li>Precio Potencia (5) ${ResConsulta.precioPotencia5}</li>
        <li>Precio Potencia (6) ${ResConsulta.precioPotencia6}</li>
        Potencia Contratada
        <li>Potencia Contratada (1) ${ResConsulta.potenciaContratada1}</li>
        <li>Potencia Contratada (2) ${ResConsulta.potenciaContratada2}</li>
        <li>Potencia Contratada (3) ${ResConsulta.potenciaContratada3}</li>
        <li>Potencia Contratada (4) ${ResConsulta.potenciaContratada4}</li>
        <li>Potencia Contratada (5) ${ResConsulta.potenciaContratada5}</li>
        <li>Potencia Contratada (6) ${ResConsulta.potenciaContratada6}</li>
        ****************Fin Datos de Periodo***************<br>
        ****************Datos Magnitud***************<br>
        Datos A# kWh (Confirmar Orden)
        <li>Consumo calculado (1) ${ResConsulta.consumoCalculado1}</li>
        <li>Consumo calculado (2) ${ResConsulta.consumoCalculado2}</li>
        <li>Consumo calculado (3) ${ResConsulta.consumoCalculado3}</li>
        <li>Consumo calculado (4) ${ResConsulta.consumoCalculado4}</li>
        <li>Consumo calculado (5) ${ResConsulta.consumoCalculado5}</li>
        <li>Consumo calculado (6) ${ResConsulta.consumoCalculado6}</li>
        ****************fin Datos Magnitud***************<br>
        <li>Codigo Fiscal Factura ${ResConsulta.codigoFiscalFactura}</li>
        <li>Tipo Factura ${ResConsulta.tipoFactura}</li>
        <li>Remesa ${ResConsulta.remesa}</li>
        <hr>
        </c:forEach>

        <jsp:include page="/WEB-INF/paginas/comunes/piePagina.jsp"></jsp:include>
    </body>
</html>