package cn.edu.shu.pourrfot.server.controller;

import cn.edu.shu.pourrfot.server.config.CustomMySQLContainer;
import cn.edu.shu.pourrfot.server.enums.RoleEnum;
import cn.edu.shu.pourrfot.server.enums.SexEnum;
import cn.edu.shu.pourrfot.server.model.PourrfotUser;
import cn.edu.shu.pourrfot.server.model.StudentTransaction;
import cn.edu.shu.pourrfot.server.service.PourrfotUserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.Matchers;
import org.junit.ClassRule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MySQLContainer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
@AutoConfigureMockMvc(addFilters = false)
public class StudentTransactionControllerTest {
  @ClassRule
  public static MySQLContainer<CustomMySQLContainer> customMySQLContainer = CustomMySQLContainer.getInstance();
  final private PourrfotUser sender = PourrfotUser.builder()
    .username("123456")
    .nickname("mock-user1")
    .sex(SexEnum.male)
    .role(RoleEnum.student)
    .password("123456")
    .build();
  final private PourrfotUser receiver = PourrfotUser.builder()
    .username("456789")
    .nickname("mock-user2")
    .sex(SexEnum.male)
    .role(RoleEnum.student)
    .password("456789")
    .build();
  @Autowired
  private PourrfotUserService pourrfotUserService;
  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private ObjectMapper objectMapper;

  @BeforeEach
  void prepare() {
    assertTrue(pourrfotUserService.save(sender));
    assertTrue(pourrfotUserService.save(receiver));
  }

  @Transactional
  @Test
  void integrationTest() throws Exception {
    final List<StudentTransaction> toCreates = List.of(StudentTransaction.builder()
        .sender(sender.getId())
        .receiver(receiver.getId())
        .title("mock title1")
        .content("mock content1")
        .urgent(false)
        .build(),
      StudentTransaction.builder()
        .sender(receiver.getId())
        .receiver(sender.getId())
        .title("中文测试")
        .content("content mock2")
        .urgent(true)
        .build()
    );
    final List<String> locations = new ArrayList<>(toCreates.size());
    // POST create
    for (StudentTransaction toCreate : toCreates) {
      mockMvc.perform(post("/student-transactions")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(toCreate))
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isCreated())
        .andExpect(header().exists(HttpHeaders.LOCATION))
        .andDo(result -> locations.add(Objects.requireNonNull(result.getResponse()
          .getHeader(HttpHeaders.LOCATION))
          .replace("/api", "")));
    }
    // GET detail
    for (String location : locations) {
      mockMvc.perform(get(location))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.createTime").exists())
        .andExpect(jsonPath("$.data.updateTime").exists())
        .andExpect(jsonPath("$.data.id").exists())
        .andDo(result -> log.info("Detail success: {}", result.getResponse().getContentAsString()));
    }
    // GET detail not found
    mockMvc.perform(get("/student-transactions/999"))
      .andExpect(status().isNotFound());
    // page
    mockMvc.perform(get("/student-transactions")
      .contentType(MediaType.APPLICATION_JSON)
      .accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.data.records").exists())
      .andExpect(jsonPath("$.data.records").isArray())
      .andExpect(jsonPath("$.data.records", Matchers.hasSize(toCreates.size())))
      .andDo(result -> log.info("Page success: {}", result.getResponse().getContentAsString()));
    mockMvc.perform(get("/student-transactions")
      .param("isUrgent", String.valueOf(false))
      .contentType(MediaType.APPLICATION_JSON)
      .accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.data.records").exists())
      .andExpect(jsonPath("$.data.records").isArray())
      .andExpect(jsonPath("$.data.records", Matchers.hasSize(1)))
      .andDo(result -> log.info("Page success: {}", result.getResponse().getContentAsString()));
    mockMvc.perform(get("/student-transactions")
      .param("sender", String.valueOf(sender.getId()))
      .contentType(MediaType.APPLICATION_JSON)
      .accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.data.records").exists())
      .andExpect(jsonPath("$.data.records").isArray())
      .andExpect(jsonPath("$.data.records", Matchers.hasSize(1)))
      .andDo(result -> log.info("Page success: {}", result.getResponse().getContentAsString()));
    mockMvc.perform(get("/student-transactions")
      .param("receiver", String.valueOf(receiver.getId()))
      .contentType(MediaType.APPLICATION_JSON)
      .accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.data.records").exists())
      .andExpect(jsonPath("$.data.records").isArray())
      .andExpect(jsonPath("$.data.records", Matchers.hasSize(1)))
      .andDo(result -> log.info("Page success: {}", result.getResponse().getContentAsString()));
    mockMvc.perform(get("/student-transactions")
      .param("title", "测试")
      .contentType(MediaType.APPLICATION_JSON)
      .accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.data.records").exists())
      .andExpect(jsonPath("$.data.records").isArray())
      .andExpect(jsonPath("$.data.records", Matchers.hasSize(1)))
      .andDo(result -> log.info("Page success: {}", result.getResponse().getContentAsString()));
    // PUT update
    mockMvc.perform(put(locations.get(0))
      .contentType(MediaType.APPLICATION_JSON)
      .content(objectMapper.writeValueAsString(toCreates.get(0).setTitle("TEST")))
      .accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.data.createTime").exists())
      .andExpect(jsonPath("$.data.updateTime").exists())
      .andExpect(jsonPath("$.data.title").value("TEST"))
      .andDo(result -> log.info("Update success: {}", result.getResponse().getContentAsString()));
    // DELETE Delete
    for (String location : locations) {
      mockMvc.perform(delete(location))
        .andExpect(status().isNoContent())
        .andDo(result -> log.info("Delete success: {}", result.getResponse().getContentAsString()));
      mockMvc.perform(delete(location))
        .andExpect(status().isNotFound())
        .andDo(result -> log.info("Delete failed because not found: {}", result.getResponse().getContentAsString()));
    }
  }
}
