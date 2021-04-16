package you.manage.model;

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
public class Block implements Serializable {

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
     * 区块高度
     */
    @TableField("height")
    private Long height;

    /**
     * 包含的跨链交易笔数
     */
    @TableField("number")
    private Long number;

    /**
     * hash
     */
    @TableField("hash")
    private String hash;

    /**
     * hash
     */
    @TableField("parentHash")
    private String parentHash;

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
