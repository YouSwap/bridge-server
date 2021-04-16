package you.tools.rabbitmq;

import lombok.Data;
import you.manage.model.Orders;

import java.io.Serializable;

@Data
public class RabbitTableInitRequest implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 实现类
     */
    private String interfaceName;
    /**
     * 实现类的方法名
     */
    private String methodName="dealThing";
    /**
     * 订单
     */
    private Orders orders;
    /**
     * 交易hash
     */
    private String Hash;

}
