<spring:htmlEscape defaultHtmlEscape="true"/>
<ul id="menu">
	<li class="first">
		<a href="${pageContext.request.contextPath}/admin"><spring:message code="admin.title.short"/></a>
	</li>
	
    <li
    <c:if test='<%= request.getRequestURI().contains("/mclinic/program") %>'>class="active"</c:if>>
    <a href="${pageContext.request.contextPath}/module/mclinic/program/programList.form">
        <spring:message code="mclinic.programConfiguration.manage"/>
    </a>
    </li>

    <li
    <c:if test='<%= request.getRequestURI().contains("/mclinic/reporting") %>'>class="active"</c:if>>
    <a href="${pageContext.request.contextPath}/module/mclinic/reporting/manageProperty.form">
        <spring:message code="mclinic.cohortDefinition.manage"/>
    </a>
    </li>
	
	<openmrs:hasPrivilege privilege="Manage Downloadable Forms">
		<li <c:if test='<%= request.getRequestURI().contains("mclinic/downloadableXforms") %>'>class="active"</c:if>>
			<a href="${pageContext.request.contextPath}/module/mclinic/downloadableXforms.list">
				<spring:message code="mclinic.downloadableXforms"/>
			</a>
		</li>
	</openmrs:hasPrivilege>
	
	<openmrs:hasPrivilege privilege="View Xforms Errors">
		<li <c:if test='<%= request.getRequestURI().contains("mclinic/resolveError") %>'>class="active"</c:if>
			<c:if test='<%= request.getRequestURI().contains("mclinic/editXform") %>'>class="active"</c:if>>
			<a href="${pageContext.request.contextPath}/module/mclinic/resolveErrors.list">
				<spring:message code="mclinic.resolveErrors.title"/>
			</a>
		</li>
	</openmrs:hasPrivilege>
	
	<openmrs:hasPrivilege privilege="View Xforms Errors">
		<li <c:if test='<%= request.getRequestURI().contains("mclinic/processingWarning") %>'>class="active"</c:if>>
			<a href="${pageContext.request.contextPath}/module/mclinic/processingWarning.list">
				<spring:message code="mclinic.processingWarning.title"/>
			</a>
		</li>
	</openmrs:hasPrivilege>
	
	<openmrs:hasPrivilege privilege="View Mclinic Properties">
		<li <c:if test='<%= request.getRequestURI().contains("mclinic/syncLog") %>'>class="active"</c:if>>
			<a href="${pageContext.request.contextPath}/module/mclinic/syncLog.list">
				<spring:message code="mclinic.sync.title"/>
			</a>
		</li>
	</openmrs:hasPrivilege>

    <openmrs:hasPrivilege privilege="View Mclinic Properties">
        <li <c:if test='<%= request.getRequestURI().contains("mclinic/properties") %>'>class="active"</c:if>>
        <a href="${pageContext.request.contextPath}/module/mclinic/propertiesPage.form">
            <spring:message code="mclinic.properties"/>
        </a>
        </li>
    </openmrs:hasPrivilege>
</ul>