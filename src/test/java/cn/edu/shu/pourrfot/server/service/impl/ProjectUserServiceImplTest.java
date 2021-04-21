package cn.edu.shu.pourrfot.server.service.impl;

import cn.edu.shu.pourrfot.server.config.CustomMySQLContainer;
import cn.edu.shu.pourrfot.server.enums.RoleEnum;
import cn.edu.shu.pourrfot.server.enums.SexEnum;
import cn.edu.shu.pourrfot.server.exception.IllegalCRUDOperationException;
import cn.edu.shu.pourrfot.server.exception.NotFoundException;
import cn.edu.shu.pourrfot.server.model.PourrfotUser;
import cn.edu.shu.pourrfot.server.model.Project;
import cn.edu.shu.pourrfot.server.model.ProjectUser;
import cn.edu.shu.pourrfot.server.repository.PourrfotUserMapper;
import cn.edu.shu.pourrfot.server.repository.ProjectMapper;
import cn.edu.shu.pourrfot.server.service.ProjectUserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.ClassRule;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MySQLContainer;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
class ProjectUserServiceImplTest {
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
  @Autowired
  private ProjectUserService projectUserService;
  @Autowired
  private ProjectMapper projectMapper;
  @Autowired
  private PourrfotUserMapper pourrfotUserMapper;

  @Transactional
  @Test
  void updateById() {
    assertEquals(1, pourrfotUserMapper.insert(owner));
    assertEquals(1, pourrfotUserMapper.insert(student));
    assertEquals(1, projectMapper.insert(project.setOwnerId(owner.getId())));
    final ProjectUser projectUser = ProjectUser.builder()
      .projectId(project.getId())
      .userId(student.getId())
      .roleName("participant")
      .build();

    assertThrows(NotFoundException.class, () -> projectUserService.updateById(projectUser));
    assertTrue(projectUserService.save(projectUser));

    assertTrue(projectUserService.updateById(projectUser.setRoleName("UPDATE")));
    assertThrows(IllegalCRUDOperationException.class, () -> projectUserService.updateById(projectUser.setUserId(999)));
  }
}
