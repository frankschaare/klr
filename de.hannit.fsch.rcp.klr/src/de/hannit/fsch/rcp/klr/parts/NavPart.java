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
import java.util.TreeMap;

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
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Tree;
import org.osgi.service.event.Event;

import de.hannit.fsch.common.AppConstants;
import de.hannit.fsch.common.AuswertungsMonat;
import de.hannit.fsch.common.LogMessage;
import de.hannit.fsch.common.RunAndTrackExample;
import de.hannit.fsch.common.mitarbeiter.Mitarbeiter;
import de.hannit.fsch.klr.dataservice.DataService;
import de.hannit.fsch.rcp.klr.provider.NavTreeContentProvider;

public class NavPart 
{
private AuswertungsMonat auswertungsMonat = new AuswertungsMonat();
	
@Inject
IEventBroker broker;

@Inject
DataService dataService;

@Inject
private EPartService partService;

	private Label label;
	private TableViewer tableViewer;
	private ArrayList<Mitarbeiter> mitarbeiter;	
	@Inject @Optional private MApplication application;
	private IEclipseContext context;
	private TreeMap<Integer, LogMessage> logStack = new TreeMap<Integer, LogMessage>();	

	/*
	 * Beispiel für Registrierung an Eclipse Framework Events
	 * Bei dem Event handelt es sich um einen org.osgi.service.event.Event !
	 * 
	 */
	@Inject
	@Optional
	public void partActivation(@UIEventTopic(UIEvents.UILifeCycle.ACTIVATE) Event event) 
	{
	// TODO: org.osgi.service.event.Event sind noch nicht in den Product Dependencies	
	// Den aktiven Part ausgeben:	
	MPart activePart = (MPart) event.getProperty(UIEvents.EventTags.ELEMENT);
	MWindow main = application.getChildren().get(0);
	main.setLabel("HannIT KLR - " + dataService.getConnectionInfo());
	// Den Eclipse Context ausgeben:
	context = application.getContext();
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
		parent.setLayout(new GridLayout(1, false));
		
		Composite top = new Composite(parent, SWT.NONE);
		GridLayout gl_top = new GridLayout(6, false);
		gl_top.verticalSpacing = 0;
		gl_top.marginWidth = 0;
		gl_top.marginHeight = 0;
		top.setLayout(gl_top);
		top.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Button btnBack = new Button(top, SWT.FLAT | SWT.ARROW | SWT.LEFT);
		btnBack.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		btnBack.setToolTipText("Zum Vormonat wechseln");
		btnBack.setText("<<");
		new Label(top, SWT.NONE);
		
		Label lblMonat = new Label(top, SWT.CENTER);
		lblMonat.setToolTipText("Mitarbeiterdaten f\u00FCr diesen Abrechnungsmonat darstellen");
		lblMonat.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		lblMonat.setText(auswertungsMonat.getActualMonth());
		new Label(top, SWT.NONE);
		
		Button btnForward = new Button(top, SWT.FLAT | SWT.ARROW | SWT.RIGHT);
		btnForward.setToolTipText("Zum n\u00E4chsten Monat wechseln. (Nicht verf\u00FCgbar, wenn der aktuelle Monat gleich dem letzten Monat ist)");
		btnForward.setEnabled(auswertungsMonat.lastMonth());;
		
		Combo comboYear = new Combo(top, SWT.READ_ONLY);
		comboYear.setToolTipText("Liste der Verf\u00FCgbaren Berichtsjahre");
		comboYear.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		comboYear.add(auswertungsMonat.getActualYear());
		comboYear.setText(auswertungsMonat.getActualYear());
		
		Composite bottom = new Composite(parent, SWT.NONE);
		GridLayout gl_bottom = new GridLayout(1, false);
		gl_bottom.verticalSpacing = 0;
		gl_bottom.marginWidth = 0;
		gl_bottom.marginHeight = 0;
		bottom.setLayout(gl_bottom);
		bottom.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		TabFolder tabs = new TabFolder(bottom, SWT.NONE);
		tabs.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		TabItem tbtmAktuell = new TabItem(tabs, SWT.NONE);
		tbtmAktuell.setToolTipText("Alle Mitarbeiter, die f\u00FCr den ausgew\u00E4hlten Monat Gehalt bezogen haben");
		tbtmAktuell.setText("Aktuell");
		
		TabItem tabItem_1 = new TabItem(tabs, SWT.NONE);
		tabItem_1.setText("New Item");
		
		TreeViewer treeViewer = new TreeViewer(tabs, SWT.BORDER);
		NavTreeContentProvider cp = new NavTreeContentProvider();
		treeViewer.setContentProvider(cp);
		treeViewer.setLabelProvider(cp);
			
		Tree tree = treeViewer.getTree();
		tbtmAktuell.setControl(tree);
		tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1));
	
		logStack.put(logStack.size(), new LogMessage(IStatus.INFO, this.getClass().getName(), "Fordere Mitarbeiterliste vom DataService an."));
		mitarbeiter = dataService.getMitarbeiter();
		logStack.put(logStack.size(), new LogMessage(IStatus.INFO, this.getClass().getName(), "Mitarbeiterliste enthält " + mitarbeiter.size() + " Mitarbeiter"));
		treeViewer.setInput(mitarbeiter);
	
		// application.getContext().declareModifiable(AppConstants.LOG_STACK);
		application.getContext().runAndTrack(new RunAndTrackExample(application.getContext(), logStack));
		
		application.getContext().modify(AppConstants.LOG_STACK, logStack);
		MPart mpart = partService.findPart("de.hannit.fsch.rcp.klr.parts.ConsolePart");
		mpart.setVisible(true);
	}

	@Focus
	public void setFocus() 
	{
		// tableViewer.getTable().setFocus();
	}
}
