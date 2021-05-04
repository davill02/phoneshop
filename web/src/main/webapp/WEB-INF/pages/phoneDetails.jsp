<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

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

<div class="container-lg">
    <div class="row mt-4">
        <div class="col-6"><img
                src="https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/${phoneDetails.imageUrl}"
                alt="">
        </div>
        <div class="col-6">
            <table class="table table-bordered">
                <thead><p class="fw-bold">Display</p></thead>
                <tags:notNull name="Size" value="${phoneDetails.displaySizeInches}" additionText="inches"/>
                <tags:notNull name="Resolution" value="${phoneDetails.displayResolution}"/>
                <tags:notNull name="Technology" value="${phoneDetails.displayTechnology}"/>
                <tags:notNull name="Pixel destiny" value="${phoneDetails.pixelDensity}"/>
            </table>
        </div>
    </div>
    <div class="row">
        <div class="col-6"><p>${phoneDetails.description}</p></div>
        <div class="col-6">
            <table class="table table-bordered">
                <thead><p class="fw-bold">Dimensions & weight</p></thead>
                <tags:notNull name="Lenght" value="${phoneDetails.lengthMm}" additionText="mm"/>
                <tags:notNull name="Width" value="${phoneDetails.widthMm}" additionText="mm"/>
                <tr>
                    <td>Color</td>
                    <td><c:forEach var="color" items="${phoneDetails.colors}">
                        <p>${color.code}</p>
                    </c:forEach></td>
                </tr>
                <tags:notNull name="Weight" value="${phoneDetails.weightGr}" additionText="g"/>
            </table>
        </div>
    </div>
    <div class="row">
        <div class="col-6">
            <c:if test="${phoneDetails.price != null}">
                <div class="container-fluid"></div>
                <div class="row">
                    <div class="col-6"><p class="fw-bold"> Price: ${phoneDetails.price}</p></div>
                </div>
                <div class="row">
                    <div id="phone" class="col-6">
                        <input value="1" class="form-control " id="${phoneDetails.id}" type="text"/>
                    </div>
                    <div class="col-6">
                        <button class="btn btn-outline-success" id="${phoneDetails.id}">Add to</button>
                    </div>
                </div>
            </c:if>
            <div class="col-6">
                <c:if test="${phoneDetails.price == null}">
                    <p class="fw-bold fs-5">Sorry, this item doesn't have price. You can call to our office to find
                        price.</p>
                </c:if>
            </div>
        </div>
        <div class="col-6">
            <table class="table table-bordered">
                <thead><p class="fw-bold">Camera</p></thead>
                <tags:notNull name="Front camera" value="${phoneDetails.frontCameraMegapixels}"
                              additionText="megapixels"/>
                <tags:notNull name="Back camera" value="${phoneDetails.backCameraMegapixels}"
                              additionText="megapixels"/>
            </table>
        </div>
    </div>

</div>
<div class="row">
    <div class="col-6"></div>
    <div class="col-6">
        <table class="table table-bordered">
            <thead><p class="fw-bold">Battery</p></thead>
            <tags:notNull name="Talk time" additionText="h" value="${phoneDetails.talkTimeHours}"/>
            <tags:notNull name="Stand by time" value="${phoneDetails.standByTimeHours}" additionText="h"/>
            <tags:notNull value="${phoneDetails.batteryCapacityMah}" name="Battery capacity"
                          additionText="Mah"/>
        </table>
    </div>
</div>
<div class="row">
    <div class="col-6">
    </div>
    <div class="col-6">
        <table class="table table-bordered">
            <thead><p class="fw-bold">Other</p></thead>
            <tags:notNull name="Device type" value="${phoneDetails.deviceType}"/>
            <tags:notNull name="Bluetooth" value="${phoneDetails.bluetooth}"/>
            <tags:notNull name="Os" value="${phoneDetails.os}"/>
            <tags:notNull name="RAM" value="${phoneDetails.ramGb}" additionText="Gb"/>
            <tags:notNull name="Storage" value="${phoneDetails.internalStorageGb}" additionText="Gb"/>
        </table>
    </div>
</div>
<script type="text/javascript">
    $('button').on('click', function () {
        let id = $(this).attr('id')
        let count = $('input[id=' + id.toString() + ']').val()
        $('div[id=' + id.toString() + ']').remove()
        $.ajax({
            url: '${pageContext.servletContext.contextPath}/ajaxCart',
            method: 'post',
            data: {
                phoneId: id,
                quantity: count
            },
            success: function (data) {
                let ob = data
                if (ob.code === 0) {
                    for (let str of ob.message.toString().split('\n')) {
                        $('div[id=phone]').append('<div id="' + id.toString() + '" class="text-danger">' + str + '</div>')
                    }
                }
                else {
                    alert(ob.message)
                    $('#quantity').html("Quantity : " + ob.quantity.toString())
                    $('#price').html("Price : " + ob.totalPrice.toString() + ' $')
                }
            }
        });
    })





</script>