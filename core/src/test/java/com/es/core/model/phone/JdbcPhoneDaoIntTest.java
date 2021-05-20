package com.es.core.model.phone;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.jdbc.JdbcTestUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@ContextConfiguration("/test-jdbc-phone-dao-conf.xml")
public class JdbcPhoneDaoIntTest {
    private static final int COUNT_PHONES_IN_TABLE = 11;
    private static final long FIRST_PHONE_ID = 1000L;
    private static final int COUNT_OF_COLORS_FIRST_PHONE = 3;
    private static final String BRAND_SAMSUNG = "Samsung";
    private static final String MODEL_GALAXY_2020 = "GALAXY 2020";
    private static final Integer PIXEL_DENSITY = 17;
    private static final String ARCHOS_101_TITANIUM_MODEL = "ARCHOS 101 Titanium";
    private static final String ARCHOS_28_INTERNET_TABLET = "ARCHOS 28 Internet Tablet";
    private static final int WITHOUT_STOCK = 2;
    private static final String ORDER_BY_MODEL_NOT_ZERO_STOCK = "SELECT * FROM PHONES JOIN STOCKS S on PHONES.ID = S.PHONEID WHERE STOCK > 0 ORDER BY MODEL  LIMIT ? OFFSET ?";
    private static final String ORDER_BY_PRICE_DESC_NOT_ZERO_STOCK = "SELECT price FROM PHONES JOIN STOCKS S on PHONES.ID = S.PHONEID WHERE STOCK > 0 ORDER BY PRICE desc LIMIT ? OFFSET ?";
    private static final String LIKE_ARCHOS_101_ORDER_BY_PRICE_DESC_NOT_ZERO_STOCK = "SELECT PRICE FROM PHONES JOIN STOCKS S on PHONES.ID = S.PHONEID WHERE MODEL LIKE '%ARCHOS 101%' AND STOCK > 0 ORDER BY PRICE DESC  LIMIT ? OFFSET ?";
    private static final String ILLEGAL_FIELD = "IllegalField";
    private static final String ASC = "ASC";
    private static final String SEARCH_FIELD_MODEL = "model";
    private static final String SEARCH_FIELD_PRICE = "price";
    private static final String DESC = "DESC";
    private static final String ILLEGAL_SEARCH_FIELD_COLORS = "colors";
    private static final String ILLEGAL_ORDER = "sdf";
    private static final String COLUMN_COLOR_ID = "colorId";
    private static final String INSERT_INTO_PHONES_BRAND_MODEL_VALUES = "INSERT INTO phones (brand,model) values ( ?, ? )";
    private static final String SELECT_COLORS = "SELECT * FROM colors";
    private static final String SELECT_PHONES_WITH_BRAND_AND_MODEL = "SELECT * FROM phones WHERE brand = ? and model = ?";
    private static final String SELECT_COLORS_BY_PHONE_ID = "SELECT * FROM phone2color WHERE phoneId = ?";
    private static final String TABLE_NAME_PHONES = "phones";
    private static final String COUNT_PHONES_WITH_101_AND_NOT_ZERO_STOCK = "SELECT COUNT(*) FROM PHONES JOIN STOCKS S on PHONES.ID = S.PHONEID WHERE model like '%101%' AND STOCK > 0";
    private static final Integer STOCK = 20;
    private static final String TABLE_NAME_STOCKS = "stocks";
    private static final long PHONE_WITH_19_STOCK = 1009L;

    @Resource
    private JdbcPhoneDao phoneDao;
    @Resource
    private JdbcTemplate jdbcTemplate;

    @After
    public void tearDown() {
        JdbcTestUtils.deleteFromTableWhere(jdbcTemplate, TABLE_NAME_PHONES, "model = ? and brand = ?",
                MODEL_GALAXY_2020, BRAND_SAMSUNG);
        JdbcTestUtils.deleteFromTableWhere(jdbcTemplate, TABLE_NAME_STOCKS, "phoneId = ?", FIRST_PHONE_ID);
        jdbcTemplate.update("UPDATE STOCKS SET STOCK = 19 WHERE PHONEID = ?", PHONE_WITH_19_STOCK);
    }

    @Test
    public void shouldGetNull() {
        Optional<Phone> phone = phoneDao.get(null);

        assertFalse(phone.isPresent());
    }

    @Test
    public void shouldGetFirstPhone() {
        Optional<Phone> phone = phoneDao.get(FIRST_PHONE_ID);

        assertTrue(phone.isPresent());
    }

    @Test
    public void shouldGetNonExistPhone() {
        Optional<Phone> phone = phoneDao.get(12123L);

        assertFalse(phone.isPresent());
    }

    @Test
    public void shouldGetAllPhones() {
        List<Phone> phones = phoneDao.findAll(0, COUNT_PHONES_IN_TABLE);
        int resultSize = phones.size();

        assertEquals(COUNT_PHONES_IN_TABLE, resultSize);
    }

    @Test
    public void shouldGetAllPhonesWithOffsetMoreThanCount() {
        List<Phone> phones = phoneDao.findAll(COUNT_PHONES_IN_TABLE, COUNT_PHONES_IN_TABLE);

        assertTrue(phones.isEmpty());
    }

    @Test
    public void shouldGetFirstPhoneWithAllColors() {
        Optional<Phone> phone = phoneDao.get(FIRST_PHONE_ID);
        int result = phone.orElse(new Phone()).getColors().size();

        assertEquals(COUNT_OF_COLORS_FIRST_PHONE, result);
    }

    @Test
    public void shouldSavePhone() {
        phoneDao.save(getValidPhone());
        Phone phone = getTestPhone(BRAND_SAMSUNG, MODEL_GALAXY_2020);

        assertNotNull(phone.getId());
        assertEquals(BRAND_SAMSUNG, phone.getBrand());
        assertEquals(MODEL_GALAXY_2020, phone.getModel());
    }

    @Test
    public void shouldUpdatePhone() {
        Phone phone = getValidPhone();
        jdbcTemplate.update(INSERT_INTO_PHONES_BRAND_MODEL_VALUES, phone.getBrand(), phone.getModel());
        phone.setPixelDensity(PIXEL_DENSITY);

        phoneDao.save(phone);
        Phone result = getTestPhone(BRAND_SAMSUNG, MODEL_GALAXY_2020);

        assertEquals(PIXEL_DENSITY, result.getPixelDensity());
    }

    private Phone getValidPhone() {
        Phone phone = new Phone();
        phone.setBrand(BRAND_SAMSUNG);
        phone.setModel(MODEL_GALAXY_2020);
        return phone;
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldSaveIllegalPhoneAndThrowException() {
        Phone phone = getValidPhone();
        phone.setModel(null);

        phoneDao.save(phone);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldSaveNullAndThrowException() {
        Phone phone = getValidPhone();
        phone.setModel(null);

        phoneDao.save(phone);
    }

    @Test
    public void shouldSavePhoneWithColors() {
        int excepted = getColors().size();

        phoneDao.save(getValidPhoneWithAllColors());
        Phone phone = getTestPhone(BRAND_SAMSUNG, MODEL_GALAXY_2020);
        List<Long> colorIds = jdbcTemplate.query(SELECT_COLORS_BY_PHONE_ID,
                (rs, rowNum) -> rs.getLong(COLUMN_COLOR_ID), phone.getId());

        assertEquals(excepted, colorIds.size());
    }

    private Phone getTestPhone(String brand, String model) {
        return jdbcTemplate.queryForObject(SELECT_PHONES_WITH_BRAND_AND_MODEL, new BeanPropertyRowMapper<>(Phone.class), brand, model);
    }

    private Phone getValidPhoneWithAllColors() {
        Phone phone = getValidPhone();
        List<Color> colors = getColors();
        Set<Color> colorSet = new HashSet<>(colors);
        phone.setColors(colorSet);
        return phone;
    }

    private List<Color> getColors() {
        return jdbcTemplate.query(SELECT_COLORS, new BeanPropertyRowMapper<>(Color.class));
    }

    @Test
    public void shouldFindAllPhones() {
        List<Phone> phones = phoneDao.findAllOrderBy(0, COUNT_PHONES_IN_TABLE, null, null, null);
        int resultSize = phones.size();

        assertEquals(COUNT_PHONES_IN_TABLE - WITHOUT_STOCK, resultSize);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFindOrderByIllegalFieldAndThrowException() {
        phoneDao.findAllOrderBy(0, COUNT_PHONES_IN_TABLE, null, ILLEGAL_FIELD, null);
    }

    @Test
    public void shouldFindModelPhone() {
        List<Phone> phones = phoneDao.findAllOrderBy(0, COUNT_PHONES_IN_TABLE, null, null, ARCHOS_101_TITANIUM_MODEL);
        Phone phone = phones.get(0);

        assertEquals(ARCHOS_101_TITANIUM_MODEL, phone.getModel());
    }

    @Test
    public void shouldFindAllPhonesSortedByModelAsc() {
        List<Phone> phones = phoneDao.findAllOrderBy(0, COUNT_PHONES_IN_TABLE, ASC, SEARCH_FIELD_MODEL, null);
        List<Phone> result = jdbcTemplate.query(ORDER_BY_MODEL_NOT_ZERO_STOCK,
                new BeanPropertyRowMapper<>(Phone.class), COUNT_PHONES_IN_TABLE, 0);

        assertEquals(result.size(), phones.size());
        assertListPhonesEqualsByBrandAndModel(phones, result);
    }

    private void assertListPhonesEqualsByBrandAndModel(List<Phone> phones, List<Phone> result) {
        for (int i = 0; i < result.size(); i++) {
            assertEquals(result.get(i).getModel(), phones.get(i).getModel());
            assertEquals(result.get(i).getBrand(), phones.get(i).getBrand());
        }
    }

    @Test
    public void shouldFindAllPhonesSortedByPriceDesc() {
        List<BigDecimal> phonesPrice = phoneDao.findAllOrderBy(0, COUNT_PHONES_IN_TABLE, DESC, SEARCH_FIELD_PRICE, null).stream()
                .map(Phone::getPrice)
                .collect(Collectors.toList());
        List<BigDecimal> resultPrice = jdbcTemplate.queryForList(ORDER_BY_PRICE_DESC_NOT_ZERO_STOCK,
                BigDecimal.class, COUNT_PHONES_IN_TABLE, 0);

        assertEquals(resultPrice.size(), phonesPrice.size());
        assertEquals(resultPrice, phonesPrice);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFindAllPhonesSortedByIllegalFieldDescAndThrowException() {
        phoneDao.findAllOrderBy(0, COUNT_PHONES_IN_TABLE, DESC, ILLEGAL_SEARCH_FIELD_COLORS, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFindAllPhonesSortedByLegalFieldAndIllegalOrderAndThrowException() {
        phoneDao.findAllOrderBy(0, COUNT_PHONES_IN_TABLE, ILLEGAL_ORDER, SEARCH_FIELD_PRICE, null);
    }

    @Test
    public void shouldSaveNewPhoneAndFindNothingBySearch() {
        Phone phone = getValidPhone();

        phoneDao.save(phone);
        List<Phone> phones = phoneDao.findAllOrderBy(0, COUNT_PHONES_IN_TABLE + 1, null, null, phone.getModel());

        assertEquals(0, phones.size());
    }

    @Test
    public void shouldSearchAndOrderByPriceDesc() {
        List<Phone> phones = phoneDao.findAllOrderBy(0, COUNT_PHONES_IN_TABLE, "DESC", "price", ARCHOS_28_INTERNET_TABLET);
        List<BigDecimal> result = jdbcTemplate.queryForList(LIKE_ARCHOS_101_ORDER_BY_PRICE_DESC_NOT_ZERO_STOCK,
                BigDecimal.class, COUNT_PHONES_IN_TABLE, 0);
        String model = phones.get(0).getModel();
        phones.remove(0);
        List<BigDecimal> phonePrice = phones.stream()
                .map(Phone::getPrice)
                .collect(Collectors.toList());

        assertEquals(ARCHOS_28_INTERNET_TABLET, model);
        assertEquals(result, phonePrice);
    }

    @Test
    public void shouldGetCountAllPhones() {
        Long realCount = (long) COUNT_PHONES_IN_TABLE - WITHOUT_STOCK;

        Long result = phoneDao.countResultsFindAllOrderBy(null);

        assertEquals(realCount, result);
    }

    @Test
    public void shouldGetCountNonExistPhone() {
        Long zero = (long) 0;

        Long result = phoneDao.countResultsFindAllOrderBy(BRAND_SAMSUNG);

        assertEquals(zero, result);
    }

    @Test
    public void shouldGetCountPhonesWith101() {
        Long realCount = jdbcTemplate.queryForObject(COUNT_PHONES_WITH_101_AND_NOT_ZERO_STOCK, Long.class);

        Long result = phoneDao.countResultsFindAllOrderBy("101");

        assertEquals(realCount, result);
    }


    @Test
    public void shouldGetStockNonExistPhone() {
        Optional<Stock> stock = phoneDao.getStock(null);

        assertFalse(stock.isPresent());
    }

    @Test
    public void shouldGetStockExistPhoneWithoutStockRowInData() {
        Optional<Stock> stock = phoneDao.getStock(FIRST_PHONE_ID);

        assertFalse(stock.isPresent());
    }

    @Test
    public void shouldGetStockExistPhone() {
        jdbcTemplate.update("INSERT INTO STOCKS (PHONEID, STOCK, RESERVED) VALUES ( ?, ?, ? )", FIRST_PHONE_ID, STOCK, 0);

        Optional<Stock> stock = phoneDao.getStock(FIRST_PHONE_ID);

        assertTrue(stock.isPresent());
        assertEquals(STOCK, stock.get().getStock());
        assertNotNull(stock.get().getPhone());
        assertNotNull(stock.get().getPhone().getModel());
    }

    @Test
    public void shouldDecreaseAllStock() {
        phoneDao.decreaseStock(PHONE_WITH_19_STOCK, 19L);
        Long result = jdbcTemplate.queryForObject("SELECT stock FROM stocks WHERE phoneId =?", Long.class, PHONE_WITH_19_STOCK);

        assertEquals((Long) 0L, result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldDecreaseStockAndThrowIllegalArgumentExceptionByNull() {
        phoneDao.decreaseStock(PHONE_WITH_19_STOCK, null);
    }
    @Test(expected = IllegalArgumentException.class)
    public void shouldIncreaseStockAndThrowIllegalArgumentExceptionByNull() {
        phoneDao.decreaseStock(PHONE_WITH_19_STOCK, null);
    }
}