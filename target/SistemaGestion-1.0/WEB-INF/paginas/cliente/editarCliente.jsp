<!DOCTYPE html>
<html>
    <head>
        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@4.5.3/dist/css/bootstrap.min.css" integrity="sha384-TX8t27EcRE3e/ihU7zmQxVncDAy5uIKz4rEkgIXeMed4M0jlfIDPvg6uqKI2xXr2" crossorigin="anonymous">
        <link rel="stylesheet" href="https://use.fontawesome.com/releases/v5.15.1/css/all.css" integrity="sha384-vp86vTRFVJgpjF9jiIGPEEqYqlDwgyBgEF109VFjmqGmIY/Y4HV4d3Gp2irVfcrp" crossorigin="anonymous">
        <title>Editar Clientes</title>
    </head>
    <body>
        <!--Cabecero-->
        <jsp:include page="/WEB-INF/paginas/comunes/cabecero.jsp"></jsp:include>
        
        <div class="alert alert-warning alert-dismissible fade show" role="alert">
            <div class="container">
                <strong>${mensajeRegistro}</strong>
            </div>
        </div>
        
        
        <!--Formulario-->
        <form action="${pageContext.request.contextPath}/ControlClientes?accion=actualizar&idCliente=${cliente.idCliente}" method="post" class="was-validated" onsubmit="return validacion()">
            
            <section class="details mt-5">
                <div class="container">
                    <div class="row">
                        <div class="col">
                            <div class="card">
                                <div class="card-header">
                                    <div class="row">
                                        <div class="col-md-6">
                                            <h4>Editando <strong>${cliente.nombreCliente}</strong></h4>
                                        </div>
                                        <div class="col-md-6"> 
                                           <div class="row">
                                                <div class="col-md-4">
                                                    <a href="${pageContext.request.contextPath}/ControlClientes?accion=cancelar" class="btn btn-primary btn-block"><i class="fas fa-arrow-left"></i> Ir al inicio</a>                
                                                </div>
                                                <div class="col-md-4">
                                                    <button type="submit" class="btn btn-success btn-block"><i class="fas fa-check"></i> Actualizar</button>
                                                </div>
                                                <div class="col-md-4">
                                                    <a href="${pageContext.request.contextPath}/ControlClientes?accion=eliminar&idCliente=${cliente.idCliente}" class="btn btn-danger btn-block"><i class="fas fa-times"></i> Archivar</a>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                                <div class="card-body">
                                    <div class="form-group">
                                        <label for="cups">CUPS:</label>
                                        <input type="text" class="form-control" value="${cliente.cups}" disabled>
                                        <input type="hidden" name="cups" name="cups" id="cups" value="${cliente.cups}">
                                    </div>
                                    <div class="form-group">
                                        <label for="nombre">Nombre</label>
                                        <input type="text" class="form-control" id="nombre" name="nombre" required value="${cliente.nombreCliente}">
                                    </div>
                                    <div class="form-group">
                                        <label for="tarifa">Tarifa</label>
                                        <input type="text" class="form-control" id="tarifa" name="tarifa" required value="${cliente.tarifa}">
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </section>
        </form>
        
        
        <script type="text/javascript">

            function isEmpty(campo, valor){

                if( valor === null || valor.length === 0 || /^\s+$/.test(valor) ) {
                    alert("El campo " + campo + " está vacío.");
                    return true;
                } else {
                    return false;
                }

            }

            function validarCups(cups){
                if (cups.length > 19 && cups.length < 23) {
                    if (cups.substring(0 , 2) !== "ES" ) {
                        alert("El cups debe comenzar con ES");
                        return false;
                    } else {
                        valor = parseInt(cups.substring(2, 18));
                        fin = cups.substring(18, 20);

                        aux = (valor % 529) / 23;
                        parteDecimal = aux % 1;
                        parteEntera = aux - parteDecimal;

                        letra1 = letraCups(parteEntera);
                        letra2 = letraCups(Math.round(parteDecimal * 23));

                        if(fin === letra1+letra2 ){
                            return true;
                        }else{
                            alert("Revisar los carácteres " + fin);
                            return false;
                        }
                    }
                } else {
                    alert("El cups debe tener entre 20 y 23 carácteres.");
                    return false;
                }
            }

            function letraCups(num){
                letraControl = "";

                switch(num){
                    case 0:
                        letraControl = "T";
                        break; 
                    case 1:
                        letraControl = "R";
                        break; 
                    case 2:
                        letraControl = "W";
                        break; 
                    case 3:
                        letraControl = "A";
                        break; 
                    case 4:
                        letraControl = "G";
                        break; 
                    case 5:
                        letraControl = "M";
                        break; 
                    case 6:
                        letraControl = "Y";
                        break; 
                    case 7:
                        letraControl = "F";
                        break; 
                    case 8:
                        letraControl = "P";
                        break; 
                    case 9:
                        letraControl = "D";
                        break; 
                    case 10:
                        letraControl = "X";
                        break; 
                    case 11:
                        letraControl = "B";
                        break; 
                    case 12:
                        letraControl = "N";
                        break; 
                    case 13:
                        letraControl = "J";
                        break; 
                    case 14:
                        letraControl = "Z";
                        break; 
                    case 15:
                        letraControl = "S";
                        break; 
                    case 16:
                        letraControl = "Q";
                        break; 
                    case 17:
                        letraControl = "V";
                        break; 
                    case 18:
                        letraControl = "H";
                        break; 
                    case 19:
                        letraControl = "L";
                        break; 
                    case 20:
                        letraControl = "C";
                        break; 
                    case 21:
                        letraControl = "K";
                        break; 
                    case 22:
                        letraControl = "E";
                        break; 
                    default:
                        letraControl = "0";
                        break; 
               }

                return letraControl;
            }
            
            function validarTarifa(tarifa){
                switch(tarifa){
                    case "T20A":
                        return true;
                        break;
                    case "T21A":
                        return true;
                        break;
                    case "T20DHA":
                        return true;
                        break;
                    case "T21DHA":
                        return true;
                        break;
                    case "T30A":
                        return true;
                        break;
                    case "T31A":
                        return true;
                        break;
                    case "T61A":
                        return true;
                        break;
                    case "T62A":
                        return true;
                        break;
                    case "T20TD":
                        return true;
                        break;
                    case "T30TD":
                        return true;
                        break;
                    case "T61TD":
                        return true;
                        break;
                    case "T62TD":
                        return true;
                        break;
                    default:
                        alert("La tarifa " + tarifa + " no es válida" );
                        return false;
                        break;
                }
                
                return false;
            }
            
            function validacion(){

                cups = document.getElementById("cups").value;
                nombre = document.getElementById("nombre").value;
                tarifa = document.getElementById("tarifa").value;

                if ( isEmpty("cups", cups) || isEmpty("nombre", nombre) || isEmpty("tarifa", tarifa) ) {
                    return false;
                } else {
                    if( validarCups(cups) && validarTarifa(tarifa) ){
                        return true;
                    }else {
                        return false;
                    }
                }
            }
        </script>
                                    
            <!--Footer-->
        <jsp:include page="/WEB-INF/paginas/comunes/piePagina.jsp"></jsp:include>
        <script src="https://code.jquery.com/jquery-3.5.1.slim.min.js" integrity="sha384-DfXdz2htPH0lsSSs5nCTpuj/zy4C+OGpamoFVy38MVBnE+IbbVYUew+OrCXaRkfj" crossorigin="anonymous"></script>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@4.5.3/dist/js/bootstrap.bundle.min.js" integrity="sha384-ho+j7jyWK8fNQe+A12Hb8AhRq26LrZ/JpcUGGOn+Y7RsweNrtN/tE3MoK7ZeZDyx" crossorigin="anonymous"></script>
    </body>
</html>
