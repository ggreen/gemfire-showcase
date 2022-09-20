package com.vmware.data.services.gemfire.spring.security;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;

import org.apache.geode.cache.Region;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.vmware.data.services.gemfire.spring.security.data.UserProfileDetails;

public class GemFireUserDetailsServiceTest
{
	@SuppressWarnings("unchecked")
	@Test
	public void testLoadUserByUsernameStrings()
	{
		String[] roles = {"READ", "WRITE"};
		
		UserProfileDetails nyla = new UserProfileDetails("nyla", "nyla", roles);


		Region<String, UserProfileDetails> region = mock(Region.class);
		when(region.get("nyla")).thenReturn(nyla);
		
		GemFireUserDetailsService us = new GemFireUserDetailsService(region);
		
		us.registerUser(nyla);
		
		verify(region).create(anyString(), any(UserProfileDetails.class));
		
		try{us.loadUserByUsername(null);} catch(UsernameNotFoundException e) {}
		
		try
		{
			 us.loadUserByUsername("ggreen");
			//fail();
		}
		catch(UsernameNotFoundException e) {}
		
		UserDetails nylaOut = us.loadUserByUsername("nyla");
		assertNotNull(nylaOut);
		
		 assertTrue(nylaOut
		 		.getAuthorities()
		 		.stream().anyMatch(a -> "READ".equals(a.getAuthority())));
		 
		 
		 assertTrue(nylaOut
	 		.getAuthorities()
	 		.stream().anyMatch(a -> "WRITE".equals(a.getAuthority())));
		 
			
	}
	@SuppressWarnings("unchecked")
	@Test
	public void testLoadUserByUsername()
	{
		GrantedAuthority[] roles = {new SimpleGrantedAuthority("READ"), new SimpleGrantedAuthority("WRITE")};
		
		UserProfileDetails nyla = new UserProfileDetails("nyla", "nyla", Arrays.asList(roles));


		Region<String, UserProfileDetails> region = mock(Region.class);
		when(region.get("nyla")).thenReturn(nyla);
		
		GemFireUserDetailsService us = new GemFireUserDetailsService(region);
		
		us.registerUser(nyla);
		
		verify(region).create(anyString(), any(UserProfileDetails.class));
		
		try
		{
			us.loadUserByUsername(null);
			//fail();
		}
		catch(UsernameNotFoundException e)
		{}
		
		try
		{
			us.loadUserByUsername("ggreen");
			//fail();
		}
		catch(UsernameNotFoundException e)
		{}
		
		UserDetails nylaOut = us.loadUserByUsername("nyla");
		assertNotNull(nylaOut);
		
		 assertTrue(nylaOut
		 		.getAuthorities()
		 		.stream().anyMatch(a -> "READ".equals(a.getAuthority())));
		 
		 
		 assertTrue(nylaOut
	 		.getAuthorities()
	 		.stream().anyMatch(a -> "WRITE".equals(a.getAuthority())));
		 
			
	}

}
