 
package de.hannit.fsch.rcp.klr.handler.azv;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

import javax.inject.Inject;
import javax.inject.Named;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import de.hannit.fsch.common.AppConstants;
import de.hannit.fsch.common.ContextLogger;
import de.hannit.fsch.common.Datumsformate;
import de.hannit.fsch.klr.dataservice.DataService;
import de.hannit.fsch.klr.model.azv.AZVDaten;
import de.hannit.fsch.klr.model.azv.AZVDatensatz;
import de.hannit.fsch.rcp.klr.constants.Topics;
import de.hannit.fsch.soa.osecm.IAZVClient;

public class ExuecuteAZVServiceHandler 
{
@Inject @Named(AppConstants.LOGGER) private ContextLogger log;
@Inject IEventBroker broker;
@Inject IAZVClient webService;
@Inject DataService dataService;

private Exception e = null;
private Document doc = null;
private AZVDaten azvDaten;
private ArrayList<AZVDatensatz> azvMeldungen = null;
private XPathFactory xpathfactory = XPathFactory.newInstance();
private XPath xpath = xpathfactory.newXPath();
private String plugin = this.getClass().getName() + ".execute()";

	@Inject @Optional
	public void handleEvent(@UIEventTopic(Topics.AZV_WEBSERVICE) AZVDaten incoming)
	{
	this.azvDaten = incoming;	
	}	

	@Execute
	public void execute() 
	{
	// AZV Request vorbereiten
	Date start = new Date();	
	log.info("Starte Anfrage an den OS/ECM Webservice für den Berichtsmonat " + azvDaten.getRequestedMonth() + " " + azvDaten.getRequestedYear(), plugin);
	e = webService.setAZVRequest(azvDaten.getRequestedMonth(), azvDaten.getRequestedYear());
		if (e != null)
		{
		log.error(e.getMessage(), plugin, e);	
		}
		else
		{
		doc = webService.getResultList();
		Date end = new Date();
		long anfrageDauer = end.getTime() - start.getTime();
		log.confirm("Anfrage an den OS/ECM Webservice wurde in " + String.valueOf(anfrageDauer) + " Millisekunden abgeschlossen.", plugin);
		parseDocument();

		azvDaten.setAzvMeldungen(azvMeldungen);
		azvDaten.setErrors(false);
		azvDaten.setChecked(false);
		azvDaten.setRequestComplete(true);
		broker.send(Topics.AZV_WEBSERVICE, azvDaten);
		}
	}

	/*
	 * Response parsen
	 * Die Rückgabe des Webservices ist richtig kranker Scheiss.
	 * 
	 * Offensichtlich hat niemand ernsthaft damit gerechnet, das jemand diesen Schwachsinn einmal parsen muss
	 * und alles gegeben, um unparsable Markup zu generieren.
	 * 
	 * Beispielsweise gibt es jeweils mindestens Knotentypen mit den Tagnamen ObjectType, Object und Childobjects.
	 * 
	 * Es sind daher einige Verrenkungen notwendig, um die benötigte Information aus diesem Wust herauszufiltern.
	 * 
	 * Benötigt wird 
	 * - der Benutzer: 	//Object/Fields/Field(name=Benutzer)
	 * - der Monat: 	//Object/Fields/Field(name=Monat)
	 * - das Jahr: 		//Object/Fields/Field(name=Jahr)
	 * - KST/KTR:		//Object/TableFields/TableField(name=AZV-Verteilung)/Row/Value[0]
	 * - Prozentanteil:	//Object/TableFields/TableField(name=AZV-Verteilung)/Row/Value[2]
	 * 
	 */
	private void parseDocument()
	{
    AZVDatensatz azvMeldung = null;
    String strNachname = null;
    String strTeam = null;
    String strBenutzername = null;
    String strEMail = null;
    String strKSTKTR = null;
    String strProzentanteil = null;
    XPathExpression xpMitarbeiterdaten = null;
    azvMeldungen = new ArrayList<AZVDatensatz>();
    
    
    	if (doc != null)
		{
    	//Schritt 1: Zuerst wird eine NodeList aller Objekte erstellt
    	NodeList allObjects = doc.getElementsByTagName("Object");
	    	for (int a = 0; a < allObjects.getLength(); a++)
			{
	    	Element mitarbeiterNode = (Element) allObjects.item(a);
	    	
	    		/*
	    		 *  Schritt 2: Dann werden die Object-Nodes heraussortiert, welche einem Mitarbeiter zugeordnet sind.
	    		 *  Aufgrund der kruden Bezeichnung der Tags, welche sich dreimal wiederholen, mache ich das etwas umständlich:
	    		 */
	    		if (mitarbeiterNode.getParentNode().getParentNode().getParentNode().getNodeName().equalsIgnoreCase("Archive"))
				{
	    		/*
	    		 * Es wurde ein Object-Node gefunden, welcher ein Mitarbeiter ist.
	    		 * Nun wird die ID des Knotens ausgelesen und die Kopfdaten des Mitarbeiters ausgelesen:	
	    		 */
	    		String strID = mitarbeiterNode.getAttribute("id");
					try
					{
					xpMitarbeiterdaten = xpath.compile("/DMSContent/Archive/ObjectType/ObjectList/Object[@id='" + strID + "']/Fields/Field");
					NodeList mitarbeiterDaten = (NodeList) xpMitarbeiterdaten.evaluate(doc, XPathConstants.NODESET);
	
				    	for (int m = 0; m < mitarbeiterDaten.getLength(); m++)
						{
				    	Element field = (Element) mitarbeiterDaten.item(m);	
				    		switch (field.getAttribute("name"))
							{
							case "Name":
							strNachname = field.getTextContent();	
							break;
							
							case "Team":
							strTeam = field.getTextContent();	
							break;
							
							case "Benutzername":
							strBenutzername = field.getTextContent();	
							break;	
							
							case "E-Mail":
							strEMail = field.getTextContent();	
							break;								
	
							default:
							break;
							}
				    		
						}
					}
					catch (XPathExpressionException e)
					{
					e.printStackTrace();
					}	    		
	    		
	    		
	    		NodeList tableFields = mitarbeiterNode.getElementsByTagName("TableField");	
	    		
	    			/*
	    			 * Die NodeList tableFields sollte zwei Elemente enthalten,
	    			 * - Attribut name = 'AZV-Verteilung': enthält die benötigten Row-Elemente für die AZV's
	    			 * - Attribut name = 'WF-Protokoll': enthält Workflow-Daten, die hier nicht benötigt werden
	    			 * 
	    			 */
			    	for (int t = 0; t < tableFields.getLength(); t++)
					{
			    	Element tableField = (Element) tableFields.item(t);	
			    		switch (tableField.getAttribute("name"))
						{
						case "AZV-Verteilung":
						NodeList rows = tableField.getElementsByTagName("Row");
							/*
							 * Endlich bei den gesuchten Daten angekommen, werden hier die AZV-Daten ausgelesen. Eine Row hat folgendes Format:
							 * 		<Row id="n">
							 * 			<Value>Kostenstelle</Value>
							 * 			<Value>Kostenträger</Value>
							 * 			<Value>Prozentanteil</Value>
							 * 			<Value>Bemerkung</Value>
							 * 			<Value>Unterschrift</Value>
							 * 		</Row>
							 * Für jedes Row-Element wird ein AZVDatensatz generiert.
							 * 
							 */
					    	for (int r = 0; r < rows.getLength(); r++)
							{
					    	Element azvRow = (Element) rows.item(r);
					    	NodeList azvAnteile = azvRow.getChildNodes();
					    	
				            azvMeldung = new AZVDatensatz();
				            azvMeldung.setNachname(strNachname);
				            azvMeldung.setTeam(strTeam);
				            azvMeldung.setUserName(strBenutzername);
				            
				            int iPNR = dataService.getPersonalnummer(strNachname);
				            
				            	if (iPNR == 0)
								{
								iPNR = dataService.getPersonalnummerbyUserName(strBenutzername);
								}
				            azvMeldung.setPersonalNummer(iPNR);
				            
				            azvMeldung.setEMail(strEMail);
				            azvMeldung.setBerichtsMonatAsString(azvDaten.getRequestedMonth());
				            azvMeldung.setBerichtsJahrAsString(azvDaten.getRequestedYear());
					            try
								{
								azvMeldung.setBerichtsMonat(Datumsformate.MONATLANG_JAHR.parse(azvDaten.getRequestedMonth() + " " + azvDaten.getRequestedYear()));
								}
								catch (ParseException e)
								{
								e.printStackTrace();
								}						    	
			            	strKSTKTR = azvAnteile.item(0).getTextContent();
			            	strKSTKTR = (strKSTKTR.length() > 0) ? strKSTKTR : azvAnteile.item(1).getTextContent();
			            	strProzentanteil = azvAnteile.item(2).getTextContent();
			            	azvMeldung.setKostenArt(strKSTKTR);
			            	azvMeldung.setProzentanteil(Integer.parseInt(strProzentanteil));	
			            	
			            	azvMeldungen.add(azvMeldung);
							}
						break;					

						default:
						break;
						} 		
					}    					
				}
			}	
		}	
	}
	
    @CanExecute
	public boolean canExecute() 
	{
	boolean ready = false;
	
		if (azvDaten != null && azvDaten.getRequestedMonth() != null && azvDaten.getRequestedYear() != null)
		{
		ready = true;
		}
	return ready;
	}		
}
