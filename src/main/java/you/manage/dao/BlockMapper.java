package you.manage.dao;

import org.apache.ibatis.annotations.Param;
import you.manage.model.Block;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import you.manage.model.OrderList;

import java.util.List;
import java.util.Map;


/**
 * <p>
 *  Mapper 接口
 * </p>
 * @author zhBlock
 * @since 2019-06-23
 */
public interface BlockMapper extends BaseMapper<Block> {

    Block findOneBlock(@Param("block") Block block);
}
