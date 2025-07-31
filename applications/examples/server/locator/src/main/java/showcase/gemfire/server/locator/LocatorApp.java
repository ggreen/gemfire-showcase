package showcase.gemfire.server.locator;


import org.apache.geode.distributed.LocatorLauncher;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author Gregory Green
 */
public class LocatorApp
{
    public static void main(String[] args) throws Exception {

        var address =  java.net.InetAddress.getByName("localhost");

        var hostName = address.getHostName();

        System.out.println("******HOST*****"+hostName);

        var path = Path.of("runtime/locator");
        if(!Files.exists(path))
            Files.createDirectory(path);

        final LocatorLauncher locatorLauncher = new LocatorLauncher.Builder()
                .setMemberName("locator1")
                .set("log-file","locator1.log")
                .setBindAddress("127.0.0.1")
                .setWorkingDirectory("runtime/locator")
                .setPort(10334).build();

        locatorLauncher.start();

        System.out.println("Locator successfully started");
    }

}