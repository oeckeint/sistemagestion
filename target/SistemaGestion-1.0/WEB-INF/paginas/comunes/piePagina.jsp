<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js" integrity="sha384-ka7Sk0Gln4gmtz2MlQnikT1wXgYsOg+OMhuP+IlRH9sENBO0LRn5q+8nbTov4+1p" crossorigin="anonymous"></script>
<footer id="pie-pagina" class="bg-warning text-white mt-3 p-5">
    <div class="container">
        <div class="row justify-content-md-between">
            <div class="col-4">
                <p class="lead text-center">Copyright &COPY; DevWorkshop 2021 V0.5.4</p>
            </div>
            <div class="col-3">
                <p id="reloj" class="lead text-center bg-secondary"></p>
            </div>
        </div>
    </div>
</footer>
<script type="text/javascript">
    function startTime() {
        today = new Date();
        var mesok = new Array(12);
        mesok[0] = "Enero";
        mesok[1] = "Febrero";
        mesok[2] = "Marzo";
        mesok[3] = "Abril";
        mesok[4] = "Mayo";
        mesok[5] = "Junio";
        mesok[6] = "Julio";
        mesok[7] = "Agosto";
        mesok[8] = "Septiembre";
        mesok[9] = "Octubre";
        mesok[10] = "Noviembre";
        mesok[11] = "Diciembre";

        h = today.getHours();
        m = today.getMinutes();
        s = today.getSeconds();
        m = checkTime(m);
        s = checkTime(s);
        d = today.getDate();
        mt = today.getMonth();
        y = today.getFullYear();
        document.getElementById('reloj').innerHTML = d + " " + mesok[today.getMonth()] + ", " + y + " - " + h + ":" + m + ":" + s;
        t = setTimeout('startTime()', 500);
    }
    function checkTime(i)
    {
        if (i < 10) {
            i = "0" + i;
        }
        return i;
    }
    window.onload = function () {
        startTime();
    }
</script>

