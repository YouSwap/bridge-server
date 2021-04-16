package you.manage.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.transaction.annotation.Transactional;
import you.manage.dao.ConfigMapper;
import you.manage.model.Config;
import you.manage.service.ConfigService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import java.util.*;


/**
 * @author zhBlock
 */
@Slf4j
@Service("configService")
public class ConfigServiceImpl extends ServiceImpl<ConfigMapper, Config> implements ConfigService {

    @Override
    public List<Config> findConfigs(Config Config) {
        QueryWrapper<Config> queryWrapper = new QueryWrapper<>();
        return this.baseMapper.selectList(queryWrapper);
    }

    @Override
    @Transactional
    public void createConfig(Config Config) {
        Config.setCreated(new Date());
        this.save(Config);
    }

    @Override
    @Transactional
    public void updateConfig(Config Config) {
        Config.setModified(new Date());
        this.baseMapper.updateById(Config);
    }

    @Override
    @Transactional
    public void deleteConfigs(String[] ConfigIds) {
        Arrays.stream(ConfigIds).forEach(ConfigId -> this.baseMapper.deleteById(ConfigId));
    }

    /**
     * 判断链是否开启跨链
     *
     * @param chainId
     * @return
     */
    @Override
    public boolean startMonitor(Integer chainId) {
        QueryWrapper<Config> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("chainId",chainId);
        Config config = this.baseMapper.selectOne(queryWrapper);
        if(config!=null){
            if(config.getState()==1){
                return true;
            }else{
                log.info(config.getChainName()+"链已关闭");
            }
        }else{
            log.info(chainId+"链未配置启动参数");
        }
        return false;
    }

    @Override
    public Config getConfig(Integer chainId) {
        QueryWrapper<Config> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("chainId",chainId);
        Config config = this.baseMapper.selectOne(queryWrapper);
        if(config!=null){
            return config;
        }
        return null;
    }
}
