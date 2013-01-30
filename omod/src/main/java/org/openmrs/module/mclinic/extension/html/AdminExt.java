package org.openmrs.module.mclinic.extension.html;

import org.openmrs.module.Extension;
import org.openmrs.module.web.extension.AdministrationSectionExt;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * This class defines the links that will appear on the administration page under the
 * "mclinic.title" heading.
 */
public class AdminExt extends AdministrationSectionExt {

	/**
	 * @see AdministrationSectionExt#getMediaType()
	 */
	public Extension.MEDIA_TYPE getMediaType() {
		return Extension.MEDIA_TYPE.html;
	}

	/**
	 * @see AdministrationSectionExt#getTitle()
	 */
	public String getTitle() {
		return "mclinic.title";
	}

	/**
	 * @see AdministrationSectionExt#getLinks()
	 */
	public Map<String, String> getLinks() {
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		map.put("/module/mclinic/program/programList.form", "mclinic.programConfiguration.manage");
		map.put("/module/mclinic/reporting/manageProperty.form", "mclinic.cohortDefinition.manage");
		map.put("module/mclinic/downloadableXforms.list", "mclinic.downloadableXforms");
		map.put("module/mclinic/resolveErrors.list", "mclinic.resolveErrors.title");
		map.put("module/mclinic/processingWarning.list", "mclinic.processingWarning.title");
		map.put("module/mclinic/syncLog.list", "mclinic.sync.title");
		return map;
	}

}
