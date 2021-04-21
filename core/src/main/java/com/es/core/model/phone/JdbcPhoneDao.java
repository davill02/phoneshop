package com.es.core.model.phone;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;

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

    private static final String COLUMN_ID = "id";
    private static final String COLUMN_CODE = "code";
    private static final String COLUMN_COLOR_ID = "colorId";
    private static final String TABLE_PHONES = "phones";
    private static final String COLUMN_COLORS = "colors";
    private static final String ILLEGAL_ARGUMENT_MSG = "phone or phone.getBrand() or phone. getModel() is null";
    private static final String DELETE_COLORS_BY_PHONE_ID = "DELETE FROM phone2color WHERE phoneId = ?";

    @Resource
    private JdbcTemplate jdbcTemplate;
    private PhoneMapper phoneMapper;
    private SimpleJdbcInsert insertPhone;
    private final List<String> forbidProperties = new ArrayList<>();

    @PostConstruct
    private void init() {
        insertPhone = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName(TABLE_PHONES)
                .usingGeneratedKeyColumns(COLUMN_ID);
        phoneMapper = new PhoneMapper();
        forbidProperties.add(COLUMN_ID);
        forbidProperties.add(COLUMN_COLORS);
    }

    public Optional<Phone> get(final Long key) {
        if (key == null) {
            return Optional.empty();
        }
        Optional<Phone> phone = Optional.ofNullable(
                jdbcTemplate.queryForObject(SELECT_PHONE_BY_ID, new BeanPropertyRowMapper<>(Phone.class), key));
        phone.ifPresent(this::setColors);
        return phone;
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
        Map<String, Object> parameters = phoneMapper.map(phone, forbidProperties);
        phone.setId(insertPhone.executeAndReturnKey(parameters).longValue());
    }


    public List<Phone> findAll(int offset, int limit) {
        List<Phone> phones = jdbcTemplate.query(SELECT_FROM_PHONES_LIMIT_OFFSET,
                new BeanPropertyRowMapper<>(Phone.class), limit, offset);
        phones.forEach(phone -> Optional.ofNullable(phone).ifPresent(this::setColors));
        return phones;
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
}
