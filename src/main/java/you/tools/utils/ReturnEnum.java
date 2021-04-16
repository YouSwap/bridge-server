package you.tools.utils;

/**
 * @author: zenghao
 * @date: 2020/7/28 13:59
 */
public enum ReturnEnum {
    CONSTANT_SUCCESS("操作成功", "88"),
    CONSTANT_EXCEPTION("服务器异常", "100"),
    CONSTANT_ISBLANK("参数不全", "101"),
    CONSTANT_ABNORMAL("数据异常", "102");

    //用于获取说明，
    private String message;

    //获取数值
    private String code;

    ReturnEnum(String message, String code) {
        this.message = message;
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

}
