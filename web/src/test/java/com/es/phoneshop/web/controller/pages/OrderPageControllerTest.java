package com.es.phoneshop.web.controller.pages;

import com.es.core.model.order.Order;
import com.es.core.order.OrderService;
import com.es.core.order.PersonalDataForm;
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

import static com.es.phoneshop.web.controller.pages.ControllersConstants.BINDING_RESULT_PERSONAL_DATA_FORM_ATTR;
import static com.es.phoneshop.web.controller.pages.ControllersConstants.ORDER_ATTR;
import static com.es.phoneshop.web.controller.pages.ControllersConstants.PERSONAL_DATA_FORM_ATTR;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringRunner.class)
@WebAppConfiguration
@ContextConfiguration("/order-test.xml")
public class OrderPageControllerTest {
    @Resource
    private OrderService orderService;
    @Resource
    private WebApplicationContext webApplicationContext;
    private MockMvc mockMvc;

    private MockHttpSession session;

    @Before
    public void setup() {
        mockMvc = webAppContextSetup(this.webApplicationContext).build();
        session = new MockHttpSession();
        session.setAttribute(ORDER_ATTR, new Order());
    }

    @Test
    public void shouldGetOrder() throws Exception {
        mockMvc.perform(get("/order"))
                .andExpect(model().attributeExists(PERSONAL_DATA_FORM_ATTR))
                .andExpect(view().name("order"));
    }

    @Test
    public void shouldPostByErrorsOrder() throws Exception {

        mockMvc.perform(post("/order").session(session)
                .sessionAttr(PERSONAL_DATA_FORM_ATTR, new PersonalDataForm())
                .param("lastname", "")
                .param("firstname", "")
                .param("deliveryAddress", "")
                .param("phoneNumber", ""))
                .andExpect(flash().attributeExists(BINDING_RESULT_PERSONAL_DATA_FORM_ATTR))
                .andExpect(redirectedUrl("order"));
    }
    @Test
    public void shouldPostOrder() throws Exception {

        mockMvc.perform(post("/order").session(session)
                .sessionAttr(PERSONAL_DATA_FORM_ATTR, new PersonalDataForm())
                .param("lastname", ";lkjhg")
                .param("firstname", "l;kjh")
                .param("deliveryAddress", "';lkj")
                .param("phoneNumber", "+1234567890"))
                .andExpect(redirectedUrl("/orderOverview/null"));
    }
}