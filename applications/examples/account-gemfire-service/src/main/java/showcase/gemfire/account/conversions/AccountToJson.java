package showcase.gemfire.account.conversions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import nyla.solutions.core.exception.FormatException;
import showcase.gemfire.account.domain.UserAccount;

import java.util.function.Function;

/**
 * @author Gregory Green
 */
public class AccountToJson implements Function<UserAccount,String>
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
    public String apply(UserAccount userAccount)
    {
        try {
            return objectMapper.writeValueAsString(userAccount);
        }
        catch (JsonProcessingException e) {
            throw new FormatException(e);
        }
    }
}
