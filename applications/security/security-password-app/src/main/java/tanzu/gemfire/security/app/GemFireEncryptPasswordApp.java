package tanzu.gemfire.security.app;

import nyla.solutions.core.util.Cryption;

/**
 * Generates an encrypted passwords.
 *
 */
public class GemFireEncryptPasswordApp
{
    public static void main( String[] args )
    {
    	if(args == null || args.length == 0)
    	{
    		System.err.println("ERROR: Password argument required!!");
    		return;
    		
    	}
        Cryption.main(args);
    }
}
