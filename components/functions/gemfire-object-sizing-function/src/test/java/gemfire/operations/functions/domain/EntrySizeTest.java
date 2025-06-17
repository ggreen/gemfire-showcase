package gemfire.operations.functions.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EntrySizeTest {

    @Test
    void compareTo() {
        var e1 = new EntrySize("99",1);
        var e2 = new EntrySize("1",2);

        assertThat(e2.compareTo(e1)).isGreaterThan(0);
    }

    @Test
    void compareToEqual() {
        var e1 = new EntrySize("99",99);
        var e2 = new EntrySize("1",99);

        assertThat(e2.compareTo(e1)).isEqualTo(0);
    }

    @Test
    void testToString() {
        var subject = new EntrySize("99",99);

        var actual = subject.toString();
        System.out.println(actual);
        assertThat(actual).contains(subject.key().toString());
        assertThat(actual).contains(String.valueOf(subject.size()));
    }
}