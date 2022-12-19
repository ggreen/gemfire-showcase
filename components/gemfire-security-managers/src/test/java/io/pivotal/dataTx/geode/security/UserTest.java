package io.pivotal.dataTx.geode.security;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import io.pivotal.dataTx.geode.security.User;

public class UserTest
{

	@Test
	public void testToStringDoesNotHavePassword()
	{
		User user = new User();
		
		byte[] passwords = {1,23,23};
		
		user.setEncryptedPassword(passwords);
		
		assertTrue(!user.toString().contains("encryptedPassword"));
	}

}
