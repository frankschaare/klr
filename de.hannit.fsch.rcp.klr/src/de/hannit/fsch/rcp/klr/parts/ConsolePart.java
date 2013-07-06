 
package de.hannit.fsch.rcp.klr.parts;

import javax.inject.Inject;
import javax.annotation.PostConstruct;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;

import de.hannit.fsch.common.CSVDatei;
import de.hannit.fsch.rcp.klr.constants.Topics;
import de.hannit.fsch.rcp.klr.provider.CSVLabelProvider;

public class ConsolePart 
{
private Label message = null;

	@Inject
	public ConsolePart() 
	{
		//TODO Your code here
	}
	
	@Inject
	@Optional
	public void handleEvent(@UIEventTopic(Topics.LOGGING) String msg)
	{
		if (message != null) 
		{
		message.setText(msg);	
		}
	}	
	
	@PostConstruct
	public void postConstruct(Composite parent) {
		parent.setLayout(new GridLayout(1, false));
		
		message = new Label(parent, SWT.NONE);
		message.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1));
		message.setText("message");
		//TODO Your code here
	}
	
	
	
	@Focus
	public void onFocus() {
		//TODO Your code here
	}
}