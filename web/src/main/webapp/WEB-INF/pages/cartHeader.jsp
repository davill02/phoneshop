<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<jsp:useBean id="cart" type="com.es.core.cart.Cart" scope="session"/>
<li class="nav-item">
    <div class="btn-group" role="group">
        <a href="${pageContext.servletContext.contextPath}/cart" class="btn btn-primary">Cart</a>
        <button id="quantity" type="button" class="btn btn-primary" disabled>
            Quantity: ${cart.quantity} </button>
        <button id="price" type="button" class="btn btn-primary" disabled>Price: ${cart.totalPrice} $</button>
    </div>
</li>