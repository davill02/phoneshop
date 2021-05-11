package com.es.phoneshop.web.controller.pages;

import com.es.core.model.order.Order;
import com.es.core.order.OrderService;
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

import static com.es.phoneshop.web.controller.pages.ControllersConstants.ORDER_ATTR;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringRunner.class)
@WebAppConfiguration
@ContextConfiguration("/overview-test.xml")
public class OrderOverviewPageControllerTest {
    @Resource
    private OrderService orderService;
    @Resource
    private WebApplicationContext webApplicationContext;


    private MockMvc mockMvc;

    @Before
    public void setup() {
        mockMvc = webAppContextSetup(this.webApplicationContext).build();
    }

    @Test
    public void shouldFoundOverview() throws Exception {
        when(orderService.getOrderByUuid(any())).thenReturn(Optional.of(new Order()));

        mockMvc.perform(get("/orderOverview/uuid"))
                .andExpect(view().name("orderOverview"))
                .andExpect(model().attributeExists(ORDER_ATTR));
    }

    @Test
    public void shouldNotFoundOverview() throws Exception {
        when(orderService.getOrderByUuid(any())).thenReturn(Optional.empty());

        mockMvc.perform(get("/orderOverview/uuid"))
                .andExpect(view().name("orderNotFound"));
    }


}