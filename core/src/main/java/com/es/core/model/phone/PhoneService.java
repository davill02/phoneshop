package com.es.core.model.phone;

import java.util.List;
import java.util.Optional;

public interface PhoneService {
    List<Phone> findAllOrderBy(int offset, int limit, String order, String searchField, String searchRequest);

    Optional<Phone> get(Long key);

    Long countResultsFindAllOrderBy(String query);

    Optional<Stock> getStock(Long id);
}
