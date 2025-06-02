package showcase.gemfire.health.analyzer.runners.search;

import com.vmware.data.services.gemfire.client.GemFireClient;
import nyla.solutions.core.operations.performance.BenchMarker;
import nyla.solutions.core.operations.performance.PerformanceCheck;
import nyla.solutions.core.patterns.creational.generator.JavaBeanGeneratorCreator;
import org.apache.geode.cache.query.IndexExistsException;
import org.apache.geode.cache.query.IndexNameConflictException;
import org.apache.geode.cache.query.RegionNotFoundException;
import showcase.gemfire.health.analyzer.runners.search.domain.SearchAccount;

import java.sql.SQLException;

public class OqlTest {

    private static Long loopCount = 100L;
    private static Long recordCount = 10000000L;


    private static String sqlText = """
            select * from /SearchAccounts where code1 = 444
            AND startRange <= '2340236472000000123'
            ANd endRange >= '2340236472000000123'
            AND  code3= '33333' 
            """;


    private static JavaBeanGeneratorCreator<SearchAccount> creator = JavaBeanGeneratorCreator.of(SearchAccount.class);
    private static int capacity = 1000;
    private static BenchMarker marker;
    private static String regionName = "/SearchAccounts";

    public static void main(String[] args) throws InterruptedException, SQLException, RegionNotFoundException, IndexExistsException, IndexNameConflictException {

        var gemfire = GemFireClient.builder().locators("localhost[10334]")
                .clientName("OqlTest")
                .build();

        var searchAccountRegion = gemfire.getRegion("SearchAccounts");

        var keys = searchAccountRegion.keySetOnServer();
        if(keys == null || keys.isEmpty() || keys.size() < recordCount)
        {
            System.out.println("!!!!!  LOADING DATA !!!!!!!!");
            for(int i  =0 ; i <recordCount;++i)
            {

                var entry = creator.create();
                entry.setCode1(i);
                searchAccountRegion.put(entry.getCode1(),entry);
            }
        }

        var queryService = gemfire.getQuerierService();

        var qs = gemfire.getClientCache().getQueryService();


//         gemfire.getRegion(regionName);
/*
        create region --name=SearchAccounts --type=PARTITION
        create index --name=codeIndex --expression=code1 --region=/SearchAccounts
        create index --name=code3Index --expression=code3 --region=/SearchAccounts
        create index --name=startRangeIndex --expression=startRange --region=/SearchAccounts
        create index --name=endRangeIndex --expression=endRange --region=/SearchAccounts
 */
//        qs.createIndex("","", regionName);
//        qs.createIndex("endRangeIndex","endRange", regionName);


        marker = BenchMarker.builder().threadCount(1)
                .loopCount(loopCount)
                .build();

        PerformanceCheck performanceCheck = new PerformanceCheck(marker,capacity);

        performanceCheck.perfCheck(() -> {
            queryService.query(sqlText);
        });

        System.out.println(performanceCheck.getReport());

    }
}
