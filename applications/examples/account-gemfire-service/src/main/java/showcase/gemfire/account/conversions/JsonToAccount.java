package showcase.gemfire.account.conversions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import nyla.solutions.core.exception.FormatException;
import showcase.gemfire.account.domain.UserAccount;

import java.util.function.Function;

/**
 * @author Gregory Green
 */
public class JsonToAccount implements Function<String, UserAccount>
{
    private final ObjectMapper objectMapper;

    public JsonToAccount()
    {
        this(new ObjectMapper());
    }

    public JsonToAccount(ObjectMapper objectMapper)
    {
        this.objectMapper = objectMapper;
    }

    @Override
    public UserAccount apply(String json)
    {
        try {
            return objectMapper.readValue(json, UserAccount.class);
        }
        catch (JsonProcessingException e) {
            throw new FormatException(e);
        }
    }
}
