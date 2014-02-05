/**
 * 
 */
package de.hannit.fsch.common;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TreeMap;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

import de.hannit.fsch.common.mitarbeiter.besoldung.Tarifgruppe;
import de.hannit.fsch.klr.kostenrechnung.Kostenrechnungsobjekt;

/**
 * @author fsch
 *
 */
public class MonatsSummen implements ITableLabelProvider
{
private TreeMap<String, Double> gesamtSummen = null;
private TreeMap<String, Kostenrechnungsobjekt> gesamtKosten = null;
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
private	SimpleDateFormat datumsformat = new SimpleDateFormat("yyyy-MM-dd");

private String label;

	/**
	 * 
	 */
	public MonatsSummen()
	{
		// TODO Auto-generated constructor stub
	}

	public void setGesamtSummen(TreeMap<String, Double> gesamtSummen)
	{
	this.gesamtSummen = gesamtSummen;
	gesamtKosten = new TreeMap<String, Kostenrechnungsobjekt>();
	
		for (String s : gesamtSummen.keySet())
		{
		kto = new Kostenrechnungsobjekt();	
		kto.setBezeichnung(s);
		kto.setSumme(gesamtSummen.get(s));
		
		gesamtKosten.put(s, kto);
		}
	}
	
	public String getBerichtsMonat()
	{
	return datumsformat.format(berichtsMonat);
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

	public TreeMap<String, Kostenrechnungsobjekt> getGesamtKosten()
	{
	return gesamtKosten;
	}

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
		label = kto.getBezeichnung();
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
