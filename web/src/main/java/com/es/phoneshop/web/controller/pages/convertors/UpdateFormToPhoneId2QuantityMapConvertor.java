package com.es.phoneshop.web.controller.pages.convertors;

import com.es.phoneshop.web.controller.pages.entites.UpdateForm;
import org.springframework.core.convert.converter.Converter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UpdateFormToPhoneId2QuantityMapConvertor implements Converter<UpdateForm, Map<Long, Long>> {

    public static final String IDS_AND_QUANTITY_SIZES_MUST_BE_EQUAL_MSG = "Ids and quantity sizes must be equal";
    public static final String QUANTITY_CAN_T_BE_STRING_MSG = "Quantity can't be String";

    @Override
    public Map<Long, Long> convert(UpdateForm source) {
        Map<Long, Long> map = new HashMap<>();
        List<String> quantity = source.getQuantity();
        List<Long> ids = source.getPhoneId();
        checkSizes(quantity, ids);
        fillMap(source, map, quantity, ids);
        return map;
    }

    private void checkSizes(List<String> quantity, List<Long> ids) {
        if (ids.size() != quantity.size()) {
            throw new IllegalArgumentException(IDS_AND_QUANTITY_SIZES_MUST_BE_EQUAL_MSG);
        }
    }

    private void fillMap(UpdateForm source, Map<Long, Long> map, List<String> quantity, List<Long> ids) {
        try {
            for (int i = 0; i < source.getPhoneId().size(); i++) {
                map.put(ids.get(i), Long.parseLong(quantity.get(i)));
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(QUANTITY_CAN_T_BE_STRING_MSG);
        }
    }
}
