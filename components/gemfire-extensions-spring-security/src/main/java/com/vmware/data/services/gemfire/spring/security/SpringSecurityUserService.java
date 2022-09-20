package com.vmware.data.services.gemfire.spring.security;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.vmware.data.services.gemfire.spring.security.data.UserProfileDetails;

@Service
public interface SpringSecurityUserService extends UserDetailsService
{
	public void registerUser(UserProfileDetails userDetails);
	
	public UserProfileDetails findUserProfileDetailsByUserName(String userName)
	throws UsernameNotFoundException;
}
