<%@ include file="/WEB-INF/template/include.jsp" %>

<openmrs:require privilege="View Mclinic Properties" otherwise="/login.htm" redirect="/module/mclinic/syncLog.list"/>

<%@ include file="/WEB-INF/template/header.jsp" %>
<%@ include file="template/localHeader.jsp"%>

<h2><spring:message code="mclinic.sync.title"/></h2>
<div class="box">
<form method="post">
	&nbsp;&nbsp;&nbsp; <spring:message code="mclinic.sync.selectSyncDate"/>
	<select id="logDate" name="logDate">
		<c:forEach items="${files}" var="logFile" varStatus="varStatus">
				<option>${logFile}</option>
		</c:forEach>
	</select>
	&nbsp;&nbsp;&nbsp;
	<input type="submit" name="action" value="Get Sync Log">
</form>
</div>
<br/>

<c:set var="logSize" value="${fn:length(logs)}" />

<c:choose>
	<c:when test="${logSize < 1}">
		<br/>
		<i>&nbsp;&nbsp;&nbsp;(<spring:message code="mclinic.sync.empty" arguments="${logDate}"/>)</i>
		<br/>
	</c:when>
	<c:otherwise>
		<b class="boxHeader"><spring:message code="mclinic.sync.log"/>${logDate}</b>
		<table cellpadding="4" cellspacing="0" border="0" class="box">
			<tr>
				<th style="white-space: nowrap"><spring:message code="mclinic.sync.provider" /></th>
				<th><spring:message code="mclinic.sync.syncDate" /></th>
				<th><spring:message code="mclinic.sync.syncDevice" /></th>
				<th><spring:message code="mclinic.sync.fileName" /></th>
				<th><spring:message code="mclinic.sync.fileSize" /></th>
			</tr>
			<c:forEach items="${logs}" var="log" varStatus="varStatus">
				<tr class="<c:choose><c:when test="${varStatus.index % 2 == 0}">evenRow</c:when><c:otherwise>oddRow</c:otherwise></c:choose>">
					<td style="white-space: nowrap">${log.providerId}</td>
					<td><openmrs:formatDate date="${log.syncDate}" type="long" /></td>
					<td>${log.deviceId}</td>
					<td>${log.fileName}</td>
					<td>${log.fileSize}</td>
				</tr>
			</c:forEach>
		</table>
		<br/>
		&nbsp;&nbsp;&nbsp;<spring:message code="mclinic.sync.info" />
	</c:otherwise>
</c:choose>
<br/>
<%@ include file="/WEB-INF/template/footer.jsp"%>