/*
 *
 *  * Copyright 2023 VMware, Inc.
 *  * SPDX-License-Identifier: GPL-3.0
 *
 */
package showcase.event.stream.rabbitmq.account.repostories;

import com.vmware.tanzu.data.services.rabbitmq.streaming.account.domain.Account;
import org.springframework.stereotype.Repository;


@Repository("EventAccount")
public interface AccountRepository {
    void save(Account account);
}
