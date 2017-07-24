package utilities;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dtos.UrlParametersDto;
import org.apache.poi.ss.formula.functions.T;

import java.io.IOException;

/**
 * Created by Robertson_Laptop on 11/21/2016.
 */
public class JSONUtils {
    public static String convertToJSON(Object object) {
        ObjectMapper mapper = new ObjectMapper();
        String json = null;
        try {
            json = mapper.writeValueAsString(object);


        } catch (JsonGenerationException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return json;
    }

    public static UrlParametersDto convertUrlParameterDtoToObject(String json)
    {
        UrlParametersDto retDto = null;
        ObjectMapper mapper = new ObjectMapper();

        try {
            retDto = mapper.readValue(json, UrlParametersDto.class);
        } catch (Exception e)
        {

        }

        return retDto;
    }
}
