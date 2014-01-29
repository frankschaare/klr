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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

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
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
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
import de.hannit.fsch.common.CSVConstants;
import de.hannit.fsch.common.ContextLogger;
import de.hannit.fsch.common.mitarbeiter.Mitarbeiter;
import de.hannit.fsch.common.organisation.hannit.Organisation;
import de.hannit.fsch.klr.dataservice.DataService;
import de.hannit.fsch.rcp.klr.provider.NavTreeContentProvider;

public class NavPart 
{
private AuswertungsMonat auswertungsMonat = new AuswertungsMonat();
	
@Inject IEventBroker broker;
@Inject DataService dataService;
@Inject @Optional Organisation hannit;
@Inject private EPartService partService;
@Inject @Named(AppConstants.LOGGER) private ContextLogger log;

private TreeMap<Integer, Mitarbeiter> mitarbeiter;	
@Inject @Optional private MApplication application;
private IEclipseContext context;

private TreeViewer treeViewer = null;
private Combo comboMonth = null;
private Combo comboYear = null; 
private Date selectedMonth = null;
private	SimpleDateFormat fMonat = new SimpleDateFormat("MMMM");
private	SimpleDateFormat fMonatJahr = new SimpleDateFormat("MMMM.yyyy");

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
	
	@Inject
	@Optional
	public void handleEvent(@UIEventTopic(AppConstants.ActiveSelections.AUSWERTUNGSMONAT) Date selectedMonth)
	{
	String plugin = this.getClass().getName() + ".handleEvent()";	
	String[] parts = (fMonatJahr.format(selectedMonth).split("\\."));	
	log.info("Fordere Mitarbeiterliste und AZV-Daten für den Monat " + parts[0] + " " + parts[1] + " vom DataService an.", plugin);	
	mitarbeiter = dataService.getAZVMonat(selectedMonth);
	log.confirm("Für den Monat " + parts[0] + " " + parts[1] + " liegen "+ mitarbeiter.size() + " AZV-Meldungen vor.", plugin);
	treeViewer.setInput(mitarbeiter);
	}		

	@PostConstruct
	public void createComposite(Composite parent) 
	{
	String method = this.getClass().getName() + ".createComposite()";
	hannit = dataService.getOrganisation();
	
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
		
		comboMonth = new Combo(top, SWT.READ_ONLY);
		comboMonth.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false, 1, 1));
			for (Date date : hannit.getMonatsBerichte().keySet())
			{
			comboMonth.add(fMonat.format(date));
			comboMonth.setText(comboMonth.getItem(comboMonth.getItemCount()-1));
			}
		comboMonth.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				try
				{
				selectedMonth = fMonatJahr.parse(comboMonth.getItem(comboMonth.getSelectionIndex())  +  "." + comboYear.getItem(comboYear.getSelectionIndex()));
				}
				catch (ParseException ex)
				{
				ex.printStackTrace();
				log.error("ParseException beim Auslesen von NavPart.comboMonth !", this.getClass().getName() + ".widgetSelected(SelectionEvent e)", ex);
				}	
			broker.send(AppConstants.ActiveSelections.AUSWERTUNGSMONAT, selectedMonth);
			}	
		});	
			
		new Label(top, SWT.NONE);
		
		Button btnForward = new Button(top, SWT.FLAT | SWT.ARROW | SWT.RIGHT);
		btnForward.setToolTipText("Zum n\u00E4chsten Monat wechseln. (Nicht verf\u00FCgbar, wenn der aktuelle Monat gleich dem letzten Monat ist)");
		btnForward.setEnabled(auswertungsMonat.lastMonth());;
		
		comboYear = new Combo(top, SWT.READ_ONLY);
		comboYear.setToolTipText("Liste der Verf\u00FCgbaren Berichtsjahre");
		comboYear.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		comboYear.add("2013");
		comboYear.setText("2013");
		// comboYear.add(auswertungsMonat.getActualYear());
		// comboYear.setText(auswertungsMonat.getActualYear());
		
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
		
		treeViewer = new TreeViewer(tabs, SWT.BORDER);
		NavTreeContentProvider cp = new NavTreeContentProvider();
		treeViewer.setContentProvider(cp);
		treeViewer.setLabelProvider(cp);
			
		Tree tree = treeViewer.getTree();
		tbtmAktuell.setControl(tree);
		tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1));
	
			try
			{
			selectedMonth = fMonatJahr.parse(comboMonth.getItem(comboMonth.getSelectionIndex())  +  "." + comboYear.getItem(comboYear.getSelectionIndex()));
			log.info("Fordere Mitarbeiterliste und AZV-Daten für den Monat " + comboMonth.getItem(comboMonth.getSelectionIndex()) + " " + comboYear.getItem(comboYear.getSelectionIndex()) + " vom DataService an.", method);
			}
			catch (ParseException ex)
			{
			ex.printStackTrace();
			log.error("ParseException beim Auslesen von NavPart.comboMonth !", this.getClass().getName() + ".widgetSelected(SelectionEvent e)", ex);
			}
		mitarbeiter = dataService.getAZVMonat(selectedMonth);
		
		log.confirm("Mitarbeiterliste enthält " + mitarbeiter.size() + " Mitarbeiter", method);

		treeViewer.setInput(mitarbeiter);
	
		// application.getContext().declareModifiable(AppConstants.LOG_STACK);
		//application.getContext().runAndTrack(new RunAndTrackExample(application.getContext(), logStack));
		
		MPart mpart = partService.findPart("de.hannit.fsch.rcp.klr.parts.ConsolePart");
		partService.activate(mpart);
	}

	@Focus
	public void setFocus() 
	{
		// tableViewer.getTable().setFocus();
	}
}
