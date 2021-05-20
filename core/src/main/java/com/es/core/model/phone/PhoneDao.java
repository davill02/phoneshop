package com.es.core.model.phone;

import java.util.List;
import java.util.Optional;

public interface PhoneDao {
    Optional<Phone> get(Long key);

    void save(Phone phone);

    List<Phone> findAll(int offset, int limit);

    List<Phone> findAllOrderBy(int offset, int limit, String order, String searchField, String searchRequest);

    Long countResultsFindAllOrderBy(String searchRequest);

    Optional<Stock> getStock(Long key);

    void decreaseStock(Long key, Long count);

    void increaseStock(Long key, Long count);
}
