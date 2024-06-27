package showcase.gemfire.account.repository;

import showcase.gemfire.account.domain.UserAccount;

public interface AccountRepository {
    UserAccount create(UserAccount userAccount);

    UserAccount findById(Long accountId);

    UserAccount update(UserAccount userAccount);

    boolean deleteAccountById(Long accountId);

    UserAccount save(UserAccount userAccount);
}
