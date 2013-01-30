<%@ include file="/WEB-INF/template/include.jsp" %>

<openmrs:require privilege="Manage Downloadable Forms" otherwise="/login.htm" redirect="/module/mclinic/downloadableXforms.list" />
	
<%@ include file="/WEB-INF/template/header.jsp" %>
<%@ include file="template/localHeader.jsp" %>

<script type="text/javascript">
	$j(document).ready(function() {
		$j('#addFormPopup').dialog({
			autoOpen: false,
			modal: true,
			title: '<spring:message code="mclinic.downloadableXforms.add" javaScriptEscape="true"/>',
			width: '90%'
		});
				
		$j('#addFormButton').click(function() {
			$j('#addFormPopup').dialog('open');
		});

	});
</script>

<h2><spring:message code="mclinic.downloadableXforms" /></h2>

<div id="buttonPanel">
	<div style="float:left">
		<input type="button" id="addFormButton" value='<spring:message code="mclinic.downloadableXforms.add"/>'/>
		<div id="addFormPopup">
			<br/>
			<form id="resourceAddForm" name="uploader" method="post" enctype="multipart/form-data">
				<spring:message code="mclinic.downloadableXforms.add.program"/>
				<select id="program" name="program">
					<option value="0">Select Program</option>
					<c:forEach items="${programs}" var="program">
						<option value="${program.id}">${program.name}</option>
					</c:forEach>
				</select>
				<br/>
				<br/>
				<b class="boxHeader"><spring:message code="mclinic.downloadableXforms.add.filesystem"/></b>
				<div class="box">
					<input type="file" name="resourceFile" size="40"  />
					<input type="submit" name="action" value='<spring:message code="mclinic.downloadableXforms.upload"/>'
						onClick="return (checkType() && checkProgram());"/>
				</div>
				<br/>
	
				<b class="boxHeader"><spring:message code="mclinic.downloadableXforms.add.database"/></b>
				<div class="box">
					<spring:message code="mclinic.downloadableXforms.select"/>
					<select id="form" name="form">
						<c:forEach items="${forms}" var="form">
							<option value="${form.id}">${form.name}</option>
						</c:forEach>
					</select>
					<input type="submit" name="action" value='<spring:message code="mclinic.downloadableXforms.loadXform"/>'
						onClick="return checkProgram();"/>
				</div>
				<br/>
			</form>
		</div>
	</div>
	<div style="clear:both">&nbsp;</div>
</div>	

<c:if test="${fn:length(downloadableXforms) > 0}">
	<b class="boxHeader"><spring:message code="mclinic.downloadableXforms"/></b>
	<div class="box">
		<form method="post" name="resourcesForm" onSubmit="return (checkSelected(this) && confirmDelete())">
			<table cellpadding="2" cellspacing="0" width="98%">
				<tr>
					<th></th>
					<th><spring:message code="mclinic.downloadableXforms.formId"/></th>
					<th><spring:message code="general.name"/></th>
	                      <th><spring:message code="mclinic.programConfiguration.title"/></th>
					<th><spring:message code="mclinic.downloadableXforms.xform.uploadedOn"/></th>
				</tr>
				<c:forEach var="var" items="${downloadableXforms}" varStatus="status">
					<tr class="<c:choose><c:when test="${status.index % 2 == 0}">oddRow</c:when><c:otherwise>evenRow</c:otherwise></c:choose>">
						<td><input type="checkbox" name="mclinicXformId" value="${var.mclinicXformId}" onclick="clearError('mclinicXformId')"/></td>
						<td valign="top" style="white-space: nowrap">${var.xformId}</td>
						<td valign="top" style="white-space: nowrap">${var.xformName}</td>
	                          <td valign="top" style="white-space: nowrap">${var.program.name}</td>
						<td valign="top">${var.xformMeta}</td>
					</tr>
				</c:forEach>
			</table>
			<input type="submit" name="action" value="<spring:message code='mclinic.downloadableXforms.delete'/>">
			<span class="error" id="mclinicXformIdError"><spring:message code='mclinic.downloadableXforms.delete.empty'/></span>
		</form>
	</div>
</c:if>

<c:if test="${fn:length(downloadableXforms) == 0}">
	<b class="boxHeader"><spring:message code="mclinic.downloadableXforms"/></b>
	<div class="box">
		<i> &nbsp; <spring:message code="mclinic.downloadableXforms.noUploaded"/></i><br/>
	</div>
</c:if>

<br style="clear:both"/>

<div>
	<b class="boxHeader"><spring:message code="mclinic.downloadableXforms.help" /></b>
	<div class="box">
		<ul>
			<li><i><spring:message code="mclinic.downloadableXforms.help.main"/></i>
		</ul>
	</div>
</div>

<script type="text/javascript" language="JavaScript">
	clearError("mclinicXformId");

	function checkSelected(form) {
	    //get total number of CheckBoxes in form
	    var formLength = form.length;
	    var chkBoxCount = 0;
	    for (i=0;i<formLength;i++){
			if (form[i].type == 'checkbox') 
				chkBoxCount++;
		}

	    if (chkBoxCount==1) { //we dont have an array
	    	if (form.mclinicXformId.checked) 
	        {
	            //it's checked so return true and exit
	            return true;
	        }
	    }else {
		    //loop through each CheckBox
		    for (var i = 0; i < chkBoxCount; i++) 
		    {
		        if (form.mclinicXformId[i].checked) 
		        {
		            //it's checked so return true and exit
		            return true;
		        }
		    }
	    }
	    document.getElementById("mclinicXformIdError").style.display = "";
	    return false;
	}

	function clearError(errorName) {
		document.getElementById(errorName + "Error").style.display = "none";
	}
	
	function checkType() {
		var extension = new Array();
		
		var fieldvalue = document.uploader.resourceFile.value;
		
		extension[0] = ".xml";
		extension[1] = ".xhtml";
		
		var thisext = fieldvalue.substr(fieldvalue.lastIndexOf('.'));
		for(var i = 0; i < extension.length; i++) {
			if(thisext == extension[i]) { 
				return true; 
			}
		}
		alert('<spring:message code="mclinic.downloadableXforms.upload.invalidType"/>');
		return false;
	}
	
	function checkProgram() {
		var program = document.getElementById("program").value
		if (program==0) {
            alert('<spring:message code="mclinic.downloadableXforms.add.program" javaScriptEscape="true"/>');
			return false;
		}
		return true;
	}
	
	function confirmDelete() {
		if (confirm('<spring:message code="mclinic.downloadableXforms.deleteWarning" javaScriptEscape="true"/>')) { 
			return true;
		} else {
			return false;
		}
	}
</script>
<%@ include file="/WEB-INF/template/footer.jsp" %>