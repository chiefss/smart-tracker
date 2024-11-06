package org.devel.smarttracker.integration.controllers.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.devel.smarttracker.application.dto.AdviceRestControllerDto;
import org.devel.smarttracker.integration.AbstractIntegrationMvcTest;
import org.devel.smarttracker.application.dto.ItemDetailDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WithMockUser(username = "user")
class TestItemDetailApiController extends AbstractIntegrationMvcTest {

    @Test
    void testGetDetailAllByItemId() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/api/item-detail/{id}", 1))
                .andExpect(status().isOk())
                .andReturn();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        List<ItemDetailDto> itemDetailDtos = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {});

        Assertions.assertFalse(itemDetailDtos.isEmpty());
    }

    @Test
    void testGetDetailAllByItemId_NotFound() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/api/item-detail/{itemId}", 999999))
                .andExpect(status().isNotFound())
                .andReturn();

        ObjectMapper objectMapper = new ObjectMapper();
        AdviceRestControllerDto adviceRestControllerDto = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {});

        Assertions.assertNotNull(adviceRestControllerDto.getMessage());
    }
}
