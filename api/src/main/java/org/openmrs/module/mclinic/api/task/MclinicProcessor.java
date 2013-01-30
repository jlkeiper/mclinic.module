package org.openmrs.module.mclinic.api.task;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * @author Samuel Mbugua
 */

public class MclinicProcessor {

	private static final Log log = LogFactory.getLog(MclinicProcessor.class);
	private MclinicSplitProcessor splitProcessor = null;
	private MclinicRawFormsProcessor queueProcessor = null;
	private MclinicQueueProcessor uploadProcessor = null;

	public void processXforms() {
		log.debug("mclinic: Processing xforms");
				
			if (queueProcessor == null)
				queueProcessor = new MclinicRawFormsProcessor();
			queueProcessor.processRawFormsQueue();
	
			// Split submitted xforms
			if (splitProcessor == null)
				splitProcessor = new MclinicSplitProcessor();
			splitProcessor.splitForms();
			
			// Upload patients to xforms module for processing
			if (uploadProcessor == null)
				uploadProcessor = new MclinicQueueProcessor();
			uploadProcessor.processMclinicQueue();
	
		}
	}
