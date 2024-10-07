package gemfire.showcase.account.web.batch.respository;

import gemfire.showcase.account.web.batch.domain.Account;
import org.springframework.data.gemfire.repository.GemfireRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends GemfireRepository<Account,String> {
}
