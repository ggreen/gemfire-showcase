package io.spring.gemfire.rest.app;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vmware.data.services.gemfire.client.GemFireClient;
import io.spring.gemfire.rest.app.exception.DataError;
import io.spring.gemfire.rest.app.exception.DataServiceSystemException;
import io.spring.gemfire.rest.app.exception.FaultAgent;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.RegionDestroyedException;
import org.apache.geode.cache.client.ServerOperationException;
import org.apache.geode.pdx.PdxInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * Service for manage region records
 */
@RestController
@RequestMapping("/region")
public class GemFireRegionRestService
{
    @Autowired
    GemFireClient gemfire;

    @Autowired
    FaultAgent faultAgent;

    ObjectMapper objectMapper = new ObjectMapper();
    private final PdxService pdxService;

	public GemFireRegionRestService(PdxService pdxService)
	{
		this.pdxService = pdxService;
	}

	/**
     * Put a new records
     *
     * @param region the region name
     * @param key    the region key
     * @param value  the region value
     * @return previous Region values in JSON
     * @throws Exception when an unknown error occurs
     */
    @PostMapping(path = "{region}/{key}", produces = "application/json")
    public void putEntry(@PathVariable String region, @PathVariable String key, @RequestBody String value)
    throws Exception
    {

        if (region == null || region.length() == 0 || key == null || value == null)
            return;

        try
        {
            Region<String, Object> gemRegion = gemfire.getRegion(region);

            PdxInstance pdxInstance = pdxService.fromJSON(value);
            gemRegion.put(key, pdxInstance);

        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw e;
        }
    }//------------------------------------------------

    /**
     * Delete a region entry by a key
     *
     * @param regionName the region name
     * @param key        the region key
     * @return the deleted region value in JSON format
     */
    @DeleteMapping(path = "{regionName}/{key}", produces = "application/json")
    public void delete(@PathVariable String regionName, @PathVariable String key)
    {
        Region<String, Object> region = this.gemfire.getRegion(regionName);

        Object obj = region.remove(key);

    }//------------------------------------------------

    /**
     * Get a value by a given key
     *
     * @param region the region name
     * @param key    the region key
     * @return the value of the region in JSON format
     */
    @GetMapping(path = "{region}/{key}", produces = "application/json")
    String getValueByKey(@PathVariable String region, @PathVariable String key, String type)
    {
        try
        {
            if (region == null || region.length() == 0 || key == null)
                return null;

            Region<String, Object> gemRegion = gemfire.getRegion(region);

            Object value = gemRegion.get(key);

            if (value == null)
                return null;

            if (value instanceof PdxInstance)
                return pdxService.toJSON((PdxInstance) value, type);
            else
                return objectMapper.writeValueAsString(value);
        }

        catch (ServerOperationException serverError)
        {
            Throwable cause = serverError.getRootCause();
            if (cause instanceof RegionDestroyedException)
            {
                throw new DataServiceSystemException("Region \"" + region + "\" not found");
            }
            throw new DataServiceSystemException(serverError.getMessage(), serverError);
        }
        catch (JsonProcessingException | RuntimeException e)
        {

            e.printStackTrace();

            throw new DataServiceSystemException(e.getMessage(), e);
        }
    }//------------------------------------------------
    /**
     * HAndling exceptions in general for REST responses
     * @param request the HTTP request
     * @param response the HTTP reponse
     * @param e the exception
     * @return Data Error details
     */
    /**
     * Handling exceptions in general for REST responses
     *
     * @param request  the HTTP request
     * @param response the HTTP reponse
     * @param e        the exception
     * @return Data Error details
     */
    @ExceptionHandler(Exception.class)
    private DataError handleException(HttpServletRequest request, HttpServletResponse response, Exception e)
    {
        return faultAgent.handleException(request, response, e);
    }//------------------------------------------------
}
