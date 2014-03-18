/**
 * 
 */
package de.hannit.fsch.common;

import java.text.NumberFormat;
import java.util.Date;
import java.util.TreeMap;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

import de.hannit.fsch.common.csv.azv.Arbeitszeitanteil;
import de.hannit.fsch.common.mitarbeiter.Mitarbeiter;
import de.hannit.fsch.klr.kostenrechnung.Kostenrechnungsobjekt;

/**
 * @author fsch
 *
 */
public class MonatsSummen implements ITableLabelProvider
{
private TreeMap<String, Kostenrechnungsobjekt> gesamtKosten = null;
private TreeMap<String, Kostenrechnungsobjekt> gesamtKostenstellen = null;
private TreeMap<String, Kostenrechnungsobjekt> gesamtKostentraeger = null;
private Kostenrechnungsobjekt kto = null;
private double kstktrMonatssumme = 0;
/**
 * Die folgenden Flags werden im NavPart gesetzt. Dort werden die Monatsummen geprüft
 * und es wird ermittelt, ob die Monatssummen der gemeldeten AZV-Anteile gleich des Bruttoaufwandes ist.
 * 
 * Nur, wenn diese beiden Bedingungen erfüllt sind, können die Daten gespeichert werden !
 */
private boolean isChecked = false;
private boolean summeOK = false;

private Date berichtsMonat = null;
private String label;

	/**
	 * 
	 */
	public MonatsSummen()
	{
		// TODO Auto-generated constructor stub
	}
	
	public void setGesamtSummen(TreeMap<Integer, Mitarbeiter> incoming)
	{
	gesamtKosten = new TreeMap<String, Kostenrechnungsobjekt>();
	gesamtKostenstellen = new TreeMap<String, Kostenrechnungsobjekt>();
	gesamtKostentraeger = new TreeMap<String, Kostenrechnungsobjekt>();
	
		for (Mitarbeiter m : incoming.values())
		{	
			for (Arbeitszeitanteil azv : m.getAzvMonat().values())
			{
				/*
				 * Ist die Kostenstelle / Kostenträger bereits in den Monatssummen gespeichert ?
				 * Wenn Ja, wird der Bruttoaufwand addiert,
				 * Wenn Nein, wird die Kostenstelle / Kostenträger neu eingefügt:
				 */
				String bezeichnung = (azv.getKostenstelle() != null) ? azv.getKostenstelle() : azv.getKostentraeger();
				if (gesamtKosten.containsKey(bezeichnung))
				{
				kto = gesamtKosten.get(bezeichnung);	
				kto.setSumme((kto.getSumme() + azv.getBruttoAufwand()));
				}
				else
				{
				kto = new Kostenrechnungsobjekt();	
				kto.setBezeichnung(bezeichnung);
				kto.setBeschreibung(azv.isKostenstelle() ? azv.getKostenStelleBezeichnung() : azv.getKostenTraegerBezeichnung());
				kto.setSumme(azv.getBruttoAufwand());
				
				gesamtKosten.put(bezeichnung, kto);
				}
			}
		}
		for (Kostenrechnungsobjekt k : gesamtKosten.values())
		{
			if (k.getArt().equalsIgnoreCase(Kostenrechnungsobjekt.KST))
			{
			gesamtKostenstellen.put(k.getBezeichnung(), k);	
			}
			else
			{
			gesamtKostentraeger.put(k.getBezeichnung(), k);	
			}
			
		}
	}	
	
	public String getBerichtsMonat()
	{
	return Datumsformate.STANDARDFORMAT_SQLSERVER.format(berichtsMonat);
	}
	
	public Date getBerichtsMonatAsDate()
	{
	return berichtsMonat;
	}

	public void setBerichtsMonat(Date berichtsMonat)
	{
	this.berichtsMonat = berichtsMonat;
	}

	/*
	 * Wurden die Monatssummen geprüft ?
	 */
	public boolean isChecked() {return isChecked;}
	public void setChecked(boolean isChecked) {this.isChecked = isChecked;}

	/*
	 * Stimmen die Monatssummen mit dem Gesamtbruttoaufwand überein ?
	 */
	public boolean isSummeOK() {return summeOK;}
	public void setSummeOK(boolean summeOK) {this.summeOK = summeOK;}

	public TreeMap<String, Kostenrechnungsobjekt> getGesamtKosten()	{return gesamtKosten;}
	public TreeMap<String, Kostenrechnungsobjekt> getGesamtKostenstellen()	{return gesamtKostenstellen;}
	public TreeMap<String, Kostenrechnungsobjekt> getGesamtKostentraeger()	{return gesamtKostentraeger;}

	/*
	 * Die Gesamtsumme alle gemeldeten Kostenstellen / Kostenträger
	 */
	public double getKstktrMonatssumme()
	{
	kstktrMonatssumme = 0;

		for (String s : gesamtKosten.keySet())
		{
		kstktrMonatssumme += gesamtKosten.get(s).getSumme();	
		}

	return kstktrMonatssumme;
	}

	/*
	 * Die Gesamtsumme alle gemeldeten Kostenstellen
	 */
	public double getKSTMonatssumme()
	{
	double result = 0;

		for (String s : gesamtKostenstellen.keySet())
		{
		result += gesamtKostenstellen.get(s).getSumme();	
		}

	return result;
	}
	
	/*
	 * Die Gesamtsumme alle gemeldeten Kostenstellen
	 */
	public double getKTRMonatssumme()
	{
	double result = 0;

		for (String s : gesamtKostentraeger.keySet())
		{
		result += gesamtKostentraeger.get(s).getSumme();	
		}

	return result;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#addListener(org.eclipse.jface.viewers.ILabelProviderListener)
	 */
	@Override
	public void addListener(ILabelProviderListener listener)
	{
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
	 */
	@Override
	public void dispose()
	{
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(java.lang.Object, java.lang.String)
	 */
	@Override
	public boolean isLabelProperty(Object element, String property)
	{
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(org.eclipse.jface.viewers.ILabelProviderListener)
	 */
	@Override
	public void removeListener(ILabelProviderListener listener)
	{
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
	 */
	@Override
	public Image getColumnImage(Object element, int columnIndex)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
	 */
	@Override
	public String getColumnText(Object element, int columnIndex)
	{
	kto = (Kostenrechnungsobjekt) element;

		switch (columnIndex) 
		{
		case 0:
		label = (kto.getBeschreibung() != null) ? kto.getBezeichnung() + ": " + kto.getBeschreibung() : kto.getBezeichnung() ;
		break;
		
		case 1:
		label = NumberFormat.getCurrencyInstance().format((kto.getSumme()));
		break;
		
		default:
		label = "ERROR";
		break;
			
		}
	return label;
	}
}
