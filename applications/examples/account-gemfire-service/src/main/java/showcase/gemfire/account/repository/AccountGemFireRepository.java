package showcase.gemfire.account.repository;


import com.vmware.data.services.gemfire.client.GemFireClient;
import com.vmware.data.services.gemfire.io.QuerierService;
import nyla.solutions.core.util.Debugger;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.query.Struct;
import showcase.gemfire.account.domain.UserAccount;

import java.util.Collection;

/**
 * @author Gregory Green
 */
public class AccountGemFireRepository implements AccountRepository
{
    private final Region<Long, UserAccount> userAccountRegion;
    private final QuerierService querierService;

    public AccountGemFireRepository()
    {
        try {
            GemFireClient gemfireClient = GemFireClient.connect();

            userAccountRegion = gemfireClient.getRegion("UserAccount");
            this.querierService = gemfireClient.getQuerierService();
        }
        catch (RuntimeException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public AccountGemFireRepository(Region<Long, UserAccount> userAccountRegion, QuerierService queryService)
    {
        this.userAccountRegion = userAccountRegion;
        this.querierService = queryService;
    }

    @Override
    public UserAccount create(UserAccount userAccount)
    {
        Debugger.println(this,"create account");
        populateTimestamp(userAccount);

        this.userAccountRegion.create(getAccountId(userAccount), userAccount);
        return userAccount;
    }


    @Override
    public UserAccount findById(Long accountId)
    {
        Debugger.println(this,"read account accountId");
        return userAccountRegion.get(accountId);
    }

    @Override
    public UserAccount update(UserAccount userAccount)
    {
        Debugger.println(this,"update account");
        populateTimestamp(userAccount);
        userAccountRegion.put(getAccountId(userAccount), userAccount);
        return userAccount;
    }

    @Override
    public boolean deleteAccountById(Long accountId)
    {
        Debugger.println(this,"delete account");
        userAccountRegion.remove(accountId);
        return true;
    }

    private Long getAccountId(UserAccount userAccount)
    {
        Long id = userAccount.getId();
        if(id == null)
            throw new IllegalArgumentException("account.id required");
        return id;
    }

    @Override
    public UserAccount save(UserAccount userAccount)
    {
        return update(userAccount);
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

    private void populateTimestamp(UserAccount userAccount)
    {
        if(userAccount.getCurrentTimestamp() == null)
            userAccount.setCurrentTimestamp(System.currentTimeMillis());
    }
}
