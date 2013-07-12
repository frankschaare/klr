/**
 * 
 */
package de.hannit.fsch.common;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.osgi.service.event.Event;

/**
 * @author fsch
 * @since 14.06.2013
 *
 */
public class CSVDatei extends File 
{
private Charset charset = Charset.forName("ISO-8859-1");
private List<String> lines;
private ArrayList<String[]> fields = new ArrayList<String[]>();	

private boolean hasHeader = true;
private String delimiter = ";";
private int lineCount = -1;

protected TreeMap<Integer, Event> logStack;
protected IEclipseContext appContext = null;
protected IEventBroker broker = null;
	/**
	 * @param arg0
	 */
	public CSVDatei(String arg0) 
	{
	super(arg0);
	}

	/**
	 * @param arg0
	 */
	public CSVDatei(URI arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public CSVDatei(String arg0, String arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public CSVDatei(File arg0, String arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}
	
	public void read() 
	{
	lineCount = 1;
		try 
		{
		lines = Files.readAllLines(Paths.get(super.getPath()), charset);
			for (String line : lines)
			{
			switch (lineCount) 
			{
			// Erste Zeile wird nur verarbeitet wenn keine Kopfzeile vorhanden ist
			case 1:
				if (!hasHeader) 
				{
				fields.add(line.split(delimiter));	
				}
			break;

			default:
			fields.add(line.split(delimiter));
			break;
			}	
			lineCount++;
			}	
		} 
		catch (IOException e) 
		{
		e.printStackTrace();	
		}
		
	}
	
	
	public List<String> getLines() 
	{
	return lines;
	}

	public ArrayList<String[]> getFields() 
	{
		return fields;
	}

	public boolean isHeader() {
		return hasHeader;
	}

	public void hasHeader(boolean hasHeader) 
	{
		this.hasHeader = hasHeader;
	}

	public String getDelimiter() {
		return delimiter;
	}

	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}

	public int getLineCount() 
	{
		if (hasHeader) 
		{
		return (lineCount - 2);	
		} 
		else 
		{
		return (lineCount - 1);
		}
	}

	public void setLineCount(int lineCount) {
		this.lineCount = lineCount;
	}
	
	

}
