<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<div class="container">
    <a href="javascript:languageLink('es');"><fmt:message key="lang.es"/></a> | 
    <a href="javascript:languageLink('en');"><fmt:message key="lang.en"/></a>
</div>
<footer id="pie-pagina" class="bg-warning text-white mt-3 p-5">
    <div class="container">
        <div class="row justify-content-md-between">
            <div class="col">
                <p class="lead text-center"><fmt:message key="footer.copyright" /> &COPY; <fmt:message key="footer.company" /> <fmt:message key="footer.year" /> <fmt:message key="footer.version" /></p>
            </div>
        </div>
    </div>
</footer>
<script type="text/javascript">
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
    };
</script>

