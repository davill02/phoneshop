<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<jsp:useBean id="cart" type="com.es.core.cart.Cart" scope="session"/>
<head>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.0-beta3/dist/css/bootstrap.min.css" rel="stylesheet"
          integrity="sha384-eOJMYsd53ii+scO/bJGFsiCZc+5NDVN2yr8+0RDqr0Ql0h+rP48ckxlpbzKgwra6" crossorigin="anonymous">
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.0-beta3/dist/js/bootstrap.bundle.min.js"
            integrity="sha384-JEW9xMcG8R+pH31jmWH6WWP0WintQrMb4s7ZOdauHnUtxwoG2vI5DkLtS3qm9Ekf"
            crossorigin="anonymous"></script>
    <script
            src="https://code.jquery.com/jquery-3.6.0.js"
            integrity="sha256-H+K7U5CnXl1h5ywQfKtSj8PCmoN9aaq30gDh27Xc0jk="
            crossorigin="anonymous"></script>
    <title>PhoneShop</title>
</head>

<nav class="navbar navbar-light bg-light">
    <tags:phonify/>
    <ul class="navbar-nav me-auto">
        <li class="nav-item">
            <div class="btn-group" role="group">
                <a href="${pageContext.servletContext.contextPath}/cart" class="btn btn-primary">Cart</a>
                <button id="quantity" type="button" class="btn btn-primary" disabled>
                    Quantity: ${cart.quantity} </button>
                <button id="price" type="button" class="btn btn-primary" disabled>Price: ${cart.totalPrice} $</button>
            </div>
        </li>
    </ul>

</nav>


<div class="container mt-5">
    <c:if test="${successMessage != null}">
        <div class="row"><p class="text-success">${successMessage}</p></div>
    </c:if>
    <form:form id="postForm" method="post" commandName="quickCartForm">
        <table class="table table-bordered border-primary align-middle caption-top">
            <thead>
            <tr>
                <td>Model</td>
                <td>Quantity
                </td>

            </tr>
            </thead>

            <c:forEach var="i" begin="0" end="10" varStatus="counter">
                <tr>
                    <td><form:input path="model[${counter.count -1}]" name="model"
                                    class="form-control"
                                    type="text"/>
                        <p class="text-danger"><form:errors path="model[${counter.count - 1}]"/></p></td>
                    <td>
                        <form:input path="quantity[${counter.count -1}]" name="quantity"
                                    class="form-control"
                                    type="text"/>

                        <p class="text-danger"><form:errors path="quantity[${counter.count - 1}]"/></p>
                    </td>
                </tr>
            </c:forEach>

        </table>
        <div class="row">
            <div class="col-4">
                <a class="btn btn-primary" href="${pageContext.servletContext.contextPath}/productList">Back to product
                    list</a>
            </div>
            <div class="col-6"></div>
            <div class="col-1">
                <button class="btn btn-success" type="submit">Add to cart</button>
            </div>

        </div>
    </form:form>

</div>
