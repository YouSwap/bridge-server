package you.manage.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.ethereum.crypto.HashUtil;
import org.ethereum.util.ByteUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.web3j.abi.TypeDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.*;
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.abi.datatypes.generated.Uint8;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Convert;
import you.chain.ApiChain;
import you.chain.utils.ContractConstant;
import you.chain.utils.TransferModel;
import you.manage.dao.OrdersMapper;
import you.manage.model.*;
import you.manage.service.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import you.tools.rabbitmq.RabbitConfig;
import you.tools.rabbitmq.RabbitTableInitRequest;
import you.tools.rabbitmq.producer.MsgProducer;
import you.tools.redis.RedisConstant;
import you.tools.utils.AesEncryptUtils;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;


/**
 * @author zhBlock
 */
@Slf4j
@Service("ordersService")
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements OrdersService {
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private MsgProducer msgProducer;
    @Autowired
    private CommonService commonService;
    @Autowired
    private ConfigService configService;
    @Autowired
    private SignService signService;
    @Autowired
    private GasService gasService;
    @Autowired
    private ContractService contractService;

    @Override
    public List<Orders> findOrders(Orders Orders) {
        QueryWrapper<Orders> queryWrapper = new QueryWrapper<>();
        return this.baseMapper.selectList(queryWrapper);
    }

    @Override
    public List<OrderList> findList(String sender) {
        Orders orders = new Orders();
        orders.setSender(sender);
        List<Orders> list = this.baseMapper.findList(orders);
        List<OrderList> newList = new ArrayList<OrderList>();
        for(Orders order:list){
            OrderList orderList = new OrderList();
            Config config1 = configService.getConfig(order.getFromChainId());
            Config config2 = configService.getConfig(order.getToChainId());
            orderList.setFromFigConfirm(config1.getConfirmNum());
            orderList.setToFigConfirm(config2.getConfirmNum());
            if(order.getState() !=10){
                orderList.setToConfirm(order.getToConfirm());
                orderList.setToHeight(order.getToHeight());
                orderList.setToHash(order.getToHash());
            }else{
                orderList.setToConfirm(0);
                orderList.setToHeight(Long.valueOf(0));
                orderList.setToHash("0x0000");
            }
            orderList.setToChainId(order.getToChainId());
            orderList.setOrderId(order.getOrderId());
            orderList.setSender(order.getSender());
            orderList.setRecipient(order.getRecipient());
            orderList.setAmount(order.getAmount());
            orderList.setState(order.getState());
            orderList.setFromChainId(order.getFromChainId());
            orderList.setFromConfirm(order.getFromConfirm());
            orderList.setFromHash(order.getFromHash());
            orderList.setFromHeight(order.getFromHeight());
            orderList.setDatestamp(order.getDatestamp());
            orderList.setCreated(order.getCreated());
            newList.add(orderList);
        }
        return newList;
    }

    @Override
    @Transactional
    public void createOrders(Orders Orders) {
        Orders.setCreated(new Date());
        this.save(Orders);
    }

    @Override
    @Transactional
    public void updateOrders(Orders Orders) {
        Orders.setModified(new Date());
        this.baseMapper.updateById(Orders);
    }

    @Override
    public Orders getOrderId(Long orderId) {
        QueryWrapper<Orders> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("orderId",orderId);
        Orders orders = this.baseMapper.selectOne(queryWrapper);
        return orders;
    }
    @Override
    public Orders getOrderState(Integer state) {
        QueryWrapper<Orders> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("state",state);
        Orders orders = this.baseMapper.selectOne(queryWrapper);
        return orders;
    }

    /**
     * 订单入库
     * @param transaction
     * @param chainId
     */
    @Override
    public void addOrders(Log logs, EthBlock.TransactionObject transaction, Integer chainId) {
        try {
            String data = logs.getData();
            List<String> topic = logs.getTopics();
            Method refMethod = TypeDecoder.class.getDeclaredMethod("decode", String.class, int.class, Class.class);
            refMethod.setAccessible(true);
            String _from = ApiChain.getRefMethod(topic.get(1),"address");
            String _to = ApiChain.getRefMethod(topic.get(2),"address");
            String str = data.substring(2);
            String orderId = ApiChain.getRefMethod(str.substring(0,64),"uint");
            String toChainId = ApiChain.getRefMethod(str.substring(64,128),"uint");
            String amount = ApiChain.getRefMethod(str.substring(128),"uint");
            //判断订单是否已经存在
            Orders orders = this.getOrderId(Long.valueOf(orderId));
            if(orders == null){
                Common common = commonService.findOneCommon();
                //订单入库
                Orders order = new Orders();
                order.setOrderId(Long.valueOf(orderId));
                order.setFromChainId(chainId);
                order.setToChainId(Integer.valueOf(toChainId));
                order.setSender(_from);
                order.setRecipient(_to);
                order.setAmount(new BigDecimal(amount));
                order.setFromHash(transaction.getHash());
                order.setFromConfirm(1);
                order.setState(0);
                order.setFromHeight(Long.valueOf(transaction.getBlockNumber().toString()));
                order.setDatestamp(String.valueOf(System.currentTimeMillis()));
                //生成一个随机加密字符串 -- 并加密
                String strPassword = RandomStringUtils.randomAlphanumeric(18).toLowerCase();
                //-- 并加密
                String strProof = common.getPassword()+AesEncryptUtils.PASSWORDKEY;
                String profKey = AesEncryptUtils.encrypt(strPassword,strProof);
                order.setReserve(profKey);
                //防篡改加密字符串
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bos.write(ByteUtil.bigIntegerToBytes(BigInteger.valueOf(Long.valueOf(orderId)), 32));
                bos.write(ByteUtil.bigIntegerToBytes(BigInteger.valueOf(chainId)));
                bos.write(ByteUtil.hexStringToBytes(_to));
                bos.write(ByteUtil.bigIntegerToBytes(BigInteger.valueOf(Long.valueOf(amount)), 32));
                bos.write(strPassword.getBytes());
                String proof = ByteUtil.toHexString(HashUtil.sha3(bos.toByteArray()));
                String footprintKey = AesEncryptUtils.encrypt(proof,strProof);
                order.setFootprint(footprintKey);
                this.createOrders(order);
            }else{
                log.info(orderId+"订单已存在");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 获取待跨链交易
     */
    @Override
    public void getCrossTransaction() {
        //查询已达确认数 待跨链的交易
        QueryWrapper<Orders> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("state",1);
        List<Orders> list = this.baseMapper.selectList(queryWrapper);
        for(Orders orders:list){
            RabbitTableInitRequest rr = new RabbitTableInitRequest();
            rr.setInterfaceName("ordersService");
            rr.setMethodName("startCrossTransaction");
            rr.setOrders(orders);
            msgProducer.sendMsg(RabbitConfig.EXCHANGE_A,RabbitConfig.routingkey_one,rr);
        }

    }

    /**
     * 跨链签名广播交易
     * @param rr
     */
    @Override
    public void startCrossTransaction(RabbitTableInitRequest rr) {
        Orders orders = rr.getOrders();
        //重新查询交易状态--避免订单多次被消费
        Orders newOrders = this.baseMapper.selectById(orders.getId());
        if(newOrders!=null && newOrders.getState()==1){
            try{
                log.info("订单ID="+orders.getOrderId()+"=====开始跨链交易===toChainId="+orders.getToChainId());
                //密码
                Common common = commonService.findOneCommon();
                //获取RPC配置信息
                Config config = configService.getConfig(orders.getToChainId());
                Web3j chainRpc = Web3j.build(new HttpService(config.getChainRpc()));
                String redisNonce = RedisConstant.CHAIN_NOCE+orders.getToChainId()+":"+common.getAddress();
                String nonce = redisTemplate.opsForValue().get(redisNonce);
                if(StringUtils.isBlank(nonce)){
                    nonce = ApiChain.getNonce(common.getAddress(),chainRpc).toString();
                    redisTemplate.opsForValue().set(redisNonce,nonce);
                }
                //解密动态密码
                String strProof = common.getPassword()+AesEncryptUtils.PASSWORDKEY;
                String profKey = AesEncryptUtils.decrypt(orders.getReserve(),strProof);

                TransferModel transferModel = createTransferModel(config.getContractAddress(),orders.getToChainId(),nonce,ContractConstant.consumeOrder);
                List<Type> inputArgs = new ArrayList<>();
                // 构建输入参数
                inputArgs.add(new Uint256(orders.getOrderId()));
                inputArgs.add(new Uint8(orders.getFromChainId()));
                inputArgs.add(new Address(orders.getRecipient()));
                inputArgs.add(new Uint256(orders.getAmount().toBigInteger()));
                inputArgs.add(new Utf8String(profKey));

                //计算 keccak256
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                byte[] orderId = ByteUtil.bigIntegerToBytes(BigInteger.valueOf(orders.getOrderId()), 32);
                byte[] fromChain = ByteUtil.bigIntegerToBytes(BigInteger.valueOf(orders.getFromChainId()));
                byte[] recipient =  ByteUtil.hexStringToBytes(orders.getRecipient());
                byte[] amount = ByteUtil.bigIntegerToBytes(orders.getAmount().toBigInteger(), 32);
                byte[] proof = profKey.getBytes();
                bos.write(orderId);
                bos.write(fromChain);
                bos.write(recipient);
                bos.write(amount);
                bos.write(proof);
                inputArgs.add(new Bytes32(HashUtil.sha3(bos.toByteArray())));
                //判断数据是否被篡改

                String footprint = ByteUtil.toHexString(HashUtil.sha3(bos.toByteArray()));
                String footprintKey = AesEncryptUtils.decrypt(orders.getFootprint(),strProof);
                if(footprint.equalsIgnoreCase(footprintKey)){
                    Map<String, Object> maps = ApiChain.signSendTransaction(ContractConstant.consumeOrder,transferModel,inputArgs,chainRpc);
                    String hash = maps.get("hash").toString();
                    String status = maps.get("status").toString();
                    if(status.equalsIgnoreCase("nonce")){
                        //查询最新nonce
                        nonce = ApiChain.getNonce(common.getAddress(),chainRpc).toString();
                        redisTemplate.opsForValue().set(redisNonce,nonce);
                    }else if(status.equalsIgnoreCase("msg")){
                        log.error("error:"+hash);
                    }else{
                        //签名入库
                        createTransaction(maps,orders.getToChainId(),nonce,common.getAddress());
                        //修改订单状态
                        orders.setState(3);
                        orders.setToHash(hash);
                        this.updateOrders(orders);
                        //更新Nonce
                        BigInteger nonceNumber = BigInteger.valueOf(Long.valueOf(nonce));
                        long nonceStr = nonceNumber.intValue() + 1;
                        redisTemplate.opsForValue().set(redisNonce,String.valueOf(nonceStr));
                    }
                }else{
                    log.info("orderId="+orderId+"************数据异常");
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    /**
     * 跨链交易已执行
     * @param logs
     * @param transaction
     */
    @Override
    public void orderConsumed(Log logs, EthBlock.TransactionObject transaction) {
         try{
             String data = logs.getData();
             String str = data.substring(2);
             String orderId = ApiChain.getRefMethod(str.substring(0,64),"uint");
             //String toChainId = ApiChain.getRefMethod(str.substring(64,128),"uint");
             //String address = ApiChain.getRefMethod(str.substring(128,192),"address");
             //String amount = ApiChain.getRefMethod(str.substring(192),"uint");
             //改变交易状态
             QueryWrapper<Orders> queryWrapper = new QueryWrapper<>();
             queryWrapper.eq("orderId",orderId);
             Orders orders = this.baseMapper.selectOne(queryWrapper);
             if(orders!=null){
                 orders.setToConfirm(1);
                 orders.setState(4);
                 orders.setToHeight(Long.valueOf(transaction.getBlockNumber().toString()));
                 this.updateOrders(orders);
             }else{
                 log.info("orderConsumed异常"+orderId);
             }
         }catch (Exception e){
             e.printStackTrace();
         }
    }

    /**
     * 设置交易完成
     *
     * @param logs
     * @param transaction
     */
    @Override
    public void completeOrder(Log logs, EthBlock.TransactionObject transaction) {
        String data = logs.getData();
        String str = data.substring(2);
        String orderId = ApiChain.getRefMethod(str.substring(0,64),"uint");
        Orders orders = this.getOrderId(Long.valueOf(orderId));
        if(orders!=null){
            orders.setState(7);
            orders.setCompleteConfirm(1);
            orders.setCompleteHash(transaction.getHash());
            orders.setCompleteHeight(Long.valueOf(transaction.getBlockNumber().toString()));
            this.updateOrders(orders);
        }else{
            log.info("completeOrder异常"+orderId);
        }
    }

    /**
     * 订单取消事件
     * @param logs
     * @param transaction
     */
    @Override
    public void orderCanceled(Log logs, EthBlock.TransactionObject transaction) {
        String data = logs.getData();
        String str = data.substring(2);
        String orderId = ApiChain.getRefMethod(str.substring(0,64),"uint");
        Orders orders = this.getOrderId(Long.valueOf(orderId));
        if(orders!=null){
            orders.setState(9);
            orders.setCancelHash(transaction.getHash());
            orders.setCancelHeight(Long.valueOf(transaction.getBlockNumber().toString()));
            this.updateOrders(orders);
        }else{
            log.info("orderCanceled异常"+orderId);
        }
    }

    @Override
    public void consumeOrderConfirm(){
        QueryWrapper<Orders> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("state",4);
        List<Orders> ordersList = this.baseMapper.selectList(queryWrapper);
        for(Orders orders:ordersList){
            Config config = configService.getConfig(orders.getToChainId());
            //获取当前区块高度
            Web3j chainRpc = Web3j.build(new HttpService(config.getChainRpc()));
            BigInteger newHeight = ApiChain.getBlockHeight(chainRpc);
            BigInteger number = newHeight.subtract(new BigInteger(orders.getToHeight().toString()));
            //是否达到确认数
            if(number.compareTo(new BigInteger(config.getConfirmNum().toString())) >=0){
                //判断交易Hash所在的区块高度是否 和订单入库的区块高度一样
                TransactionReceipt receipt = ApiChain.getTransactionData(orders.getToHash(),chainRpc);
                if(receipt!=null && receipt.getBlockNumber().compareTo(new BigInteger(orders.getToHeight().toString())) ==0){
                    orders.setState(5);
                    if(number.compareTo(new BigInteger(config.getConfirmNum().toString())) >=1){
                        orders.setToConfirm(config.getConfirmNum());
                    }else{
                        orders.setToConfirm(Integer.valueOf(number.toString()));
                    }
                    this.updateOrders(orders);
                    //设置交易为已完成
                    RabbitTableInitRequest rr = new RabbitTableInitRequest();
                    rr.setInterfaceName("ordersService");
                    rr.setMethodName("startCompleteOrder");
                    rr.setOrders(orders);
                    msgProducer.sendMsg(RabbitConfig.EXCHANGE_A,RabbitConfig.routingkey_one,rr);
                }else{
                    //重新跨链交易
                    orders.setState(1);
                }
            }else {
                //更新确认数
                if(number.compareTo(new BigInteger(config.getConfirmNum().toString())) >=1){
                    orders.setToConfirm(config.getConfirmNum());
                }else{
                    orders.setToConfirm(Integer.valueOf(number.toString()));
                }
                this.updateOrders(orders);
            }
        }
    }
    /**
     * 交易确认数
     */
    @Override
    public void exchangeConfirm(Integer chainId) {
        //交易确认数
        QueryWrapper<Orders> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("state",0,7);
        queryWrapper.eq("fromChainId",chainId);
        List<Orders> ordersList = this.baseMapper.selectList(queryWrapper);
        if(ordersList.size()>0){
            Config config = configService.getConfig(chainId);
            //获取当前区块高度
            Web3j chainRpc = Web3j.build(new HttpService(config.getChainRpc()));
            BigInteger newHeight = ApiChain.getBlockHeight(chainRpc);
            for(Orders orders:ordersList){
                BigInteger compareNumber = BigInteger.valueOf(1);
                //判断确认数
                if(orders.getState() ==0){
                    compareNumber = new BigInteger(orders.getFromHeight().toString());
                }else if(orders.getState() ==7){
                    compareNumber = new BigInteger(orders.getCompleteHeight().toString());
                }
                BigInteger number = newHeight.subtract(compareNumber);
                //是否达到确认数
                if(number.compareTo(new BigInteger(config.getConfirmNum().toString())) >=0){
                    if(orders.getState() ==0){
                        //判断交易Hash所在的区块高度是否 和订单入库的区块高度一样
                        TransactionReceipt receipt = ApiChain.getTransactionData(orders.getFromHash(),chainRpc);
                        if(receipt!=null && receipt.getBlockNumber().compareTo(new BigInteger(orders.getFromHeight().toString())) ==0){
                            orders.setState(1);
                            if(number.compareTo(new BigInteger(config.getConfirmNum().toString())) >=1){
                                orders.setFromConfirm(config.getConfirmNum());
                            }else{
                                orders.setFromConfirm(Integer.valueOf(number.toString()));
                            }
                            //达到确认数 开始跨链交易
                            RabbitTableInitRequest rr = new RabbitTableInitRequest();
                            rr.setInterfaceName("ordersService");
                            rr.setMethodName("startCrossTransaction");
                            rr.setOrders(orders);
                            msgProducer.sendMsg(RabbitConfig.EXCHANGE_A,RabbitConfig.routingkey_one,rr);
                        }else{
                            //交易失败
                            orders.setState(10);
                        }
                    }else if(orders.getState() ==7){
                        TransactionReceipt receipt = ApiChain.getTransactionData(orders.getCompleteHash(),chainRpc);
                        if(receipt!=null && receipt.getBlockNumber().compareTo(new BigInteger(orders.getCompleteHeight().toString())) ==0){
                            //更新确认数
                            if(number.compareTo(new BigInteger(config.getConfirmNum().toString())) >=1){
                                orders.setCompleteConfirm(config.getConfirmNum());
                            }else{
                                orders.setCompleteConfirm(Integer.valueOf(number.toString()));
                            }
                            orders.setState(8);
                        }else{
                            //重新设置已完成状态
                            orders.setState(5);
                        }
                    }
                }else{
                    //更新确认数
                    if(number.compareTo(new BigInteger(config.getConfirmNum().toString())) >=1){
                        number = new BigInteger(config.getConfirmNum().toString());
                    }
                    if(orders.getState() ==0){
                        orders.setFromConfirm(Integer.valueOf(number.toString()));
                    }else if(orders.getState() ==7){
                        orders.setCompleteConfirm(Integer.valueOf(number.toString()));
                    }
                }
                this.updateOrders(orders);
            }
        }
    }

    /**
     * 查询跨链完成的订单
     */
    @Override
    public void completeOrder() {
        QueryWrapper<Orders> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("state",5);
        List<Orders> list = this.baseMapper.selectList(queryWrapper);
        for(Orders orders:list){
            RabbitTableInitRequest rr = new RabbitTableInitRequest();
            rr.setInterfaceName("ordersService");
            rr.setMethodName("startCompleteOrder");
            rr.setOrders(orders);
            msgProducer.sendMsg(RabbitConfig.EXCHANGE_A,RabbitConfig.routingkey_one,rr);
        }
    }
    /**
     * 设置订单为已完成
     * @param rr
     */
    @Override
    public void startCompleteOrder(RabbitTableInitRequest rr) {
        Orders orders = rr.getOrders();
        //重新查询交易状态--避免订单多次被消费
        Orders newOrders = this.baseMapper.selectById(orders.getId());
        if(newOrders!=null && newOrders.getState()==5){
            Common common = commonService.findOneCommon();
            //获取RPC配置信息
            Config config = configService.getConfig(orders.getFromChainId());
            Web3j chainRpc = Web3j.build(new HttpService(config.getChainRpc()));
            String redisNonce = RedisConstant.CHAIN_NOCE+orders.getFromChainId()+":"+common.getAddress();
            String nonce = redisTemplate.opsForValue().get(redisNonce);
            if(StringUtils.isBlank(nonce)){
                nonce = ApiChain.getNonce(common.getAddress(),chainRpc).toString();
                redisTemplate.opsForValue().set(redisNonce,nonce);
            }
            TransferModel transferModel = createTransferModel(config.getContractAddress(),orders.getFromChainId(),nonce,ContractConstant.completeOrder);
            List<Type> inputArgs = new ArrayList<>();
            // 构建输入参数
            inputArgs.add(new Uint256(orders.getOrderId()));
            log.info("********开始签名交易********");
            Map<String, Object> maps = ApiChain.signSendTransaction(ContractConstant.completeOrder,transferModel,inputArgs,chainRpc);
            String hash = maps.get("hash").toString();
            String status = maps.get("status").toString();
            if (status.equalsIgnoreCase("nonce")) {
                //查询最新nonce
                nonce = ApiChain.getNonce(common.getAddress(), chainRpc).toString();
                redisTemplate.opsForValue().set(redisNonce, nonce);
            }else if(status.equalsIgnoreCase("msg")){
                log.error("error:"+hash);
            }else{
                //签名入库
                createTransaction(maps,orders.getFromChainId(),nonce,common.getAddress());
                //修改订单状态
                orders.setState(6);
                orders.setCompleteHash(hash);
                orders.setCompleteConfirm(1);
                this.updateOrders(orders);
                //更新Nonce
                BigInteger nonceNumber = BigInteger.valueOf(Long.valueOf(nonce));
                long nonceStr = nonceNumber.intValue() + 1;
                redisTemplate.opsForValue().set(redisNonce,String.valueOf(nonceStr));
            }
        }
    }

    /**
     * 上链失败
     * @param chainId
     */
    @Override
    public void failOrder(EthBlock.TransactionObject transaction, Integer chainId) {
        //判断是什么交易失败
        String methodId = transaction.getInput().substring(0,10);
        if(methodId.equalsIgnoreCase(ContractConstant.exchangeMethodId)){
            //失败订单入库
            Orders order = new Orders();
            order.setOrderId(Long.valueOf(0));
            order.setFromChainId(chainId);
            order.setToChainId(2);
            order.setSender(transaction.getFrom());
            order.setRecipient(transaction.getFrom());
            order.setAmount(new BigDecimal(0));
            order.setFromHash(transaction.getHash());
            order.setFromConfirm(1);
            order.setFromHeight(Long.valueOf(transaction.getBlockNumber().toString()));
            order.setDatestamp(String.valueOf(System.currentTimeMillis()));
            order.setState(10);
            this.createOrders(order);
        }else if(methodId.equalsIgnoreCase(ContractConstant.consumeOrderMethodId)){
            QueryWrapper<Orders> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("toHash",transaction.getHash());
            Orders orders = this.baseMapper.selectOne(queryWrapper);
            if(orders!=null){
                orders.setState(1);
                this.updateOrders(orders);
            }
        }else if(methodId.equalsIgnoreCase(ContractConstant.completeOrderMethodId)){
            QueryWrapper<Orders> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("completeHash",transaction.getHash());
            Orders orders = this.baseMapper.selectOne(queryWrapper);
            if(orders!=null){
                orders.setState(5);
                this.updateOrders(orders);
            }
        }
    }

    /**
     * 封装交易参数
     * @param contractAddress
     * @param chainId
     * @param nonce
     * @return
     */
    private TransferModel createTransferModel(String contractAddress,Integer chainId,String nonce,String funcName){
        TransferModel transferModel = new TransferModel();
        try{
            //获取手续费配置
            Gas gas = gasService.getGasInfo(chainId);
            BigInteger gasPrice = Convert.toWei(gas.getPropose().toString(), Convert.Unit.GWEI).toBigInteger();
            //获取Limit
            Contract contract = contractService.getContractInfo(funcName);
            //获取管理员私钥和地址
            Common common = commonService.findOneCommon();
            //私钥解密
            String key = common.getPassword()+AesEncryptUtils.PASSWORDKEY;
            String privateKey = AesEncryptUtils.decrypt(common.getPrivateKey(),key);
            transferModel.setFrom(common.getAddress());
            transferModel.setTo(contractAddress);
            transferModel.setContractAddress(contractAddress);
            transferModel.setPrivateKey(privateKey);
            transferModel.setGasLimit(BigInteger.valueOf(contract.getFuncLimit()));
            transferModel.setGasPrice(gasPrice);
            transferModel.setNonce(BigInteger.valueOf(Long.parseLong(nonce)));
        }catch (Exception e){
            e.printStackTrace();
        }
        return transferModel;
    }
    /**
     * 签名交易入库
     */
    private void createTransaction(Map<String, Object> maps,Integer chainId,String nonce,String address){
        String funcData = maps.get("funcData").toString();
        String signData = maps.get("signData").toString();
        String hash = maps.get("hash").toString();
        //签名交易入库
        Sign sign = new Sign();
        sign.setChainId(chainId);
        sign.setName(ContractConstant.completeOrder);
        sign.setData(funcData);
        sign.setRawData(signData);
        sign.setAddress(address);
        sign.setNonce(Long.valueOf(nonce));
        sign.setHash(hash);
        signService.createSign(sign);
    }

}
