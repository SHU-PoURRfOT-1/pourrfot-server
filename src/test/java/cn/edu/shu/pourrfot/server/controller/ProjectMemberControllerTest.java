package cn.edu.shu.pourrfot.server.controller;

import cn.edu.shu.pourrfot.server.config.CustomMySQLContainer;
import cn.edu.shu.pourrfot.server.enums.RoleEnum;
import cn.edu.shu.pourrfot.server.enums.SexEnum;
import cn.edu.shu.pourrfot.server.model.PourrfotUser;
import cn.edu.shu.pourrfot.server.model.Project;
import cn.edu.shu.pourrfot.server.model.ProjectMember;
import cn.edu.shu.pourrfot.server.repository.PourrfotUserMapper;
import cn.edu.shu.pourrfot.server.repository.ProjectMapper;
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
class ProjectMemberControllerTest {
  @ClassRule
  public static MySQLContainer<CustomMySQLContainer> customMySQLContainer = CustomMySQLContainer.getInstance();
  private final PourrfotUser owner = PourrfotUser.builder()
    .username("teacher4")
    .nickname("teacher4")
    .password("password")
    .role(RoleEnum.teacher)
    .sex(SexEnum.male)
    .createTime(new Date())
    .updateTime(new Date())
    .build();
  private final PourrfotUser student = PourrfotUser.builder()
    .username("student4")
    .nickname("student4")
    .password("password")
    .role(RoleEnum.student)
    .sex(SexEnum.male)
    .createTime(new Date())
    .updateTime(new Date())
    .build();
  private final Project project = Project.builder()
    .createTime(new Date())
    .updateTime(new Date())
    .projectCode("projectCode2")
    .projectName("projectName2")
    .profilePhoto("profilePhoto2")
    .build();
  private final ProjectMember[] projectMembers = new ProjectMember[]{
    ProjectMember.builder()
      .roleName("participant")
      .build(),
    ProjectMember.builder()
      .roleName("owner")
      .build()
  };
  @Autowired
  private ProjectMapper projectMapper;
  @Autowired
  private PourrfotUserMapper pourrfotUserMapper;
  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private ObjectMapper objectMapper;

  @BeforeEach
  void prepare() {
    assertEquals(1, pourrfotUserMapper.insert(owner));
    assertEquals(1, pourrfotUserMapper.insert(student));
    assertEquals(1, projectMapper.insert(project.setOwnerId(owner.getId())));
  }

  @Transactional
  @Test
  void integrationTest() throws Exception {
    final List<String> locations = new ArrayList<>(projectMembers.length);
    projectMembers[0].setUserId(student.getId());
    projectMembers[1].setUserId(owner.getId());
    // POST create
    for (ProjectMember projectMember : projectMembers) {
      mockMvc.perform(post(String.format("/projects/%d/members", project.getId()))
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(projectMember.setProjectId(project.getId())))
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
        .andDo(result -> log.info("Detail success: {}", result.getResponse().getContentAsString()));
    }
    // GET detail not found
    mockMvc.perform(get("/projects/999/members/999"))
      .andExpect(status().isNotFound());
    // page
    mockMvc.perform(get(String.format("/projects/%d/members", project.getId()))
      .contentType(MediaType.APPLICATION_JSON)
      .accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.data.records").exists())
      .andExpect(jsonPath("$.data.records").isArray())
      .andExpect(jsonPath("$.data.records", Matchers.hasSize(projectMembers.length)))
      .andDo(result -> log.info("Page success: {}", result.getResponse().getContentAsString()));
    mockMvc.perform(get(String.format("/projects/%d/members", project.getId()))
      .param("roleName", "owner")
      .contentType(MediaType.APPLICATION_JSON)
      .accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.data.records").exists())
      .andExpect(jsonPath("$.data.records").isArray())
      .andExpect(jsonPath("$.data.records", Matchers.hasSize(1)))
      .andDo(result -> log.info("Page success: {}", result.getResponse().getContentAsString()));
    // PUT update
    mockMvc.perform(put(locations.get(1))
      .contentType(MediaType.APPLICATION_JSON)
      .content(objectMapper.writeValueAsString(projectMembers[1].setRoleName("UPDATE")))
      .accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.data.createTime").exists())
      .andExpect(jsonPath("$.data.updateTime").exists())
      .andExpect(jsonPath("$.data.roleName").value("UPDATE"))
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
