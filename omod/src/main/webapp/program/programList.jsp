<%@ include file="/WEB-INF/template/include.jsp" %>
<%@ include file="/WEB-INF/template/header.jsp" %>

<openmrs:require privilege="Manage Connector" otherwise="/login.htm" redirect="/module/mclinic/program/programList.list"/>

<%@ include file="../template/localHeader.jsp" %>

<c:if test="${message != null}">
	<div id="message"><spring:message code="${message}" text="${message}"/></div>
</c:if>

<script type="text/javascript">

	var $j = jQuery.noConflict();

	$j(document).ready(function () {
		$j(".delete").click (function () {
			if  (confirm("<spring:message code='mclinic.programConfiguration.confirmDelete' />")) {
				var row = $j(this).parent().parent();
				var uuid = row.attr("uuid");
				jQuery.ajax({
					url:"deleteConfiguration.form",
					type: "GET",
					dataType:"json",
					data:{
						"uuid": uuid
					},
					success:function (data) {
						if (data)
							row.hide();
					}
				})
			}
		});
	});
</script>

<style>
	#programList {
		border: 1px solid black;
		clear: both;
		margin: 5px auto;
		width: 100%;
		padding: 3px 3px;
	}

	#programList thead th {
		background-color: #FFFFFF;
		border-bottom: 1px solid black;
		font-weight: bold;
	}

	#programList tr.even {
		background-color: #E2E4FF;
	}

	#programList tr.odd {
		background-color: #D3D6FF;
	}
</style>

<div>
	<a href="programConfiguration.form"><spring:message code="mclinic.programConfiguration.create" /></a>
	<table id="programList">
		<thead>
		<tr>
			<th>&nbsp;</th>
			<th>&nbsp;</th>
			<th><spring:message code="mclinic.programConfiguration.id" /></th>
			<th><spring:message code="mclinic.programConfiguration.name" /></th>
			<th><spring:message code="mclinic.programConfiguration.description" /></th>
		</tr>
		</thead>
		<tbody>
		<c:forEach items="${configurations}" var="configuration" varStatus="counterRow">
			<tr class="<c:choose><c:when test="${counterRow.index % 2 == 0}">even</c:when><c:otherwise>odd</c:otherwise></c:choose>" uuid="${configuration.uuid}">
				<td><a href="manageProgram.form?uuid=${configuration.uuid}">
						<img src="${pageContext.request.contextPath}/images/edit.gif" 
								title='<spring:message code="general.edit"/>' border="0" align="top"/></a></td>
				<td><a href="#" class="delete">
						<img src="${pageContext.request.contextPath}/images/trash.gif" 
							title='<spring:message code="general.delete"/>' border="0" align="top"/></a></td>
				<td>${configuration.id}</td>
				<td>${configuration.name}</td>
				<td>${configuration.description}</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
</div>

<%@ include file="/WEB-INF/template/footer.jsp" %>
