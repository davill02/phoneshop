package com.es.phoneshop.web.controller.pages;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import javax.annotation.Resource;

import static com.es.phoneshop.web.controller.pages.ControllersConstants.COUNT_PHONES_ATTR;
import static com.es.phoneshop.web.controller.pages.ControllersConstants.PHONES_ATTR;
import static com.es.phoneshop.web.controller.pages.ControllersConstants.PRODUCT_LIST_PAGE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringRunner.class)
@WebAppConfiguration
@ContextConfiguration("/product-list-page.xml")
public class ProductListPageControllerTest {
    @Resource
    private WebApplicationContext webApplicationContext;
    private MockMvc mockMvc;

    @Before
    public void setup() {
        mockMvc = webAppContextSetup(this.webApplicationContext).build();
    }

    @Test
    public void shouldGetFirstPage() throws Exception {
        mockMvc.perform(get("/productList"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists(PHONES_ATTR, COUNT_PHONES_ATTR))
                .andExpect(view().name(PRODUCT_LIST_PAGE));
    }

}