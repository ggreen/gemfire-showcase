package tanzu.gemfire.security;

import nyla.solutions.core.util.Cryption;

public class SecurityCryption
{
	public static void main(String[] args)
	{
		if(args.length == 0)
		{
			System.out.println("Usage: java "+SecurityCryption.class.getName()+" password");
			System.exit(0);
		}
		Cryption.main(args);
	}
}
