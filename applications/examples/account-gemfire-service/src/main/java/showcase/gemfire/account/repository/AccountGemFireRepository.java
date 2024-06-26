package showcase.gemfire.account.repository;


import com.vmware.data.services.gemfire.client.GemFireClient;
import com.vmware.data.services.gemfire.io.QuerierService;
import lombok.RequiredArgsConstructor;
import nyla.solutions.core.util.Debugger;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.query.Struct;
import showcase.gemfire.account.domain.Account;

import java.util.Collection;

/**
 * @author Gregory Green
 */
public class AccountGemFireRepository implements AccountRepository
{
    private final Region<Long, Account> accountRegion;
    private final QuerierService querierService;

    public AccountGemFireRepository()
    {
        try {
            GemFireClient geodeClient = GemFireClient.connect();

            accountRegion = geodeClient.getRegion("accounts");
            this.querierService = geodeClient.getQuerierService();
        }
        catch (RuntimeException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public AccountGemFireRepository(Region<Long, Account> accountRegion, QuerierService queryService)
    {
        this.accountRegion = accountRegion;
        this.querierService = queryService;
    }

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

    public Long[] selectMaxAccountIdAndTimestamp()
    {
        String query = "select max(id) as id,max(currentTimestamp) as currentTimestamp  from /accounts";
        Collection<Struct> maxIdAndTimestamp = this.querierService.query(query);
        if(maxIdAndTimestamp == null || maxIdAndTimestamp.isEmpty())
            return null;


        Struct struct = maxIdAndTimestamp.iterator().next();
        Long[] longs = new Long[2];

        longs[0] = (Long)struct.get("id");
        longs[1] = (Long)struct.get("currentTimestamp");

        return longs;

    }

    private void populateTimestamp(Account account)
    {
        if(account.getCurrentTimestamp() == null)
            account.setCurrentTimestamp(System.currentTimeMillis());
    }
}
