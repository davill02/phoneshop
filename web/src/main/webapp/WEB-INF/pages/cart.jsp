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
    <a class="navbar-brand" href="${pageContext.servletContext.contextPath}/productList">
        <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-phone"
             viewBox="0 0 16 16">
            <path d="M11 1a1 1 0 0 1 1 1v12a1 1 0 0 1-1 1H5a1 1 0 0 1-1-1V2a1 1 0 0 1 1-1h6zM5 0a2 2 0 0 0-2 2v12a2 2 0 0 0 2 2h6a2 2 0 0 0 2-2V2a2 2 0 0 0-2-2H5z"></path>
            <path d="M8 14a1 1 0 1 0 0-2 1 1 0 0 0 0 2z"></path>
        </svg>
        Phonify
    </a>
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
    <ul class="navbar-nav">
        <li class="nav-item">
            <a href="#" class="nav-link me-4">Login</a>
        </li>
    </ul>
</nav>

<div class="container mt-5">
    <form:form id="putForm" method="put" commandName="updateForm">
        <table class="table table-bordered border-primary align-middle caption-top">
            <thead>
            <tr>
                <td>Image</td>
                <td>Brand
                </td>
                <td>Model
                </td>
                <td>Colors</td>
                <td>Display size
                </td>
                <td>Price
                </td>
                <td>
                    Quantity
                </td>
                <td>
                    Add to
                </td>
            </tr>
            </thead>

            <c:forEach var="cartItem" items="${cart.items}" varStatus="counter">
                <tr>
                    <td>
                        <img height="150px" width="150px" class="img-thumbnail"
                             src="https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/${cartItem.phone.imageUrl}"
                             alt="Picture">
                    </td>
                    <td>${cartItem.phone.brand}</td>
                    <td>
                        <a href="${pageContext.servletContext.contextPath}/productDetails/${cartItem.phone.id}">${cartItem.phone.model}</a>
                    </td>
                    <td><c:forEach var="color" items="${cartItem.phone.colors}">
                        ${color.code}
                    </c:forEach></td>
                    <td>${cartItem.phone.displaySizeInches}"</td>
                    <td>$ ${cartItem.phone.price}</td>
                    <td id="${cartItem.phone.id}"><label for="${cartItem.phone.id}"></label>
                        <input type="hidden" name="phoneId" value="${cartItem.phone.id}">
                        <form:input path="quantity[${counter.count -1}]" name="quantity"
                                    class="form-control"
                                    id="${cartItem.phone.id}" type="text"/>
                        <p class="text-danger"><form:errors path="phoneId[${counter.count - 1}]"/></p>
                        <p class="text-danger"><form:errors path="quantity[${counter.count - 1}]"/></p>
                    </td>
                    <td>
                        <form:form form:id="deleteForm" method="delete">
                            <button id="deleteForm" class="btn btn-outline-danger" type="submit" name="id"
                                    value="${cartItem.phone.id}">Remove
                            </button>
                        </form:form>
                    </td>
                </tr>
            </c:forEach>

        </table>
        <button type="submit" id="putForm">Submit</button>
    </form:form>
</div>