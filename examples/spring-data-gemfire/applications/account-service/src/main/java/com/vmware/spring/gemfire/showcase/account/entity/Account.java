package com.vmware.spring.gemfire.showcase.account.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.gemfire.mapping.annotation.Region;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Account
{
    private String id;
    private String name;
}




