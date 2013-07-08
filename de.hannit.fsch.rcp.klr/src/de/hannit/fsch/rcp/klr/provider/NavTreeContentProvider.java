/**
 * 
 */
package de.hannit.fsch.rcp.klr.provider;

import java.net.URL;
import java.util.ArrayList;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import de.hannit.fsch.common.mitarbeiter.Mitarbeiter;

/**
 * @author fsch
 *
 */
public class NavTreeContentProvider extends LabelProvider implements ITreeContentProvider 
{
	/**
	 * 
	 */
	public NavTreeContentProvider() 
	{
		
	}	
	
	@Override
	public Image getImage(Object element) 
	{
	Bundle bundle = FrameworkUtil.getBundle(this.getClass());
	URL url = FileLocator.find(bundle, new Path("icons/User16px.png"), null);		
	ImageDescriptor image = ImageDescriptor.createFromURL(url);	
	return image.createImage();
	}

	@Override
	public String getText(Object element) 
	{
	String text = "ERROR";
	Mitarbeiter m;
	
		if (element instanceof Mitarbeiter) 
		{
		m = (Mitarbeiter)element;	
		text = m.getNachname() + ", " + m.getVorname();	
		}	
	
	return text;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	@Override
	public void dispose() {
		// TODO Auto-generated method stub

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
	
		if (inputElement instanceof ArrayList<?>) 
		{
		objects = ((ArrayList<Mitarbeiter>) inputElement).toArray();	
		}
		
	return objects;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
	 */
	@Override
	public Object[] getChildren(Object parentElement) {
		// TODO Auto-generated method stub
		return null;
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
	public boolean hasChildren(Object element) {
		// TODO Auto-generated method stub
		return false;
	}

}
