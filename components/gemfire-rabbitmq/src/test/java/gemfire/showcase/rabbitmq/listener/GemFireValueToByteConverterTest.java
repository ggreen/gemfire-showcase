package gemfire.showcase.rabbitmq.listener;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GemFireValueToByteConverterTest {

    private GemFireValueToByteConverter subject = new GemFireValueToByteConverter();

    @Test
    void testNull()
    {
        assertNull(subject.apply(null));
    }

    @Test
    void testString()
    {
        assertNotNull(subject.apply(""));
    }
    @Test
    void testNumber()
    {
        assertNotNull(subject.apply(1));
    }
}