package tanzu.gemfire.security.app;


import org.junit.jupiter.api.Test;

/**
 * Unit test Password encryption
 */
public class GemFireEncryptPasswordAppTest
{
	@Test
    public void testApp()
    {
		System.setProperty("CRYPTION_KEY", "DATA_TX_JUNIT_TEST");
		String[] args = {"mypassword"};
		GemFireEncryptPasswordApp.main(args);
    }
}
