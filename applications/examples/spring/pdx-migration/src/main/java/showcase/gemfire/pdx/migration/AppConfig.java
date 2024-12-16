package showcase.gemfire.pdx.migration;

import lombok.extern.slf4j.Slf4j;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.snapshot.SnapshotIterator;
import org.apache.geode.cache.snapshot.SnapshotReader;
import org.apache.geode.pdx.PdxInstance;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import showcase.gemfire.pdx.migration.domain.MyData;
import showcase.gemfire.pdx.migration.domain.MyEnum;

import java.io.File;
import java.nio.file.Paths;
import java.util.Map;

@Configuration
@Slf4j
public class AppConfig {


    @Bean
    CommandLineRunner migration (ClientCache cache, Region<String, MyData> region){
        return args -> {


            File snapshot = Paths.get("/Users/Projects/VMware/Tanzu/TanzuData/TanzuGemFire/dev/gemfire-showcase/applications/examples/spring/pdx-migration/runtime/PdxMigration-192168863443596.gfd").toFile();
            SnapshotIterator<String, PdxInstance> iter = SnapshotReader.read(snapshot);
            try {
                while (iter.hasNext()) {
                    Map.Entry<String, PdxInstance> entry = iter.next();

                    String key = entry.getKey();
                    PdxInstance value = entry.getValue();
                    System.out.println(key + " = " + value);

                    //Migration to New Enum
                    var oldCode = value.getField("myEnum").toString();

                    MyEnum newEnum=  switch (oldCode)
                    {
                        case "CODE13" -> MyEnum.CODE13;
                        case "CODE12" -> MyEnum.CODE12;
                        default -> MyEnum.CODE110;
                    };

                    //migrate remove enum or change Enum
                    region.put(key, new MyData(value.getField("id").toString(),
                            newEnum));

                }
            } finally {
                iter.close();
            }

        };
    }

}
