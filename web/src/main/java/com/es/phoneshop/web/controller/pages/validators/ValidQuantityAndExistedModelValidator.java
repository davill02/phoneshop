package com.es.phoneshop.web.controller.pages.validators;

import com.es.core.model.phone.PhoneService;
import com.es.core.model.phone.Stock;
import com.es.phoneshop.web.controller.pages.entites.AddingPhoneRow;
import com.es.phoneshop.web.controller.pages.validators.annotations.ValidQuantityAndExistedModel;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Optional;

@Component
public class ValidQuantityAndExistedModelValidator implements ConstraintValidator<ValidQuantityAndExistedModel, Object> {
    private static final String PRODUCT_NOT_FOUND_MSG = "We don't have %s. Please remove this product";
    private static final String OUT_OF_STOCK_MSG = "We don't have product %s in quantity %s.";
    @Resource
    private PhoneService phoneService;

    @Override
    public void initialize(ValidQuantityAndExistedModel constraintAnnotation) {

    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (!(value instanceof AddingPhoneRow)) {
            return true;
        }
        AddingPhoneRow addingPhoneRow = (AddingPhoneRow) value;
        if (!StringUtils.hasText(addingPhoneRow.getModel()) || addingPhoneRow.getQuantity() == null) {
            return true;
        }
        Optional<Stock> stockOptional = phoneService.getStockByModel(addingPhoneRow.getModel());
        if (!stockOptional.isPresent()) {
            context.buildConstraintViolationWithTemplate(String.format(PRODUCT_NOT_FOUND_MSG, addingPhoneRow.getModel()))
                    .addConstraintViolation();
            return false;
        }
        Stock stock = stockOptional.get();
        if (stock.getStock() - addingPhoneRow.getQuantity() < 0) {
            context.buildConstraintViolationWithTemplate(String.format(OUT_OF_STOCK_MSG, addingPhoneRow.getModel(), addingPhoneRow.getQuantity()))
                    .addConstraintViolation();
            return false;
        }
        return true;
    }
}
