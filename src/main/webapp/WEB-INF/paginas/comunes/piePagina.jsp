<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="security" uri="http://www.springframework.org/security/tags" %>
<div class="container">
    <a href="javascript:languageLink('es');"><fmt:message key="lang.es"/></a> | 
    <a href="javascript:languageLink('en');"><fmt:message key="lang.en"/></a>
</div>
<footer id="pie-pagina" class="bg-warning text-white mt-3 p-5">
    <div class="container">
        <div class="row justify-content-md-between">
            <div class="col-12 col-md-4">
                <p class="lead text-center"><fmt:message key="footer.copyright" /> &COPY; <fmt:message key="footer.company" /> <fmt:message key="footer.year" /> <fmt:message key="footer.author" /></p>
            </div>
            <div class="col-12 col-md-3">
                <p id="reloj" class="lead text-center bg-secondary"></p>
            </div>
        </div>
    </div>
</footer>
<script type="text/javascript">
	const path = "${pageContext.request.contextPath}";

    function languageLink(lang) {
        var hash = {};
        var parser = document.createElement('a');

        parser.href = window.location.href;

        var parameters = parser.search.split(/\?|&/);

        for (var i = 0; i < parameters.length; i++) {
            if (!parameters[i])
                continue;

            var ary = parameters[i].split('=');
            hash[ary[0]] = ary[1];
        }

        hash["language"] = lang;

        var list = [];
        Object.keys(hash).forEach(function (key) {
            list.push(key + '=' + hash[key]);
        });

        parser.search = '?' + list.join('&');
        location.href = parser.href;
    }

    function startTime() {
        today = new Date();
        var mesok = new Array(12);
        mesok[0] = "<fmt:message key='footer.month.0'/>";
        mesok[1] = "<fmt:message key='footer.month.1'/>";
        mesok[2] = "<fmt:message key='footer.month.2'/>";
        mesok[3] = "<fmt:message key='footer.month.3'/>";
        mesok[4] = "<fmt:message key='footer.month.4'/>";
        mesok[5] = "<fmt:message key='footer.month.5'/>";
        mesok[6] = "<fmt:message key='footer.month.6'/>";
        mesok[7] = "<fmt:message key='footer.month.7'/>";
        mesok[8] = "<fmt:message key='footer.month.8'/>";
        mesok[9] = "<fmt:message key='footer.month.9'/>";
        mesok[10] = "<fmt:message key='footer.month.10'/>";
        mesok[11] = "<fmt:message key='footer.month.11'/>";

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
    
    function defineTitles(){
        const url = window.location.href;
        if(url.includes("/procesar")){
            document.getElementById("pageHeader").innerHTML = "<fmt:message key='process.icon'/> <fmt:message key='header.process'/>";
        } else if(url.includes("/clasificar")){
            document.getElementById("pageHeader").innerHTML = "<fmt:message key='sort.icon'/> <fmt:message key='header.sort'/>";
        } else if(url.includes("/peajes/detalles")){
            document.getElementById("backList").setAttribute("href", "${pageContext.request.contextPath}/peajes");
        } else if(url.includes("/facturas/detalles")){
            document.getElementById("backList").setAttribute("href", "${pageContext.request.contextPath}/facturas");
        }
    }
    
    window.onload = function () {
        defineTitles();
        startTime();
    };
    
    Mousetrap.bind(['alt+shift+0', 'i n', '0 enter'], function(){location.href= path + "/";});
    Mousetrap.bind(['alt+shift+p', 'p e'], function(){location.href= path + "/peajes";});
    Mousetrap.bind(['alt+shift+f', 'f a'], function(){location.href= path + "/facturas";});
    Mousetrap.bind(['alt+shift+o', 'o f'], function(){location.href= path + "/otrasfacturas";});
    Mousetrap.bind(['alt+shift+c', 'c l i'], function(){location.href= path + "/clientes";});
    Mousetrap.bind(['alt+shift+b', 'b u'], function(){ document.getElementById("searchValue").focus();});
    <security:authorize access="hasRole('ADMIN')">
	    Mousetrap.bind(['c l a'], function(){location.href= path + "/clasificar";});
		Mousetrap.bind(['p r'], function(){location.href= path + "/procesar";});
    </security:authorize>
</script>

