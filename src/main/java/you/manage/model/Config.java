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
public class Config implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 唯一标识
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 链ID
     */
    @TableField("chainId")
    private Long chainId;

    /**
     * 链名称：1:ETH 2:HECO 3:BSC 4:TRX
     */
    @TableField("chainName")
    private String chainName;

    /**
     * 链RPC
     */
    @TableField("chainRpc")
    private String chainRpc;

    /**
     * 扫快起始高度
     */
    @TableField("initHeight")
    private Long initHeight;

    /**
     * 区块确认数
     */
    @TableField("confirmNum")
    private Integer confirmNum;

    /**
     * 交互合约地址
     */
    @TableField("contractAddress")
    private String contractAddress;

    /**
     * 0=已关闭，1=运行中
     */
    @TableField("state")
    private Integer state;


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
