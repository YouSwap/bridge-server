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
public class Gas implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 唯一标识
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 链ID:1:ETH 2:HECO 3:BSC 4:TRX
     */
    @TableField("chainId")
    private Integer chainId;

    /**
     * 链名称：ETH HECO BSC TRX
     */
    @TableField("chainName")
    private String chainName;

    /**
     * 慢
     */
    @TableField("safe")
    private Long safe;

    /**
     * 平均
     */
    @TableField("propose")
    private Long propose;

    /**
     * 快
     */
    @TableField("fast")
    private Long fast;

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
