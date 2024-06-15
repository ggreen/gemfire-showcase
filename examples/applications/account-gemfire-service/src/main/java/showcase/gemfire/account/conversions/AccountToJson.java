package showcase.gemfire.account.conversions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import nyla.solutions.core.exception.FormatException;
import showcase.gemfire.account.domain.Account;

import java.util.function.Function;

/**
 * @author Gregory Green
 */
public class AccountToJson implements Function<Account,String>
{
    private final ObjectMapper objectMapper;

    public AccountToJson()
    {
        this(new ObjectMapper());
    }
    public AccountToJson(ObjectMapper objectMapper)
    {
        this.objectMapper = objectMapper;
    }

    @Override
    public String apply(Account account)
    {
        try {
            return objectMapper.writeValueAsString(account);
        }
        catch (JsonProcessingException e) {
            throw new FormatException(e);
        }
    }
}
