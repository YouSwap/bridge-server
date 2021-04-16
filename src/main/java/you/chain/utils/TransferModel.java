package you.chain.utils;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * 转账交易对象
 */
@Data
public class TransferModel implements Serializable {
   //发送方
   private String from;
   //接收方
   private String to;
   //转账金额
   private BigDecimal amount;
   //发送方私钥
   private String privateKey;
   //手续费limit
   private BigInteger gasLimit;
   //手续费price
   private BigInteger gasPrice;
   //ERC20代币合约地址  ETH为空
   private String contractAddress;
   //ERC20代币 精度
   private String decimals;
   //消耗的矿工费
   private BigDecimal minerFee;
   //交易编号
   private BigInteger nonce;
   //转出方ETH余额
   private BigDecimal balance;

}
