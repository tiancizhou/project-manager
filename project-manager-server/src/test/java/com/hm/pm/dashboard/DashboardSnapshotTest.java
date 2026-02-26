package com.hm.pm.dashboard;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class DashboardSnapshotTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void daily_snapshot_aggregates_progress_and_workload() throws Exception {
        String token = loginAndGetToken();

        String projectResp = mockMvc.perform(post("/api/pm/projects")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"项目管理系统\"}"))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        Long projectId = objectMapper.readTree(projectResp).get("id").asLong();

        String taskResp = mockMvc.perform(post("/api/pm/tasks")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"projectId\":" + projectId + ",\"title\":\"实现看板\"}"))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        Long taskId = objectMapper.readTree(taskResp).get("id").asLong();

        mockMvc.perform(post("/api/pm/worklogs")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"taskId\":" + taskId + ",\"hours\":3.0}"))
                .andExpect(status().isCreated());

        mockMvc.perform(put("/api/pm/tasks/{id}/done", taskId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        LocalDate snapshotDate = LocalDate.now();

        mockMvc.perform(post("/api/dashboard/snapshots/build")
                        .header("Authorization", "Bearer " + token)
                        .param("date", snapshotDate.toString()))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/dashboard/summary")
                        .header("Authorization", "Bearer " + token)
                        .param("date", snapshotDate.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.completedTaskCount").value(1))
                .andExpect(jsonPath("$.effectiveHours").value(3.0));
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
