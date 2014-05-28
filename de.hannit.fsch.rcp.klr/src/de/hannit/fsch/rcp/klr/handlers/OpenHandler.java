/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package de.hannit.fsch.rcp.klr.handlers;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

import de.hannit.fsch.common.AppConstants;
import de.hannit.fsch.common.ContextLogger;
import de.hannit.fsch.rcp.klr.csv.CSVDatei;

public class OpenHandler 
{
@Inject @Named(AppConstants.LOGGER) private ContextLogger log;
@Inject IEventBroker broker;

@Execute
	public void execute (@Named(IServiceConstants.ACTIVE_SHELL) Shell shell)
	{
	FileDialog dialog = new FileDialog(shell);
	dialog.setFilterExtensions(new String[] {"*.csv","*.txt", "*.*"});
	String path = dialog.open();
	
		if (path != null) 
		{
		CSVDatei csv = new CSVDatei(path);
		csv.hasHeader(true);
		csv.setDelimiter("\\|");
		csv.read();
		
		broker.send("CSV/Daten", csv);
		log.info("CSV-Datei: " + path + " wurde an den Event Broker gesendet", this.getClass().getName() + "execute()");
		}
	}
}
