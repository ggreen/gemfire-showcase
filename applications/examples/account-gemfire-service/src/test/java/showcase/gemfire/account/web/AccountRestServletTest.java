package showcase.gemfire.account.web;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nyla.solutions.core.patterns.creational.generator.JavaBeanGeneratorCreator;
import nyla.solutions.core.patterns.creational.servicefactory.ServiceFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import showcase.gemfire.account.domain.UserAccount;
import showcase.gemfire.account.repository.AccountGemFireRepository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

class AccountRestServletTest {

    private AccountGemFireRepository repository;
    private AccountRestServlet subject;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private PrintWriter printWriter;
    private Function<UserAccount, String> acctToJSon;
    private Function<String, UserAccount> jsonToAccount;
    private BufferedReader bufferReader;
    private BufferedReader reader;

    @BeforeEach
    void setUp() throws IOException
    {
        repository = mock(AccountGemFireRepository.class);
        acctToJSon = mock(Function.class);
        jsonToAccount = mock(Function.class);
        subject = new AccountRestServlet(repository, acctToJSon,jsonToAccount);
        request = mock(HttpServletRequest.class);

        reader = mock(BufferedReader.class);

        response = mock(HttpServletResponse.class);
        printWriter = mock(PrintWriter.class);
        when(response.getWriter()).thenReturn(printWriter);

    }


    @Test
    void createWithServiceFactory()
    {
        ServiceFactory serviceFactory = mock(ServiceFactory.class);
        when(serviceFactory.create("REPOSITORY")).thenReturn(repository);

        subject = new AccountRestServlet(serviceFactory);
        verify(serviceFactory).create(anyString());
    }

    @Test
    @DisplayName("When account is null then toAccount returns null")
    void toAccount_when_null() throws IOException
    {
        assertNull(subject.toAccount(request));
    }

    @Nested
    class WhenCreate
    {
        @Test
        void given_account_then_save() throws IOException, ServletException {
            when(request.getReader()).thenReturn(reader);

            String json = "{}";
            when(reader.readLine()).thenReturn(json).thenReturn(null);

            subject.doPost(request,response);
            verify(jsonToAccount).apply(anyString());
            verify(repository).save(any());
        }
    }


    @Nested
    public class WhenRead
    {
        @Test
        public void given_valid_acctId_Then_Return_account() throws ServletException, IOException, SQLException
        {
            String uri = "/accounts/db/14";

            when(request.getRequestURI()).thenReturn(uri);
            subject.doGet(request, response);
            verify(repository).findById(14L);
        }

        @Test
        public void given_invalid_acctId_Then_Return_null() throws ServletException, IOException, SQLException
        {

            String expectedUri = "/accounts/1";
            when(request.getRequestURI()).thenReturn(expectedUri);
            when(repository.findById(anyLong())).thenReturn(null);

            subject.doGet(request, response);
            verify(repository).findById(1L);
            verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);

        }
    }

    @Nested
    class WhenUpdate
    {
        @Test
        void given_valid_account_then_update() throws ServletException, IOException
        {
            UserAccount expected = JavaBeanGeneratorCreator.of(UserAccount.class)
                    .randomizeAll().create();

            when(jsonToAccount.apply(any())).thenReturn(expected);
            when(repository.update(any())).thenReturn(expected);
            when(acctToJSon.apply(any())).thenReturn("{}");
            subject.doPut(request, response);

            verify(repository).update(any());
            verify(acctToJSon).apply(any());
            verify(printWriter).write(anyString());

        }
    }
    @Nested
    class WhenDelete
    {
        @Test
        void given_nullAccount_throws_Error() throws ServletException
        {
            try
            {
                subject.doDelete(request, response);
            }catch(NullPointerException e)
            {
                assertTrue(e.getMessage().contains("not found"));
            }
        }

        @Test
        void given_id_delete_account() throws ServletException
        {
            String exceptedId = "1";
            when(request.getRequestURI()).thenReturn("/accounts/"+exceptedId);
            subject.doDelete(request, response);
            verify(repository).deleteAccountById(anyLong());

        }
    }

    @Test
    @DisplayName("When URI is valid Then Return accountID")
    void accountId()
    {
        Long expected = 123L;
        String uri = "/db/"+expected;
        when(request.getRequestURI()).thenReturn(uri);
        Long actual = subject.accountId(request);
        assertEquals(expected,actual);

        when(request.getRequestURI()).thenReturn("/");
        actual = subject.accountId(request);
        assertNull(actual);
    }

}