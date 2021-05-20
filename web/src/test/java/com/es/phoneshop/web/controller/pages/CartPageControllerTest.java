package com.es.phoneshop.web.controller.pages;

import com.es.core.cart.CartService;
import com.es.phoneshop.web.controller.pages.entites.UpdateForm;
import com.es.phoneshop.web.controller.pages.validators.UpdateFormValidator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.context.WebApplicationContext;

import javax.annotation.Resource;

import static com.es.phoneshop.web.controller.pages.ControllersConstants.CART_ATTR;
import static com.es.phoneshop.web.controller.pages.ControllersConstants.UPDATE_FORM_ATTR;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringRunner.class)
@WebAppConfiguration
@ContextConfiguration("/cart-test.xml")
public class CartPageControllerTest {
    private static final long ANY_LONG = 12L;
    private static final String URL_CART = "/cart";
    @Resource
    private CartService mockCartService;
    @Resource
    private UpdateFormValidator validator;
    @Resource
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        when(validator.supports(UpdateForm.class)).thenReturn(true);
        mockMvc = webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void shouldRemoveById() throws Exception {
        mockMvc.perform(delete(URL_CART)
                .param("id", Long.toString(ANY_LONG)))
                .andExpect(status().is2xxSuccessful());
        verify(mockCartService).remove(eq(ANY_LONG), any());
    }

    @Test
    public void shouldHaveUpdateFormAttr() throws Exception {
        mockMvc.perform(get(URL_CART))
                .andExpect(MockMvcResultMatchers.model().attributeExists(UPDATE_FORM_ATTR))
                .andExpect(status().isOk())
                .andExpect(view().name(ControllersConstants.CART_PAGE));
    }

}