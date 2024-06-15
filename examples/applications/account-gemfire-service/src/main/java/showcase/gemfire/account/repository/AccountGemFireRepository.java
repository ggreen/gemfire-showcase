package showcase.gemfire.account.repository;


import lombok.RequiredArgsConstructor;
import nyla.solutions.core.util.Debugger;
import org.apache.geode.cache.Region;
import showcase.gemfire.account.domain.Account;

/**
 * @author Gregory Green
 */
@RequiredArgsConstructor
public class AccountGemFireRepository implements AccountRepository
{
    private final Region<Long, Account> accountRegion;

    @Override
    public Account create(Account account)
    {
        Debugger.println(this,"create account");
        populateTimestamp(account);

        this.accountRegion.create(getAccountId(account),account);
        return account;
    }

    @Override
    public Account findById(Long accountId)
    {
        Debugger.println(this,"read account");
        return accountRegion.get(accountId);
    }

    @Override
    public Account update(Account account)
    {
        Debugger.println(this,"update account");
        populateTimestamp(account);
        accountRegion.put(getAccountId(account),account);
        return account;
    }

    @Override
    public boolean deleteAccountById(Long accountId)
    {
        Debugger.println(this,"delete account");
        accountRegion.remove(accountId);
        return true;
    }

    private Long getAccountId(Account account)
    {
        Long id = account.getId();
        if(id == null)
            throw new IllegalArgumentException("account.id required");
        return id;
    }

    @Override
    public Account save(Account account)
    {
        return update(account);
    }


    private void populateTimestamp(Account account)
    {
        if(account.getCurrentTimestamp() == null)
            account.setCurrentTimestamp(System.currentTimeMillis());
    }
}
