package com.es.phoneshop.web.controller.ajax;

import com.es.core.cart.Cart;
import com.es.core.cart.CartService;
import com.es.core.cart.exception.IllegalPhoneException;
import com.es.phoneshop.web.controller.ajax.convertors.CartToCartDTO;
import com.es.phoneshop.web.controller.ajax.entities.CartDTO;
import com.es.phoneshop.web.controller.ajax.entities.ExceptionMessageDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import javax.annotation.Resource;
import java.math.BigDecimal;

import static com.es.phoneshop.web.controller.pages.ControllersConstants.CART_ATTR;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringRunner.class)
@WebAppConfiguration
@ContextConfiguration("/ajax-test.xml")
public class AjaxCartControllerTest {
    private static final String AJAX_CART_PATH = "/ajaxCart";
    private static final String VALID_FORM = "phoneId=1000&quantity=23";
    private static final String INVALID_QUANTITY = "phoneId=2311&quantity=wwks";
    @Resource
    private CartService cartService;
    @Resource
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;
    private MockHttpSession session;

    @Before
    public void setup() {
        Cart cart = new Cart();
        cart.setQuantity(100L);
        mockMvc = webAppContextSetup(this.webApplicationContext).build();
        session = new MockHttpSession();
        session.setAttribute(CART_ATTR, cart);
        cart.setTotalPrice(BigDecimal.TEN);
    }

    @Test
    public void shouldReturnOk() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        String jsonString = mapper.writeValueAsString(new CartDTO(AjaxMessageCode.SUCCESS.code, CartToCartDTO.ADDED_TO_CART_MSG, BigDecimal.TEN, 100L));

        mockMvc.perform(post(AJAX_CART_PATH)
                .content(VALID_FORM)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .session(session))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(content().string(jsonString));
    }

    @Test
    public void shouldReturnOkWithExceptionMessage() throws Exception {
        doThrow(new IllegalPhoneException()).when(cartService).addPhone(anyLong(), anyLong(), any());
        ObjectMapper mapper = new ObjectMapper();
        String jsonString = mapper.writeValueAsString(new ExceptionMessageDTO(AjaxMessageCode.ERROR.code, IllegalPhoneException.EXCEPTION_ILLEGAL_PHONE));

        standaloneSetup(new AjaxCartController(cartService)).build()
                .perform(post(AJAX_CART_PATH)
                        .content(VALID_FORM)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(content().string(jsonString));
    }

    @Test
    public void shouldReturnOkWithNotValidData() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        String jsonString = mapper.writeValueAsString(new ExceptionMessageDTO(AjaxMessageCode.ERROR.code, "Quantity can't be string\n"));

        mockMvc.perform(post(AJAX_CART_PATH)
                .content(INVALID_QUANTITY)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(content().string(jsonString));
    }
}