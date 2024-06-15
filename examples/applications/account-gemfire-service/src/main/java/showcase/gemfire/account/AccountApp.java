package showcase.gemfire.account;

import nyla.solutions.core.exception.FatalException;
import nyla.solutions.core.exception.SystemException;
import nyla.solutions.core.patterns.creational.servicefactory.ServiceFactory;
import nyla.solutions.core.util.Config;
import nyla.solutions.core.util.Debugger;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;
import showcase.gemfire.account.web.AccountRestServlet;
import showcase.gemfire.account.web.AccountWebServer;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Gregory Green
 */
class AccountApp
{
    public static void main(String[] args) throws Exception {
        var server = new Server(8082);

        Handler handler = new ServletHandler();
        server.setHandler(handler);

        handler.addServletWithMapping(AccountRestServlet.class, "/*");
        server.start();
        server.join();

    }
}