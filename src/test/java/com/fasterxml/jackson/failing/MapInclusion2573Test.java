package com.fasterxml.jackson.failing;

import java.util.*;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.json.JsonMapper;

public class MapInclusion2573Test extends BaseMapTest
{
    @JsonPropertyOrder({ "model", "properties" })
    static class Car
    {
        public String model;
        public Map<String, Integer> properties;
    }

    /*
    /**********************************************************
    /* Test methods
    /**********************************************************
     */

    private final Map<String, Integer> CAR_PROPERTIES = new LinkedHashMap<>();
    {
        CAR_PROPERTIES.put("Speed", 100);
        CAR_PROPERTIES.put("Weight", null);
    }

    private final Car CAR = new Car();
    {
        CAR.model = "F60";
        CAR.properties = CAR_PROPERTIES;
    }

    private final JsonInclude.Value BOTH_NON_NULL = JsonInclude.Value.construct(JsonInclude.Include.NON_NULL,
            JsonInclude.Include.NON_NULL);
    
//    final private ObjectMapper MAPPER = objectMapper();

    // [databind#2572]
    public void test2572MapDefault() throws Exception
    {
                
        ObjectMapper mapper = JsonMapper.builder()
                .changeDefaultPropertyInclusion(incl -> BOTH_NON_NULL)
                .build();
        assertEquals(aposToQuotes("{'Speed':100}"),
                mapper.writeValueAsString(CAR_PROPERTIES));
        assertEquals(aposToQuotes("{'model':'F60','properties':{'Speed':100}}"),
                mapper.writeValueAsString(CAR));
    }

    // [databind#2572]
    public void test2572MapOverrideUseDefaults() throws Exception
    {
        ObjectMapper mapper = JsonMapper.builder()
                .changeDefaultPropertyInclusion(incl -> BOTH_NON_NULL)
                .withConfigOverride(Map.class, 
                        o -> o.setInclude(JsonInclude.Value.construct(JsonInclude.Include.USE_DEFAULTS,
                        JsonInclude.Include.USE_DEFAULTS)))
                .build();
        assertEquals(aposToQuotes("{'Speed':100}"),
                mapper.writeValueAsString(CAR_PROPERTIES));
        assertEquals(aposToQuotes("{'model':'F60','properties':{'Speed':100}}"),
                mapper.writeValueAsString(CAR));
    }

    // [databind#2572]
    public void test2572MapOverrideInclAlways() throws Exception
    {
        ObjectMapper mapper = JsonMapper.builder()
                .changeDefaultPropertyInclusion(incl -> BOTH_NON_NULL)
                .withConfigOverride(Map.class,
                        o -> o.setInclude(JsonInclude.Value.construct(JsonInclude.Include.ALWAYS,
                        JsonInclude.Include.ALWAYS)))
                .build();
        assertEquals(aposToQuotes("{'Speed':100,'Weight':null}"),
                mapper.writeValueAsString(CAR_PROPERTIES));
        assertEquals(aposToQuotes("{'model':'F60','properties':{'Speed':100,'Weight':null}}}"),
                mapper.writeValueAsString(CAR));
    }
}