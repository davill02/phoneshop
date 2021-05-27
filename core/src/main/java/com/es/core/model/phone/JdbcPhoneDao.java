package com.es.core.model.phone;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;


@Component
public class JdbcPhoneDao implements PhoneDao {
    private static final String SELECT_PHONE_BY_ID = "SELECT * FROM phones WHERE id = ?";
    private static final String FIND_COLOR_BY_ID = "SELECT colorId, code FROM colors JOIN phone2color ON colors.id = phone2color.colorId WHERE phoneId = ?";
    private static final String INSERT_INTO_PHONE_2_COLOR = "INSERT INTO phone2color (phoneId, colorId) VALUES  (?, ?)";
    private static final String FIND_ID_PHONE_BY_BRAND_AND_MODEL = "SELECT id FROM phones WHERE brand = ? AND model = ? ";
    private static final String SELECT_FROM_PHONES_LIMIT_OFFSET = "SELECT * FROM phones LIMIT ? OFFSET ?";
    private static final String UPDATE_PHONE = "UPDATE phones SET brand = ?, model = ?, price = ?, displaySizeInches = ?, weightGr = ?, lengthMm = ?, widthMm = ?, heightMm = ?, announced = ?, deviceType = ?, os = ?, displayResolution = ?, pixelDensity = ?, displayTechnology = ?, backCameraMegapixels = ?, frontCameraMegapixels = ?, ramGb = ?, internalStorageGb = ?, batteryCapacityMah = ?, talkTimeHours = ?, standByTimeHours = ?, bluetooth = ?, positioning = ?, imageUrl = ?, description = ? WHERE id = ?";
    private static final String COLUMNS = " brand,model,price,displaySizeInches,weightGr, lengthMm,\n" +
            "widthMm, heightMm, announced, deviceType, os, displayResolution,pixelDensity, displayTechnology, backCameraMegapixels,\n" +
            "frontCameraMegapixels, ramGb, internalStorageGb, batteryCapacityMah, talkTimeHours, standByTimeHours, bluetooth,\n" +
            "positioning, imageUrl, description, phoneId as id\n";
    private static final String SQL_SEARCH_FUNCTION = "SELECT " + COLUMNS +
            ", (LENGTH(LOWER(MODEL)) + LENGTH(LOWER(BRAND)) - LENGTH(REGEXP_REPLACE(LOWER(BRAND),?,''))-  LENGTH(REGEXP_REPLACE(LOWER(MODEL),?, ''))) AS rank " +
            "FROM phones JOIN stocks s on phones.id = S.phoneId\n" +
            "where (LENGTH(LOWER(model)) + LENGTH(LOWER(BRAND)) - LENGTH(REGEXP_REPLACE(LOWER(BRAND),?,'')) - LENGTH(REGEXP_REPLACE(LOWER(model), ?, ''))) > 0 AND stock > 0\n";
    private static final String FIND_ORDER_BY = "SELECT " + COLUMNS + " FROM phones JOIN stocks s on phones.id = S.phoneId\n" +
            "WHERE stock > 0 " + "ORDER BY %s %s" + " LIMIT ? OFFSET ?";
    private static final String RANK_SEARCH = SQL_SEARCH_FUNCTION + "ORDER BY rank DESC" + " LIMIT ? OFFSET ?";
    private static final String RANK_SEARCH_WITH_FIELD = SQL_SEARCH_FUNCTION + "ORDER BY rank DESC, %s %s" + " LIMIT ? OFFSET ?";
    private static final String DELETE_COLORS_BY_PHONE_ID = "DELETE FROM phone2color WHERE phoneId = ?";
    private static final String SEARCH_COUNT = "SELECT COUNT(*) FROM PHONES JOIN STOCKS S on PHONES.ID = S.PHONEID where (LENGTH(LOWER(MODEL)) + LENGTH(LOWER(BRAND)) - LENGTH(REGEXP_REPLACE(LOWER(BRAND),?,'')) - LENGTH(REGEXP_REPLACE(LOWER(MODEL), ?, ''))) > 0 AND STOCK > 0";
    private static final String ALL_PHONES_COUNT = "SELECT COUNT(*) FROM PHONES JOIN STOCKS S on PHONES.ID = S.PHONEID where STOCK > 0";
    private static final String SELECT_STOCK_BY_ID = "SELECT stock, reserved FROM stocks WHERE phoneId = ?";

    private static final String COLUMN_ID = "id";
    private static final String COLUMN_CODE = "code";
    private static final String COLUMN_COLOR_ID = "colorId";
    private static final String COLUMN_STOCK = "stock";
    private static final String COLUMN_COLORS = "colors";
    private static final String COLUMN_RESERVED = "reserved";
    private static final String TABLE_PHONES = "phones";
    private static final String ASC = "ASC";
    private static final String DESC = "DESC";

    private static final String ILLEGAL_ORDER_MSG = "Order can be ASC or DESC, now order = ";
    private static final String ILLEGAL_ARGUMENT_MSG = "phone or phone.getBrand() or phone. getModel() is null";
    private static final String ILLEGAL_SEARCH_FIELD_MSG = "searchField cant be ";
    private static final char WHITESPACE = ' ';
    private static final String COUNT_IS_NULL_MSG = "Count is null";
    private static final String UPDATE_STOCK_WITH_ID = "UPDATE stocks SET stock = ? WHERE phoneId = ?";


    @Resource
    private JdbcTemplate jdbcTemplate;
    private PhoneMapper phoneMapper;
    private SimpleJdbcInsert insertPhone;

    @PostConstruct
    private void init() {
        insertPhone = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName(TABLE_PHONES)
                .usingGeneratedKeyColumns(COLUMN_ID);
        List<String> forbidProperties = new ArrayList<>();
        forbidProperties.add(COLUMN_ID);
        forbidProperties.add(COLUMN_COLORS);
        phoneMapper = new PhoneMapper(forbidProperties);
    }

    public Optional<Phone> get(final Long key) {
        if (key == null) {
            return Optional.empty();
        }
        Optional<Phone> phone = getOptionalPhone(key);
        phone.ifPresent(this::setColors);
        return phone;
    }

    private Optional<Phone> getOptionalPhone(Long key) {
        List<Phone> phones =
                jdbcTemplate.query(SELECT_PHONE_BY_ID, new BeanPropertyRowMapper<>(Phone.class), key);
        Optional<Phone> result = Optional.empty();
        if (phones.size() != 0) {
            result = Optional.of(phones.get(0));
        }
        return result;
    }

    public Optional<Stock> getStock(final Long key) {
        Optional<Phone> phone = this.get(key);
        if (!phone.isPresent()) {
            return Optional.empty();
        }
        List<Stock> stockList = jdbcTemplate.query(SELECT_STOCK_BY_ID, (rs, num) -> {
            Stock stock = new Stock();
            stock.setStock(rs.getInt(COLUMN_STOCK));
            stock.setReserved(rs.getInt(COLUMN_RESERVED));
            return stock;
        }, phone.get().getId());
        if (stockList.size() == 0) {
            return Optional.empty();
        } else {
            stockList.get(0).setPhone(phone.get());
            return Optional.ofNullable(stockList.get(0));
        }
    }

    public Optional<Stock> getStockByModel(final String model) {
        return getStock(getOptionalPhoneByModel(model).orElse(new Phone()).getId());
    }

    @Override
    public Optional<Long> getIdByModel(String model) {
        List<Long> ids = jdbcTemplate.queryForList("SELECT id FROM PHONES WHERE MODEL = ?", Long.class, model);
        if (ids.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(ids.get(0));
        }
    }

    private Optional<Phone> getOptionalPhoneByModel(String model) {
        List<Phone> phones =
                jdbcTemplate.query("SELECT * FROM PHONES WHERE model = ?", new BeanPropertyRowMapper<>(Phone.class), model);
        Optional<Phone> result = Optional.empty();
        if (phones.size() != 0) {
            result = Optional.of(phones.get(0));
        }
        return result;
    }

    private void setColors(Phone phone) {
        List<Color> colors = jdbcTemplate.query(FIND_COLOR_BY_ID, (resultSet, i) -> {
            Color color = new Color();
            color.setId(resultSet.getLong(COLUMN_COLOR_ID));
            color.setCode(resultSet.getString(COLUMN_CODE));
            return color;
        }, phone.getId());
        Set<Color> colorSet = new HashSet<>(colors);
        phone.setColors(colorSet);
    }

    public void save(final Phone phone) {
        if (phone != null && phone.getBrand() != null && phone.getModel() != null) {
            saveValidPhone(phone);
        } else {
            throw new IllegalArgumentException(ILLEGAL_ARGUMENT_MSG);
        }
    }

    private void saveValidPhone(Phone phone) {
        if (containsPhone(phone)) {
            phone.setId(getExistPhoneId(phone));
            addPhoneIfExist(phone);
        } else {
            addPhoneIfNotExist(phone);
        }
    }

    private void addPhoneIfExist(Phone phone) {
        jdbcTemplate.update(UPDATE_PHONE, phone.getBrand(), phone.getModel(), phone.getPrice(),
                phone.getDisplaySizeInches(), phone.getWeightGr(), phone.getLengthMm(), phone.getWidthMm(), phone.getHeightMm(),
                phone.getAnnounced(), phone.getDeviceType(), phone.getOs(), phone.getDisplayResolution(), phone.getPixelDensity(),
                phone.getDisplayTechnology(), phone.getBackCameraMegapixels(), phone.getFrontCameraMegapixels(), phone.getRamGb(),
                phone.getInternalStorageGb(), phone.getBatteryCapacityMah(), phone.getTalkTimeHours(), phone.getStandByTimeHours(),
                phone.getBluetooth(), phone.getPositioning(), phone.getImageUrl(), phone.getDescription(), phone.getId());
        jdbcTemplate.update(DELETE_COLORS_BY_PHONE_ID, phone.getId());
        addColors(phone);
    }

    private Long getExistPhoneId(Phone phone) {
        return jdbcTemplate.queryForObject(FIND_ID_PHONE_BY_BRAND_AND_MODEL, Long.class, phone.getBrand(), phone.getModel());
    }

    private void addPhoneIfNotExist(Phone phone) {
        addPhone(phone);
        addColors(phone);
    }

    private boolean containsPhone(Phone phone) {
        List<Long> phones = jdbcTemplate.queryForList(FIND_ID_PHONE_BY_BRAND_AND_MODEL, Long.class, phone.getBrand(), phone.getModel());
        return !phones.isEmpty();
    }

    private void addColors(Phone phone) {
        if (phone.getColors() != null && !phone.getColors().isEmpty()) {
            phone.getColors().forEach(color -> jdbcTemplate.update(INSERT_INTO_PHONE_2_COLOR, phone.getId(), color.getId()));
        }
    }

    private void addPhone(Phone phone) {
        Map<String, Object> parameters = phoneMapper.map(phone);
        phone.setId(insertPhone.executeAndReturnKey(parameters).longValue());
    }


    public List<Phone> findAll(int offset, int limit) {
        List<Phone> phones = jdbcTemplate.query(SELECT_FROM_PHONES_LIMIT_OFFSET,
                new BeanPropertyRowMapper<>(Phone.class), limit, offset);
        phones.forEach(phone -> Optional.ofNullable(phone).ifPresent(this::setColors));
        return phones;
    }

    @Override
    public List<Phone> findAllOrderBy(int offset, int limit, String order, String sort, String query) {
        validateOrder(order);
        validateSearchField(sort);
        return getPhones(offset, limit, order, sort, query);
    }

    private void validateOrder(String order) {
        if (StringUtils.hasText(order)) {
            if (!order.trim().equalsIgnoreCase(ASC) && !order.trim().equalsIgnoreCase(DESC))
                throw new IllegalArgumentException(ILLEGAL_ORDER_MSG + order);
        }
    }

    private void validateSearchField(String sort) {
        if (StringUtils.hasText(sort)) {
            long count = phoneMapper.getPropertyNames()
                    .stream()
                    .filter(sort::equalsIgnoreCase)
                    .count();
            if (count == 0) {
                throw new IllegalArgumentException(ILLEGAL_SEARCH_FIELD_MSG + sort);
            }
        }
    }

    @Override
    public Long countResultsFindAllOrderBy(String query) {
        if (StringUtils.hasText(query)) {
            return jdbcTemplate.queryForObject(SEARCH_COUNT, Long.class, getRegex(query), getRegex(query));
        } else {
            return jdbcTemplate.queryForObject(ALL_PHONES_COUNT, Long.class);
        }
    }

    private List<Phone> getPhones(int offset, int limit, String order, String sort, String query) {
        if (StringUtils.hasText(query)) {
            return searchByQuery(offset, limit, order, sort, query);
        } else {
            return findAllOrderBySelectedColumnOrById(offset, limit, order, sort);
        }
    }

    private List<Phone> findAllOrderBySelectedColumnOrById(int offset, int limit, String order, String sort) {
        if (!StringUtils.hasText(order) || !StringUtils.hasText(sort)) {
            return findAllOrderBy(offset, limit, "", COLUMN_ID);
        } else {
            return findAllOrderBy(offset, limit, order, sort);
        }
    }

    private List<Phone> searchByQuery(int offset, int limit, String order, String sort, String query) {
        if (!StringUtils.hasText(order) || !StringUtils.hasText(sort)) {
            return rankSearch(offset, limit, RANK_SEARCH, query);
        } else {
            return rankSearch(offset, limit, order, sort, query);
        }
    }

    private List<Phone> findAllOrderBy(int offset, int limit, String order, String sort) {
        List<Phone> phones = jdbcTemplate.query(String.format(FIND_ORDER_BY, sort, order),
                new BeanPropertyRowMapper<>(Phone.class), limit, offset);
        phones.forEach(phone -> Optional.ofNullable(phone).ifPresent(this::setColors));
        return phones;
    }

    private List<Phone> rankSearch(int offset, int limit, String order, String sort, String query) {
        return rankSearch(offset, limit, String.format(RANK_SEARCH_WITH_FIELD, sort, order), query);
    }

    private List<Phone> rankSearch(int offset, int limit, String sqlSearch, String query) {
        String regex = getRegex(query);
        List<Phone> phones = jdbcTemplate.query(sqlSearch,
                new BeanPropertyRowMapper<>(Phone.class), regex, regex, regex, regex, limit, offset);
        phones.forEach(phone -> Optional.ofNullable(phone).ifPresent(this::setColors));
        return phones;
    }

    private String getRegex(String request) {
        return request.toLowerCase().trim().replace(WHITESPACE, '|');
    }

    public void decreaseStock(Long key, Long count) {
        validateCount(count);
        getStock(key).ifPresent(next -> jdbcTemplate.update(UPDATE_STOCK_WITH_ID, next.getStock() - count, next.getPhone().getId()));
    }

    private void validateCount(Long count) {
        if (count == null) {
            throw new IllegalArgumentException(COUNT_IS_NULL_MSG);
        }
    }

    public void increaseStock(Long key, Long count) {
        decreaseStock(key, -count);
    }


    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
}
