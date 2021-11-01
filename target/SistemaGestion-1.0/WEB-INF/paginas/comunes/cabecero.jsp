<header id="main-header" class="py-2 bg-info text-white">
    <div class="container">
        <nav class="navbar navbar-expand-lg navbar-dark">
            <div class="container-fluid">
                <h3>${titulo}</h3>
                <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNavDarkDropdown" aria-controls="navbarNavDarkDropdown" aria-expanded="false" aria-label="Toggle navigation">
                    <span class="navbar-toggler-icon"></span>
                </button>
                <div class="row justify-content-end">
                    <div class="collapse navbar-collapse" id="navbarNavDarkDropdown">
                        <ul class="navbar-nav">
                            <li class="nav-item dropdown">
                                <a class="nav-link dropdown-toggle text-white" href="#" id="navbarDarkDropdownMenuLink" role="button" data-bs-toggle="dropdown" aria-expanded="false">Clientes</a>
                                <ul class="dropdown-menu dropdown-menu-dark" aria-labelledby="navbarDarkDropdownMenuLink">
                                    <li><a class="dropdown-item" href="${pageContext.request.contextPath}/clientes">Lista</a></li>
                                    <li><a class="dropdown-item" href="${pageContext.request.contextPath}/clientes/formulario">Agregar</a></li>
                                    <!--<li><a class="dropdown-item" href="#">B�squeda Avanzada</a></li>-->
                                </ul>
                            </li>
                            <li class="nav-item dropdown">
                                <a class="nav-link dropdown-toggle text-white" href="#" id="navbarDarkDropdownMenuLink" role="button" data-bs-toggle="dropdown" aria-expanded="false">XML</a>
                                <ul class="dropdown-menu dropdown-menu-dark" aria-labelledby="navbarDarkDropdownMenuLink">
                                    <li><a class="dropdown-item" href="${pageContext.request.contextPath}/peajes">Peajes</a></li>
                                    <li><a class="dropdown-item" href="${pageContext.request.contextPath}/facturas">Facturas</a></li>
                                    <li><a class="dropdown-item" href="${pageContext.request.contextPath}/otrasfacturas">Otras Facturas <span class="badge badge-danger">Fixed!</span></a></li>
                                    <li><a class="dropdown-item" href="${pageContext.request.contextPath}/clasificar">Clasificar <span class="badge badge-danger">Fixed!</span></a></li>
                                    <li><a class="dropdown-item" href="${pageContext.request.contextPath}/procesar">Procesar <span class="badge badge-danger">New!</span></a></li>
                                </ul>
                            </li>
                            <li class="nav-item dropdown">
                                <a class="nav-link dropdown-toggle text-white" href="#" id="navbarDarkDropdownMenuLink" role="button" data-bs-toggle="dropdown" aria-expanded="false"><i class="fas fa-user"></i></a>
                                <ul class="dropdown-menu dropdown-menu-dark" aria-labelledby="navbarDarkDropdownMenuLink">
                                    <li><a class="dropdown-item" href="#">Cerrar Sesi�n</a></li>
                                </ul>
                            </li>
                        </ul>
                        <a class="nav-link text-white p-0" href="${pageContext.request.contextPath}/"><i class="fas fa-home"></i></a>
                    </div>
                </div>
            </div>
        </nav>
    </div>
</header>
