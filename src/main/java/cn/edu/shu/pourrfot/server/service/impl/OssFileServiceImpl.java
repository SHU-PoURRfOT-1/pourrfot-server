package cn.edu.shu.pourrfot.server.service.impl;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.edu.shu.pourrfot.server.repository.OssFileMapper;
import cn.edu.shu.pourrfot.server.model.OssFile;
import cn.edu.shu.pourrfot.server.service.OssFileService;
/**
 * @author spencercjh
 */
@Service
public class OssFileServiceImpl extends ServiceImpl<OssFileMapper, OssFile> implements OssFileService{

}
