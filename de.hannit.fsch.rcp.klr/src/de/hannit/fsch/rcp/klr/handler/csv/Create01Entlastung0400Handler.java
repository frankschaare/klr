 
package de.hannit.fsch.rcp.klr.handler.csv;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.TreeMap;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.workbench.modeling.EModelService;

import de.hannit.fsch.common.AppConstants;
import de.hannit.fsch.common.ContextLogger;
import de.hannit.fsch.common.Dezimalformate;
import de.hannit.fsch.common.MonatsSummen;
import de.hannit.fsch.klr.model.kostenrechnung.KostenStelle;
import de.hannit.fsch.klr.model.kostenrechnung.KostenTraeger;
import de.hannit.fsch.klr.model.kostenrechnung.Kostenrechnungsobjekt;
import de.hannit.fsch.rcp.klr.constants.Topics;
import de.hannit.fsch.rcp.klr.csv.CSV01Datei;
import de.hannit.fsch.rcp.klr.csv.CSVDatei;

public class Create01Entlastung0400Handler extends CSVHandler
{
@Inject @Named(AppConstants.LOGGER) private ContextLogger log;
@Inject IEventBroker broker;
private String plugin = this.getClass().getName();

private MonatsSummen mSummen = null;
private TreeMap<String, KostenStelle> tmKostenstellen = null;
private TreeMap<String, KostenTraeger> tmKostenträger = null;
private double sumKST = 0;
private double sumKSTGerundet = 0;

private CSV01Datei csvDatei = null;
	
	@Inject @Optional
	public void handleEvent(@UIEventTopic(Topics.MONATSSUMMEN) MonatsSummen incoming)
	{
	this.mSummen = incoming;
	}	

	@Execute
	public void execute(MApplication app, EModelService modelService) 
	{
	splitKostenobjekte();
	createCSV();
	
	MPartStack details = (MPartStack) modelService.find("de.hannit.fsch.rcp.klr.partstack.details", app);
	MPart csv01 = createPart(AppConstants.PartIDs.CSV01);
	csv01.getContext().set(AppConstants.CONTEXT_CSV01, csvDatei);
	details.getChildren().add(csv01);
	
	broker.send(Topics.CSV01, csvDatei);
	
	partService.activate(csv01);
	}
	
	/*
	 * Erstellt alle Zeilen der Datei und schreibt diese
	 */
	private void createCSV()
	{
	String feld1 = null;
	String feld2 = null;
	String feld3 = null;
	String feld4 = null;
	String feld5 = null;
	String feld6 = null;
	String feld7 = null;
	String delimiter = CSVDatei.DEFAULT_DELIMITER;
	
	ArrayList<String> lines = new ArrayList<String>();	
	
	feld1 = getLetzterTagdesMonats(mSummen.getBerichtsMonatAsDate()) + delimiter;
	feld2 = CSV01Datei.ZELLE2_NEHME + delimiter;
	feld3 = CSV01Datei.ENTLASTUNGSKONTO + delimiter;
	feld4 = CSV01Datei.ZELLE4_NEHME + delimiter;
	feld5 = CSV01Datei.ZELLE5_PRÄFIX + getMonatsnummer(mSummen.getBerichtsMonatAsDate()) + delimiter;
	feld6 = CSV01Datei.ZELLE6_PRÄFIX + getMonatLang(mSummen.getBerichtsMonatAsDate()) + delimiter;
	
	feld7 = "-" + Dezimalformate.DEFAULT.format(sumKSTGerundet);
	
	lines.add(feld1+feld2+feld3+feld4+feld5+feld6+feld7);
	
		/*
		 * Abschliessend werden die Zeilen für alle Kostenstellen geschrieben
		 * Die Zellen 1,6 und 6 bleiben dabei gleich
		 */
	feld2 = CSV01Datei.ZELLE2_GEBE + delimiter;
	feld4 = CSV01Datei.ZELLE4_GEBE + delimiter;
		
		double summeBelastung = 0;
		for (KostenStelle kst : tmKostenstellen.values())
		{
		summeBelastung += kst.getSummeGerundet();	
		feld3 = kst.getBezeichnung() + delimiter;
		feld7 = Dezimalformate.DEFAULT.format(kst.getSummeGerundet());
		
		lines.add(feld1+feld2+feld3+feld4+feld5+feld6+feld7);
		}
		
		if (sumKSTGerundet != summeBelastung)
		{
		log.error("Buchungssätze wurden erstellt. Entlastung 0400 = " + Dezimalformate.DEFAULT.format(sumKSTGerundet) + " entspricht NICHT der Summe Belastung Kostenstellen = " +  Dezimalformate.DEFAULT.format(summeBelastung), plugin + ".createCSV()", null);	
		}
		else
		{
		log.confirm("Buchungssätze wurden erstellt. Entlastung 0400 = " + Dezimalformate.DEFAULT.format(sumKSTGerundet) + " entspricht der Summe Belastung Kostenstellen = " +  Dezimalformate.DEFAULT.format(summeBelastung),  plugin + ".createCSV()");
		}	
	
	/*
	 * Alle Werte sind nun in der ArrayLIst lines gesichert, 
	 * es muss nur noch die CSV Datei geschrieben werden. 
	 * 
	 * Dazu wird ein Pfad nach dem Muster:
	 * PATH_PRÄFIX + YYYY Quartal # + PATH_SUFFIX + DATEINAME_PRÄFIX + YYYYMM + DATEINAME_SUFFIX benötigt: 	
	 */
	String strPath = CSV01Datei.PATH_PRÄFIX + getJahr(mSummen.getBerichtsMonatAsDate()) + " Quartal " + getQuartalsnummer(mSummen.getBerichtsMonatAsDate()) + CSV01Datei.PATH_SUFFIX;

	csvDatei = new CSV01Datei(strPath);
	csvDatei.setLog(log);
	csvDatei.hasHeader(false);
	csvDatei.setContent(lines);
	}

	/*
	 * Splitte die Monatssumen nach Kostenstellen und Kostenträgern
	 * Für diese Datei werden nur die Kostenstellen benötigt !
	 */
	private void splitKostenobjekte()
	{
		KostenStelle kst = null;
		KostenTraeger ktr = null;
		tmKostenstellen = new TreeMap<String, KostenStelle>();
		tmKostenträger = new TreeMap<String, KostenTraeger>();
			
			for (Kostenrechnungsobjekt kto : mSummen.getGesamtKosten().values())
			{
				switch (kto.getBezeichnung().length())
				{
				case 4:
				kst = new KostenStelle();
				kst.setBezeichnung(kto.getBezeichnung());
				kst.setSumme(kto.getSumme());
				tmKostenstellen.put(kst.getBezeichnung(), kst);		
				break;
				case 8:
				ktr = new KostenTraeger();
				ktr.setBezeichnung(kto.getBezeichnung());
				ktr.setSumme(kto.getSumme());
				tmKostenträger.put(ktr.getBezeichnung(), ktr);	
				break;

				default:
				log.error("Monatssummen enthält ungültige Bezeichnung (" + kto.getBezeichnung() + " für Kostenrechnungsobjekt !)", this.getClass().getName(), null);	
				break;
				}
			}
			/*
			 * Sicherheitshalber werden Kostenstellen und Kostenträger erneut aufsummiert
			 * und mit den Gesamtkosten verglichlichen. Diese MÜSSEN gleich sein !	
			 */
				sumKST = 0;
				sumKSTGerundet = 0;
				for (KostenStelle ks : tmKostenstellen.values())
				{
				sumKST += ks.getSumme();
				sumKSTGerundet += ks.getSummeGerundet();
				}
				
				double sumKTR = 0;
				for (KostenTraeger kt : tmKostenträger.values())
				{
				sumKTR += kt.getSumme();	
				}	
		log.confirm("Gesamtsumme (" + NumberFormat.getCurrencyInstance().format(mSummen.getKstktrMonatssumme()) + ") wurde erfolgreich in Kostenstellen (" + NumberFormat.getCurrencyInstance().format(sumKST) + ") und Kostenträgern (" + NumberFormat.getCurrencyInstance().format(sumKTR) + ") gesplittet.", this.getClass().getName() + ".execute()");		
	}
	
	
	@CanExecute
	public boolean canExecute() 
	{
	boolean ready = false;
	
		if (mSummen.isChecked() && mSummen.isSummeOK())
		{
		ready = true;
		}
	return ready;
	}
		
}