 
package de.hannit.fsch.rcp.klr.parts;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import de.hannit.fsch.common.AppConstants;
import de.hannit.fsch.common.CSVDatei;
import de.hannit.fsch.common.ContextLogger;
import de.hannit.fsch.rcp.klr.csv.CSV01Datei;

public class CSV01Part 
{
@Inject IEventBroker broker;
@Inject @Named(AppConstants.LOGGER) private ContextLogger log;
@Inject @Named(AppConstants.CONTEXT_CSV01) CSV01Datei csv;

private Group grpBerichtsmonat = null;
private Label lblDateiName;

private Group grpDateiInhalt;
private TableViewer csvViever;
private Table csvTable;
private TableColumn zelle;
private TableViewerColumn tableViewerColumn;


	@Inject
	public CSV01Part() 
	{
	}
	
	@PostConstruct
	public void createComposite(Composite parent) 
	{
	parent.setLayout(new GridLayout(1, false));
	
	grpBerichtsmonat = new Group(parent, SWT.NONE);
	grpBerichtsmonat.setLayout(new GridLayout(2, true));
	grpBerichtsmonat.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
	grpBerichtsmonat.setText("Berichtsmonat");
	
	lblDateiName = new Label(grpBerichtsmonat, SWT.NONE);
	lblDateiName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
	lblDateiName.setText("Dateiname:");
		
	grpDateiInhalt = new Group(parent, SWT.NONE);
	grpDateiInhalt.setLayout(new GridLayout(1, false));
	grpDateiInhalt.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
	grpDateiInhalt.setText("Dateiinhalt");
	
	csvViever = new TableViewer(grpDateiInhalt, SWT.BORDER | SWT.FULL_SELECTION);
	csvTable = csvViever.getTable();
	csvTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
	csvViever.setContentProvider(new ArrayContentProvider());
	
		if (csv != null)
		{
		createColumns();	
		csvViever.setLabelProvider(csv);
		csvViever.setInput((csv.getCompleteContent() != null) ? csv.getCompleteContent().toArray() : csv.getContent().toArray());
		lblDateiName.setText("Dateiname:" + csv.getPath());
		}
		
	}	
	
	/*
	 * Tabellenzellen werden dynamisch erstellt
	 */
	private void createColumns()
	{
	String[] line = csv.getContent().get(0).split(CSVDatei.DEFAULT_DELIMITER);
	
		for (int i = 0; i < line.length; i++)
		{
		tableViewerColumn = new TableViewerColumn(csvViever, SWT.RIGHT, i);
		zelle = tableViewerColumn.getColumn();
		zelle.setWidth(100);
		zelle.setText("Zelle" + i);			
		}
		
	}
	
	@Focus
	public void onFocus() 
	{
		//TODO Your code here
	}


}