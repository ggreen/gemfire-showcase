package gemfire.showcase.account.web.batch;

import lombok.extern.slf4j.Slf4j;
import nyla.solutions.core.patterns.creational.generator.FullNameCreator;
import nyla.solutions.core.util.Text;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Map;

import static java.lang.String.valueOf;

@Configuration
@Slf4j
public class LoadDataConfig {

    @Value("${db.schema}")
    private String schemaName;

    private String insertSql  = """
                INSERT INTO ${schemaName}.accounts (id, "name") VALUES(?, ?)
                """;

    private String deleteSql  = """
                truncate ${schemaName}.accounts
                """;

    @Value("${account.data.count:100}")
    private int accountCount;

    @Value("${account.data.batch.size:10}")
    private int batchSize;

    @Value("${account.data.prefix:LLC}")
    private String accountNamePrefix;


    private String createSchemaSql = """
            create schema if not exists taccounts;
            CREATE TABLE IF NOT EXISTS taccounts.accounts (
                id varchar(100) NOT NULL,
                name varchar(255) NOT NULL,
                PRIMARY KEY (ID)
            );
            """;

    @ConditionalOnProperty( name= "batch.load.accounts", havingValue = "true")
    @Order(9)
    @Bean
    CommandLineRunner loadData(DataSource dataSource)
    {
        var map = Map.of("schemaName",schemaName);
        insertSql = Text.format(insertSql,map);
        deleteSql  = Text.format(deleteSql,map);

        var fullNameCreator = new FullNameCreator();

        return args -> {
            log.info("Inserting accounts {}",insertSql);

            try (var conn = dataSource.getConnection();
                 var statement = conn.createStatement();
                 var insertPreparedStatement = conn.prepareStatement(insertSql);
                 var deleteStatement = conn.createStatement()) {

                // Create schema
                statement.execute(createSchemaSql);

                log.info("Delete account data SQL: {}",deleteSql);
                deleteStatement.execute(deleteSql);

                for (int i = 0; i < accountCount; i++) {
                    insertPreparedStatement.setString(1, valueOf(i));
                    insertPreparedStatement.setString(2, accountNamePrefix +" "+i+" "+fullNameCreator.create());
                    insertPreparedStatement.addBatch();

                    if((i+1) % batchSize == 0)
                    {
                        insertPreparedStatement.executeBatch();
                        log.info("executed batch size: {}",i);
                    }

                }

                insertPreparedStatement.executeBatch();

            } catch (SQLException e) {
                log.warn("Cannot records",e);
            }
        };
    }
}
