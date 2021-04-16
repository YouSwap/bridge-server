package you.manage.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 
 *
 * @author zhBlock
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class OrderList implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 发起链：1:ETH 2:HECO 3:BSC 4:TRX
     */
    private Integer fromChainId;
    /**
     * 目标链：1:ETH 2:HECO 3:BSC 4:TRX
     */
    private Integer toChainId;
    /**
     * 发送方地址
     */
    private String sender;
    /**
     * 接收方地址
     */
    private String recipient;
    /**
     * 跨链转移数量
     */
    private BigDecimal amount;
    /**
     * 订单状态
     */
    private Integer state;
    /**
     * 订单hash
     */
    private String fromHash;
    /**
     * 交易确认数
     */
    private Integer fromConfirm;
    private Integer fromFigConfirm;
    /**
     * 区块高度
     */
    private Long fromHeight;
    /**
     * 跨链hash
     */
    private String toHash;
    /**
     * 跨链确认数
     */
    private Integer toConfirm;
    private Integer toFigConfirm;
    /**
     * 跨链区块高度
     */
    private Long toHeight;
    private String datestamp;
    private Date created;



}
