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
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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

    @Resource
    private JdbcPhoneDao phoneDao;
    @Resource
    private JdbcTemplate jdbcTemplate;

    @After
    public void tearDown() {
        JdbcTestUtils.deleteFromTableWhere(jdbcTemplate, "phones", "model = ? and brand = ?",
                MODEL_GALAXY_2020, BRAND_SAMSUNG);
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
        jdbcTemplate.update("INSERT INTO phones (brand,model) values ( ?, ? )", phone.getBrand(), phone.getModel());
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
        List<Long> colorIds = jdbcTemplate.query("SELECT * FROM phone2color WHERE phoneId = ?",
                (rs, rowNum) -> rs.getLong("colorId"), phone.getId());

        assertEquals(excepted, colorIds.size());
    }

    private Phone getTestPhone(String brand, String model) {
        return jdbcTemplate
                .queryForObject("SELECT * FROM phones WHERE brand = ? and model = ?", new BeanPropertyRowMapper<>(Phone.class), brand, model);
    }

    private Phone getValidPhoneWithAllColors() {
        Phone phone = getValidPhone();
        List<Color> colors = getColors();
        Set<Color> colorSet = new HashSet<>(colors);
        phone.setColors(colorSet);
        return phone;
    }

    private List<Color> getColors() {
        return jdbcTemplate.query("SELECT * FROM colors", new BeanPropertyRowMapper<>(Color.class));
    }


}