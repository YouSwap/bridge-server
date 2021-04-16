package you.manage.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 
 *
 * @author zhBlock
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class Orders implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 唯一标识
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 订单ID
     */
    @TableField("orderId")
    private Long orderId;

    /**
     * 发起链：1:ETH 2:HECO 3:BSC 4:TRX
     */
    @TableField("fromChainId")
    private Integer fromChainId;

    /**
     * 目标链：1:ETH 2:HECO 3:BSC 4:TRX
     */
    @TableField("toChainId")
    private Integer toChainId;

    /**
     * 发送方地址
     */
    @TableField("sender")
    private String sender;

    /**
     * 接收方地址
     */
    @TableField("recipient")
    private String recipient;

    /**
     * 跨链转移数量
     */
    @TableField("amount")
    private BigDecimal amount;

    /**
     * 订单状态
     -- 0= 交易未达确认数,
     -- 1= 订单待跨链，
     -- 2= 跨链交易已签名,---暂未使用
     -- 3= 跨链交易已广播,
     -- 4= 跨链交易已打包  未达确认数,
     -- 5= 资产已跨链,
     -- 6= 订单完成签名已广播,
     -- 7= 订单完成 未达确认数,
     -- 8= 订单已完成，
     -- 9= 订单已取消，
     -- 10= 交易上链失败
     */
    @TableField("state")
    private Integer state;
    /**
     * 订单hash
     */
    @TableField("fromHash")
    private String fromHash;

    /**
     * 交易确认数
     */
    @TableField("fromConfirm")
    private Integer fromConfirm;

    /**
     * 区块高度
     */
    @TableField("fromHeight")
    private Long fromHeight;

    /**
     * 跨链hash
     */
    @TableField("toHash")
    private String toHash;

    /**
     * 跨链确认数
     */
    @TableField("toConfirm")
    private Integer toConfirm;

    /**
     * 跨链区块高度
     */
    @TableField("toHeight")
    private Long toHeight;

    /**
     * 跨链完成hash
     */
    @TableField("completeHash")
    private String completeHash;

    /**
     * 确认数
     */
    @TableField("completeConfirm")
    private Integer completeConfirm;

    /**
     * 跨链完成区块高度
     */
    @TableField("completeHeight")
    private Long completeHeight;

    /**
     * 取消订单hash
     */
    @TableField("cancelHash")
    private String cancelHash;

    /**
     * 取消区块高度
     */
    @TableField("cancelHeight")
    private Long cancelHeight;

    /**
     * 防篡改字符串
     */
    private String footprint;

    /**
     * 时间戳
     */
    @TableField("datestamp")
    private String datestamp;

    /**
     * 备用字段 -- 订单密码
     */
    @TableField("reserve")
    private String reserve;

    /**
     * 创建时间
     */
    @TableField("created")
    private Date created;

    /**
     * 修改时间
     */
    @TableField("modified")
    private Date modified;


}
