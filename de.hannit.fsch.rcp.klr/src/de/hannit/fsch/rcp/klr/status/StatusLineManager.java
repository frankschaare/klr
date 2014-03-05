/**
 * 
 */
package de.hannit.fsch.rcp.klr.status;

import javax.annotation.PostConstruct;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;

/**
 * @author fsch
 *
 */
public class StatusLineManager extends org.eclipse.jface.action.StatusLineManager 
{

	/**
	 * 
	 */
	public StatusLineManager()
	{
		// TODO Auto-generated constructor stub
	}
	
	@PostConstruct
	public void createComposite(Composite parent) 
	{
		parent.setLayout(new GridLayout(1, true));
		
		Label lblNewLabel = new Label(parent, SWT.NONE);
		lblNewLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		lblNewLabel.setText("StatusLine");
	System.out.println("Ole !");	
	}

}
