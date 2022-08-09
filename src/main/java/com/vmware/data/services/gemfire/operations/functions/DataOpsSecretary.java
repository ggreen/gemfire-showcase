package com.vmware.data.services.gemfire.operations.functions;

import java.io.File;

import com.vmware.data.services.gemfire.data.ExportFileType;


/**
 * Helper class for data manager operations
 * @author Gregory Green
 *
 */
public class DataOpsSecretary
{
	/**
	 * directoryPath = System.getProperty("io.pivotal.gemfire_addon.dataOps.DataOpsSecretary.directoryPath",".")
	 */
	public static final String directoryPath = System.getProperty("io.pivotal.gemfire_addon.dataOps.DataOpsSecretary.directoryPath",".");
	
	private static String fileSeparator = System.getProperty("file.separator");
	
	public  static final String EXPORT_FILE_TYPE_USAGE = "(gfd|json)";
	
	/**
	 * 
	 * @param exportFileType the export file type
	 * @param regionName the region
	 * @return the file 
	 */
	public static File determineFile(ExportFileType exportFileType,String regionName)
	{
		
		File resultFile = new File(new StringBuilder(directoryPath)
		.append(fileSeparator).append(regionName).append(".").append(exportFileType).toString());
		return resultFile;
	}// --------------------------------------------------------

	/**
	 * 
	 * @param extension the value
	 * @return ExportFileType
	 */
	public static ExportFileType determineType(String extension)
	{
		try
		{
			return ExportFileType.valueOf(extension);
		}
		catch (Exception e)
		{
			throw new IllegalArgumentException("Exported type extension:"+EXPORT_FILE_TYPE_USAGE);
		}
		
	}// --------------------------------------------------------
}
