package com.vmware.spring.gemfire.showcase.account.controller;

import com.vmware.spring.gemfire.showcase.account.controller.exceptions.GemFireNotAvailableException;
import com.vmware.spring.gemfire.showcase.account.entity.Account;
import com.vmware.spring.gemfire.showcase.account.repostories.AccountRepository;
import com.vmware.spring.gemfire.showcase.account.utils.GemFireQqUtil;
import nyla.solutions.core.patterns.creational.generator.JavaBeanGeneratorCreator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
    void throwNoServiceAvailable() {


        when(accountRepository.findById(account.getId())).thenThrow(GemFireQqUtil.noAvailableServersException());

        assertThrows(GemFireNotAvailableException.class,
                () -> subject.findById(account.getId()));
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