<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<nav aria-label="Page navigation example">
    <ul class="pagination justify-content-end">
        <c:url var="prevPage" value="/${controller}">
            <c:param name="rows" value="${rows}"/>
            <c:param name="page" value="${paginaActual - 1}"/>
        </c:url>
        <c:url var="nextPage" value="/${controller}">
            <c:param name="rows" value="${rows}"/>
            <c:param name="page" value="${paginaActual + 1}"/>
        </c:url>
        <c:url var="pag1" value="/${controller}">
            <c:param name="rows" value="${rows}"/>
            <c:param name="page" value="${paginaActual - 3}"/>
        </c:url>
        <c:url var="pag2" value="/${controller}">
            <c:param name="rows" value="${rows}"/>
            <c:param name="page" value="${paginaActual - 2}"/>
        </c:url>
        <c:url var="pag3" value="/${controller}">
            <c:param name="rows" value="${rows}"/>
            <c:param name="page" value="${paginaActual - 1}"/>
        </c:url>
        <c:url var="pagIni1" value="/${controller}">
            <c:param name="rows" value="${rows}"/>
            <c:param name="page" value="${ultimaPagina - ultimaPagina + 1}"/>
        </c:url>
        <c:url var="pagIni2" value="/${controller}">
            <c:param name="rows" value="${rows}"/>
            <c:param name="page" value="${ultimaPagina - ultimaPagina + 2}"/>
        </c:url>
        <c:url var="pagIni3" value="/${controller}">
            <c:param name="rows" value="${rows}"/>
            <c:param name="page" value="${ultimaPagina - ultimaPagina + 3}"/>
        </c:url>
        <c:url var="pagFin1" value="/${controller}">
            <c:param name="rows" value="${rows}"/>
            <c:param name="page" value="${ultimaPagina - 2}"/>
        </c:url>
        <c:url var="pagFin2" value="/${controller}">
            <c:param name="rows" value="${rows}"/>
            <c:param name="page" value="${ultimaPagina - 1}"/>
        </c:url>
        <c:url var="pagFin3" value="/${controller}">
            <c:param name="rows" value="${rows}"/>
            <c:param name="page" value="${ultimaPagina}"/>
        </c:url>
        <li class="page-item">
            <select class="page-link" id="numberOfRows" onchange="changeNumberOfRows()">
                <option value="25" ${rows == 25 ? 'selected' : ''}>25</option>
                <option value="50" ${rows == 50 ? 'selected' : ''}>50</option>
                <option value="75" ${rows == 75 ? 'selected' : ''}>75</option>
            </select>
        </li>
        <c:choose>
            <c:when test="${paginaActual == 1}">
                <li class="page-item disabled"><a class="page-link">${paginaActual}</a></li>
                <li class="page-item"><a class="page-link" href="${pagIni2}">${paginaActual + 1}</a></li>
                <li class="page-item"><a class="page-link" href="${pagIni3}">${paginaActual + 2}</a></li>
                <li class="page-item disabled"><a class="page-link">...</a></li>
                <li class="page-item"><a class="page-link" href="${pagFin1}">${ultimaPagina - 2}</a></li>
                <li class="page-item"><a class="page-link" href="${pagFin2}">${ultimaPagina - 1}</a></li>
                <li class="page-item"><a class="page-link" href="${pagFin3}">${ultimaPagina}</a></li>
                <li class="page-item">
                    <a class="page-link" href="${nextPage}">Siguiente</a>
                </li>
            </c:when>
            <c:when test="${paginaActual == 2}">
                <li class="page-item">
                    <a class="page-link" href="${prevPage}" tabindex="-1" aria-disabled="true">Anterior</a>
                </li>
                <li class="page-item"><a class="page-link" href="${pagIni1}">${paginaActual - 1}</a></li>
                <li class="page-item disabled"><a class="page-link">${paginaActual}</a></li>
                <li class="page-item"><a class="page-link" href="${pagIni3}">${paginaActual + 1}</a></li>
                <li class="page-item disabled"><a class="page-link" href="#">...</a></li>
                <li class="page-item"><a class="page-link" href="${pagFin1}">${ultimaPagina - 2}</a></li>
                <li class="page-item"><a class="page-link" href="${pagFin2}">${ultimaPagina - 1}</a></li>
                <li class="page-item"><a class="page-link" href="${pagFin3}">${ultimaPagina}</a></li>
                <li class="page-item">
                    <a class="page-link" href="${nextPage}">Siguiente</a>
                </li>
            </c:when>
            <c:when test="${paginaActual == ultimaPagina}">
                <li class="page-item">
                    <a class="page-link" href="${prevPage}" tabindex="-1" aria-disabled="true">Anterior</a>
                </li>
                <li class="page-item"><a class="page-link" href="${pagIni1}">1</a></li>
                <li class="page-item"><a class="page-link" href="${pagIni2}">2</a></li>
                <li class="page-item"><a class="page-link" href="${pagIni3}">3</a></li>
                <li class="page-item disabled"><a class="page-link" href="#">...</a></li>
                <li class="page-item"><a class="page-link" href="${pagFin1}">${ultimaPagina - 2}</a></li>
                <li class="page-item"><a class="page-link" href="${pagFin2}">${ultimaPagina - 1}</a></li>
                <li class="page-item disabled"><a class="page-link">${paginaActual}</a></li>
                </c:when>
                <c:when test="${paginaActual == ultimaPagina - 1}">
                <li class="page-item">
                    <a class="page-link" href="${prevPage}" tabindex="-1" aria-disabled="true">Anterior</a>
                </li>
                <li class="page-item"><a class="page-link" href="${pagIni1}">1</a></li>
                <li class="page-item"><a class="page-link" href="${pagIni2}">2</a></li>
                <li class="page-item"><a class="page-link" href="${pagIni3}">3</a></li>
                <li class="page-item disabled"><a class="page-link" href="#">...</a></li>
                <li class="page-item"><a class="page-link" href="${pagFin1}">${ultimaPagina - 2}</a></li>
                <li class="page-item disabled"><a class="page-link">${paginaActual}</a></li>
                <li class="page-item"><a class="page-link" href="${pagFin3}">${ultimaPagina}</a></li>
                <li class="page-item">
                    <a class="page-link" href="${nextPage}">Siguiente</a>
                </li>
            </c:when>
            <c:when test="${paginaActual == ultimaPagina - 2}">
                <li class="page-item">
                    <a class="page-link" href="${prevPage}" tabindex="-1" aria-disabled="true">Anterior</a>
                </li>
                <li class="page-item"><a class="page-link" href="${pagIni1}">1</a></li>
                <li class="page-item"><a class="page-link" href="${pagIni2}">2</a></li>
                <li class="page-item"><a class="page-link" href="${pagIni3}">3</a></li>
                <li class="page-item disabled"><a class="page-link" href="#">...</a></li>
                <li class="page-item disabled"><a class="page-link">${paginaActual}</a></li>
                <li class="page-item"><a class="page-link" href="${pagFin2}">${ultimaPagina - 1}</a></li>
                <li class="page-item"><a class="page-link" href="${pagFin3}">${ultimaPagina}</a></li>
                <li class="page-item">
                    <a class="page-link" href="${nextPage}">Siguiente</a>
                </li>
            </c:when>
            <c:otherwise>
                <li class="page-item">
                    <a class="page-link" href="${prevPage}" tabindex="-1" aria-disabled="true">Anterior</a>
                </li>
                <li class="page-item"><a class="page-link" href="${pag2}">${paginaActual - 2 }</a></li>
                <li class="page-item"><a class="page-link" href="${pag3}">${paginaActual - 1}</a></li>
                <li class="page-item disabled"><a class="page-link">${paginaActual}</a></li>
                <li class="page-item disabled"><a class="page-link" href="#">...</a></li>
                <li class="page-item"><a class="page-link" href="${pagFin1}">${ultimaPagina - 2}</a></li>
                <li class="page-item"><a class="page-link" href="${pagFin2}">${ultimaPagina - 1}</a></li>
                <li class="page-item"><a class="page-link" href="${pagFin3}">${ultimaPagina}</a></li>
                <li class="page-item">
                    <a class="page-link" href="${nextPage}">Siguiente</a>
                </li>
            </c:otherwise>
        </c:choose>
    </ul>
</nav>
<script type="text/javascript">
    function changeNumberOfRows() {
        var x = document.getElementById("numberOfRows").value;
        showRows(x);
    }

    function showRows(rows) {
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

        hash["rows"] = rows;

        var list = [];
        Object.keys(hash).forEach(function (key) {
            list.push(key + '=' + hash[key]);
        });

        parser.search = '?' + list.join('&');
        location.href = parser.href;
    }
</script>