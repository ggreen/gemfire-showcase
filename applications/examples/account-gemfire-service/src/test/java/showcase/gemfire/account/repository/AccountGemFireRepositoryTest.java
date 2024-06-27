package showcase.gemfire.account.repository;

import com.vmware.data.services.gemfire.io.QuerierService;
import nyla.solutions.core.patterns.creational.generator.JavaBeanGeneratorCreator;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.query.Struct;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import showcase.gemfire.account.domain.UserAccount;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

class AccountGemFireRepositoryTest {


    private Region<Long, UserAccount> region;
    private QuerierService queryService;
    private AccountGemFireRepository subject;
    private UserAccount expected;

    @BeforeEach
    void setUp()
    {
        region = mock(Region.class);
        queryService = mock(QuerierService.class);
        subject = new AccountGemFireRepository(region,queryService);
        expected = new JavaBeanGeneratorCreator<>(UserAccount.class)
                .randomizeAll().create();
    }

    @Test
    void create()
    {
        UserAccount actual = subject.create(expected);
        assertEquals(expected,actual);
        verify(region).create(actual.getId(),actual);

    }
    @Test
    void create_hasCurrentTimestamp()
    {
        UserAccount expected = UserAccount.builder().id(2L)
                .name("test").build();
        UserAccount actual = subject.create(expected);
        assertNotNull(actual.getCurrentTimestamp());

    }

    @Test
    void create_throwsExceptionWhenIdNotGiven()
    {
        UserAccount expected = new UserAccount();
        assertThrows(IllegalArgumentException.class, () -> subject.create(expected));
    }

    @Test
    void findById()
    {
        Long expectedId = 1L;
        subject.findById(expectedId);
        verify(region).get(expectedId);
    }

    @Test
    void update()
    {

        UserAccount actual = subject.update(expected);
        assertEquals(expected,actual);
        verify(region).put(actual.getId(),actual);
    }
    @Test
    void update_hasCurrentTimestamp()
    {
        UserAccount expected = UserAccount.builder().id(2L)
                .name("test").build();
        UserAccount actual = subject.update(expected);
        assertNotNull(actual.getCurrentTimestamp());

    }

    @Test
    void update_throwsIllegalArgumentException()
    {
        assertThrows(IllegalArgumentException.class,
                () ->subject.update(new UserAccount()));
    }
    @Test
    void save()
    {
        UserAccount actual = subject.save(expected);
        assertEquals(expected,actual);
        verify(region).put(actual.getId(),actual);
    }

    @Test
    void save_hasCurrentTimestamp()
    {
        UserAccount expected = UserAccount.builder().id(2L)
                .name("test").build();
        UserAccount actual = subject.save(expected);
        assertNotNull(actual.getCurrentTimestamp());

    }

    @Test
    void deleteAccountById_When_found_returns_true()
    {
        Long expectedAccountId = 3L;
        boolean actual = subject.deleteAccountById(expectedAccountId);
        assertTrue(actual);
        verify(region).remove(expectedAccountId);
    }

    @Nested
    public class WhenSelectMaxAccountIdAndTimestamp {
        ;

        @Test
        void selectMaxAccountIdAndTimestamp() {
            long expectedMaxAccountId = Integer.MAX_VALUE / 2;
            long expectedMaxTimestamp = System.currentTimeMillis();
            Struct expectedIdAndTimestamp = mock(Struct.class);
            when(expectedIdAndTimestamp.get("id")).thenReturn(expectedMaxAccountId);
            when(expectedIdAndTimestamp.get("currentTimestamp")).thenReturn(expectedMaxTimestamp);
            Collection<Object> expectedResults = Arrays.asList(expectedIdAndTimestamp);
            when(queryService.query(anyString())).thenReturn(expectedResults);

            Long[] longs = subject.selectMaxAccountIdAndTimestamp();
            verify(queryService).query(anyString());
            assertNotNull(longs);
            assertEquals(expectedMaxAccountId, longs[0]);
            assertEquals(expectedMaxTimestamp, longs[1]);

        }

        @Test
        void selectMaxAccountIdAndTimestamp_returnNullTimestamp() {
            Long expectedMaxAccountId = Long.valueOf(2);
            Long expectedMaxTimestamp = null;
            Struct expectedIdAndTimestamp = mock(Struct.class);
            when(expectedIdAndTimestamp.get("id")).thenReturn(expectedMaxAccountId);
            when(expectedIdAndTimestamp.get("currentTimestamp")).thenReturn(expectedMaxTimestamp);
            Collection<Object> expectedResults = Arrays.asList(expectedIdAndTimestamp);
            when(queryService.query(anyString())).thenReturn(expectedResults);

            Long[] longs = subject.selectMaxAccountIdAndTimestamp();
            verify(queryService).query(anyString());
            assertNotNull(longs);
            assertEquals(expectedMaxAccountId, longs[0]);
            assertEquals(expectedMaxTimestamp, longs[1]);

        }

        @Test
        void selectMaxAccountIdAndTimestamp_returnNullAccountId() {
            Long expectedMaxAccountId = null;
            Long expectedMaxTimestamp = System.currentTimeMillis();
            Struct expectedIdAndTimestamp = mock(Struct.class);
            when(expectedIdAndTimestamp.get("id")).thenReturn(expectedMaxAccountId);
            when(expectedIdAndTimestamp.get("currentTimestamp")).thenReturn(expectedMaxTimestamp);
            Collection<Object> expectedResults = Arrays.asList(expectedIdAndTimestamp);
            when(queryService.query(anyString())).thenReturn(expectedResults);

            Long[] longs = subject.selectMaxAccountIdAndTimestamp();
            verify(queryService).query(anyString());
            assertNotNull(longs);
            assertEquals(expectedMaxAccountId, longs[0]);
            assertEquals(expectedMaxTimestamp, longs[1]);
        }

        @Test
        void selectMaxAccountIdAndTimestamp_returnsNull() {
            Long[] longs = subject.selectMaxAccountIdAndTimestamp();
            verify(queryService).query(anyString());
            assertNull(longs);

        }
    }
}