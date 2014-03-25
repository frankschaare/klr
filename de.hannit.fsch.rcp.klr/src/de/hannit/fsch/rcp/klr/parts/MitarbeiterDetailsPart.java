 
package de.hannit.fsch.rcp.klr.parts;

import java.util.ArrayList;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.TreeItem;

import de.hannit.fsch.common.AppConstants;
import de.hannit.fsch.common.ContextLogger;
import de.hannit.fsch.common.Datumsformate;
import de.hannit.fsch.klr.dataservice.DataService;
import de.hannit.fsch.klr.model.azv.Arbeitszeitanteil;
import de.hannit.fsch.klr.model.mitarbeiter.Mitarbeiter;
import de.hannit.fsch.rcp.klr.constants.Topics;

public class MitarbeiterDetailsPart implements ITableLabelProvider
{
@Inject DataService dataService;
@Inject @Named(AppConstants.LOGGER) private ContextLogger log;

private Mitarbeiter m = null;
private Arbeitszeitanteil azv = null;
private ArrayList<Arbeitszeitanteil> dbContent = null;

private TableViewerColumn column = null;
private TableViewer	tableViewer = null;
private TableViewer dbTableViewer = null;
private Table table;
private Text textPNr;
private Text textUsername;
private Text textNachname;
private Text textVorname;
private Button btnInsert = null;

private String label = null;

private String plugin  = this.getClass().getName();
private Table dbTable;

	/*
	 * Wird im NavPart ein Mitarbeiter im Navigationsbaum ausgewählt, wird er über den Broker versendet.
	 * Diese Methode wird aber erst dann aufgerufen, wenn der Part aktiv ist. Initial ist dieser Part unsichtbar,
	 * daher wird die allererste Selection vom DetailsHandler im PartContext gespeichert.
	 */
	@Inject @Optional
	public void handleEvent(@UIEventTopic(Topics.TREESELECTION_MITARBEITER) TreeItem incoming)
	{
		if (incoming.getData() instanceof Mitarbeiter)
		{
		setSelectedMitarbeiter((Mitarbeiter) incoming.getData());
		updateControls();
		}
	}
	
	private void updateControls()
	{
		/*
		 * Sind bereits Mitarbeiterdaten vorhanden, werden die Textfelder gefüllt: 
		 */
		if (m != null)
		{
		textPNr.setText(String.valueOf(m.getPersonalNR()));
		textNachname.setText(m.getNachname());
		textVorname.setText(m.getVorname());
		tableViewer.setContentProvider(new ArrayContentProvider());
		tableViewer.setLabelProvider(this);
		tableViewer.setInput(m.getAzvMonat().values().toArray());
		}	
		
		if (dbContent != null)
		{
		dbTableViewer.setContentProvider(new ArrayContentProvider());
		dbTableViewer.setLabelProvider(this);
		dbTableViewer.setInput(dbContent.toArray());
		}
	}
	
	
	/*
	 * Speichert den aktuell ausgewählten Mitarbeiter
	 * Gleichzeitig werden die Rohdaten aus der Datenbank geladen, um eventuell Korrekturen durchführen zu können
	 */
	private void setSelectedMitarbeiter(Mitarbeiter incoming)
	{
	this.m = incoming;	
	dbContent = dataService.getArbeitszeitanteile(m.getPersonalNR(), m.getAbrechnungsMonat());
	}

	@PostConstruct
	public void postConstruct(Composite parent, @Named(AppConstants.CONTEXT_SELECTED_MITARBEITER) Mitarbeiter incoming) 
	{
	setSelectedMitarbeiter(incoming);	
	
		parent.setLayout(new GridLayout(1, false));
		
		Group grpWhat = new Group(parent, SWT.NONE);
		grpWhat.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		grpWhat.setText("Mitarbeiter hinzuf\u00FCgen");
		grpWhat.setLayout(new GridLayout(1, false));
		
		Label lblHint = new Label(grpWhat, SWT.NONE);
		lblHint.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		lblHint.setText("In diesem Formular k\u00F6nnen die Daten des ausgew\u00E4hlten Mitarbeiters angesehen und bearbeitet werden.");
		
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(3, true));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		Group grpMitarbeiterDaten = new Group(composite, SWT.NONE);
		grpMitarbeiterDaten.setLayout(new GridLayout(3, true));
		grpMitarbeiterDaten.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 2));
		grpMitarbeiterDaten.setText("Mitarbeiter Daten");
		
		Label lblPNr = new Label(grpMitarbeiterDaten, SWT.NONE);
		lblPNr.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblPNr.setText("Personalnummer:");
		
		textPNr = new Text(grpMitarbeiterDaten, SWT.BORDER);

		textPNr.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		new Label(grpMitarbeiterDaten, SWT.NONE);
		new Label(grpMitarbeiterDaten, SWT.NONE);
		new Label(grpMitarbeiterDaten, SWT.NONE);
		new Label(grpMitarbeiterDaten, SWT.NONE);
		
		Label lblUsername = new Label(grpMitarbeiterDaten, SWT.NONE);
		lblUsername.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblUsername.setText("Netzwerkkennung:");
		
		textUsername = new Text(grpMitarbeiterDaten, SWT.BORDER);
		textUsername.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		new Label(grpMitarbeiterDaten, SWT.NONE);
		new Label(grpMitarbeiterDaten, SWT.NONE);
		new Label(grpMitarbeiterDaten, SWT.NONE);
		new Label(grpMitarbeiterDaten, SWT.NONE);
		
		Label lblNachname = new Label(grpMitarbeiterDaten, SWT.NONE);
		lblNachname.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));
		lblNachname.setText("Nachname:");
		
		textNachname = new Text(grpMitarbeiterDaten, SWT.BORDER);
		textNachname.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		new Label(grpMitarbeiterDaten, SWT.NONE);
		new Label(grpMitarbeiterDaten, SWT.NONE);
		new Label(grpMitarbeiterDaten, SWT.NONE);
		
		Label lblNewLabel = new Label(grpMitarbeiterDaten, SWT.NONE);
		lblNewLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel.setText("Vorname:");
		
		textVorname = new Text(grpMitarbeiterDaten, SWT.BORDER);
		textVorname.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		
		Group grpAktuelleDaten = new Group(composite, SWT.NONE);
		grpAktuelleDaten.setText("Gemeldete Arbeitszeitanteile");
		grpAktuelleDaten.setLayout(new GridLayout(1, false));
		grpAktuelleDaten.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		
		tableViewer = new TableViewer(grpAktuelleDaten, SWT.BORDER | SWT.FULL_SELECTION);
		table = tableViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true); 
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		column = new TableViewerColumn(tableViewer, SWT.LEFT, 0);
		column.getColumn().setText("Team");
		column.getColumn().setWidth(50);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(true);

		column = new TableViewerColumn(tableViewer, SWT.LEFT, 1);
		column.getColumn().setText("Berichtsmonat");
		column.getColumn().setWidth(100);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(true);
		
		column = new TableViewerColumn(tableViewer, SWT.LEFT, 2);
		column.getColumn().setText("Kostenart");
		column.getColumn().setWidth(60);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(true);
		
		column = new TableViewerColumn(tableViewer, SWT.LEFT, 3);
		column.getColumn().setText("Bezeichnung");
		column.getColumn().setWidth(400);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(true);
		
		column = new TableViewerColumn(tableViewer, SWT.LEFT, 4);
		column.getColumn().setText("%");
		column.getColumn().setWidth(100);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(true);
		
		Group grpDatenbanktabelle = new Group(composite, SWT.NONE);
		grpDatenbanktabelle.setLayout(new GridLayout(1, false));
		GridData gd_grpDatenbanktabelle = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
		gd_grpDatenbanktabelle.widthHint = 807;
		grpDatenbanktabelle.setLayoutData(gd_grpDatenbanktabelle);
		grpDatenbanktabelle.setText("Datenbank (Tabelle: Arbeitszeitanteile)");
		
		dbTableViewer = new TableViewer(grpDatenbanktabelle, SWT.BORDER | SWT.FULL_SELECTION);
		dbTable = dbTableViewer.getTable();
		dbTable.setHeaderVisible(true);
		dbTable.setLinesVisible(true);
		dbTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		column = new TableViewerColumn(dbTableViewer, SWT.LEFT, 0);
		column.getColumn().setText("Team");
		column.getColumn().setWidth(50);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(true);

		column = new TableViewerColumn(dbTableViewer, SWT.LEFT, 1);
		column.getColumn().setText("Berichtsmonat");
		column.getColumn().setWidth(100);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(true);
		
		column = new TableViewerColumn(dbTableViewer, SWT.LEFT, 2);
		column.getColumn().setText("Kostenart");
		column.getColumn().setWidth(60);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(true);
		
		column = new TableViewerColumn(dbTableViewer, SWT.LEFT, 3);
		column.getColumn().setText("Bezeichnung");
		column.getColumn().setWidth(400);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(true);
		
		column = new TableViewerColumn(dbTableViewer, SWT.LEFT, 4);
		column.getColumn().setText("%");
		column.getColumn().setWidth(100);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(true);
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);

		updateControls();
		
		tableViewer.setContentProvider(new ArrayContentProvider());
		tableViewer.setLabelProvider(this);
		tableViewer.setInput(m.getAzvMonat().values().toArray());
	}
	
	@Focus
	public void onFocus() {
		//TODO Your code here
	}

	@Override
	public void addListener(ILabelProviderListener listener) {}

	@Override
	public void dispose() {	}

	@Override
	public boolean isLabelProperty(Object element, String property)	{ return false;	}

	@Override
	public void removeListener(ILabelProviderListener listener)	{}

	@Override
	public Image getColumnImage(Object element, int columnIndex) { return null;	}

	@Override
	public String getColumnText(Object element, int columnIndex) 
	{ 
	azv =  (Arbeitszeitanteil) element;

		switch (columnIndex) 
		{
		case 0:
		label = String.valueOf(azv.getITeam());
		break;
		
		case 1:
		label = Datumsformate.MONATLANG_JAHR.format(azv.getBerichtsMonat());
		break;
		
		case 2:
		label = azv.getKostenstelle() != null ? azv.getKostenstelle() : azv.getKostentraeger();
		break;
		
		case 3:
		label = azv.getKostenstelle() != null ? azv.getKostenStelleBezeichnung() : azv.getKostenTraegerBezeichnung();
		break;
		
		default:
		label = String.valueOf(azv.getProzentanteil());
		break;
		}
	return label;
	}
}