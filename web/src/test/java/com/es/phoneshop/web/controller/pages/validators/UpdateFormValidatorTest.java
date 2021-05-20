package com.es.phoneshop.web.controller.pages.validators;

import com.es.core.model.phone.PhoneService;
import com.es.core.model.phone.Stock;
import com.es.phoneshop.web.controller.pages.entites.UpdateForm;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.validation.Errors;

import java.util.ArrayList;
import java.util.Optional;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UpdateFormValidatorTest {
    public static final long PHONE_ID = 100L;
    private static final int STOCK = 10;
    @InjectMocks
    private UpdateFormValidator validator = new UpdateFormValidator();
    @Mock
    private Errors errors;
    @Mock
    private PhoneService service;

    @Test
    public void shouldValidateEmptyUpdateForm() {
        UpdateForm updateForm = getUpdateForm();

        validator.validate(updateForm, errors);

        verify(errors, never()).rejectValue(any(), any());
    }

    private UpdateForm getUpdateForm() {
        UpdateForm updateForm = new UpdateForm();
        updateForm.setPhoneId(new ArrayList<>());
        updateForm.setQuantity(new ArrayList<>());
        return updateForm;
    }

    @Test
    public void shouldValidateUpdateForm() {
        UpdateForm updateForm = getValidForm();

        validator.validate(updateForm, errors);

        verify(errors, never()).rejectValue(any(), any());
    }

    @Test
    public void shouldNotPassValidationByStringInQuantityValidation() {
        UpdateForm updateForm = getUpdateForm();
        updateForm.getPhoneId().add(PHONE_ID);
        updateForm.getQuantity().add("wear4sd");

        validator.validate(updateForm, errors);

        verify(errors).rejectValue(any(), any(), any());
    }

    @Test
    public void shouldNotPassValidationByNegativeQuantityValidation() {
        UpdateForm updateForm = getUpdateForm();
        updateForm.getPhoneId().add(PHONE_ID);
        updateForm.getQuantity().add("0");

        validator.validate(updateForm, errors);

        verify(errors).rejectValue(any(), any(), any());
    }

    @Test
    public void shouldNotPassValidationByNotHavingStockValidation() {
        UpdateForm updateForm = getValidForm();
        when(service.getStock(any())).thenReturn(Optional.empty());

        validator.validate(updateForm, errors);

        verify(errors).rejectValue(any(), any(), any());
    }

    @Test
    public void shouldNotPassValidationByOutOfStockStockValidation() {
        UpdateForm updateForm = getValidForm();
        Stock stock = new Stock();
        stock.setStock(STOCK - 1);
        when(service.getStock(any())).thenReturn(Optional.of(stock));

        validator.validate(updateForm, errors);

        verify(errors).rejectValue(any(), any(), any());
    }

    public UpdateForm getValidForm() {
        Stock stock = new Stock();
        stock.setStock(STOCK);
        when(service.getStock(eq(PHONE_ID))).thenReturn(java.util.Optional.of(stock));
        UpdateForm updateForm = getUpdateForm();
        updateForm.getQuantity().add(Integer.toString(STOCK));
        updateForm.getPhoneId().add(PHONE_ID);
        return updateForm;
    }

}