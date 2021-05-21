<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<ul class="navbar-nav">
    <sec:authorize access="!isAuthenticated()">
        <li class="nav-item">
            <a href="${pageContext.servletContext.contextPath}/login" class="nav-link me-4">Login</a>
        </li>
    </sec:authorize>
    <sec:authorize access="isAuthenticated()">
        <li class="nav-item">
            <a href="${pageContext.servletContext.contextPath}/admin/orders" class="nav-link me-4">Admin page</a>
        </li>
        <li class="nav-item">
            <a href="${pageContext.servletContext.contextPath}/logout" class="nav-link me-4">Logout</a>
        </li>
    </sec:authorize>
</ul>