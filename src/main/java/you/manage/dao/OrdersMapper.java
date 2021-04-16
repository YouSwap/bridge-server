package you.manage.dao;

import org.apache.ibatis.annotations.Param;
import you.manage.model.Block;
import you.manage.model.OrderList;
import you.manage.model.Orders;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;
import java.util.Map;


/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author zhBlock
 * @since 2019-06-23
 */
public interface OrdersMapper extends BaseMapper<Orders> {

    //List<OrderList> findList(@Param("params") Map<String, Object> map);

    List<Orders> findList(@Param("orders") Orders orders);
}
