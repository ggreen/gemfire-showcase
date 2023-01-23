package io.spring.gemfire.rest.app.exception;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;


public class FaultAgentTest
{

	/**
	 * Test handle exception
	 */
	@Test
	public void testHandleException()
	{
		FaultAgent fa = new FaultAgent();
		
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
	
		DataError de = fa.handleException(request, response, null);
		
		assertNotNull(de);
		
		
	}//------------------------------------------------

}
