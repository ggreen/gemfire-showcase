package com.vmware.spring.geode.showcase.account.controller;

import com.vmware.spring.geode.showcase.account.entity.Account;
import com.vmware.spring.geode.showcase.account.repostories.AccountRepository;
import nyla.solutions.core.patterns.creational.generator.JavaBeanGeneratorCreator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountControllerTest
{
    @Mock
    private AccountRepository accountRepository;

    private AccountController subject;
    private Account account;

    @BeforeEach
    void setUp()
    {


        account = JavaBeanGeneratorCreator.of(Account.class).create();
        subject = new AccountController(accountRepository);


    }

    @Test
    @DisplayName("Given account When save Then Can get account by Id")
    void createRead()
    {
        when(accountRepository.findById(account.getId())).thenReturn(Optional.of(account));
        subject.save(account);
        verify(accountRepository).save(account);
        assertEquals(account,subject.findById(account.getId()).get());

    }

    @Test
    @DisplayName("Given saved account When delete Then repository delete called")
    void delete()
    {
        subject.deleteById(account.getId());
        verify(accountRepository).deleteById(account.getId());
    }
}