package com.vmware.data.services.apache.geode.operations.csv.function;

import static org.mockito.Mockito.*;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.execute.FunctionContext;
import org.apache.geode.cache.execute.ResultSender;
import org.junit.jupiter.api.*;

import com.vmware.data.services.apache.geode.operations.csv.function.ExportCsvFunction;

public class ExportCsvFunctionTest
{

	@SuppressWarnings("unchecked")
	@Test
	public void testExportRegion()
	{
		ExportCsvFunction csvFunc = new ExportCsvFunction();
		
		FunctionContext<String[]> fcArgs = mock(FunctionContext.class);
		
		String[] args = {"test","/tmp"};
		
		when(fcArgs.getArguments()).thenReturn(args);
		ResultSender<Object> rs = mock(ResultSender.class);
		
		when(fcArgs.getResultSender()).thenReturn(rs);
		
		//csvFunc.execute(fcArgs);
		
		//verify(rs,times(0)).sendException(any());
		
		Region<Object,Object> region = mock(Region.class); 
		csvFunc.exportRegion(region, "runtime/");
	}

}
