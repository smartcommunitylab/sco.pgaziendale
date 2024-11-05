/*******************************************************************************
 * Copyright 2015 Fondazione Bruno Kessler
 * 
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 * 
 *        http://www.apache.org/licenses/LICENSE-2.0
 * 
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 ******************************************************************************/
package it.smartcommunitylab.pgazienda.web.rest;

import java.io.IOException;
import java.io.InputStream;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * Utility class for testing REST controllers.
 */
public final class TestUtil {

    private static final ObjectMapper mapper = createObjectMapper();

    /**
     * Creates an object mapper for JSON serialization and deserialization.
     *
     * This object mapper is configured to:
     * <ul>
     *     <li>Not write durations as timestamps.</li>
     *     <li>Including only non-empty fields in JSON output.</li>
     *     <li>Using the JavaTimeModule for date and time serialization and deserialization.</li>
     * </ul>
     *
     * @return the object mapper.
     */
    private static ObjectMapper createObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS, false);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }

    /**
     * Convert an object to JSON byte array.
     *
     * @param object the object to convert.
     * @return the JSON byte array.
     * @throws IOException
     */
    public static byte[] convertObjectToJsonBytes(Object object) throws IOException {
        return mapper.writeValueAsBytes(object);
    }
    
    /**
     * Convert an object from JSON byte array.
     *
     * @param is the input stream of JSON data.
     * @param cls the type of the object to convert.
     * @return the converted object.
     * @throws JsonParseException if the JSON data is not valid JSON.
     * @throws JsonMappingException if the JSON data cannot be mapped to an object of the given type.
     * @throws IOException if there is an IO error while reading the JSON data.
     */
    public static <T> T readObject(InputStream is, Class<T> cls) throws JsonParseException, JsonMappingException, IOException {
    	return mapper.readValue(is, cls);
    } 

    private TestUtil() {}
}
