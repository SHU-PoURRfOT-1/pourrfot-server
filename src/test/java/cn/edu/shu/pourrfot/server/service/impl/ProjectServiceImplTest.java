package cn.edu.shu.pourrfot.server.service.impl;

import cn.edu.shu.pourrfot.server.enums.RoleEnum;
import cn.edu.shu.pourrfot.server.enums.SexEnum;
import cn.edu.shu.pourrfot.server.exception.NotFoundException;
import cn.edu.shu.pourrfot.server.model.PourrfotUser;
import cn.edu.shu.pourrfot.server.model.Project;
import cn.edu.shu.pourrfot.server.repository.PourrfotUserMapper;
import cn.edu.shu.pourrfot.server.service.ProjectService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("local")
@Slf4j
class ProjectServiceImplTest {
//  @ClassRule
//  public static MySQLContainer<CustomMySQLContainer> customMySQLContainer = CustomMySQLContainer.getInstance();

  private final PourrfotUser owner = PourrfotUser.builder()
    .username("teacher3")
    .nickname("teacher3")
    .password("password")
    .role(RoleEnum.teacher)
    .sex(SexEnum.male)
    .createTime(new Date())
    .updateTime(new Date())
    .build();
  private final Project project = Project.builder()
    .createTime(new Date())
    .updateTime(new Date())
    .projectCode("projectCode1")
    .projectName("projectName1")
    .profilePhoto("profilePhoto1")
    .build();
  @Autowired
  private ProjectService projectService;
  @Autowired
  private PourrfotUserMapper pourrfotUserMapper;

  @Transactional
  @Test
  void updateById() {
    assertEquals(1, pourrfotUserMapper.insert(owner));

    assertThrows(NotFoundException.class, () -> projectService.updateById(project));
    assertTrue(projectService.save(project.setOwnerId(owner.getId())));
    assertTrue(projectService.updateById(project.setProjectName("UPDATE")));
    assertThrows(NotFoundException.class, () -> projectService.updateById(project.setOwnerId(999)));
  }
}
