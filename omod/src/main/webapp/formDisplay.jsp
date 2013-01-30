<%@ include file="/WEB-INF/template/include.jsp" %>
<%@ include file="/WEB-INF/template/headerMinimal.jsp" %>

<openmrs:require privilege="View Xform" otherwise="/login.htm" />

<c:choose>
	<c:when test="${errorObject == null}">
		<br/>
		<i>(<spring:message code="mclinic.resolveErrors.null"/>)</i>
		<br/>
	</c:when>
	<c:otherwise>
		<textarea rows="40" cols="120" readonly="true">${errorObject.formName}</textarea>
	</c:otherwise>
</c:choose>