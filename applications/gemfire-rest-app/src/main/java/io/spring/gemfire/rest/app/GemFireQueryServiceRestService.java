package io.spring.gemfire.rest.app;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vmware.data.services.gemfire.io.QuerierService;
import io.spring.gemfire.rest.app.exception.DataError;
import io.spring.gemfire.rest.app.exception.FaultAgent;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.geode.cache.query.Struct;
import org.apache.geode.cache.query.types.StructType;
import org.apache.geode.pdx.JSONFormatter;
import org.apache.geode.pdx.PdxInstance;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * Generic READ/WRITE GemFire REST service
 *
 * @author Gregory Green
 */
@RestController
@RequestMapping("/query")
public class GemFireQueryServiceRestService
{
    private Logger logger = LogManager.getLogger(getClass());

    private static final String EMPTY_JSON = "{}";
    ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    QuerierService querierService;

    @Autowired
    FaultAgent faultAgent;

    /**
     * Execute a GemFire query with no limit
     *
     * @param query the OQL to execute
     * @return the JSON array records
     * @throws Exception
     */
    @PostMapping(path = "/", produces = "application/json")
    public String query(@RequestBody String query)
    throws Exception
    {
        return queryLimit(query, -1);
    }//------------------------------------------------


    /**
     * Execute a GemFire query
     *
     * @param query the OQL to execute
     * @param limit the results count limit
     * @return the JSON records
     * @throws Exception
     */
    @PostMapping(path = "{limit}", produces = "application/json")
    public String queryLimit(@RequestBody String query, @PathVariable int limit)
    throws Exception
    {
        if (query == null || query.length() == 0)
            return null;

        try
        {

            query = this.appendLimit(query, limit);


            logger.trace("QueryService: START query " + query);
            Collection<Object> results = querierService.query(query);
            logger.trace("QueryService: END query " + query);

            if (results == null)
                return "[]";

            return toJson(results);

        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw e;
        }
    }//------------------------------------------------

    /**
     * Handling exceptions in general for REST responses
     *
     * @param request  the HTTP request
     * @param response the HTTP response
     * @param e        the exception
     * @return Data Error details
     */
    @ExceptionHandler(Exception.class)
    DataError handleException(HttpServletRequest request, HttpServletResponse response, Exception e)
    {
        return faultAgent.handleException(request, response, e);
    }//------------------------------------------------

    /**
     * @param query the OQL to append
     * @param limit the limit number
     * @return the OQL appended with limit if > 0
     */
    protected String appendLimit(String query, int limit)
    {
        if (limit <= 0 || query.contains(" limit "))
            return query;

        return new StringBuilder(query).append(" limit ").append(limit).toString();
    }

    protected String toJson(Collection<?> list)
    throws JsonProcessingException
    {

        if (list.isEmpty())
            return EMPTY_JSON;

        //get first element
        Object firstElement = list.iterator().next();

        //if struct
        if (firstElement instanceof Struct)
        {
            return toJsonFromStruct((Collection<Struct>) list, ((Struct) firstElement).getStructType());
        }
        else if (firstElement instanceof PdxInstance)
        {
            //else normal

            Collection<PdxInstance> results = (Collection<PdxInstance>) list;

            var responseJson = new StringBuilder();

            int count = 0;

            for (PdxInstance pdxInstance : results)
            {

                if (count > 0)
                    responseJson.append(",");

                responseJson.append(JSONFormatter.toJSON(pdxInstance));

                count++;

            }

            StringBuilder allResults = new StringBuilder("[").append(responseJson).append("]");

            return allResults.toString();
        }


        //list of object (not Pdx or Struct)

        return objectMapper.writeValueAsString(list);


    }//-------------------------------------------

    /**
     * @param results    the collection of results
     * @param structType the struct type
     */
    private String toJsonFromStruct(Collection<Struct> results, StructType structType)
    throws JsonProcessingException
    {
        String[] fieldNames = structType.getFieldNames();


        List<Map<String, String>> listMaps = new ArrayList<>(results.size());

        for (Struct struct : results)
        {

            Object[] values = struct.getFieldValues();

            if (values == null)
                continue;

            HashMap<String, String> map = new HashMap(values.length);

            for (int i = 0; i < values.length; i++)
            {
                map.put(fieldNames[i], values[i].toString());
            }

            listMaps.add(map);

        }

        return objectMapper.writeValueAsString(listMaps);
    }

}
