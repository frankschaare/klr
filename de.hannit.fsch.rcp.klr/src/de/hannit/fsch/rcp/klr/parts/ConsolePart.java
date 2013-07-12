 
package de.hannit.fsch.rcp.klr.parts;

import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.osgi.service.event.Event;

import de.hannit.fsch.common.AppConstants;
import de.hannit.fsch.rcp.klr.constants.Topics;
import de.hannit.fsch.rcp.klr.provider.LogTableLabelProvider;

public class ConsolePart 
{
private TreeMap<Integer, Event> logStack = new TreeMap<Integer, Event>();

private Label message = null;
private Table table;
private TableViewerColumn column = null;
private TableViewer tableViewer;
@Inject @Optional private MApplication application;
	
	@Inject
	public ConsolePart() 
	{
		//TODO Your code here
	}
	
	@Inject
	public void test(@Optional @Named(AppConstants.LOG_STACK) Object item )
	{
		if (item != null) 
		{
		logStack = (TreeMap<Integer, Event>) item;	
		System.out.println("Empfange LogStack mit " + logStack.size() + " Meldungen.");	
		}
	}
	
	
	@Inject
	@Optional
	public void handleEvent(@UIEventTopic(Topics.LOGGING) Event event)
	{
	logStack = (TreeMap<Integer, Event>) application.getContext().get(AppConstants.LOG_STACK);	
	//logStack.put(logStack.size(), event);
	//application.getContext().modify(AppConstants.LOG_STACK, logStack);
		if (message != null) 
		{
		message.setText(logStack.size() + " Meldungen");	
		}
	tableViewer.setInput(logStack.descendingMap().values().toArray());	
	}	
	
	@PostConstruct
	public void postConstruct(Composite parent) {
		parent.setLayout(new GridLayout(1, false));
		
		message = new Label(parent, SWT.NONE);
		message.setText("0 Meldungen");
		
		tableViewer = new TableViewer(parent, SWT.BORDER | SWT.FULL_SELECTION);
		table = tableViewer.getTable();
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		table.setHeaderVisible(true);

		column = new TableViewerColumn(tableViewer, SWT.LEFT, 0);
		column.getColumn().setText("Meldung");
		column.getColumn().setWidth(1000);
	    column.getColumn().setResizable(true);
	    column.getColumn().setMoveable(true);
	    
		column = new TableViewerColumn(tableViewer, SWT.LEFT, 1);
		column.getColumn().setText("Plug-in");
		column.getColumn().setWidth(450);
	    column.getColumn().setResizable(true);
	    column.getColumn().setMoveable(true);
	    
		column = new TableViewerColumn(tableViewer, SWT.LEFT, 2);
		column.getColumn().setText("Datum");
		column.getColumn().setWidth(200);
	    column.getColumn().setResizable(true);
	    column.getColumn().setMoveable(true);

	    tableViewer.setContentProvider(new ArrayContentProvider());
	    tableViewer.setLabelProvider(new LogTableLabelProvider());
	}
	
	
	
	@Focus
	public void onFocus() 
	{
	logStack = (TreeMap<Integer, Event>) application.getContext().get(AppConstants.LOG_STACK);	
	
			if (message != null) 
			{
			message.setText(logStack.size() + " Meldungen");	
			}
			if (tableViewer != null)
			{
			tableViewer.setInput(logStack.descendingMap().values().toArray());		
			}
	}
}