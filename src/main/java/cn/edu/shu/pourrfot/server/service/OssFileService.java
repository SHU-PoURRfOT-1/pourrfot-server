package cn.edu.shu.pourrfot.server.service;

import cn.edu.shu.pourrfot.server.model.OssFile;
import cn.edu.shu.pourrfot.server.model.dto.CompleteOssFile;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @author spencercjh
 */
public interface OssFileService extends IService<OssFile> {
  Page<CompleteOssFile> page(Page<OssFile> page, Wrapper<OssFile> queryWrapper);

  boolean isAccessibleForCurrentUser(OssFile ossFile);

  CompleteOssFile getCompleteOssFileById(int id);
}
