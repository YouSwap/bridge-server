package you.manage.thread;

import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.Log;
import you.manage.service.OrdersService;
import you.tools.utils.SpringContextUtil;

public class OrderRunnable implements Runnable {

    private EthBlock.TransactionObject transaction;
    private Log log;
    private Integer chainId;

    public OrderRunnable(Log log,EthBlock.TransactionObject transaction, Integer chainId) {
        this.transaction = transaction;
        this.chainId = chainId;
        this.log = log;
    }
    @Override
    public void run() {
        OrdersService ordersService = (OrdersService) SpringContextUtil.getBean("ordersService");
        ordersService.addOrders(log,transaction,chainId);
    }

}
