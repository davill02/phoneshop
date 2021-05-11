<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ tag trimDirectiveWhitespaces="true" %>
<%@ attribute name="path" required="true" %>
<%@ attribute name="title" required="true" %>
<tr>
    <td>${title}</td>
    <td>
        <form:input path="${path}" class="form-control" type="text"/>
        <p class="text-danger">
            <form:errors path="${path}"/>
        </p>
    </td>
</tr>