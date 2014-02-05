 
package de.hannit.fsch.rcp.klr.handler.csv;

import java.text.NumberFormat;
import java.util.TreeMap;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.UIEventTopic;

import de.hannit.fsch.common.AppConstants;
import de.hannit.fsch.common.ContextLogger;
import de.hannit.fsch.common.MonatsSummen;
import de.hannit.fsch.klr.dataservice.DataService;
import de.hannit.fsch.klr.kostenrechnung.KostenStelle;
import de.hannit.fsch.klr.kostenrechnung.KostenTraeger;
import de.hannit.fsch.klr.kostenrechnung.Kostenrechnungsobjekt;
import de.hannit.fsch.rcp.klr.constants.Topics;

public class Create01Entlastung0400Handler 
{
@Inject @Named(AppConstants.LOGGER) private ContextLogger log;
@Inject DataService dataService;
private MonatsSummen mSummen = null;
private TreeMap<String, KostenStelle> tmKostenstellen = null;
private TreeMap<String, KostenTraeger> tmKostenträger = null;
private double sumKST = 0;

private static final String ZELLE2_NEHME = "0";
private static final String ZELLE2_GEBE = "1";
private static final String ZELLE4_NEHME = "1100100";
private static final String ZELLE4_GEBE = "1110100";
/**
 * "UML-" + Monatsziffer (01,02,03)
 */
private static final String ZELLE5_PRÄFIX = "UML-";
/**
 * "AZV " + Monat lang (MMMM)
 */
private static final String ZELLE6_PRÄFIX = "AZV ";


	
	@Inject @Optional
	public void handleEvent(@UIEventTopic(Topics.MONATSSUMMEN) MonatsSummen incoming)
	{
	this.mSummen = incoming;
	}	

	@Execute
	public void execute() 
	{
	/*
	 * Splitte die Monatssumen nach Kostenstellen und Kostenträgern
	 * Für diese Datei werden nur die Kostenstelle benötigt !
	 */
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
			for (KostenStelle ks : tmKostenstellen.values())
			{
			sumKST += ks.getSumme();	
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