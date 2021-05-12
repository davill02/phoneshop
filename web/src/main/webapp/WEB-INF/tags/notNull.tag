<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ tag trimDirectiveWhitespaces="true" %>
<%@ attribute name="name" required="true" %>
<%@ attribute name="value" required="true" type="java.lang.Object" %>
<%@attribute name="additionText" %>
<c:if test="${value != null}">
    <tr>
        <td>${name}</td>
        <td>${value}${" "}${additionText}</td>
    </tr>
</c:if>