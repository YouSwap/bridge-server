package you.manage.controller;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import you.manage.model.OrderList;
import you.manage.service.OrdersService;
import you.tools.utils.JsonResult;
import you.tools.utils.ReturnEnum;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
@CrossOrigin
@RestController
@RequestMapping("/you/chain")
public class ApiController {
    @Autowired
    private OrdersService ordersService;

    @GetMapping("/getOrderList")
    public JsonResult getOrderList(HttpServletRequest request){
        JsonResult jsonResult = new JsonResult();
        String sender = request.getParameter("sender");
        if(StringUtils.isNotBlank(sender)){
            List<OrderList> list = ordersService.findList(sender);
            jsonResult.setObj(list);
            jsonResult.setCode(ReturnEnum.CONSTANT_SUCCESS.getCode());
            jsonResult.setMsg(ReturnEnum.CONSTANT_SUCCESS.getMessage());
            jsonResult.setSuccess(true);
        }else{
            jsonResult.setCode(ReturnEnum.CONSTANT_ISBLANK.getCode());
            jsonResult.setMsg(ReturnEnum.CONSTANT_ISBLANK.getMessage());
        }

        return jsonResult;
    }
}
