/*
 *
 *  * Copyright 2023 VMware, Inc.
 *  * SPDX-License-Identifier: GPL-3.0
 *
 */
package showcase.event.stream.rabbitmq.account.functions;

import com.vmware.tanzu.data.services.rabbitmq.streaming.account.domain.Account;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import showcase.event.stream.rabbitmq.account.repostories.AccountRepository;

import java.util.function.Consumer;

@RequiredArgsConstructor
@Component
@Slf4j
public class AccountConsumer implements Consumer<Account> {
    private final AccountRepository repository;

    public void accept(Account account) {
        log.info("Saving account: {}",account);
        repository.save(account);
    }
}
