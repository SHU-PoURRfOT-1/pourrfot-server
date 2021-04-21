package cn.edu.shu.pourrfot.server.controller;

import cn.edu.shu.pourrfot.server.config.CustomMySQLContainer;
import cn.edu.shu.pourrfot.server.enums.RoleEnum;
import cn.edu.shu.pourrfot.server.enums.SexEnum;
import cn.edu.shu.pourrfot.server.model.PourrfotUser;
import cn.edu.shu.pourrfot.server.model.Project;
import cn.edu.shu.pourrfot.server.repository.PourrfotUserMapper;
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
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
@AutoConfigureMockMvc(addFilters = false)
class ProjectControllerTest {
  @ClassRule
  public static MySQLContainer<CustomMySQLContainer> customMySQLContainer = CustomMySQLContainer.getInstance();
  private final PourrfotUser owner = PourrfotUser.builder()
    .username("teacher22")
    .nickname("teacher22")
    .password("password")
    .role(RoleEnum.teacher)
    .sex(SexEnum.male)
    .createTime(new Date())
    .updateTime(new Date())
    .build();
  private final Project[] projects = new Project[]{
    Project.builder()
      .createTime(new Date())
      .updateTime(new Date())
      .projectCode("projectCode1")
      .projectName("projectName1")
      .profilePhoto("profilePhoto1")
      .build(),
    Project.builder()
      .createTime(new Date())
      .updateTime(new Date())
      .projectCode("projectCode2")
      .projectName("projectName2")
      .profilePhoto("profilePhoto2")
      .build()
  };
  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private ObjectMapper objectMapper;
  @Autowired
  private PourrfotUserMapper pourrfotUserMapper;

  @BeforeEach
  void prepare() {
    assertEquals(1, pourrfotUserMapper.insert(owner));
  }


  @Transactional
  @Test
  void integrationTest() throws Exception {
    final List<String> locations = new ArrayList<>(projects.length);
    // POST create
    for (Project project : projects) {
      mockMvc.perform(post("/projects")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(project.setOwnerId(owner.getId())))
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
        .andExpect(jsonPath("$.createTime").exists())
        .andExpect(jsonPath("$.updateTime").exists())
        .andDo(result -> log.info("Detail success: {}", result.getResponse().getContentAsString()));
    }
    // page
    mockMvc.perform(get("/projects")
      .param("projectName", "projectName1")
      .contentType(MediaType.APPLICATION_JSON)
      .accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.records").exists())
      .andExpect(jsonPath("$.records").isArray())
      .andExpect(jsonPath("$.records", Matchers.hasSize(1)))
      .andDo(result -> log.info("Page success: {}", result.getResponse().getContentAsString()));
    mockMvc.perform(get("/projects")
      .param("projectCode", "projectCode1")
      .contentType(MediaType.APPLICATION_JSON)
      .accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.records").exists())
      .andExpect(jsonPath("$.records").isArray())
      .andExpect(jsonPath("$.records", Matchers.hasSize(1)))
      .andDo(result -> log.info("Page success: {}", result.getResponse().getContentAsString()));
    mockMvc.perform(get("/projects")
      .contentType(MediaType.APPLICATION_JSON)
      .accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.records").exists())
      .andExpect(jsonPath("$.records").isArray())
      .andExpect(jsonPath("$.records", Matchers.hasSize(projects.length)))
      .andDo(result -> log.info("Page success: {}", result.getResponse().getContentAsString()));
    mockMvc.perform(get("/projects")
      .param("ownerId", owner.getId().toString())
      .contentType(MediaType.APPLICATION_JSON)
      .accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.records").exists())
      .andExpect(jsonPath("$.records").isArray())
      .andExpect(jsonPath("$.records", Matchers.hasSize(projects.length)))
      .andDo(result -> log.info("Page success: {}", result.getResponse().getContentAsString()));
    // PUT update
    mockMvc.perform(put(locations.get(1))
      .contentType(MediaType.APPLICATION_JSON)
      .content(objectMapper.writeValueAsString(projects[1].setProjectName("UPDATE")))
      .accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.createTime").exists())
      .andExpect(jsonPath("$.updateTime").exists())
      .andExpect(jsonPath("$.projectName").value("UPDATE"))
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