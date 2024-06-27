package showcase.gemfire.account;


import nyla.solutions.core.util.Config;
import nyla.solutions.core.util.Debugger;
import org.eclipse.jetty.ee10.servlet.ServletContextHandler;
import org.eclipse.jetty.ee10.websocket.jakarta.server.config.JakartaWebSocketServletContainerInitializer;
import org.eclipse.jetty.server.Server;
import showcase.gemfire.account.web.AccountRestServlet;

/**
 * @author Gregory Green
 */
public class AccountApp
{
    public static void main(String[] args) throws Exception {

        try {
            Config.loadArgs(args);
            // Create a Server with a ServerConnector listening on port 8080.
            Server server = new Server(8080);

            // Create a ServletContextHandler with the given context path.
            ServletContextHandler handler = new ServletContextHandler("/");
            server.setHandler(handler);

            // Ensure that JavaxWebSocketServletContainerInitializer is initialized,
            // to setup the ServerContainer for this web application context.
            JakartaWebSocketServletContainerInitializer.configure(handler, null);

            // Add a WebSocket-initializer Servlet to register WebSocket endpoints.
            handler.addServlet(new AccountRestServlet(), "/accounts/*");
//        handler.addServlet(AccountRestServlet.class, "/*");

            // Starting the Server will start the ServletContextHandler.
            server.start();
        } catch (Exception e) {
            Debugger.printFatal(e);
        }


    }
}