 
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
import de.hannit.fsch.common.CSVConstants;
import de.hannit.fsch.common.ContextLogger;
import de.hannit.fsch.common.Datumsformate;
import de.hannit.fsch.common.csv.azv.AZVDatensatz;
import de.hannit.fsch.rcp.klr.azv.AZVDaten;
import de.hannit.fsch.rcp.klr.constants.Topics;
import de.hannit.fsch.soa.osecm.IAZVClient;

public class ExuecuteAZVServiceHandler 
{
@Inject @Named(AppConstants.LOGGER) private ContextLogger log;
@Inject IEventBroker broker;
@Inject IAZVClient webService;

private Exception e = null;
private Document doc = null;
private AZVDaten azvDaten;
private ArrayList<AZVDatensatz> azvMeldungen = null;
private XPathFactory xpathfactory = XPathFactory.newInstance();
private XPath xpath = xpathfactory.newXPath();

	@Inject @Optional
	public void handleEvent(@UIEventTopic(Topics.AZV_WEBSERVICE) AZVDaten incoming)
	{
	this.azvDaten = incoming;	
	}	

	@Execute
	public void execute() 
	{
	// AZV Request vorbereiten
	e = webService.setAZVRequest(azvDaten.getRequestedMonth(), azvDaten.getRequestedYear());
		if (e != null)
		{
		log.error(e.getMessage(), this.getClass().getName() + "execute()", e);	
		}
		else
		{
		doc = webService.getResultList();
		parseDocument();
		azvDaten.setAzvMeldungen(azvMeldungen);
		azvDaten.setRequestComplete(true);
		broker.send(Topics.AZV_WEBSERVICE, azvDaten);
		}
	}

	/*
	 * Response parsen
	 * Die AZV Belegen liegen unter //DMSContent/Archive/ObjectList/Object
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
    XPathExpression xpRows = null;
    ArrayList<String> idValues = new ArrayList<String>();
    azvMeldungen = new ArrayList<AZVDatensatz>();
    
    
    	if (doc != null)
		{
        NodeList objectNodes = doc.getElementsByTagName("Object");	
        	for (int i = 0; i < objectNodes.getLength(); i++)
			{
        	Element object = (Element) objectNodes.item(i);
        	idValues.add(object.getAttribute("id"));
			}
        	
        	int rowCount = 1;
        	for (String id : idValues)
			{
            AZVDatensatz azvMeldung = null;
            String strKSTKTR = null;
            String strProzentanteil = null;
            
	            try
				{
            	// Nodes unter //Object/TableFields/TableField/Row verarbeiten:	        		
            	xpRows = xpath.compile("/DMSContent/Archive/ObjectType/ObjectList/Object[@id='" + id + "']/ChildObjects/ObjectType/ObjectList/Object/TableFields/TableField[@name='AZV-Verteilung']/Row");
            	NodeList rows = (NodeList) xpRows.evaluate(doc, XPathConstants.NODESET);
	            	
	            	for (int i = 0; i < rows.getLength(); i++)
	            	{
	            	strKSTKTR = rows.item(i).getChildNodes().item(0).getTextContent();
	            	strKSTKTR = (strKSTKTR.length() > 0) ? strKSTKTR : rows.item(i).getChildNodes().item(1).getTextContent();
	            	strProzentanteil = rows.item(i).getChildNodes().item(2).getTextContent();
	            		
	            	azvMeldung = new AZVDatensatz();
	            	azvMeldung.setRowCount(rowCount);
	            	azvMeldung.setBerichtsMonatAsString(azvDaten.getRequestedMonth());
	            	azvMeldung.setBerichtsJahrAsString(azvDaten.getRequestedYear());
	            	azvMeldung.setBerichtsMonat(Datumsformate.MONATLANG_JAHR.parse(azvDaten.getRequestedMonth() + " " + azvDaten.getRequestedYear()));
	            	//azvMeldung.setUserName(strUserName);
	            	azvMeldung.setKostenArt(strKSTKTR);
	            	azvMeldung.setProzentanteil(Integer.parseInt(strProzentanteil));
	            	
	            	processFields(azvMeldung, id);
	            	
	            	azvMeldungen.add(azvMeldung);
	            	rowCount += 1;
	            	}	        		
				}
				catch (XPathExpressionException | ParseException e)
				{
				e.printStackTrace();
				}	            
			}
		}	
	}
	
    /*
     *  Nodes unter /DMSContent/Archive/ObjectType/ObjectList/Object[@id='" + id + "']/ChildObjects/ObjectType/ObjectList/Object/Fields/Field verarbeiten:
     *  - Monat
     *  - Jahr
     *  - Netzwerkkennung	
     */
	private void processFields(AZVDatensatz azv, String id)
	{
	XPathExpression xpField = null;
	
		try
		{
		xpField = xpath.compile("/DMSContent/Archive/ObjectType/ObjectList/Object[@id='" + id + "']/ChildObjects/ObjectType/ObjectList/Object/Fields/Field");
		NodeList fields = (NodeList) xpField.evaluate(doc, XPathConstants.NODESET);
			for (int i = 0; i < fields.getLength(); i++)
			{
			Element field = (Element) fields.item(i);	
			
				switch (field.getAttribute("name"))
				{
				case CSVConstants.AZV.NODENAME_BERICHTSMONAT:
				azv.setBerichtsMonatAsString(field.getTextContent());		
				break;
				case CSVConstants.AZV.NODENAME_BERICHTSJAHR:
				azv.setBerichtsJahrAsString(field.getTextContent());
				break;
				case CSVConstants.AZV.NODENAME_USERNAME:
				azv.setUserName(field.getTextContent());
				break;
		
				default:
				break;
				}
			}
            /*
             *  Nodes unter /DMSContent/Archive/ObjectType/ObjectList/Object[@id='" + id + "']/Fields/Field verarbeiten:
             *  - Nachname
             *  - Team
             *  - Personalnummer	
             */
			xpField = xpath.compile("/DMSContent/Archive/ObjectType/ObjectList/Object[@id='" + id + "']/Fields/Field");
			fields = (NodeList) xpField.evaluate(doc, XPathConstants.NODESET);
	            
	        		for (int i = 0; i < fields.getLength(); i++)
					{
					Element field = (Element) fields.item(i);	
					
						switch (field.getAttribute("name"))
						{
						case CSVConstants.AZV.NODENAME_NACHNAME:
						azv.setNachname(field.getTextContent());		
						break;
						case CSVConstants.AZV.NODENAME_TEAM:
						azv.setTeam(field.getTextContent());
						break;
						case CSVConstants.AZV.NODENAME_PERSONALNUMMER:
						String strPersonalnummer = strPersonalnummer = field.getTextContent();	
						azv.setPersonalNummer((strPersonalnummer.length() > 0) ? Integer.parseInt(field.getTextContent()) : 0);
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