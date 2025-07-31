package showcase.gemfire.server.dataNode;


import org.apache.geode.distributed.ServerLauncher;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author Gregory Green
 */
public class DataNodeApp
{
    public static void main(String[] args) throws Exception {

        var workDirectory = "runtime/data-node";

        var path = Path.of(workDirectory);
        if(!Files.exists(path))
            Files.createDirectory(path);

        final ServerLauncher serverLauncher = new ServerLauncher.Builder()
                .setMemberName("server1")
                .set("locators","127.0.0.1[10334]")
                .setWorkingDirectory(workDirectory)
//                .set("jmx-manager", "true")
//                .set("jmx-manager-start", "true")
                .build();

        serverLauncher.start();

        System.out.println("Cache server successfully started");
        System.out.println("Locator successfully started");
    }

}