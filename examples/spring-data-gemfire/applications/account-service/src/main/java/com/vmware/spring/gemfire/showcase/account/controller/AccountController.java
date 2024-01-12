package com.vmware.spring.gemfire.showcase.account.controller;

import com.vmware.spring.gemfire.showcase.account.controller.exceptions.GemFireNotAvailableException;
import com.vmware.spring.gemfire.showcase.account.repostories.AccountRepository;
import com.vmware.spring.gemfire.showcase.account.entity.Account;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.geode.cache.client.NoAvailableLocatorsException;
import org.apache.geode.cache.client.NoAvailableServersException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@AllArgsConstructor
@RestController
@Slf4j
public class AccountController
{
    private final AccountRepository accountRepository;

    @PostMapping("accounts")
    public <S extends Account> S save(@RequestBody S account)
    {
        return accountRepository.save(account);
    }

    @GetMapping("accounts/{id}")
    public Optional<Account> findById(@PathVariable String id)
    {
       try{
           return accountRepository.findById(id);
       }
       catch(DataAccessResourceFailureException e)
       {
           log.warn("ERROR: {}",e);

           var cause = e.getCause();
           if(cause instanceof NoAvailableServersException ||
                   cause instanceof NoAvailableLocatorsException){
               throw new GemFireNotAvailableException(e);
           }
           throw e;
       }
    }

    @DeleteMapping("accounts/{id}")
    public void deleteById(@PathVariable String id)
    {
        accountRepository.deleteById(id);
    }
}
