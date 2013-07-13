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

/**
 * @author fsch
 * @since 14.06.2013
 *
 */
public class CSVDatei extends File 
{
/**
 * 
 */
private static final long serialVersionUID = 7390814932872973058L;

private Charset charset = Charset.forName("ISO-8859-1");
private List<String> lines;
private ArrayList<String[]> fields = new ArrayList<String[]>();	

protected boolean hasHeader = true;
protected String delimiter = ";";
protected int lineCount = -1;
protected ContextLogger log;

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
	
	public void resetLineCount(){this.lineCount = 1;}
	
	public ContextLogger getLog()
	{
	return log;
	}

	public void setLog(ContextLogger log)
	{
	this.log = log;
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
