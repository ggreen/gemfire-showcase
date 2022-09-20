package com.vmware.data.services.gemfire.spring.security.data;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import nyla.solutions.core.security.user.data.UserProfile;

/**
 * Implementation of the spring security user 
 * @author Gregory Green
 *
 */
public class UserProfileDetails extends UserProfile implements UserDetails
{


	/**
	 * 
	 */
	private static final long serialVersionUID = 4313385948608665134L;
	private String passwordEncoder = "{noop}";
	private  boolean accountNonExpired = true;
	private boolean accountNonLocked = true;
	private boolean credentialsNonExpired = true;
	private boolean enabled = true;
	private char[] password;
	private Collection<GrantedAuthority> authorities;

	public UserProfileDetails()
	{
	}//------------------------------------------------
	
	public UserProfileDetails(String userName, String password, Collection<GrantedAuthority> grantAuthorities)
	{
		this.setLoginID(userName);
		this.setPassword(password);
		this.setAuthorities(grantAuthorities);
	}

	public String getPasswordEncoder()
	{
		return passwordEncoder;
	}

	public void setPasswordEncoder(String passwordEncoder)
	{
		this.passwordEncoder = passwordEncoder;
	}

	public UserProfileDetails(String userName, String password, String[] roles)
	{
		this.setLoginID(userName);
		this.setPassword(password);
		this.setRoleAuthorities(roles);
		
	}


	/**
	 * @return the authorities
	 */
	public Collection<GrantedAuthority> getAuthorities()
	{
		return authorities;
	}
	/**
	 * @param authorities the authorities to set
	 */
	public void setRoleAuthorities(String[] authorities)
	{
		if(authorities == null || authorities.length == 0)
		{
			this.authorities = null;
		}
		else
		{
			this.setRoleAuthorities(Arrays.asList(authorities));
		}
		
	}
	/**
	 * @param authorities the authorities to set
	 */
	public void setRoleAuthorities(Collection<String> authorities)
	{
		if(authorities == null || authorities.isEmpty())
			this.authorities = null;
		else
		{
			List<GrantedAuthority> l = authorities.stream().map(a -> new SimpleGrantedAuthority(a)).collect(Collectors.toList());
			
			this.authorities = l;
		}
		
	}
	/**
	 * @param authorities the authorities to set
	 */
	public void setAuthorities(Collection<GrantedAuthority> authorities)
	{
		this.authorities = authorities;
	}


	/**
	 * @return the accountNonExpired
	 */
	public boolean isAccountNonExpired()
	{
		return accountNonExpired;
	}


	/**
	 * @return the accountNonLocked
	 */
	public boolean isAccountNonLocked()
	{
		return accountNonLocked;
	}




	/**
	 * @return the credentialsNonExpired
	 */
	public boolean isCredentialsNonExpired()
	{
		return credentialsNonExpired;
	}




	/**
	 * @return the enabled
	 */
	public boolean isEnabled()
	{
		return enabled;
	}


	/**
	 * @return the password
	 */
	public String getPassword()
	{
		if(password == null)
			return null;
		
		return String.valueOf(password);
	}


	/**
	 * @param accountNonExpired the accountNonExpired to set
	 */
	public void setAccountNonExpired(boolean accountNonExpired)
	{
		this.accountNonExpired = accountNonExpired;
	}




	/**
	 * @param accountNonLocked the accountNonLocked to set
	 */
	public void setAccountNonLocked(boolean accountNonLocked)
	{
		this.accountNonLocked = accountNonLocked;
	}




	/**
	 * @param credentialsNonExpired the credentialsNonExpired to set
	 */
	public void setCredentialsNonExpired(boolean credentialsNonExpired)
	{
		this.credentialsNonExpired = credentialsNonExpired;
	}




	/**
	 * @param enabled the enabled to set
	 */
	public void setEnabled(boolean enabled)
	{
		this.enabled = enabled;
	}


	/**
	 * @param password the password to set
	 */
	public void setPassword(String password)
	{
		if(password == null)
		{
			this.password = null;
			return;
		}

		if(!password.startsWith(passwordEncoder))
			password = passwordEncoder.concat(password);

		this.password = password.toCharArray();
	}//------------------------------------------------

	/**
	 * @param password the password to set
	 */
	public void setPassword(char[] password)
	{
		if(password == null)
			this.password = null;
		else
			setPassword(String.valueOf(password));
	}

	@Override
	public String getUsername()
	{
		return getLoginID();
	}
	


}
