package com.es.core.model.phone;

import org.springframework.beans.BeanUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PhoneMapper {
    private final Map<String, PropertyDescriptor> mappedFields = new HashMap<>();

    public Map<String, Object> map(Phone phone, List<String> forbidProperties) {
        if (mappedFields.isEmpty()) {
            PropertyDescriptor[] pds = BeanUtils.getPropertyDescriptors(Phone.class);
            initializeMappedFields(pds, forbidProperties);
        }

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

    private void initializeMappedFields(PropertyDescriptor[] pds, List<String> forbidProperties) {
        for (PropertyDescriptor pd : pds) {
            if (!forbidProperties.contains(pd.getName())) {
                mappedFields.put(pd.getName(), pd);
            }
        }
    }
}
