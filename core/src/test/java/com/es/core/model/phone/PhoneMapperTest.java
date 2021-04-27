package com.es.core.model.phone;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PhoneMapperTest {
    private static final String BRAND_SAMSUNG = "Samsung";
    private static final String MODEL_GALAXY_2020 = "GALAXY 2020";
    private static final String COLORS_PROP = "colors";
    private static final String ID_PROP = "id";
    private static final int COUNT_PHONE_PROPERTIES = 27;

    @Test
    public void shouldMapPhoneToNameValueMap() {
        PhoneMapper phoneMapper = new PhoneMapper();

        Map<String, Object> nameValue = phoneMapper.map(getValidPhone());

        assertTrue(nameValue.containsValue(BRAND_SAMSUNG));
        assertTrue(nameValue.containsValue(MODEL_GALAXY_2020));
        assertTrue(nameValue.containsKey(COLORS_PROP));
        assertTrue(nameValue.containsKey(ID_PROP));
    }

    @Test
    public void shouldGetAllProperties() {
        PhoneMapper phoneMapper = new PhoneMapper();

        List<String> propNames = phoneMapper.getPropertyNames();

        assertEquals(COUNT_PHONE_PROPERTIES, propNames.size());
    }

    @Test
    public void shouldGetAllPropertiesWithNullInConstructor() {
        PhoneMapper phoneMapper = new PhoneMapper(null);

        List<String> propNames = phoneMapper.getPropertyNames();

        assertEquals(COUNT_PHONE_PROPERTIES, propNames.size());
    }

    @Test
    public void shouldGetAllAvailableProps() {
        List<String> forbidProps = new ArrayList<>();
        forbidProps.add(COLORS_PROP);
        forbidProps.add(ID_PROP);
        PhoneMapper mapper = new PhoneMapper(forbidProps);

        List<String> props = mapper.getPropertyNames();

        assertEquals(COUNT_PHONE_PROPERTIES - forbidProps.size(), props.size());
        assertFalse(props.contains(COLORS_PROP));
        assertFalse(props.contains(ID_PROP));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldGetAllAndThrowException() {
        PhoneMapper phoneMapper = new PhoneMapper();

        phoneMapper.map(null);
    }

    private Phone getValidPhone() {
        Phone phone = new Phone();
        phone.setBrand(BRAND_SAMSUNG);
        phone.setModel(MODEL_GALAXY_2020);
        return phone;
    }
}