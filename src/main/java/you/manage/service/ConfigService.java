package you.manage.service;

import you.manage.model.Config;
import com.baomidou.mybatisplus.extension.service.IService;


import java.util.List;

/**
 * @author zhBlock
 */
public interface ConfigService extends IService<Config> {

    List<Config> findConfigs(Config Config);

    void createConfig(Config Config);

    void updateConfig(Config Config);

    void deleteConfigs(String[] ConfigIds);

    /**
     * 判断链是否开启跨链
     * @param chainId
     * @return
     */
    boolean startMonitor(Integer chainId);

    Config getConfig(Integer chainId);
}
