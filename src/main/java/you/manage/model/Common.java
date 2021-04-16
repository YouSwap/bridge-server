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
public class Common implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 唯一标识
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 总开关 0=关闭跨链，1=开启中
     */
    @TableField("state")
    private Integer state;

    /**
     * 程序启动密码
     */
    @TableField("password")
    private String password;

    /**
     * 跨链签名地址
     */
    @TableField("address")
    private String address;
    /**
     * 跨链签名加密私钥
     */
    @TableField("privateKey")
    private String privateKey;

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
