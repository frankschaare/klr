/**
 * 
 */
package de.hannit.fsch.rcp.klr.provider;

import java.net.URL;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.TreeMap;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import de.hannit.fsch.common.csv.azv.Arbeitszeitanteil;
import de.hannit.fsch.common.mitarbeiter.Mitarbeiter;
import de.hannit.fsch.common.mitarbeiter.Team;

/**
 * @author fsch
 *
 */
public class TeamTreeContentProvider extends LabelProvider implements ITreeContentProvider 
{
private URL url = null;
private TreeMap<String, Image> imageCache = null;

	/**
	 * 
	 */
	public TeamTreeContentProvider() 
	{
	Bundle bundle = FrameworkUtil.getBundle(this.getClass());	
	imageCache = new TreeMap<String, Image>();	
	url = FileLocator.find(bundle, new Path("icons/User16px.png"), null);
	ImageDescriptor image = ImageDescriptor.createFromURL(url);
	imageCache.put("userDefault", image.createImage());
	url = FileLocator.find(bundle, new Path("icons/UserYellow16px.png"), null);
	image = ImageDescriptor.createFromURL(url);
	imageCache.put("userYellow", image.createImage());
	url = FileLocator.find(bundle, new Path("icons/UserRemove16px.png"), null);
	image = ImageDescriptor.createFromURL(url);
	imageCache.put("userRemove", image.createImage());
	url = FileLocator.find(bundle, new Path("icons/UserDisabled16px.png"), null);
	image = ImageDescriptor.createFromURL(url);
	imageCache.put("userDisabled", image.createImage());
	url = FileLocator.find(bundle, new Path("icons/clock16px.png"), null);
	image = ImageDescriptor.createFromURL(url);
	imageCache.put("azvClock", image.createImage());
	url = FileLocator.find(bundle, new Path("icons/Users16x16.png"), null);
	image = ImageDescriptor.createFromURL(url);
	imageCache.put("team", image.createImage());
	}	
	
	@Override
	public Image getImage(Object element) 
	{
	String key = null;	
	
		if (element instanceof Mitarbeiter) 
		{
		Mitarbeiter m = (Mitarbeiter)element;	
			if (m.getAzvMonat() != null && m.getAzvMonat().size() > 0)
			{
				if (m.getAzvProzentSumme() == 100)
				{
					if (m.isAzvAktuell())
					{
					key = "userDefault";		
					}
					else
					{
					key = "userYellow";
					}
				}
				else
				{
				key = "userRemove";
				}
			}
			else
			{
			key = "userDisabled";
			}
		}
		else if (element instanceof Arbeitszeitanteil) 
		{
		key = "azvClock";
		}	
		else if (element instanceof Team) 
		{
		key = "team";
		}	
		
	return imageCache.get(key);
	}

	@Override
	public String getText(Object element) 
	{
	String text = "ERROR";
	Mitarbeiter m;
	Team t = null;
	
		if (element instanceof Mitarbeiter) 
		{
		m = (Mitarbeiter)element;	
		text = m.getNachname() + ", " + m.getVorname();	
		}
		else if (element instanceof Arbeitszeitanteil) 
		{
		Arbeitszeitanteil anteil = (Arbeitszeitanteil) element;	
		text = anteil.getKostenstelleOderKostentraegerLang() + " (" + anteil.getProzentanteil() + " % = " + NumberFormat.getCurrencyInstance().format(anteil.getBruttoAufwand()) + ")";
		}
		else if (element instanceof Team) 
		{
		t = (Team) element;	
		
			switch (t.getTeamNummer())
			{
			case -1: break;
			default:
			text = t.getTeamBezeichnung();	
			break;
			}
		}	
	return text;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	@Override
	public void dispose() 
	{
		if (imageCache != null)
		{
			for (Image img : imageCache.values())
			{
			img.dispose();	
			}
		imageCache = null;	
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) 
	{

	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getElements(java.lang.Object)
	 */
	@Override
	public Object[] getElements(Object inputElement) 
	{
	Object[] objects = null;
	
		if (inputElement instanceof Team) 
		{
		objects = ((Team) inputElement).getTeamMitglieder().values().toArray();	
		}
		else if (inputElement instanceof TreeMap<?, ?>) 
		{
		objects = ((TreeMap<Integer, Team>) inputElement).values().toArray();	
		}
		
	return objects;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
	 */
	@Override
	public Object[] getChildren(Object parentElement) 
	{
	Object[] objects = null;	
	Mitarbeiter m = null;
	Team t = null;
	
		if (parentElement instanceof Mitarbeiter)
		{
		m = (Mitarbeiter) parentElement;
		objects = m.getAzvMonat().values().toArray();
		}
		else if (parentElement instanceof Team) 
		{
		t = (Team) parentElement;	
		objects = t.getTeamMitglieder().values().toArray();
		}

	return objects;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
	 */
	@Override
	public Object getParent(Object element) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
	 */
	@Override
	public boolean hasChildren(Object element) 
	{
	boolean hasChildren = false;	
	Mitarbeiter m = null;
	Team t = null;
	
		if (element instanceof Mitarbeiter)
		{
		m = (Mitarbeiter) element;	
			if (m.getAzvMonat() != null)
			{	
			hasChildren = m.getAzvMonat().size() > 0 ? true : false;	
			}
		}
		else if (element instanceof Team) 
		{
		t = (Team) element;	
		hasChildren = t.getTeamMitglieder().size() > 0 ? true : false;
		}
	return hasChildren;
	}

}
