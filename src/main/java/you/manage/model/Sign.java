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
public class Sign implements Serializable {

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
     * 构建交易函数名称
     */
    @TableField("name")
    private String name;

    /**
     * 交易数据明文
     */
    @TableField("data")
    private String data;

    /**
     * 签名后数据
     */
    @TableField("rawData")
    private String rawData;

    /**
     * 签名钱包地址
     */
    @TableField("address")
    private String address;

    /**
     * nonce
     */
    @TableField("nonce")
    private Long nonce;

    /**
     * 广播交易hash
     */
    @TableField("hash")
    private String hash;

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
