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
package de.hannit.fsch.rcp.klr.parts;

import java.util.ArrayList;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Tree;
import org.osgi.service.event.Event;

import de.hannit.fsch.common.LogMessage;
import de.hannit.fsch.common.mitarbeiter.Mitarbeiter;
import de.hannit.fsch.klr.dataservice.DataService;
import de.hannit.fsch.rcp.klr.constants.Topics;
import de.hannit.fsch.rcp.klr.provider.NavTreeContentProvider;

public class NavPart 
{
@Inject
IEventBroker broker;

@Inject
DataService dataService;

	private Label label;
	private TableViewer tableViewer;
	private ArrayList<Mitarbeiter> mitarbeiter;	

	/*
	 * Beispiel für Registrierung an Eclipse Framework Events
	 * Bei dem Event handelt es sich um einen org.osgi.service.event.Event !
	 * 
	 */
	@Inject
	@Optional
	public void partActivation(@UIEventTopic(UIEvents.UILifeCycle.ACTIVATE) Event event, MApplication application) 
	{
	// TODO: org.osgi.service.event.Event sind noch nicht in den Product Dependencies	
	// Den aktiven Part ausgeben:	
	MPart activePart = (MPart) event.getProperty(UIEvents.EventTags.ELEMENT);
	MWindow main = application.getChildren().get(0);
	main.setLabel("HannIT KLR - " + dataService.getConnectionInfo());
	// Den Eclipse Context ausgeben:
	IEclipseContext context = application.getContext();
		// Eine eigene Variable setzen:
		if (activePart != null) 
		{
		context.set("myactivePartId", activePart.getElementId());
		}
		
	System.out.println(activePart.getElementId());
	} 	
	
	@PostConstruct
	public void createComposite(Composite parent) 
	{
	parent.setLayout(new GridLayout(3, true));
	
	TreeViewer treeViewer = new TreeViewer(parent, SWT.BORDER);
	NavTreeContentProvider cp = new NavTreeContentProvider();
	treeViewer.setContentProvider(cp);
	treeViewer.setLabelProvider(cp);
		
	Tree tree = treeViewer.getTree();
	tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1));

	broker.send(Topics.LOGGING, new LogMessage(IStatus.INFO, this.getClass().getName(), "Fordere Mitarbeiterliste vom DataService an."));
	mitarbeiter = dataService.getMitarbeiter();
	broker.send(Topics.LOGGING, new LogMessage(IStatus.INFO, this.getClass().getName(), "Mitarbeiterliste enthält " + mitarbeiter.size() + " Mitarbeiter"));
	treeViewer.setInput(mitarbeiter);
	}

	@Focus
	public void setFocus() 
	{
		// tableViewer.getTable().setFocus();
	}
}
