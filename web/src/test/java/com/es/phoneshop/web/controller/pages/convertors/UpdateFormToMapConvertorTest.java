package com.es.phoneshop.web.controller.pages.convertors;

import com.es.phoneshop.web.controller.pages.entites.UpdateForm;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class UpdateFormToMapConvertorTest {
    private UpdateFormToMapConvertor updateFormToMapConvertor = new UpdateFormToMapConvertor();

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionByNotEqualSizes() {
        UpdateForm updateForm = getUpdateForm(Stream.of(12L, 23L, 45L, 323L).collect(Collectors.toList()),
                Stream.of("1231", "424", "123").collect(Collectors.toList()));

        updateFormToMapConvertor.convert(updateForm);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionByStringQuantities() {
        UpdateForm updateForm = getUpdateForm(Stream.of(12L, 23L, 45L, 323L).collect(Collectors.toList()),
                Stream.of("1231", "424", "123", "asfgds").collect(Collectors.toList()));

        updateFormToMapConvertor.convert(updateForm);
    }

    @Test
    public void shouldConvertForm() {
        UpdateForm updateForm = getUpdateForm(Stream.of(12L, 23L, 45L, 323L).collect(Collectors.toList()),
                Stream.of("1231", "424", "123", "23").collect(Collectors.toList()));

        Map<Long, Long> map = updateFormToMapConvertor.convert(updateForm);

        assertTrue(map.containsKey(12L));
        assertTrue(map.containsKey(23L));
        assertEquals(4, map.size());
    }

    private UpdateForm getUpdateForm(List<Long> ids, List<String> quantities) {
        UpdateForm updateForm = new UpdateForm();
        updateForm.setPhoneId(ids);
        updateForm.setQuantity(quantities);
        return updateForm;
    }

}