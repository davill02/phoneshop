package com.es.core.model.order.mappers;

import java.util.Map;

public interface Mapper<T> {
    Map<String, Object> map(T source);
}
