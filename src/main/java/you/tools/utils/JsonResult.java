package you.tools.utils;

import lombok.Data;

import java.io.Serializable;

@Data
public class JsonResult implements Serializable {

    private Boolean success = false;
    private String msg = "";
    private Object obj = null;
    private String code = "";

    public JsonResult() {
    }


    public JsonResult setSuccess(Boolean success) {
        this.success = success;
        return this;
    }


    public JsonResult setMsg(String msg) {
        this.msg = msg;
        return this;
    }


    public JsonResult setObj(Object obj) {
        this.obj = obj;
        return this;
    }

    public JsonResult setCode(String code) {
        this.code = code;
        return this;
    }
}

