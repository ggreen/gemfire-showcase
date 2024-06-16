package com.vmware.spring.gemfire.showcase.account.controller;

import com.vmware.spring.gemfire.showcase.account.controller.exceptions.GemFireNotAvailableException;
import com.vmware.spring.gemfire.showcase.account.entity.Account;
import com.vmware.spring.gemfire.showcase.account.repostories.AccountRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.geode.cache.client.NoAvailableLocatorsException;
import org.apache.geode.cache.client.NoAvailableServersException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@Slf4j
@RequestMapping("accounts")
public class AccountController
{
    private final AccountRepository accountRepository;

    @PostMapping
    public Account save(@RequestBody Account account)
    {
        return accountRepository.save(account);
    }

    @GetMapping("{id}")
    public Account findById(@PathVariable("id") String id)
    {
       try{
           return accountRepository.findById(id).orElse(null);
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

    @DeleteMapping
    public void deleteById(@RequestParam("id") String id)
    {
        accountRepository.deleteById(id);
    }
}
