package org.devel.smarttracker.controllers.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.devel.smarttracker.AbstractIntegrationMvcTest;
import org.devel.smarttracker.application.dto.ItemDetailDto;
import org.devel.smarttracker.application.utils.Defines;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WithMockUser(username = "user")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TestIndexApiController extends AbstractIntegrationMvcTest {

    @Test
    void testGetDetailAllByItemId() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get(Defines.API_PREFIX + "detail/{id}", 1))
                .andExpect(status().isOk())
                .andReturn();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        List<ItemDetailDto> itemDetailDtos = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {});

        Assertions.assertFalse(itemDetailDtos.isEmpty());
    }
}
