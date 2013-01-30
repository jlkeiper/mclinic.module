package org.openmrs.module.mclinic.web.controller.io;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.mclinic.api.utils.MclinicConstants;
import org.openmrs.module.mclinic.api.utils.MclinicUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Provides file upload services to the module
 * 
 * @author Samuel Mbugua
 */

@Controller
@RequestMapping({"/module/mclinic/upload/xform"})
public class UploadXformController {
	private static Log log = LogFactory.getLog(UploadXformController.class);

	@RequestMapping(method=RequestMethod.POST)
	protected void postFile(HttpServletRequest request, HttpServletResponse response) {
		log.debug("Upload URL: " + request.getRequestURL());
		String username = request.getParameter("uname");
		String password = request.getParameter("pw");
		response.setHeader(MclinicConstants.HTTP_LOCATION_HEADER_NAME, MclinicConstants.HTTP_LOCATION_HEADER_VALUE);
		if (MclinicUtil.authenticate(username, password))
			doSave(request,response);
		else {
			log.warn(this.getClass().getName() + "Error authenticating");
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		}
	}

	/**
	 * Receives multipart files and saves them as individual file items on the file system
	 * directory specified by {@link MclinicUtil#getMclinicDropDir()} 
	 */
	private void doSave(HttpServletRequest request, HttpServletResponse response) {

		if (ServletFileUpload.isMultipartContent(request)) {
			ServletFileUpload servletFileUpload = new ServletFileUpload(new DiskFileItemFactory());
			List<?> fileItemsList = null;
			try {
				fileItemsList = servletFileUpload.parseRequest(request);
			} catch (FileUploadException e) {
				e.printStackTrace();
			}

			String optionalFileName = "";
			FileItem fileItem = null;
			Iterator<?> iterator = fileItemsList.iterator();
			
			while (iterator.hasNext()) {
				FileItem fileItemTemp = (FileItem) iterator.next();
				if (fileItemTemp.isFormField()) {
					if (fileItemTemp.getFieldName().equals("filename"))
						optionalFileName = fileItemTemp.getString();
				} else
					fileItem = fileItemTemp;
				saveFile(fileItem, optionalFileName);
			}
		}
	}
	
	private static void saveFile(FileItem fileItem, String optionalFileName) {
		if (fileItem != null) {
			String fileName = fileItem.getName();

			/* Save the uploaded file if its size is greater than 0. */
			if (fileItem.getSize() > 0) {
				if (optionalFileName.trim().equals(""))
					fileName = FilenameUtils.getName(fileName);
				else
					fileName = optionalFileName;

				String dirName = MclinicUtil.getMclinicDropDir().getAbsolutePath() + File.separatorChar;

				File saveTo = new File(dirName + fileName);
				try {
					fileItem.write(saveTo);

				} catch (Exception e) {

				}
			}
		}
	}
}