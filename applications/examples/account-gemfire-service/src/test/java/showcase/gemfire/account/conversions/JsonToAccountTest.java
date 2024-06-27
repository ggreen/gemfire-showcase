package showcase.gemfire.account.conversions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import nyla.solutions.core.exception.FormatException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import showcase.gemfire.account.domain.UserAccount;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class JsonToAccountTest {
    private ObjectMapper objectMapper = new ObjectMapper();

    private JsonToAccount subject;

    @BeforeEach
    void setUp() {
        subject = new JsonToAccount(objectMapper);
    }

    @Test
    void apply()
    {
        UserAccount excepted = UserAccount.builder().build();

        String json = new AccountToJson().apply(excepted);
        UserAccount actual = subject.apply(json);
        assertEquals(excepted,actual);
    }

    @Test
    void apply_when_exception_throws_FormatException() throws JsonProcessingException
    {
        assertThrows(FormatException.class,() ->  subject.apply("INVALID"));

    }


    @Test
    void workAddress() {
        var json = """
                  {
                  "id": "4",
                  "name": "User 1",
                  "contact" : {\s
                    "email" : "gideon@gemfire.dev",
                    "mobilePhone" : "555-555-5555"
                  },\s
                  "workAddress" : {
                    "streetAddress" : "875 Howard Street 5th Floor",
                    "city" : "San Francisco",
                    "state" : "CA",
                    "zip" : "9410"
                  }
                }
                """;

        var actual = subject.apply(json);

        assertThat(actual.getWorkAddress()).isNotNull();

    }
}