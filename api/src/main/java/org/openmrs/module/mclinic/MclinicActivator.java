package org.openmrs.module.mclinic;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.ModuleActivator;

/**
 * This class contains the logic that is run every time this module is either started or stopped.
 */
public class MclinicActivator implements ModuleActivator {

	private static final Log log = LogFactory.getLog(MclinicActivator.class);

	/**
	 * @see ModuleActivator#willRefreshContext()
	 */
	public void willRefreshContext() {
		log.info("Refreshing mClinic OpenMRS Module");
	}

	/**
	 * @see ModuleActivator#contextRefreshed()
	 */
	public void contextRefreshed() {
		log.info("mClinic OpenMRS Module refreshed");
	}

	/**
	 * @see ModuleActivator#willStart()
	 */
	public void willStart() {
		log.info("Starting mClinic OpenMRS Module");
	}

	/**
	 * @see ModuleActivator#started()
	 */
	public void started() {
		log.info("mClinic OpenMRS Module started");
	}

	/**
	 * @see ModuleActivator#willStop()
	 */
	public void willStop() {
		log.info("Stopping mClinic OpenMRS Module");
	}

	/**
	 * @see ModuleActivator#stopped()
	 */
	public void stopped() {
		log.info("mClinic OpenMRS Module stopped");
	}
}