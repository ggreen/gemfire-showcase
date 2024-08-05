package showcase.event.stream.rabbitmq.account;

import org.junit.jupiter.api.Test;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.GenericMessage;

import java.util.Map;

class AccountSinkAppTest {
    @Test
    void name() {

        var parser = new SpelExpressionParser();

        var expression = parser.parseExpression("headers.get('http_requestMethod')");
        String payload = "hi";
        MessageHeaders headers = new MessageHeaders(Map.of("http_requestMethod","POST"));
        var msg = new GenericMessage(payload,headers);
        var out = expression.getValue(msg);
        System.out.println(out);
    }
}