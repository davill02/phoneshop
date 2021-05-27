package com.es.phoneshop.web.controller.pages.validators;

import com.es.core.model.phone.PhoneService;
import com.es.core.model.phone.Stock;
import com.es.phoneshop.web.controller.pages.entites.QuickCartAddingForm;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

@Component
public class QuickCartFormValidator implements Validator {
    private static final String QUANTITY_PATH = "quantity[%d]";
    private static final String PHONE_ID_PATH = "model[%d]";
    private static final String QUANTITY_IS_STRING_ERR_CODE = "quantity.is.string";
    private static final String QUANTITY_NEGATIVE_VALUE_ERR_CODE = "quantity.negative.value";
    private static final String PHONE_ID_NOT_STOCK_ERR_CODE = "phoneId.not.stock";
    private static final String PHONE_ID_NOT_HAVE_ALL_ERR_CODE = "phoneId.not.have.all";
    @Resource
    private PhoneService phoneService;

    @Override
    public boolean supports(Class<?> clazz) {
        return QuickCartAddingForm.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        QuickCartAddingForm quickCartAddingForm = (QuickCartAddingForm) target;
        List<String> models = quickCartAddingForm.getModel();
        List<String> quantities = quickCartAddingForm.getQuantity();
        for (int i = 0; i < models.size(); i++) {
            validateQuantities(errors, quantities, models, i);
        }
    }

    private void validateQuantities(Errors errors, List<String> quantities, List<String> models, int i) {
        if (StringUtils.hasText(quantities.get(i)) && StringUtils.hasText(models.get(i))) {
            Long quantity = parseAndValidateQuantityString(errors, quantities, i);
            if (quantity != null) {
                checkQuantitiesAndStock(errors, models, i, quantity);
            }
        }
    }

    private Long parseAndValidateQuantityString(Errors errors, List<String> quantities, int i) {
        Long quantity = null;
        try {
            quantity = Long.parseLong(quantities.get(i));
        } catch (NumberFormatException e) {
            errors.rejectValue(String.format(QUANTITY_PATH, i), QUANTITY_IS_STRING_ERR_CODE, "Quantity can't be string");
        }
        return quantity;
    }

    private void checkQuantitiesAndStock(Errors errors, List<String> models, int i, Long quantity) {
        if (quantity < 1) {
            errors.rejectValue(String.format(QUANTITY_PATH, i), QUANTITY_NEGATIVE_VALUE_ERR_CODE, "Quantity can't be negative");
        } else {
            checkStock(errors, models, i, quantity);
        }
    }

    private void checkStock(Errors errors, List<String> models, int i, Long quantity) {
        Optional<Stock> stock = phoneService.getStockByModel(models.get(i));
        if (stock.isPresent()) {
            if (stock.get().getStock() - quantity < 0) {
                errors.rejectValue(String.format(PHONE_ID_PATH, i), PHONE_ID_NOT_HAVE_ALL_ERR_CODE, "We don't have this count phones");
            }
        } else {
            errors.rejectValue(String.format(PHONE_ID_PATH, i), PHONE_ID_NOT_STOCK_ERR_CODE, "Can't find product. Remove this product.");
        }
    }
}
