package com.es.phoneshop.web.controller.ajax;

import com.es.core.cart.Cart;
import com.es.core.cart.CartService;
import com.es.core.cart.exception.IllegalPhoneException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import javax.annotation.Resource;
import java.math.BigDecimal;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
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

    @Before
    public void setup() {
        Cart cart = new Cart();
        cart.setQuantity(100L);
        mockMvc = webAppContextSetup(this.webApplicationContext).build();
        cart.setTotalPrice(BigDecimal.TEN);
        when(cartService.getCart(any())).thenReturn(cart);
    }

    @Test
    public void shouldReturnOk() throws Exception {
        JSONObject jsonObject = new JSONObject(new AjaxMessage(AjaxMessageCode.SUCCESS.code, AjaxCartController.ADDED_TO_CART, BigDecimal.TEN, 100L));

        standaloneSetup(new AjaxCartController(cartService)).build()
                .perform(post(AJAX_CART_PATH)
                        .content(VALID_FORM)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(content().string(jsonObject.toString()));
    }

    @Test
    public void shouldReturnOkWithExceptionMessage() throws Exception {
        doThrow(new IllegalPhoneException()).when(cartService).addPhone(anyLong(), anyLong(), any());
        JSONObject jsonObject = new JSONObject(new AjaxMessage(AjaxMessageCode.ERROR.code, IllegalPhoneException.EXCEPTION_ILLEGAL_PHONE));

        standaloneSetup(new AjaxCartController(cartService)).build()
                .perform(post(AJAX_CART_PATH)
                        .content(VALID_FORM)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(content().string(jsonObject.toString()));
    }

    @Test
    public void shouldReturnOkWithNotValidData() throws Exception {
        JSONObject jsonObject = new JSONObject(new AjaxMessage(AjaxMessageCode.ERROR.code, "Quantity can't be string\n"));

        mockMvc.perform(post(AJAX_CART_PATH)
                .content(INVALID_QUANTITY)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(content().string(jsonObject.toString()));
    }
}