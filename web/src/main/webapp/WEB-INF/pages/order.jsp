<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<jsp:useBean id="order" type="com.es.core.model.order.Order" scope="session"/>
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
<c:if test="${order.orderItems.size() == 0}">
    <div class="container mt-5">
        <c:if test="${orderItemsOutOfStockMsg != null}">
            <div class="row text-center">
                <p> We remove all items.</p>
                <p class="text-danger">${orderItemsOutOfStockMsg}</p>
            </div>
        </c:if>
        <div class="row mt-4">
            <p class="">You can't order with empty cart.
                <a class="btn btn-primary" href="${pageContext.servletContext.contextPath}">Continue shopping</a>
            </p>
        </div>
    </div>
</c:if>
<c:if test="${order.orderItems.size() > 0}">
    <div class="container mt-5">
        <c:if test="${orderItemsOutOfStockMsg != null}">
            <div class="row text-center">
                <p> We remove some items.</p>
                <p class="text-danger">${orderItemsOutOfStockMsg}</p>
            </div>
        </c:if>
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
            <div class="col-5">
                <form:form method="post" commandName="personalDataForm">
                    <table class="table table-responsive">
                        <tags:inputRow path="firstname" title="First name"></tags:inputRow>
                        <tags:inputRow path="lastname" title="Last name"></tags:inputRow>
                        <tags:inputRow path="phoneNumber" title="Pnone number"></tags:inputRow>
                        <tags:inputRow path="deliveryAddress" title="Delivery address"></tags:inputRow>

                    </table>
                    <p>Additional information</p>
                    <form:textarea path="additionalInformation" class="form-control" rows="4"></form:textarea>
                    <p class="text-danger">
                        <form:errors path="additionalInformation"/>
                    </p>
                    <a class="btn btn-primary" href="${pageContext.servletContext.contextPath}/cart">Back to cart</a>
                    <input value="Buy" type="submit" class="btn btn-success">
                </form:form>
            </div>
        </div>
    </div>
</c:if>