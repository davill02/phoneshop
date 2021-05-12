package com.es.phoneshop.web.controller.pages;

import com.es.core.cart.Cart;
import com.es.core.model.phone.Phone;
import com.es.core.model.phone.PhoneDao;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import javax.annotation.Resource;
import java.util.Optional;

import static com.es.phoneshop.web.controller.pages.ControllersConstants.CART_ATTR;
import static com.es.phoneshop.web.controller.pages.ControllersConstants.PHONE_DETAILS_ATTR;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringRunner.class)
@WebAppConfiguration
@ContextConfiguration("/pdp-test.xml")
public class ProductDetailsPageControllerTest {
    @Resource
    private WebApplicationContext webApplicationContext;
    @Resource
    private PhoneDao mockPhoneDao;

    private MockMvc mockMvc;
    private MockHttpSession session;

    @Before
    public void setup() {
        mockMvc = webAppContextSetup(this.webApplicationContext).build();
        session = new MockHttpSession();
        Cart cart = new Cart();
        session.setAttribute(CART_ATTR, cart);
    }

    @Test
    public void shouldGetPhone() throws Exception {
        when(mockPhoneDao.get(anyLong())).thenReturn(Optional.of(new Phone()));

        mockMvc.perform(get("/productDetails/2"))
                .andExpect(status().isOk())
                .andExpect(view().name("phoneDetails"))
                .andExpect(model().attributeExists(PHONE_DETAILS_ATTR));
    }
    @Test
    public void shouldNotFindPhone() throws Exception {
        when(mockPhoneDao.get(anyLong())).thenReturn(Optional.empty());

        mockMvc.perform(get("/productDetails/2"))
                .andExpect(status().isOk())
                .andExpect(view().name("phoneNotFound"));
    }
    @Test
    public void shouldNotFindPhoneByStringId() throws Exception {
        when(mockPhoneDao.get(anyLong())).thenReturn(Optional.empty());

        mockMvc.perform(get("/productDetails/sdghfdg"))
                .andExpect(status().isNotFound())
                .andExpect(view().name("phoneNotFound"));
    }

}