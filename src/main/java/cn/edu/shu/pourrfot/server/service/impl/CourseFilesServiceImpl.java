package cn.edu.shu.pourrfot.server.service.impl;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.edu.shu.pourrfot.server.model.CourseFiles;
import cn.edu.shu.pourrfot.server.repository.CourseFilesMapper;
import cn.edu.shu.pourrfot.server.service.CourseFilesService;
/**
 * @author spencercjh
 */
@Service
public class CourseFilesServiceImpl extends ServiceImpl<CourseFilesMapper, CourseFiles> implements CourseFilesService{

}
