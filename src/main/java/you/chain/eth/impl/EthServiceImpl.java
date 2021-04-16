package you.chain.eth.impl;

import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import you.chain.ApiChain;
import you.chain.eth.EthService;
import you.chain.utils.ContractConstant;
import you.manage.model.*;
import you.manage.service.*;
import you.manage.thread.OrderRunnable;
import you.manage.thread.TransactionRunnable;
import you.tools.redis.RedisConstant;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service("ethService")
public class EthServiceImpl implements EthService {
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private CommonService commonService;
    @Autowired
    private ConfigService configService;
    @Autowired
    private BlockService blockService;
    @Autowired
    private OrdersService ordersService;
    /**
     * 扫快初始化
     */
    @Override
    public void initMonitor(Integer chainId,String chainName) {
        //判断是否已经开启跨链
        boolean start = commonService.startMonitor();
        if(start){
            //判断ETH链是否已经打开
            boolean openState = configService.startMonitor(chainId);
            if(openState){
                startMonitor(chainId,chainName);
            }
        }
    }
    /**
     * 开始扫快
     */
    private void startMonitor(Integer chainId,String chainName){
        try{
            //获取RPC配置信息
            Config config = blockService.getConfigBlock(chainId);
            Web3j chainRpc = Web3j.build(new HttpService(config.getChainRpc()));
            //先检查是否有之前null的区块
            this.checkBlockNullData(chainId,chainName,config.getContractAddress(),chainRpc);
            //数据库初始高度 最后一次扫快的区块高度 +1
            Long initHeight = config.getInitHeight();
            //获取区块链当前取块高度
            BigInteger newBlockHeight = ApiChain.getBlockHeight(chainRpc);
            BigInteger forBlockHeight = BigInteger.valueOf(initHeight);
            if(newBlockHeight.compareTo(forBlockHeight) < 1){
                //未出新快
                return;
            }
            List<String> blockNullList = new ArrayList();
            long loopBlockNum = forBlockHeight.intValue() + 10;
            for (int i = forBlockHeight.intValue(); i < loopBlockNum; i++) {
                BigInteger queryHeight = BigInteger.valueOf(i);
                if(queryHeight.compareTo(newBlockHeight)==1){
                    break;
                }
                log.info(chainName+"***当前扫快的区块高度=============="+queryHeight);
                //获取block
                EthBlock.Block block = ApiChain.getBlockData(queryHeight,chainRpc);
                if (block == null) {
                    blockNullList.add(String.valueOf(i));
                    continue;
                }
                this.getBlockTransaction(block,queryHeight,config.getContractAddress(),chainRpc,chainId);
            }
            //保存获取空的区块下次循环在遍历
            if(blockNullList.size()>0){
                redisTemplate.opsForList().rightPushAll(RedisConstant.BLOCK_HEIGHT+chainName,blockNullList);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    /**
     * 解析区块中的交易
     * @param block
     */
    public void getBlockTransaction(EthBlock.Block block,BigInteger blockHeight,
                                    String contractAddress,Web3j chainRpc,Integer chainId) throws Exception {
        List<EthBlock.TransactionResult> transactions = block.getTransactions();
        int num = 0;
        for (EthBlock.TransactionResult tx : transactions) {
            JSONObject jsonObject = JSONObject.fromObject(tx);
            //接收者地址
            String to = jsonObject.getString("to");
            EthBlock.TransactionObject transaction = (EthBlock.TransactionObject) tx;
            if(to.equalsIgnoreCase(contractAddress)){
                TransactionReceipt receipt = ApiChain.getTransactionData(transaction.getHash(),chainRpc);
                if(receipt!=null && receipt.getStatus().equals("0x1")){
                    List<Log> logs = receipt.getLogs();
                    boolean createTran = true;
                    for(Log log:logs){
                        String event = log.getTopics().get(0);
                        if(event.equalsIgnoreCase(ContractConstant.Transfer)){//跨链交易事件--from
                            num++;
                            //订单入库
                            OrderRunnable task = new OrderRunnable(log,transaction,chainId);
                            Thread thread = new Thread(task);
                            thread.start();
                        }else if(event.equalsIgnoreCase(ContractConstant.Transferred)){//跨链交易完成事件
                            ordersService.completeOrder(log,transaction);
                        }else if(event.equalsIgnoreCase(ContractConstant.OrderConsumed)){// 跨链交易事件--to
                            ordersService.orderConsumed(log,transaction);
                        }else if(event.equalsIgnoreCase(ContractConstant.OrderCanceled)){//订单取消事件
                            ordersService.orderCanceled(log,transaction);
                        }else{
                            createTran = false;
                        }
                    }
                    if(createTran){
                        //交易详情入库
                        TransactionRunnable task = new TransactionRunnable(transaction,chainId);
                        Thread thread = new Thread(task);
                        thread.start();
                    }
                }else{
                    System.out.println("chainId=="+chainId+"==交易失败************"+transaction.getHash());
                    ordersService.failOrder(transaction,chainId);
                }
            }
        }
        //扫快入库
        blockService.addBlock(block,num,chainId);
    }

    /**
     * 遍历之前null的区块
     */
    private void checkBlockNullData(Integer chainId,String chainName,String contractAddress,Web3j chainRpc){
        String redisBlock = RedisConstant.BLOCK_HEIGHT+chainName;
        try{
            List<String> getList = redisTemplate.opsForList().range(redisBlock, 0, -1);
            if(getList.size()>0){
                redisTemplate.opsForValue().getOperations().delete(redisBlock);
                List<String> blockNullList = new ArrayList();
                for (Object object : getList) {
                    String blockNumber = object.toString();
                    log.info(chainName+"*************************漏掉区块重新遍历："+blockNumber);
                    //获取block
                    EthBlock.Block block = ApiChain.getBlockData(BigInteger.valueOf(Integer.valueOf(blockNumber)),chainRpc);
                    if (block == null) {
                        blockNullList.add(blockNumber);
                        continue;
                    }
                    this.getBlockTransaction(block,BigInteger.valueOf(Long.valueOf(blockNumber)),contractAddress,chainRpc,chainId);
                }
                //保存获取空的区块下次循环在遍历
                if(blockNullList.size()>0){
                    redisTemplate.opsForList().rightPushAll(redisBlock,blockNullList);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
