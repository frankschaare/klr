/**
 * 
 */
package de.hannit.fsch.rcp.klr.parts;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;

import de.hannit.fsch.common.CSVConstants;
import de.hannit.fsch.rcp.klr.constants.Topics;
import de.hannit.fsch.rcp.klr.loga.LoGaDatei;

/**
 * @author fsch
 *
 */
public class LoGaPart
{	
private TableViewerColumn column = null;
private TableViewer	tableViewer = null;

	@Inject @Optional
	public void handleEvent(@UIEventTopic(Topics.LOGA_DATEN) LoGaDatei logaDatei)
	{
	logaDatei.resetLineCount();
	tableViewer.setInput(logaDatei.getDaten().values().toArray());	
	}	

	@Inject	
	public LoGaPart(Composite parent, @Named(CSVConstants.Loga.CONTEXT_DATEN) LoGaDatei logaDatei)
	{
	parent.setLayout(new GridLayout());

	Label label = new Label(parent, SWT.NONE);
	label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
	label.setText(logaDatei.getPath() + " [" + (logaDatei.getFields().size()) + " Datens�tze]");

	tableViewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
	// Make lines and make header visible
	final Table table = tableViewer.getTable();
		
	table.setHeaderVisible(true);
	table.setLinesVisible(true); 
	tableViewer.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));

	column = new TableViewerColumn(tableViewer, SWT.LEFT, 0);
	column.getColumn().setText("Zeile");
	column.getColumn().setWidth(50);
	column.getColumn().setResizable(true);
	column.getColumn().setMoveable(true);
	
	column = new TableViewerColumn(tableViewer, SWT.LEFT, CSVConstants.Loga.PERSONALNUMMER_INDEX_TABLE);
	column.getColumn().setText("PNR");
	column.getColumn().setWidth(200);
	column.getColumn().setResizable(true);
	column.getColumn().setMoveable(true);
	
	column = new TableViewerColumn(tableViewer, SWT.LEFT, CSVConstants.Loga.BRUTTO_INDEX_TABLE);
	column.getColumn().setText(CSVConstants.Loga.BRUTTO_LABEL_TABLE);
	column.getColumn().setWidth(200);
	column.getColumn().setResizable(true);
	column.getColumn().setMoveable(true);
	
	column = new TableViewerColumn(tableViewer, SWT.LEFT, CSVConstants.Loga.ABRECHNUNGSMONAT_INDEX_TABLE);
	column.getColumn().setText(CSVConstants.Loga.ABRECHNUNGSMONAT_LABEL_TABLE);
	column.getColumn().setWidth(200);
	column.getColumn().setResizable(true);
	column.getColumn().setMoveable(true);
	
	column = new TableViewerColumn(tableViewer, SWT.LEFT, CSVConstants.Loga.TARIFGRUPPE_INDEX_TABLE);
	column.getColumn().setText(CSVConstants.Loga.TARIFGRUPPE_LABEL_TABLE);
	column.getColumn().setWidth(200);
	column.getColumn().setResizable(true);
	column.getColumn().setMoveable(true);		

	column = new TableViewerColumn(tableViewer, SWT.LEFT, CSVConstants.Loga.TARIFSTUFE_INDEX_TABLE);
	column.getColumn().setText(CSVConstants.Loga.TARIFSTUFE_LABEL_TABLE);
	column.getColumn().setWidth(200);
	column.getColumn().setResizable(true);
	column.getColumn().setMoveable(true);
	
	column = new TableViewerColumn(tableViewer, SWT.LEFT, CSVConstants.Loga.STELLENNTEIL_INDEX_TABLE);
	column.getColumn().setText(CSVConstants.Loga.STELLENNTEIL_LABEL_TABLE);
	column.getColumn().setWidth(200);
	column.getColumn().setResizable(true);
	column.getColumn().setMoveable(true);		
	
	tableViewer.setContentProvider(new ArrayContentProvider());
	tableViewer.setLabelProvider(logaDatei);
		if (logaDatei != null)
		{
		logaDatei.resetLineCount();	
		tableViewer.setInput(logaDatei.getDaten().values().toArray());	
		}
	}
}
