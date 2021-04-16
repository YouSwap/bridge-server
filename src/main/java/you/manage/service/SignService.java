package you.manage.service;

import you.manage.model.Sign;
import com.baomidou.mybatisplus.extension.service.IService;


import java.util.List;

/**
 * @author zhBlock
 */
public interface SignService extends IService<Sign> {

    List<Sign> findSigns(Sign Sign);

    void createSign(Sign Sign);

    void updateSign(Sign Sign);

    void deleteSigns(String[] SignIds);
}
