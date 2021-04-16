package you.manage.model;

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
public class Transaction implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 唯一标识
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 目标链名称：1:ETH 2:HECO 3:BSC 4:TRX
     */
    @TableField("chainId")
    private Integer chainId;

    /**
     * hash
     */
    @TableField("hash")
    private String hash;

    /**
     * blockHash
     */
    @TableField("blockHash")
    private String blockHash;

    /**
     * blockNumber
     */
    @TableField("blockNumber")
    private Long blockNumber;

    /**
     * nonce
     */
    @TableField("nonce")
    private Long nonce;

    /**
     * from
     */
    @TableField("_from")
    private String _from;

    /**
     * to
     */
    @TableField("_to")
    private String _to;

    /**
     * transactionIndex
     */
    @TableField("transactionIndex")
    private Long transactionIndex;

    /**
     * input
     */
    @TableField("input")
    private String input;

    /**
     * gas
     */
    @TableField("gas")
    private Long gas;

    /**
     * gasPrice
     */
    @TableField("gasPrice")
    private Long gasPrice;

    /**
     * 备用字段
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
