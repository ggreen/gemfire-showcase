package com.vmware.data.services.gemfire.spring.security.data;


import com.vmware.data.services.gemfire.spring.security.data.UserProfileDetails;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserProfileDetailsTest
{

    @Test
    void password()
    {
        UserProfileDetails subject = new UserProfileDetails();
        String password = "password";
        subject.setPassword(password);
        String expected ="{noop}"+password;
        assertEquals(expected,subject.getPassword());
    }

    @Test
    void setPasswordEncoder()
    {
        UserProfileDetails subject = new UserProfileDetails();
        String encoder = "{decryp}";
        subject.setPasswordEncoder(encoder);
        assertEquals(encoder,subject.getPasswordEncoder());

        String password = "password";
        subject.setPassword(password);
        String expected =encoder+password;
        assertEquals(expected,subject.getPassword());
    }
}