 
package de.hannit.fsch.rcp.klr.parts;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.PojoProperties;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;

import de.hannit.fsch.common.AppConstants;
import de.hannit.fsch.common.ContextLogger;
import de.hannit.fsch.common.mitarbeiter.Mitarbeiter;
import de.hannit.fsch.klr.dataservice.DataService;
import de.hannit.fsch.rcp.klr.constants.Topics;

public class AddMitarbeiterPart implements ITableLabelProvider
{
private DataBindingContext m_bindingContext;
@Inject DataService dataService;
@Inject @Named(AppConstants.LOGGER) private ContextLogger log;
@Inject IEventBroker broker;

private Mitarbeiter m = null;
private Mitarbeiter toInsert = new Mitarbeiter();
private TableViewerColumn column = null;
private TableViewer	tableViewer = null;
private Table table;
private Text textPNr;
private Text textUsername;
private Text textNachname;
private Text textVorname;

private String label = null;

private String plugin  = this.getClass().getName();

	@PostConstruct
	public void postConstruct(Composite parent) {
		parent.setLayout(new GridLayout(1, false));
		
		Group grpWhat = new Group(parent, SWT.NONE);
		grpWhat.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		grpWhat.setText("Mitarbeiter hinzuf\u00FCgen");
		grpWhat.setLayout(new GridLayout(1, false));
		
		Label lblHint = new Label(grpWhat, SWT.NONE);
		lblHint.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		lblHint.setText("In diesem Formular k\u00F6nnen Sie neue Mitarbeiter Hinzuf\u00FCgen.\r\nWenn die Plausibilit\u00E4tspr\u00FCfung erfolgreich war, wird der Mitarbeiter in der Datenbanktabelle Mitarbeiter gespeichert.");
		
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(3, true));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		Group grpMitarbeiterDaten = new Group(composite, SWT.NONE);
		grpMitarbeiterDaten.setLayout(new GridLayout(3, true));
		grpMitarbeiterDaten.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
		grpMitarbeiterDaten.setText("Mitarbeiter Daten");
		
		Label lblPNr = new Label(grpMitarbeiterDaten, SWT.NONE);
		lblPNr.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblPNr.setText("Personalnummer:");
		
		textPNr = new Text(grpMitarbeiterDaten, SWT.BORDER);
		textPNr.addFocusListener(new FocusAdapter() 
		{
			@Override
			public void focusLost(FocusEvent e) 
			{
			// TODO: Pr�fung, ob Text 6-stellig ist	
				try
				{
				int iPNr = Integer.parseInt(textPNr.getText());
				
					if (dataService.existsMitarbeiter(iPNr))
					{
					log.error("ACHTUNG, die eingegebene Personalnummer existiert bereits ! " + textPNr.getText() + " kann nicht gespeichert werden !", plugin, null);
					textPNr.setBackground(e.display.getSystemColor(SWT.COLOR_RED));
					textPNr.setFocus();	
					}
					else
					{
					log.confirm("Die eingegebene Personalnummer wurde erfolgreich gepr�ft. ", plugin);
					textPNr.setBackground(e.display.getSystemColor(SWT.COLOR_WHITE));
					}
				}
				catch (NumberFormatException ex)
				{
				log.error("Fehlerhafte Eingabe der Personalnummer im Feld 'Personalnummer'. " + textPNr.getText() + " ist keine g�ltige Personalnummer !", this.getClass().getName(), ex);
				textPNr.setBackground(e.display.getSystemColor(SWT.COLOR_RED));
				textPNr.setFocus();
				}	
			}
		});
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
		textVorname.addFocusListener(new FocusAdapter()
		{

			@Override
			public void focusLost(FocusEvent e)
			{
				if (toInsert.getPersonalNR() != 0 && toInsert.getNachname() != null)
				{
				broker.send(Topics.MITARBEITER_INSERT, toInsert);	
				}
			}
			
		});
		new Label(grpMitarbeiterDaten, SWT.NONE);
		new Label(grpMitarbeiterDaten, SWT.NONE);
		new Label(grpMitarbeiterDaten, SWT.NONE);
		new Label(grpMitarbeiterDaten, SWT.NONE);
		new Label(grpMitarbeiterDaten, SWT.NONE);
		new Label(grpMitarbeiterDaten, SWT.NONE);
		new Label(grpMitarbeiterDaten, SWT.NONE);
		
		new Label(grpMitarbeiterDaten, SWT.NONE);
		new Label(grpMitarbeiterDaten, SWT.NONE);
		
		Group grpAktuelleDaten = new Group(composite, SWT.NONE);
		grpAktuelleDaten.setText("Aktuelle Daten");
		grpAktuelleDaten.setLayout(new GridLayout(1, false));
		grpAktuelleDaten.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		
		tableViewer = new TableViewer(grpAktuelleDaten, SWT.BORDER | SWT.FULL_SELECTION);
		table = tableViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true); 
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		column = new TableViewerColumn(tableViewer, SWT.LEFT, 0);
		column.getColumn().setText("Personalnummer");
		column.getColumn().setWidth(100);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(true);

		column = new TableViewerColumn(tableViewer, SWT.LEFT, 1);
		column.getColumn().setText("Netzwerkkennung");
		column.getColumn().setWidth(100);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(true);
		
		column = new TableViewerColumn(tableViewer, SWT.LEFT, 2);
		column.getColumn().setText("Nachname");
		column.getColumn().setWidth(300);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(true);
		
		column = new TableViewerColumn(tableViewer, SWT.LEFT, 3);
		column.getColumn().setText("Vorname");
		column.getColumn().setWidth(200);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(true);
		
		tableViewer.addSelectionChangedListener(new ISelectionChangedListener()
		{
			@Override
			public void selectionChanged(SelectionChangedEvent event)
			{
			IStructuredSelection selection = (IStructuredSelection) tableViewer.getSelection();
				if (selection.getFirstElement() instanceof Mitarbeiter)
				{
				}
			}
		});
		
		tableViewer.setContentProvider(new ArrayContentProvider());
		tableViewer.setLabelProvider(this);
		tableViewer.setInput(dataService.getMitarbeiter().toArray());
		m_bindingContext = initDataBindings();
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
	m =  (Mitarbeiter) element;

		switch (columnIndex) 
		{
		case 0:
		label = String.valueOf(m.getPersonalNR());
		break;
		
		case 1:
		label = m.getBenutzerName().equalsIgnoreCase("unbekannt") ? "" : m.getBenutzerName();
		break;
		
		case 2:
		label = m.getNachname();
		break;
		
		case 3:
		label = m.getVorname();
		break;
		
		default:
		label = "ERROR";
		break;
		}
	return label;
	}
	protected DataBindingContext initDataBindings() {
		DataBindingContext bindingContext = new DataBindingContext();
		//
		IObservableValue observeTextTextPNrObserveWidget = WidgetProperties.text(SWT.Modify).observe(textPNr);
		IObservableValue personalNRAsStringMObserveValue = PojoProperties.value("personalNRAsString").observe(toInsert);
		bindingContext.bindValue(observeTextTextPNrObserveWidget, personalNRAsStringMObserveValue, null, null);
		//
		IObservableValue observeTextTextUsernameObserveWidget = WidgetProperties.text(SWT.Modify).observe(textUsername);
		IObservableValue benutzerNameMObserveValue = PojoProperties.value("benutzerName").observe(toInsert);
		bindingContext.bindValue(observeTextTextUsernameObserveWidget, benutzerNameMObserveValue, null, null);
		//
		IObservableValue observeTextTextNachnameObserveWidget = WidgetProperties.text(SWT.Modify).observe(textNachname);
		IObservableValue nachnameMObserveValue = PojoProperties.value("nachname").observe(toInsert);
		bindingContext.bindValue(observeTextTextNachnameObserveWidget, nachnameMObserveValue, null, null);
		//
		IObservableValue observeTextTextVornameObserveWidget = WidgetProperties.text(SWT.Modify).observe(textVorname);
		IObservableValue vornameMObserveValue = PojoProperties.value("vorname").observe(toInsert);
		bindingContext.bindValue(observeTextTextVornameObserveWidget, vornameMObserveValue, null, null);
		//
		return bindingContext;
	}
}