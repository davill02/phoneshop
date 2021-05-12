package com.es.phoneshop.web.controller.pages.validators;

import com.es.phoneshop.web.controller.pages.entites.UpdateForm;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.validation.Errors;

public class UpdateFormValidatorTest {
    private UpdateFormValidator validator = new UpdateFormValidator();
    @Mock
    private Errors errors;


    @Test
    public void shouldValidateUpdateForm() {

    }

    public UpdateForm getValidForm() {
        return null;
    }

}