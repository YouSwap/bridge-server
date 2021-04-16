package you.chain.eth;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import you.manage.service.GasService;
import you.manage.service.OrdersService;
import you.tools.utils.HttpUtil;

import java.io.IOException;

@Component
@EnableScheduling
public class EthTask {
    @Autowired
    private EthService ethService;
    @Autowired
    private OrdersService ordersService;
    @Autowired
    private GasService gasService;

    /**
     * 扫快事件
     */
    @Scheduled(cron="0/3 * * * * ?")
    public void monitor() {
        ethService.initMonitor(1,"ETH");
    }

    /**
     * 交易确认数--发起交易-确认完成
     */
    @Scheduled(cron="0/5 * * * * ?")
    public void exchangeConfirm() {
        ordersService.exchangeConfirm(1);
    }
    /**
     * 交易确认数--跨链交易
     */
    @Scheduled(cron="0/5 * * * * ?")
    public void consumeOrderConfirm() {
        ordersService.consumeOrderConfirm();
    }
    /**
     * 跨链交易
     */
    @Scheduled(cron="0/15 * * * * ?")
    public void getCrossTransaction(){
        ordersService.getCrossTransaction();
    }
    /**
     * 设置订单为已完成
     */
    @Scheduled(cron="0/30 * * * * ?")
    public void completeOrder(){
        ordersService.completeOrder();
    }


    /**
     * 获取ETH交易手续费
     * @throws IOException
     */
    @Scheduled(cron="0 */3 * * * ?")
    public void getEthGasPrice() throws IOException{
        String gasLimitStr = HttpUtil.sendGet("https://gas.youswap.info",null);
        if(StringUtils.isNotBlank(gasLimitStr)){
            gasService.addGasPrice(1,"ETH",gasLimitStr);
        }
    }
}
