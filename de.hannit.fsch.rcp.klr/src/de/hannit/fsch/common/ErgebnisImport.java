/**
 * 
 */
package de.hannit.fsch.common;

import java.io.File;
import java.net.URI;

import de.hannit.fsch.rcp.klr.csv.CSVDatei;


/**
 * @author fsch
 *
 */
@SuppressWarnings("serial")
public class ErgebnisImport extends CSVDatei
{

	/**
	 * @param arg0
	 */
	public ErgebnisImport(String arg0)
	{
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 */
	public ErgebnisImport(URI arg0)
	{
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public ErgebnisImport(String arg0, String arg1)
	{
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public ErgebnisImport(File arg0, String arg1)
	{
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}

}
