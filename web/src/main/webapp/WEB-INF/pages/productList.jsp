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
    <tags:login/>
</nav>


<div class="container">
    <div class="mb-3 row"></div>
    <form class="d-flex">
        <input class="form-control me-2" name="query" value="${param.query}" type="search" placeholder="Search"
               aria-label="Search">
        <input type="hidden" name="sort" value="${param.sort}">
        <input type="hidden" name="order" value="${param.order}">
        <button class="btn btn-outline-success" type="submit">Search</button>
    </form>
</div>


<div class="container">
    <table class="table table-bordered border-primary align-middle caption-top">
        <caption>Found ${count} phones.</caption>
        <thead>
        <tr>
            <td>Image</td>
            <td>Brand
                <tags:sortedLink sort="BRAND" order="ASC">asc</tags:sortedLink>
                <tags:sortedLink sort="BRAND" order="DESC">desc</tags:sortedLink>
            </td>
            <td>Model
                <tags:sortedLink sort="MODEL" order="ASC">asc</tags:sortedLink>
                <tags:sortedLink sort="MODEL" order="DESC">desc</tags:sortedLink>
            </td>
            <td>Colors</td>
            <td>Display size
                <tags:sortedLink sort="displaySizeInches" order="ASC">asc</tags:sortedLink>
                <tags:sortedLink sort="displaySizeInches" order="DESC">desc</tags:sortedLink>
            </td>
            <td>Price
                <tags:sortedLink sort="PRICE" order="ASC">asc</tags:sortedLink>
                <tags:sortedLink sort="PRICE" order="DESC">desc</tags:sortedLink>
            </td>
            <td>
                Quantity
            </td>
            <td>
                Add to
            </td>
        </tr>
        </thead>
        <c:forEach var="phone" items="${phones}">
            <tr>
                <td>
                    <img height="150px" width="150px" class="img-thumbnail"
                         src="https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/${phone.imageUrl}">
                </td>
                <td>${phone.brand}</td>
                <td><a href="${pageContext.servletContext.contextPath}/productDetails/${phone.id}">${phone.model}</a>
                </td>
                <td><c:forEach var="color" items="${phone.colors}">
                    ${color.code}
                </c:forEach></td>
                <td>${phone.displaySizeInches}"</td>
                <td>$ ${phone.price}</td>
                <td id="${phone.id}"><input value="1" class="form-control" id="${phone.id}" type="text"/></td>
                <td>
                    <button class="btn btn-outline-success" id="${phone.id}">Add to</button>
                </td>
            </tr>
        </c:forEach>
    </table>
</div>
<nav aria-label="Page navigation example">
    <ul id="pag-list" class="pagination justify-content-center">
        <li class="page-item active"><a class="page-link" href="#">1</a></li>
        <li class="page-item"><a class="page-link" href="#">2</a></li>
        <li class="page-item"><a class="page-link" href="#">3</a></li>
    </ul>
</nav>
<script type="text/javascript">
 $('.page-item').remove()
    let tokens = $('caption').text().split(' ')
    let countPages = Math.ceil(parseInt(tokens[1]) / 10)
    let paramsString = document.location.search;
    let searchParams = new URLSearchParams(paramsString);
    let pageNum = parseInt(searchParams.get('page'))
    if (isNaN(pageNum)) {
        pageNum = 1
    }

    let first = 1
    let last = countPages
    if (pageNum > 10) {
        first = pageNum - 10
    }

    if (pageNum < countPages - 10) {

        last = pageNum + 10

    }


    for (let index = first; index < last; index++) {
         if (index != pageNum) {
            $('#pag-list').append('<li id="page-item" class="page-item"><a class="page-link" href="' + addOrUpdateUrlParam('page', index) + '">' + index.toString() + '</a></li>')
        } else {
            $('#pag-list').append('<li id="page-item" class="page-item active"><a class="page-link" href="' + addOrUpdateUrlParam('page', index) + '">' + index.toString() + '</a></li>')
        }
    }

    function addOrUpdateUrlParam(name, value) {
        var href = window.location.href;
        var regex = new RegExp("[&\\?]" + name + "=");
        if (regex.test(href)) {
            regex = new RegExp("([&\\?])" + name + "=\\d+");
            return href.replace(regex, "$1" + name + "=" + value);
        }
        else {
            if (href.indexOf("?") > -1)
                return href + "&" + name + "=" + value;
            else
                return href + "?" + name + "=" + value;
        }
    }
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
                        $('td[id=' + id.toString() + ']').append('<div id="' + id.toString() + '" class="text-danger">' + str + '</div>')
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