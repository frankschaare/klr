 
package de.hannit.fsch.rcp.klr.parts;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import de.hannit.fsch.common.CSVDatei;
import de.hannit.fsch.rcp.klr.provider.CSVLabelProvider;

public class CSVDetailsPart 
{
@Inject
private Logger log;

@Inject
IEventBroker broker;

private Label label;
private TableViewer tableViewer;	

	@Inject
	public CSVDetailsPart() 
	{
		//TODO Your code here
	}
	
	@Inject
	@Optional
	public void handleEvent(@UIEventTopic("CSV/Daten") CSVDatei csv)
	{
	label.setText(csv.getPath());
	
	createColumns(tableViewer, csv);
	tableViewer.setContentProvider(new ArrayContentProvider());
	tableViewer.setLabelProvider(new CSVLabelProvider());
	tableViewer.setInput(csv.getFields());

	}
	
	private void createColumns(TableViewer tv, CSVDatei csv) 
	{
	TableViewerColumn column = null;
	
		if (csv.isHeader()) 
		{
		String[] cols = csv.getLines().get(0).split(csv.getDelimiter());	
			for (int i = 0; i < cols.length; i++) 
			{
			column = new TableViewerColumn(tv, SWT.LEFT, i);
			column.getColumn().setText(cols[i]);
			column.getColumn().setWidth(100);
		    column.getColumn().setResizable(true);
		    column.getColumn().setMoveable(true);
			}

		}
		
	}

	@PostConstruct
	public void createComposite(Composite parent) 
	{
	parent.setLayout(new GridLayout());

		label = new Label(parent, SWT.NONE);
		label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		label.setText("Keine CSV-Daten verfügbar");

		tableViewer = new TableViewer(parent);
		// Make lines and make header visible
		final Table table = tableViewer.getTable();
		
		table.setHeaderVisible(true);
		table.setLinesVisible(true); 
		tableViewer.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
	}	
	@Focus
	public void onFocus() 
	{
		//TODO Your code here
	}
	
	
}