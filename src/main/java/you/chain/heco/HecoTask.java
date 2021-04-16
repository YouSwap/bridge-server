package you.chain.heco;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import you.chain.eth.EthService;
import you.manage.service.OrdersService;

@Component
@EnableScheduling
public class HecoTask {
    @Autowired
    private EthService ethService;
    @Autowired
    private OrdersService ordersService;
    /**
     * 扫快事件
     */
    @Scheduled(cron="0/3 * * * * ?")
    public void monitor() {
        ethService.initMonitor(2,"HECO");
    }

    /**
     * 交易确认数--发起交易-确认完成
     */
    @Scheduled(cron="0/5 * * * * ?")
    public void exchangeConfirm() {
        ordersService.exchangeConfirm(2);
    }
}
