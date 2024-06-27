package showcase.gemfire.account.web;

import jakarta.servlet.Servlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nyla.solutions.core.io.IO;
import nyla.solutions.core.patterns.creational.servicefactory.ServiceFactory;
import nyla.solutions.core.util.Debugger;
import showcase.gemfire.account.conversions.AccountToJson;
import showcase.gemfire.account.conversions.JsonToAccount;
import showcase.gemfire.account.domain.UserAccount;
import showcase.gemfire.account.repository.AccountRepository;

import java.io.IOException;
import java.util.function.Function;

/**
 * @author Gregory Green
 */
public class AccountRestServlet extends HttpServlet implements Servlet {
    private final AccountRepository repository;
    private final Function<UserAccount, String> accountToJson;
    private final Function<String, UserAccount> jsonToAccount;

    public AccountRestServlet() {
        this(ServiceFactory.getInstance());
    }

    public AccountRestServlet(ServiceFactory factory) {
        this.repository = factory.create("REPOSITORY");
        this.accountToJson = factory.create(AccountToJson.class);
        this.jsonToAccount = factory.create(JsonToAccount.class);
    }

    public AccountRestServlet(AccountRepository repository, Function<UserAccount, String> accountToJson, Function<String,
            UserAccount> jsonToAccount) {
        this.repository = repository;
        this.accountToJson = accountToJson;
        this.jsonToAccount = jsonToAccount;
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            UserAccount userAccount = toAccount(request);
            this.repository.save(userAccount);
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            Long accountId = accountId(request);
            UserAccount userAccount = repository.findById(accountId);
            if (userAccount == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            } else {
                response.getWriter().write(accountToJson.apply(userAccount));
            }
        } catch (IOException | RuntimeException e) {
            Debugger.printError(e);
            throw e;
        }


    }

    @Override
    public void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {

            UserAccount userAccount = this.toAccount(req);
            if (userAccount == null)
                throw new NullPointerException("Account JSON required in HTTP POST body");

            userAccount = repository.update(userAccount);
            resp.getWriter().write(accountToJson.apply(userAccount));
        } catch (RuntimeException e) {
            Debugger.printError(e);
            throw e;
        }
    }


    public void doDelete(HttpServletRequest request, HttpServletResponse resp) throws ServletException {
        try {

            Long accountId = this.accountId(request);
            if (accountId == null)
                throw new NullPointerException("Account Id not found in URL:" + request.getRequestURI());
            boolean found = this.repository.deleteAccountById(accountId);

            if (!found)
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            else
                resp.setStatus(HttpServletResponse.SC_OK);

        } catch (RuntimeException e) {
            Debugger.printError(e);
            throw e;
        }

    }

    protected UserAccount toAccount(HttpServletRequest request) throws IOException {
        String json = IO.readText(request.getReader());
        UserAccount userAccount = this.jsonToAccount.apply(json);
        return userAccount;
    }

    protected Long accountId(HttpServletRequest request) {
        String url = request.getRequestURI();
        if (url == null)
            return null;

        int lastSlashIndex = url.lastIndexOf("/");
        if (lastSlashIndex < 0)
            return null;

        String id = url.substring(lastSlashIndex + 1);
        if (id.length() == 0)
            return null;

        return Long.valueOf(id);
    }
}