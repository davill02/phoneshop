package com.es.core.model.phone;

import org.springframework.beans.BeanUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Component
public class JdbcPhoneDao implements PhoneDao {
    private static final String SELECT_PHONE_BY_ID = "select * from phones where id = %d";
    private static final String SELECT_COLORS_IDS_BY_PHONE_ID = "select colorId  from COLORS join phone2color on id = colorId where phoneId = %d";
    private static final String FIND_COLOR_BY_ID = "SELECT * FROM colors WHERE id = %d";
    @Resource
    private JdbcTemplate jdbcTemplate;
    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private final Map<String, PropertyDescriptor> mappedFields = new HashMap<>();


    public Optional<Phone> get(final Long key) {
        readWriteLock.readLock().lock();
        Optional<Phone> phone = Optional.ofNullable(
                (Phone) jdbcTemplate.queryForObject(
                        String.format(SELECT_PHONE_BY_ID, key),
                        new BeanPropertyRowMapper(Phone.class)
                ));
        phone.ifPresent(this::setColors);
        readWriteLock.readLock().unlock();
        return phone;
    }

    private void setColors(Phone phone) {
        List<Long> colorIds =
                jdbcTemplate
                        .query(String.format(SELECT_COLORS_IDS_BY_PHONE_ID, phone.getId()),
                                (rs, rowNum) -> rs.getLong("colorId"));
        Set<Color> colorSet = createColorSetByColorIds(colorIds);
        phone.setColors(colorSet);
    }

    private Set<Color> createColorSetByColorIds(List<Long> colorIds) {
        Set<Color> colorSet = new HashSet<>();
        colorIds.forEach(id -> {
            Color colorById = jdbcTemplate.queryForObject(String.format(FIND_COLOR_BY_ID, id), (resultSet, i) -> {
                Color color = new Color();
                color.setId(resultSet.getLong("id"));
                color.setCode(resultSet.getString("code"));
                return color;
            });
            colorSet.add(colorById);
        });
        return colorSet;
    }

    public void save(final Phone phone) {
        readWriteLock.writeLock().lock();
        if (phone != null) {
            addPhone(phone);
            addColors(phone);
        }
        readWriteLock.writeLock().unlock();
    }

    private void addColors(Phone phone) {
        if (phone.getColors() != null && !phone.getColors().isEmpty()) {
            phone.getColors()
                    .forEach(color -> jdbcTemplate.update("insert into phone2color (phoneId, colorId) values (?, ?)",
                            phone.getId(), color.getId()));
        }
    }

    private void addPhone(Phone phone) {
        if (mappedFields.isEmpty()) {
            PropertyDescriptor[] pds = BeanUtils.getPropertyDescriptors(Phone.class);
            initializeMappedFields(pds);
        }
        SimpleJdbcInsert insertPhone = new SimpleJdbcInsert(jdbcTemplate).withTableName("phones");
        Map<String, Object> parameters = new HashMap<>();
        mappedFields.forEach((name, pd) -> {
            try {
                parameters.put(name, pd.getReadMethod().invoke(phone));
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        });
        insertPhone.execute(parameters);
    }

    private void initializeMappedFields(PropertyDescriptor[] pds) {
        for (PropertyDescriptor pd : pds) {
            if (!pd.getName().equals("colors")) {
                mappedFields.put(pd.getName(), pd);
            }
        }
    }

    public List<Phone> findAll(int offset, int limit) {
        readWriteLock.readLock().lock();
        List<Phone> phones = jdbcTemplate.query("select * from phones limit " + limit  + " offset " + offset ,
                new BeanPropertyRowMapper(Phone.class));
        phones.forEach(phone -> Optional.ofNullable(phone).ifPresent(this::setColors));
        readWriteLock.readLock().unlock();
        return phones;
    }
}
