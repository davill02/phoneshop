package com.es.core.model.phone;

import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

@Service
public class PhoneServiceImpl implements PhoneService {
    @Resource
    private PhoneDao phoneDao;

    @Override
    public List<Phone> findAllOrderBy(int offset, int limit, String order, String searchField, String searchRequest) {
        return phoneDao.findAllOrderBy(offset, limit, order, searchField, searchRequest);
    }

    @Override
    public Optional<Phone> get(Long key) {
        return phoneDao.get(key);
    }

    @Override
    public Long countResultsFindAllOrderBy(String query) {
        return phoneDao.countResultsFindAllOrderBy(query);
    }

    @Override
    public Optional<Stock> getStock(Long id) {
        return phoneDao.getStock(id);
    }
}
