package com.vmware.spring.geode.showcase.account.repostories;

import com.vmware.spring.geode.showcase.account.entity.Account;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


    @Repository
    public interface AccountRepository
    extends CrudRepository<Account,String>
    {
    }
