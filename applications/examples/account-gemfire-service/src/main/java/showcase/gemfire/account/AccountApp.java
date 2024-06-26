package showcase.gemfire.account;


import nyla.solutions.core.util.Config;
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
        handler.addServlet(AccountRestServlet.class, "/*");

        // Starting the Server will start the ServletContextHandler.
        server.start();

//        Server server = new Server();
//        Connector connector = new ServerConnector(server);
//        server.addConnector(connector);
//
//// Add the CrossOriginHandler to protect from CSRF attacks.
//        CrossOriginHandler crossOriginHandler = new CrossOriginHandler();
//        crossOriginHandler.setAllowedOriginPatterns(Set.of("http://domain.com"));
//        crossOriginHandler.setAllowCredentials(true);
//        server.setHandler(crossOriginHandler);
//
//// Create a ServletContextHandler with contextPath.
//        ServletContextHandler context = new ServletContextHandler();
//        context.setContextPath("/shop");
//// Link the context to the server.
//        crossOriginHandler.setHandler(context);
//
//// Add the Servlet implementing the cart functionality to the context.
//        Servlet servlet = new AccountRestServlet();
//        ServletHolder servletHolder = new ServletHolder(servlet);
//
//        context.addServlet(servletHolder, "/*");
//        // Configure the Servlet with init-parameters.
//        servletHolder.setInitParameter("maxItems", "128");
//
//        server.start();

//        var server = new Server(8082);
//
//        Handler handler = new ServletHandler();
//        server.setHandler(handler);
//
//        handler.addServletWithMapping(AccountRestServlet.class, "/*");
//        server.start();
//        server.join();

    }
}