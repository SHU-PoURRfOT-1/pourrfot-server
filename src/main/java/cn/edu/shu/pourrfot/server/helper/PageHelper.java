package cn.edu.shu.pourrfot.server.helper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.experimental.UtilityClass;
import org.apache.commons.collections4.ListUtils;

import java.util.List;

/**
 * @author spencercjh
 */
@UtilityClass
public class PageHelper {
  /**
   * Manually optimize paging
   *
   * @param entities all entities
   * @param page     {@link IPage} page from Controller
   * @param <E>      element type
   * @param <P>      IPage type
   * @return page
   */
  @SuppressWarnings({"unchecked"})
  public static <E, P extends IPage<E>> P manuallyPage(List<E> entities, P page) {
    final long current = page.getCurrent();
    final long size = page.getSize();
    if (entities.isEmpty()) {
      return (P) page.setTotal(0).setCurrent(1).setRecords(null);
    }
    final List<List<E>> partition = ListUtils.partition(entities, Math.toIntExact(size));
    final boolean isLastPart = (current * size) > entities.size();
    return (P) page.setTotal(entities.size())
      .setRecords(isLastPart ? partition.get(partition.size() - 1) : partition.get(Math.toIntExact(current - 1)))
      .setCurrent(isLastPart ? partition.size() : current);
  }
}
