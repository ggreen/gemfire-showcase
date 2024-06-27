package showcase.gemfire.account.conversions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import nyla.solutions.core.exception.FormatException;
import org.junit.jupiter.api.Test;
import showcase.gemfire.account.domain.UserAccount;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AccountToJsonTest {

    @Test
    void apply()
    {
        AccountToJson subject = new AccountToJson();
        final long currentTimestamp = System.currentTimeMillis();
        UserAccount account =  UserAccount.builder()
                .id(1L)
                .name("hello")
                .currentTimestamp(currentTimestamp).build();

        String expected =  "{\"id\":1,\"name\":\"hello\",\"currentTimestamp\":"+currentTimestamp+"}";

        var actual = subject.apply(account);

        assertThat(actual)
                .contains(account.getId().toString())
                .contains(account.getName())
                .contains(account.getCurrentTimestamp().toString());

    }

    @Test
    void when_JsonProcessingException_throws_FormatException() throws JsonProcessingException
    {
        ObjectMapper objectMapper = mock(ObjectMapper.class);
        when(objectMapper.writeValueAsString(any())).thenThrow(JsonProcessingException.class);
        AccountToJson subject = new AccountToJson(objectMapper);
        UserAccount account = new UserAccount();

        assertThrows(FormatException.class,() ->subject.apply(account));

    }

}