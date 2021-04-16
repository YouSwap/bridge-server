package you.manage.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.commons.lang.StringUtils;
import org.springframework.transaction.annotation.Transactional;
import you.chain.ApiChain;
import you.manage.dao.CommonMapper;
import you.manage.model.Common;
import you.manage.service.CommonService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import you.tools.utils.AesEncryptUtils;

import java.util.*;


/**
 * @author zhBlock
 */
@Slf4j
@Service("commonService")
public class CommonServiceImpl extends ServiceImpl<CommonMapper, Common> implements CommonService {

    @Override
    public List<Common> findCommons(Common Common) {
        QueryWrapper<Common> queryWrapper = new QueryWrapper<>();
        return this.baseMapper.selectList(queryWrapper);
    }

    @Override
    public Common findOneCommon() {
        QueryWrapper<Common> queryWrapper = new QueryWrapper<>();
        List<Common> list = this.baseMapper.selectList(queryWrapper);
        if(list.size()>0){
            return list.get(0);
        }else{
            return null;
        }
    }

    @Override
    @Transactional
    public void createCommon(Common Common) {
        Common.setCreated(new Date());
        this.save(Common);
    }

    @Override
    @Transactional
    public void updateCommon(Common Common) {
        Common.setModified(new Date());
        this.baseMapper.updateById(Common);
    }

    @Override
    @Transactional
    public void deleteCommons(String[] CommonIds) {
        Arrays.stream(CommonIds).forEach(CommonId -> this.baseMapper.deleteById(CommonId));
    }

    /**
     * 判断是否开启跨链
     * @return
     */
    @Override
    public boolean startMonitor() {
        //判断是否已经开启跨链
        Common common = this.findOneCommon();
        if(common!=null){
            if(common.getState()==1 && StringUtils.isNotBlank(common.getPassword())){
                if(StringUtils.isBlank(common.getPrivateKey())){
                    this.createdAddress(common);
                }
                return true;
            }else{
                log.info("未启动程序或者启动密码未配置");
            }
        }else{
            log.info("未配置启动参数");
        }
        return false;
    }

    /**
     * 生成manage私钥地址
     * @param common
     */
    private void createdAddress(Common common){
        try{
            Map<String, Object> maps = ApiChain.createdAddress();
            common.setAddress(maps.get("address").toString());
            String privateKey = maps.get("privateKey").toString();
            //私钥加密
            String key = common.getPassword()+AesEncryptUtils.PASSWORDKEY;
            String pkey = AesEncryptUtils.encrypt(privateKey,key);
            common.setPrivateKey(pkey);
            this.updateCommon(common);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
