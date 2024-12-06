package tanzu.gemfire.security.exceptions;

import nyla.solutions.core.exception.fault.FaultException;

@SuppressWarnings("serial")
public class MissingSecurityProperty extends FaultException
{

	public MissingSecurityProperty(String property)
	{
		super("Missing property:"+property);
		this.setCode("SEC_MISSING_CODE");		
	}
	

}
