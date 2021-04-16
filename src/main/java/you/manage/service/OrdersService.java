package you.manage.service;

import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.Log;
import you.manage.model.OrderList;
import you.manage.model.Orders;
import com.baomidou.mybatisplus.extension.service.IService;
import you.tools.rabbitmq.RabbitTableInitRequest;


import java.util.List;

/**
 * @author zhBlock
 */
public interface OrdersService extends IService<Orders> {

    List<Orders> findOrders(Orders Orders);

    List<OrderList> findList(String sender);

    void createOrders(Orders Orders);

    void updateOrders(Orders Orders);

    Orders getOrderId(Long orderId);
    Orders getOrderState(Integer state);
    /**
     * 订单入库
     * @param log
     * @param transaction
     * @param chainId
     */
    void addOrders(Log log, EthBlock.TransactionObject transaction, Integer chainId);
    /**
     * 获取待跨链交易
     */
    void getCrossTransaction();

    /**
     * 执行跨链交易队列
     * @param rr
     */
    void startCrossTransaction(RabbitTableInitRequest rr);

    /**
     * 跨链交易已执行事件
     * @param log
     * @param transaction
     */
    void orderConsumed(Log log, EthBlock.TransactionObject transaction);

    /**
     * 设置交易完成事件
     * @param log
     * @param transaction
     */
    void completeOrder(Log log, EthBlock.TransactionObject transaction);

    /**
     * 订单取消事件
     * @param log
     * @param transaction
     */
    void orderCanceled(Log log, EthBlock.TransactionObject transaction);

    /**
     * 交易确认数
     */
    void exchangeConfirm(Integer chainId);
    void consumeOrderConfirm();

    /**
     * 设置订单为已完成
     */
    void completeOrder();
    void startCompleteOrder(RabbitTableInitRequest rr);

    /**
     * 上链失败
     * @param chainId
     */
    void failOrder(EthBlock.TransactionObject transaction,Integer chainId);
}
