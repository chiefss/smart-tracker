package org.devel.smarttracker.integration.controllers.api;

import org.devel.smarttracker.integration.AbstractIntegrationMvcTest;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WithMockUser(username = "user")
class TestIndexApiController extends AbstractIntegrationMvcTest {

    @Test
    void testNotFound() throws Exception {
        mockMvc.perform(get("/api/not-found"))
                .andExpect(status().isNotFound());
    }
}
