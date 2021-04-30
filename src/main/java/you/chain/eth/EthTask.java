package you.chain.eth;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import you.chain.ApiChain;
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
    @Scheduled(cron="0/5 * * * * ?")
    public void monitor() {
        ethService.initMonitor(1,"ETH");
    }

    /**
     * 发起链交易-确认
     */
    @Scheduled(cron="0/5 * * * * ?")
    public void exchangeConfirm() {
        ordersService.exchangeConfirm(1);
    }
    /**
     * 交易确认数--跨链交易--(通用)
     */
    @Scheduled(cron="0/5 * * * * ?")
    public void consumeOrderConfirm() {
        ordersService.consumeOrderConfirm();
    }
    /**
     * 查询待跨链的交易--(通用)
     */
    @Scheduled(cron="0/15 * * * * ?")
    public void getCrossTransaction(){
        ordersService.getCrossTransaction();
    }
    /**
     * 查询跨链完成的订单--(通用)
     */
    @Scheduled(cron="0/30 * * * * ?")
    public void completeCrossOrder(){
        ordersService.completeCrossOrder();
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


    public static void main(String[] args) {
        String str = "0xbdf109b30000000000000000000000000000000000000000000000000000000000000002000000000000000000000000c9e105f22d56a6a89077bf0e8ff1d3578764f2f00000000000000000000000000000000000000000000000000000000083d87100";
        System.out.println(str.substring(10,74));
        String id = ApiChain.getRefMethod(str.substring(10,74),"uint");
        System.out.println("id==="+id);

        String aaa = "0x82986Bf4856D7f44fC485aa69bb947327304c941";
        System.out.println(aaa.toLowerCase());
    }
}
