package com.vmware.data.services.gemfire.spring.security.data;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public class RegistrationDTO
{
	
	private String userName;
	private char[] password;
	private String email;
	private String firstName;
	private String lastName;
	private String title;
	/**
	 * @return the userName
	 */
	public String getUserName()
	{
		return userName;
	}
	/**
	 * @return the password
	 */
	public char[] getPassword()
	{
		return password;
	}
	/**
	 * @return the email
	 */
	public String getEmail()
	{
		return email;
	}
	/**
	 * @return the firstName
	 */
	public String getFirstName()
	{
		return firstName;
	}
	/**
	 * @return the lastName
	 */
	public String getLastName()
	{
		return lastName;
	}
	/**
	 * @return the title
	 */
	public String getTitle()
	{
		return title;
	}
	/**
	 * @param userName the userName to set
	 */
	public void setUserName(String userName)
	{
		this.userName = userName;
	}
	/**
	 * @param password the password to set
	 */
	public void setPassword(char[] password)
	{
		this.password = password;
	}
	/**
	 * @param email the email to set
	 */
	public void setEmail(String email)
	{
		this.email = email;
	}
	/**
	 * @param firstName the firstName to set
	 */
	public void setFirstName(String firstName)
	{
		this.firstName = firstName;
	}
	/**
	 * @param lastName the lastName to set
	 */
	public void setLastName(String lastName)
	{
		this.lastName = lastName;
	}
	/**
	 * @param title the title to set
	 */
	public void setTitle(String title)
	{
		this.title = title;
	}//------------------------------------------------
	public UserProfileDetails toUserDetails(String authority)
	{
		return toUserDetails(Collections.singleton(authority));
	}//------------------------------------------------
	public UserProfileDetails toUserDetails(Collection<String> authorities)
	{
		if (this.userName == null || this.userName.length() == 0)
			throw new IllegalArgumentException("userName is required");
		
		if (password == null || password.length == 0)
			throw new IllegalArgumentException("Password is required");
		
		if (authorities == null || authorities.isEmpty())
			throw new IllegalArgumentException("authorities is required");
		
		Collection<GrantedAuthority> grantAuthorities = authorities.stream()
		.map(a -> new SimpleGrantedAuthority(a)).collect(Collectors.toSet());
		
		UserProfileDetails user = new UserProfileDetails(this.userName,String.valueOf(password),
		grantAuthorities);
		
		user.setEmail(email);
		user.setFirstName(firstName);
		user.setLastName(lastName);
		return user;
	}
	public UserProfileDetails toUserDetails(String[] roles)
	{
		if (roles == null || roles.length == 0)
			throw new IllegalArgumentException("roles is required");
		return toUserDetails(Arrays.asList(roles));
	}
	
	
	

}
