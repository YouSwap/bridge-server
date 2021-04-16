package you.manage.service;

import you.manage.model.Common;
import com.baomidou.mybatisplus.extension.service.IService;


import java.util.List;

/**
 * @author zhBlock
 */
public interface CommonService extends IService<Common> {

    List<Common> findCommons(Common Common);

    Common findOneCommon();

    void createCommon(Common Common);

    void updateCommon(Common Common);

    void deleteCommons(String[] CommonIds);

    /**
     * 判断是否开启跨链
     * @return
     */
    boolean startMonitor();
}
