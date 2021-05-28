<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
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
    <tags:login/>
</nav>

<div class="container mt-5">
    <div class="row">
        <div class="col-3"><p class="text-left fs-4">Order number: ${order.id}</div>
        <div class="col-6"></div>
        <div class="col-3 text-right"><p class="fs-3">Order status:<strong>${order.status}</strong></p></div>
    </div>
    <div class="row">
        <table class="table table-bordered border-primary align-middle caption-top">
            <thead>
            <tr>
                <td>Image</td>
                <td>Brand</td>
                <td>Model</td>
                <td>Colors</td>
                <td>Display size</td>
                <td>Price</td>
                <td>Quantity</td>
            </tr>
            </thead>

            <c:forEach var="orderItem" items="${order.orderItems}" varStatus="counter">
                <tr>
                    <td>
                        <img height="150px" width="150px" class="img-thumbnail"
                             src="https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/${orderItem.phone.imageUrl}"
                             alt="Picture">
                    </td>
                    <td>${orderItem.phone.brand}</td>
                    <td>
                        <a href="${pageContext.servletContext.contextPath}/productDetails/${orderItem.phone.id}">${orderItem.phone.model}</a>
                    </td>
                    <td>
                        <c:forEach var="color" items="${orderItem.phone.colors}">
                            ${color.code}
                        </c:forEach>
                    </td>
                    <td>${orderItem.phone.displaySizeInches}"</td>
                    <td>$ ${orderItem.phone.price}</td>
                    <td>${orderItem.quantity}</td>

                </tr>
            </c:forEach>
            <tr>
                <td colspan="5">Subtotal</td>
                <td colspan="2">$ ${order.subtotal}</td>
            </tr>
            <tr>
                <td colspan="5">Delivery price</td>
                <td colspan="2">${order.deliveryPrice}</td>
            </tr>
            <tr>
                <td colspan="5">Total price</td>
                <td colspan="2">${order.totalPrice}</td>
            </tr>
        </table>
    </div>
    <div class="row">
        <div class="col-6 mt-5">
            <table class="table table-sm">
                <thead>
                <tr>
                    <td colspan="2"> Delivery Info</td>
                </tr>
                </thead>
                <tr>
                    <td>First name</td>
                    <td>${order.firstName}</td>
                </tr>
                <tr>
                    <td>Last name</td>
                    <td>${order.lastName}</td>
                </tr>
                <tr>
                    <td>Address</td>
                    <td>${order.deliveryAddress}</td>
                </tr>
                <tr>
                    <td>Contact phone</td>
                    <td>${order.contactPhoneNo}</td>
                </tr>
                <tr>
                    <td>Additional Information</td>
                    <td>${order.additionalInformation}</td>
                </tr>
            </table>
        </div>
    </div>
    <div class="row">
        <div class="col-3"><a href="${pageContext.servletContext.contextPath}/admin/orders/" class="btn btn-primary">Back
            to main page</a></div>
        <div class="col-9">
            <c:if test="${order.status.toString() == 'NEW'}">
                <form method="post">
                    <input type="submit" name="orderStatus" value="REJECTED">
                    <input type="submit" name="orderStatus" value="DELIVERED">
                </form>
            </c:if>
        </div>
    </div>
</div>
