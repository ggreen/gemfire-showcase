package showcase.gemfire.health.analyzer.runners.search;

import nyla.solutions.core.operations.performance.BenchMarker;
import nyla.solutions.core.operations.performance.PerformanceCheck;
import nyla.solutions.core.patterns.creational.generator.JavaBeanGeneratorCreator;
import nyla.solutions.core.patterns.jdbc.Sql;
import nyla.solutions.core.util.Config;
import nyla.solutions.core.util.Debugger;
import showcase.gemfire.health.analyzer.runners.search.domain.SearchAccount;

import java.sql.SQLException;

public class SqlTest {
    private static Long loopCount = 1000L;
    private static String driver = Config.settings().getProperty("driver", "org.postgresql.Driver");
    private static String connectionUrl = Config.settings().getProperty("connectionUrl", "jdbc:postgresql://localhost/postgres");
    private static String user = Config.settings().getProperty("user", "postgres");
    private static char[] password = Config.settings().getPropertyPassword("password", "");
    private static String sqlText = """
            select * from perf_search.accounts where code1 = 1
            """;

    private static String createSql = """
            CREATE SCHEMA IF NOT EXISTS perf_search;
            create table if not exists perf_search.accounts 
            (code1 integer not null PRIMARY KEY,
            code2 text not null,
            code3  integer not null,
            startRange text,
            endRange text,
            output smallint
            );
            """;

    /*
     INSERT INTO perf_search.accounts (code1, code2, code3, startRange,endRange,output)
            VALUES (1, ':code2', 3, ':startRange',':endRange', 0)
            ON CONFLICT(code1)
            DO UPDATE SET
              code2 = ':code2',
              code3 = 3;
     */

    private static String upsertSql = """
            INSERT INTO perf_search.accounts (code1, code2, code3, startRange,endRange,output)
            VALUES (:code1, :code2, :code3, :startRange,:endRange, :output)
            ON CONFLICT(code1)
            DO UPDATE SET
              code2 = :code2 ,
              code3 = :code3
            """;

    private static JavaBeanGeneratorCreator creator = JavaBeanGeneratorCreator.of(SearchAccount.class);
    private static int capacity = 1000;

    public static void main(String[] args) throws InterruptedException, SQLException {

        var benchMark = BenchMarker.builder().loopCount(loopCount)
                .threadCount(1)
                .loopCount(loopCount)
                .threadSleepMs(10)
                .build();

        var sql = new Sql();

        var connection = Sql.createConnection(driver, connectionUrl, user, password);

        //setup
        sql.execute(connection, createSql);

        sql.executeUpdateSqlWithJavaBean(connection, upsertSql,
                creator.create()
        );


        var pertTest = new PerformanceCheck(benchMark, capacity);
        pertTest.perfCheck(() -> {
                    try {
                        sql.queryForMap(connection, sqlText);
                    } catch (Exception e) {
                        Debugger.printError(e);
                    }
                }

        );

//        Thread.sleep(1000*60);
        System.out.println(pertTest.getReport());

    }
}
