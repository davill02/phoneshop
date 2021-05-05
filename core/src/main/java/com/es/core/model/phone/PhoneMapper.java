package com.es.core.model.phone;

import org.springframework.beans.BeanUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PhoneMapper {
    private static final String CLASS = "class";
    private static final String ILLEGAL_ARGUMENT_MSG = "Phone cant be null";
    private final Map<String, PropertyDescriptor> mappedFields = new HashMap<>();

    private List<String> forbidProperties;

    public PhoneMapper() {
        this.forbidProperties = new ArrayList<>();
        forbidProperties.add(CLASS);
    }

    public PhoneMapper(List<String> forbidProperties) {
        if (forbidProperties == null) {
            this.forbidProperties = new ArrayList<>();
        } else {
            this.forbidProperties = new ArrayList<>(forbidProperties);
        }
        this.forbidProperties.add(CLASS);

    }

    public Map<String, Object> map(Phone phone) {
        if(phone == null){
            throw new IllegalArgumentException(ILLEGAL_ARGUMENT_MSG);
        }
        initMappedFields(forbidProperties);

        Map<String, Object> parameters = new HashMap<>();
        mappedFields.forEach((name, pd) -> {
            try {
                parameters.put(name, pd.getReadMethod().invoke(phone));
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        });
        return parameters;
    }

    private void initMappedFields(List<String> forbidProperties) {
        if (mappedFields.isEmpty()) {
            PropertyDescriptor[] pds = BeanUtils.getPropertyDescriptors(Phone.class);
            initializeMappedFields(pds, forbidProperties);
        }
    }

    private void initializeMappedFields(PropertyDescriptor[] pds, List<String> forbidProperties) {
        for (PropertyDescriptor pd : pds) {
            if (!forbidProperties.contains(pd.getName())) {
                mappedFields.put(pd.getName(), pd);
            }
        }
    }

    public List<String> getPropertyNames() {
        initMappedFields(forbidProperties);
        return new ArrayList<>(mappedFields.keySet());
    }
}
