package de.hannit.fsch.rcp.klr.status;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ProgressBar;

public class ProgressMonitor implements IProgressMonitor
{
@Inject UISynchronize sync;
private ProgressBar progressBar;

	public ProgressMonitor()
	{
		// TODO Auto-generated constructor stub
	}
	
	@PostConstruct
	public void createControls(Composite parent) 
	{
	progressBar = new ProgressBar(parent, SWT.SMOOTH);
	progressBar.setBounds(100, 10, 200, 20);
	}	

	@Override
	public void beginTask(String name, int totalWork)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void done()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void internalWorked(double work)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isCanceled()
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setCanceled(boolean value)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void setTaskName(String name)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void subTask(String name)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void worked(int work)
	{
		// TODO Auto-generated method stub

	}

}
