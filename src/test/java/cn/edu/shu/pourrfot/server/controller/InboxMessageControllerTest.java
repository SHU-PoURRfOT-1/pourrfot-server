package cn.edu.shu.pourrfot.server.controller;

import cn.edu.shu.pourrfot.server.config.CustomMySQLContainer;
import cn.edu.shu.pourrfot.server.enums.RoleEnum;
import cn.edu.shu.pourrfot.server.enums.SexEnum;
import cn.edu.shu.pourrfot.server.model.Message;
import cn.edu.shu.pourrfot.server.model.PourrfotUser;
import cn.edu.shu.pourrfot.server.repository.PourrfotUserMapper;
import cn.edu.shu.pourrfot.server.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.Matchers;
import org.junit.ClassRule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MySQLContainer;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc(addFilters = false)
@Slf4j
class InboxMessageControllerTest {
  public static final int TOTAL = 50;
  @ClassRule
  public static MySQLContainer<CustomMySQLContainer> customMySQLContainer = CustomMySQLContainer.getInstance();
  final private PourrfotUser sender = PourrfotUser.builder()
    .id(100)
    .username("123456")
    .nickname("mock-user1")
    .sex(SexEnum.male)
    .role(RoleEnum.student)
    .password("123456")
    .build();
  final private PourrfotUser receiver = PourrfotUser.builder()
    .id(200)
    .username("456789")
    .nickname("mock-user2")
    .sex(SexEnum.male)
    .role(RoleEnum.student)
    .password("456789")
    .build();
  @Autowired
  private PourrfotUserMapper pourrfotUserMapper;
  @Autowired
  private MessageService messageService;
  @Autowired
  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    given(pourrfotUserMapper.selectById(eq(100))).willReturn(sender);
    given(pourrfotUserMapper.selectById(eq(200))).willReturn(receiver);
    final Message toCreate = Message.builder()
      .sender(sender.getId())
      .receiver(receiver.getId())
      .title("mock title ")
      .content("mock content ")
      .urgent(false)
      .regular(true)
      .build();
    for (int i = 0; i < TOTAL; i++) {
      messageService.save(toCreate
        .setTitle(toCreate.getTitle() + i)
        .setContent(toCreate.getContent() + i)
        .setUrgent(i % 2 == 0)
        .setRegular(i % 3 == 0));
    }
  }

  @Transactional
  @Test
  void integrationTest() throws Exception {
    // GET PAGE
    mockMvc.perform(get("/inbox-messages")
      .contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.data.records").exists())
      .andExpect(jsonPath("$.data.records").isArray())
      .andExpect(jsonPath("$.data.records", Matchers.hasSize(10)));
    mockMvc.perform(get("/inbox-messages")
      .contentType(MediaType.APPLICATION_JSON)
      .param("sender", String.valueOf(sender.getId())))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.data.records").exists())
      .andExpect(jsonPath("$.data.records").isArray())
      .andExpect(jsonPath("$.data.records", Matchers.hasSize(10)));
    mockMvc.perform(get("/inbox-messages")
      .contentType(MediaType.APPLICATION_JSON)
      .param("sender", String.valueOf(sender.getId()))
      .param("receiver", String.valueOf(receiver.getId())))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.data.records").exists())
      .andExpect(jsonPath("$.data.records").isArray())
      .andExpect(jsonPath("$.data.records", Matchers.hasSize(10)));
    mockMvc.perform(get("/inbox-messages")
      .contentType(MediaType.APPLICATION_JSON)
      .param("title", "title"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.data.records").exists())
      .andExpect(jsonPath("$.data.records").isArray())
      .andExpect(jsonPath("$.data.records", Matchers.hasSize(10)));
    mockMvc.perform(get("/inbox-messages")
      .contentType(MediaType.APPLICATION_JSON)
      .param("isUrgent", "true"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.data.records").exists())
      .andExpect(jsonPath("$.data.records").isArray())
      .andExpect(jsonPath("$.data.records", Matchers.hasSize(10)));
    mockMvc.perform(get("/inbox-messages")
      .contentType(MediaType.APPLICATION_JSON)
      .param("isRegular", "true"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.data.records").exists())
      .andExpect(jsonPath("$.data.records").isArray())
      .andExpect(jsonPath("$.data.records", Matchers.hasSize(10)));
  }

  @TestConfiguration
  public static class TestConfig {
    @Bean
    @Primary
    public PourrfotUserMapper pourrfotUserMapper() {
      return Mockito.mock(PourrfotUserMapper.class);
    }
  }
}
