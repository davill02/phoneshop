<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

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
<div>
    ${cart}
</div>
<nav class="navbar navbar-light bg-light">
    <a class="navbar-brand" href="${pageContext.servletContext.contextPath}/productList">
        <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-phone"
             viewBox="0 0 16 16">
            <path d="M11 1a1 1 0 0 1 1 1v12a1 1 0 0 1-1 1H5a1 1 0 0 1-1-1V2a1 1 0 0 1 1-1h6zM5 0a2 2 0 0 0-2 2v12a2 2 0 0 0 2 2h6a2 2 0 0 0 2-2V2a2 2 0 0 0-2-2H5z"></path>
            <path d="M8 14a1 1 0 1 0 0-2 1 1 0 0 0 0 2z"></path>
        </svg>
        Phonify
    </a>
    <ul class="navbar-nav">
        <li class="nav-item">
            <a href="#" class="nav-link me-4">Login</a>
        </li>
    </ul>
</nav>


<div class="container mt-5">
    <table class="table table-bordered border-primary align-middle caption-top">
        <caption>Found ${orders.size()} phones.</caption>
        <thead>
        <tr>
            <td>Order number</td>
            <td>Customer
            </td>
            <td>Phone
            </td>
            <td>Address</td>
            <td>Date
            </td>
            <td>Total price
            </td>
            <td>Status
            </td>

        </tr>
        </thead>
        <c:forEach var="order" items="${orders}">
            <tr>
                <td>
                    <a href="${pageContext.servletContext.contextPath}/admin/orders/${order.id}"> ${order.id}</td>
                <td>${order.firstName} ${order.lastName}</td>
                <td>
                        ${order.contactPhoneNo}
                </td>
                <td>
                        ${order.deliveryAddress}
                </td>
                <td>${order.date}</td>
                <td>$ ${order.totalPrice}</td>
                <td>
                        ${order.status}
                </td>
            </tr>
        </c:forEach>
    </table>
</div>
