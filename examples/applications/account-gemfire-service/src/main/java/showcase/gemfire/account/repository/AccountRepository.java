package showcase.gemfire.account.repository;

import showcase.gemfire.account.domain.Account;

public interface AccountRepository {
    Account create(Account account);

    Account findById(Long accountId);

    Account update(Account account);

    boolean deleteAccountById(Long accountId);

    Account save(Account account);
}
