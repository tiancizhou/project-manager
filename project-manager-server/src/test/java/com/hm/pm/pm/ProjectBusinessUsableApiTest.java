package com.hm.pm.pm;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ProjectBusinessUsableApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void project_can_be_listed_archived_and_restored() throws Exception {
        String token = loginAndGetToken();

        String created = mockMvc.perform(post("/api/pm/projects")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "业务可用改造项目",
                                  "owner": "alice",
                                  "startDate": "2026-03-01",
                                  "endDate": "2026-03-31"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("业务可用改造项目"))
                .andExpect(jsonPath("$.owner").value("alice"))
                .andExpect(jsonPath("$.status").value("ONGOING"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long projectId = objectMapper.readTree(created).get("id").asLong();

        mockMvc.perform(get("/api/pm/projects")
                        .header("Authorization", "Bearer " + token)
                        .param("status", "ONGOING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.id == %s)].status", projectId).value(hasItem("ONGOING")));

        mockMvc.perform(put("/api/pm/projects/{id}/archive", projectId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ARCHIVED"));

        mockMvc.perform(get("/api/pm/projects")
                        .header("Authorization", "Bearer " + token)
                        .param("status", "ARCHIVED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.id == %s)].status", projectId).value(hasItem("ARCHIVED")));

        mockMvc.perform(put("/api/pm/projects/{id}/restore", projectId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ONGOING"));

        mockMvc.perform(get("/api/pm/projects")
                        .header("Authorization", "Bearer " + token)
                        .param("status", "ONGOING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.id == %s)].status", projectId).value(hasItem("ONGOING")));
    }

    private String loginAndGetToken() throws Exception {
        String body = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"admin\",\"password\":\"admin123\"}"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode node = objectMapper.readTree(body);
        return node.get("token").asText();
    }
}
