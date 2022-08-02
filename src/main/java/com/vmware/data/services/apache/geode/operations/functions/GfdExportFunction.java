package com.vmware.data.services.apache.geode.operations.functions;

import java.io.File;
import java.util.Properties;
import org.apache.geode.cache.Cache;
import org.apache.geode.cache.CacheFactory;
import org.apache.geode.cache.Declarable;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.execute.Function;
import org.apache.geode.cache.execute.FunctionContext;
import org.apache.geode.cache.execute.FunctionException;
import org.apache.geode.cache.execute.RegionFunctionContext;
import org.apache.geode.cache.execute.ResultSender;
import org.apache.geode.cache.partition.PartitionRegionHelper;
import org.apache.geode.cache.snapshot.RegionSnapshotService;
import org.apache.geode.cache.snapshot.SnapshotOptions.SnapshotFormat;
import org.apache.geode.internal.cache.snapshot.SnapshotOptionsImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.vmware.data.services.apache.geode.data.ExportFileType;
import nyla.solutions.core.util.Debugger;

/**
 * <pre>
 * 
 * Exports the GDF binary containing all region data on the server.
 * 
 * The output will be stored in the current working directory.
 * One file will be written per region (format: ${region.name}.gfd)
 * 
 * Example:			gfsh&gt;gexecute function --id="GfdExportFunction" --region-myRegion
 * </pre>
 * 
 * @author Gregory Green
 *
 */
public class GfdExportFunction  implements Function<Object>, Declarable
{	
	/**
	 * String keyFileExtension = ".key"
	 */
	public static final String keyFileExtension = ".key";
	
	public GfdExportFunction()
	{
	}// ------------------------------------------------
	
	/**
	 * Export region data in JSON format
	 * @param fc the function context
	 */
	public void execute(FunctionContext<Object> fc)
	{
		
		ResultSender<Object> rs = fc.getResultSender();
		try
		{
			boolean didExport = false;
			
			if(fc instanceof RegionFunctionContext)
			{
				didExport = exportOnRegion((RegionFunctionContext)fc);	
			}
			else
			{
				
				//get region name from argument
				String[] args = (String[])fc.getArguments();
				
				if(args == null || args.length == 0)
					throw new IllegalArgumentException("Region name argument required");
				
				String regionName = args[0];
				
				Cache cache = CacheFactory.getAnyInstance();
				
	
				
				Region<Object,Object> region = cache.getRegion(regionName);
				
				if(region != null)
					didExport = exportRegion(region);
				else
					didExport = false;		
				
			}
			
			rs.lastResult(didExport);
		}
		catch (Exception e)
		{
			
			String stackTrace = Debugger.stackTrace(e);
			
			FunctionException functionException = new FunctionException(stackTrace);
			
			LogManager.getLogger(getClass()).error(stackTrace);
			rs.sendException(functionException);
			throw functionException;
		}
		
	    
	}// --------------------------------------------------------

	private boolean exportOnRegion(RegionFunctionContext rfc)
	{
		//get argument 
		
		//check if region is partitioned
	
		Region<Object,Object> region = rfc.getDataSet();
	    
		
	    return exportRegion(region);
	}// ------------------------------------------------

	protected boolean  exportRegion(Region<Object, Object> region)
	{	
		if(region  == null)
			return false;
		
		if(PartitionRegionHelper.isPartitionedRegion(region))
		{
			region = PartitionRegionHelper.getLocalData(region);
		}
		
		Logger logger = LogManager.getLogger(getClass());
		
		logger.info("Exporting region"+region.getName());
		
		//get name
	    String regionName = region.getName();
	    
		File resultFile = DataOpsSecretary.determineFile(ExportFileType.gfd, regionName);
		
		//delete previous
		logger.info("deleting file:"+resultFile.getAbsolutePath());
		boolean wasDeleted = resultFile.delete();
		
		logger.info("delete:"+wasDeleted);
		
	    try
		{
			//write data
	    	RegionSnapshotService<?,?> regionSnapshotService = region.getSnapshotService();
	    	SnapshotOptionsImpl<?,?> options = (SnapshotOptionsImpl<?,?>) regionSnapshotService.createOptions();
	    	
	    	//setting parallelMode=true will cause only the local region data to export
	    	options.setParallelMode(true);
	    	regionSnapshotService.save(resultFile, SnapshotFormat.GEMFIRE);
	    	    
			return true;
			
		}
		catch (RuntimeException e)
		{
			throw e;
		}
	    catch(Exception e)
	    {
	    	throw new FunctionException("Error exporting ERROR:"+ e.getMessage()+" "+Debugger.stackTrace(e));
	    }
	}// ------------------------------------------------
	
	public String getId()
	{
		
		return "GfdExportFunction";
	}

	public boolean hasResult()
	{
		return true;
	}

	public boolean isHA()
	{
		return false;
	}

	public boolean optimizeForWrite()
	{
		return false;
	}
	
	
	@Override
	public void init(Properties properties)
	{
		
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3148806554381339703L;




	
}
