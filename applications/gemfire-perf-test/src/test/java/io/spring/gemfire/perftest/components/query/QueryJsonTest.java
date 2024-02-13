package io.spring.gemfire.perftest.components.query;

import com.vmware.data.services.gemfire.client.GemFireClient;
import io.spring.gemfire.perftest.components.query.domain.WibCanonicalTradeDataModel;
import nyla.solutions.core.patterns.creational.generator.JavaBeanGeneratorCreator;
import nyla.solutions.core.util.Debugger;
import org.apache.geode.cache.Region;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.Map;

/**
 * create region --name=FM_TRADE_EVENTS --type=PARTITION
 *
 * query --query="select * from /FM_TRADE_EVENTS.entries"
 */
public class QueryJsonTest {

    private static GemFireClient gemfire;
    private final static String regionName = "FM_TRADE_EVENTS";
    private static Region<String, WibCanonicalTradeDataModel> region;
    private WibCanonicalTradeDataModel wibCanonicalTrade;
    private Map<String, Object> tradeDictionary;

    @BeforeAll
    static void beforeAll() {
        gemfire = GemFireClient.connect();
        region = gemfire.getRegion(regionName);
    }

    @BeforeEach
    void setUp() {
        wibCanonicalTrade = JavaBeanGeneratorCreator.of(WibCanonicalTradeDataModel.class).create();

        tradeDictionary = Map.of("TRADE_KEYWORD.AlgoState","ENTRY_2STAGE_STAGE1");

        wibCanonicalTrade.setTradeDictionary(tradeDictionary);
        wibCanonicalTrade.setTradeSource("Calypso");


    }

    /**
     *                 SELECT value.tradeDictionary
     *                 FROM /FM_TRADE_EVENTS.entries
     *                 WHERE value.tradeSource = 'Calypso'
     *                 AND value.tradeDictionary."TRADE_KEYWORD.AlgoState" = 'NONE'
     *                 ORDER BY key
     *                 LIMIT 2
     */
    @Test
    void query() {

        var query = """
                SELECT *
                FROM /FM_TRADE_EVENTS.entries
                WHERE value.tradeSource = 'Calypso'
                AND value.tradeDictionary."TRADE_KEYWORD.AlgoState" = 'NONE'
                ORDER BY key
                LIMIT 2
                """;

        Collection<Object> results = gemfire.getQuerierService().query(query);
        Debugger.println(results);
    }
}
