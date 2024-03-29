<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="language" value="${not empty param.language ? param.language : not empty language ? language : 'es'}" scope="session"/>
<fmt:setLocale value="${language}"/>
<fmt:setBundle basename="labels"/>

<!doctype html>
<html lang="${language}">
    <head>
        <!-- Required meta tags -->
        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">

        <!-- Fonts -->
        <link rel="dns-prefetch" href="https://fonts.gstatic.com">
        <link href="https://fonts.googleapis.com/css?family=Raleway:300,400,600" rel="stylesheet" type="text/css">

        <!-- Bootstrap CSS -->
        <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.1.3/css/bootstrap.min.css">

        <link href="//maxcdn.bootstrapcdn.com/bootstrap/4.1.1/css/bootstrap.min.css" rel="stylesheet" id="bootstrap-css">
        <script src="//maxcdn.bootstrapcdn.com/bootstrap/4.1.1/js/bootstrap.min.js"></script>
        <script src="//cdnjs.cloudflare.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
        <!------ Include the above in your HEAD tag ---------->

        <title>Sistema de gesti�n</title>
    </head>
    <body>
        <nav class="navbar navbar-expand-lg navbar-light navbar-laravel">
            <div class="container">
                <a class="navbar-brand" href="#"><fmt:message key="login.login"/></a>
                <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarSupportedContent" aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
                    <span class="navbar-toggler-icon"></span>
                </button>

                <div class="collapse navbar-collapse" id="navbarSupportedContent">
                    <ul class="navbar-nav ml-auto">
                        <li class="nav-item">
                            <a class="nav-link" href="#"><fmt:message key="login.login" /></a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link" href="#"><fmt:message key="login.register" /></a>
                        </li>
                    </ul>

                </div>
            </div>
        </nav>

        <main class="login-form">
            <div class="cotainer">
                <div class="row justify-content-center">
                    <div class="col-md-8">
                        <div class="card">
                            <div class="card-header">
                                <i>
                                    <c:choose>
                                        <c:when test="${param.error != null}"><fmt:message key="login.badcredentials"/></c:when>
                                        <c:when test="${param.logout != null}"><fmt:message key="login.logout.message"/></c:when>
                                        <c:otherwise><fmt:message key="login.welcome.message"/></c:otherwise>
                                    </c:choose>
                                </i>
                            </div>
                            <form:form action="${pageContext.request.contextPath}/authenticateTheUser" method="POST">
                                <div class="card-body">
                                    <div class="form-group row">
                                        <label for="username" class="col-md-4 col-form-label text-md-right"><fmt:message key="login.username"/></label>
                                        <div class="col-md-6">
                                            <input type="text" id="username" class="form-control" name="username" required autofocus>
                                        </div>
                                    </div>

                                    <div class="form-group row">
                                        <label for="password" class="col-md-4 col-form-label text-md-right"><fmt:message key="login.password"/></label>
                                        <div class="col-md-6">
                                            <input type="password" id="password" class="form-control" name="password" required>
                                        </div>
                                    </div>

                                    <div class="form-group row">
                                        <div class="col-md-6 offset-md-4">
                                            <div class="checkbox">
                                                <label>
                                                    <input type="checkbox" name="remember"><fmt:message key="login.rememberme"/>
                                                </label>
                                            </div>
                                        </div>
                                    </div>

                                    <div class="col-md-6 offset-md-4">
                                        <button type="submit" class="btn btn-primary"><fmt:message key="login.login"/></button>
                                        <a href="#" class="btn btn-link"><fmt:message key="login.forgotpassword"/></a>
                                    </div>
                                </div>
                                    <a href="${pageContext.request.contextPath}/login?language=en">English</a>
                                    <a href="${pageContext.request.contextPath}/login?language=es">Espa�ol</a>
                            </form:form>
                        </div>
                    </div>
                </div>
            </div>
        </div>

    </main>
</body>
</html>