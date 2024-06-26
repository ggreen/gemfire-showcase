package com.vmware.spring.gemfire.showcase.account.repostories;

import com.vmware.spring.gemfire.showcase.account.entity.Account;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


    @Repository
    public interface AccountRepository
    extends CrudRepository<Account,String>
    {
    }
