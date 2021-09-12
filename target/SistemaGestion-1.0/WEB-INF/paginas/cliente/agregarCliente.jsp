<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<div class="modal fade" id="agregarClienteModal">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header bg-info text-white">
                <h5 class="modal-title">Agregar cliente</h5>
                <button class="close" data-dismiss="modal"><span>&times;</span></button>
            </div>

            <form:form action="${pageContext.request.contextPath}/clientes/guardar" modelAttribute="cliente" method="post" cssClass="was-validated" onsubmit="return validation()">
                <div class="modal-body">
                    <div class="form-group">
                        <label for="cups">CUPS</label>
                        <form:input path="cups"></form:input>
                    </div>
                    <div class="form-group">
                        <label for="nombre">Nombre</label>
                        <input type="text" id="nombre" class="form-control" name="nombre">
                    </div>
                    <div class="form-group">
                        <label for="tarifa">Tarifa</label>
                        <input type="text" id="tarifa" class="form-control" name="tarifa">
                    </div>
                </div>
                <div class="modal-footer">
                    <button class="btn btn-primary" type="submit">Agregar</button>
                </div>
            </form:form>
        </div>
    </div>
</div>

<script type="text/javascript">

    function isEmpty(campo, valor) {

        if (valor === null || valor.length === 0 || /^\s+$/.test(valor)) {
            alert("El campo " + campo + " está vacío.");
            return true;
        } else {
            return false;
        }

    }

    function validarCups(cups) {
        if (cups.length > 19 && cups.length < 23) {
            if (cups.substring(0, 2) !== "ES") {
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

                if (fin === letra1 + letra2) {
                    return true;
                } else {
                    alert("Revisar los carácteres " + fin);
                    return false;
                }
            }
        } else {
            alert("El cups debe tener entre 20 y 23 carácteres.");
            return false;
        }
    }

    function letraCups(num) {
        letraControl = "";

        switch (num) {
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

    function validarTarifa(tarifa) {
        switch (tarifa) {
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
                alert("La tarifa " + tarifa + " no es válida");
                return false;
                break;
        }

        return false;
    }

    function validacion() {

        cups = document.getElementById("cups").value;
        nombre = document.getElementById("nombre").value;
        tarifa = document.getElementById("tarifa").value;

        if (isEmpty("cups", cups) || isEmpty("nombre", nombre) || isEmpty("tarifa", tarifa)) {
            return false;
        } else {
            if (validarCups(cups) && validarTarifa(tarifa)) {
                return true;
            } else {
                return false;
            }
        }
    }
</script>