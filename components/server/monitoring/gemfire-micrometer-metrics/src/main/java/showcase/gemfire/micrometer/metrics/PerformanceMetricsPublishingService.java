package showcase.gemfire.micrometer.metrics;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import org.apache.geode.metrics.MetricsPublishingService;
import org.apache.geode.metrics.MetricsSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;

public class PerformanceMetricsPublishingService implements MetricsPublishingService {
    private PrometheusMeterRegistry registry;
    private final String hostName = "localhost";
    private int port = 10133;
    private HttpServer server;
    private Logger log = LogManager.getLogger(PerformanceMetricsPublishingService.class);

    @Override
    public void start(MetricsSession metricsSession) {

        this.registry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
        metricsSession.addSubregistry(registry);


        var address = new InetSocketAddress(hostName, port);
        server = null;
        try {
            server = HttpServer.create(address, 0);

            HttpContext context = server.createContext("/");
            context.setHandler(this::requestHandler);
            server.start();

            int boundPort = server.getAddress().getPort();
            log.info("Started {} http://{}:{}/", getClass().getSimpleName(), hostName, boundPort);
        } catch (IOException thrown) {
            log.error("Exception while starting " + getClass().getSimpleName(), thrown);
        }

    }

    private void requestHandler(HttpExchange httpExchange) {
    }

    @Override
    public void stop(MetricsSession metricsSession) {



    }
}
