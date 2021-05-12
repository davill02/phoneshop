package com.es.phoneshop.web.controller.pages.validators;

import com.es.core.model.phone.PhoneDao;
import com.es.core.model.phone.Stock;
import com.es.phoneshop.web.controller.pages.entites.UpdateForm;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

@Component
public class UpdateFormValidator implements Validator {
    private static final String QUANTITY_PATH = "quantity[%d]";
    private static final String PHONE_ID_PATH = "phoneId[%d]";
    private static final String QUANTITY_IS_STRING_ERR_CODE = "quantity.is.string";
    private static final String QUANTITY_NEGATIVE_VALUE_ERR_CODE = "quantity.negative.value";
    private static final String PHONE_ID_NOT_STOCK_ERR_CODE = "phoneId.not.stock";
    private static final String PHONE_ID_NOT_HAVE_ALL_ERR_CODE = "phoneId.not.have.all";
    @Resource
    private PhoneDao phoneDao;

    @Override
    public boolean supports(Class<?> clazz) {
        return UpdateForm.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        UpdateForm updateForm = (UpdateForm) target;
        List<Long> ids = updateForm.getPhoneId();
        List<String> quantities = updateForm.getQuantity();
        for (int i = 0; i < ids.size(); i++) {
            validateQuantities(errors, quantities, ids, i);
        }
    }

    private void validateQuantities(Errors errors, List<String> quantities, List<Long> ids, int i) {
        Long quantity = parseAndValidateQuantityString(errors, quantities, i);
        if (quantity != null) {
            checkQuantitiesAndStock(errors, ids, i, quantity);
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

    private void checkQuantitiesAndStock(Errors errors, List<Long> ids, int i, Long quantity) {
        if (quantity < 1) {
            errors.rejectValue(String.format(QUANTITY_PATH, i), QUANTITY_NEGATIVE_VALUE_ERR_CODE, "Quantity can't be negative");
        } else {
            checkStock(errors, ids, i, quantity);
        }
    }

    private void checkStock(Errors errors, List<Long> ids, int i, Long quantity) {
        Optional<Stock> stock = phoneDao.getStock(ids.get(i));
        if (stock.isPresent()) {
            if (stock.get().getStock() - quantity - stock.get().getReserved() < 0) {
                errors.rejectValue(String.format(PHONE_ID_PATH, i), PHONE_ID_NOT_HAVE_ALL_ERR_CODE, "We don't have this count phones");
            }
        } else {
            errors.rejectValue(String.format(PHONE_ID_PATH, i), PHONE_ID_NOT_STOCK_ERR_CODE, "Can't find product. Remove this product.");
        }
    }
}
