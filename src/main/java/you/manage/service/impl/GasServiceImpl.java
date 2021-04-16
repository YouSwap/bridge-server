package you.manage.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.springframework.transaction.annotation.Transactional;
import you.manage.dao.GasMapper;
import you.manage.model.Gas;
import you.manage.service.GasService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import java.util.*;


/**
 * @author zhBlock
 */
@Slf4j
@Service("gasService")
public class GasServiceImpl extends ServiceImpl<GasMapper, Gas> implements GasService {

    @Override
    public List<Gas> findGass(Gas Gas) {
        QueryWrapper<Gas> queryWrapper = new QueryWrapper<>();
        return this.baseMapper.selectList(queryWrapper);
    }

    @Override
    @Transactional
    public void createGas(Gas Gas) {
        Gas.setCreated(new Date());
        this.save(Gas);
    }

    @Override
    @Transactional
    public void updateGas(Gas Gas) {
        Gas.setModified(new Date());
        this.baseMapper.updateById(Gas);
    }

    @Override
    @Transactional
    public void deleteGass(String[] GasIds) {
        Arrays.stream(GasIds).forEach(GasId -> this.baseMapper.deleteById(GasId));
    }

    @Override
    public void addGasPrice(Integer chainId, String chainName,String gasStr) {
        JSONObject jsonObject = JSONObject.fromObject(gasStr);
        String resultStr = jsonObject.getString("result");
        if(StringUtils.isNotBlank(resultStr)){
            JSONObject result =  JSONObject.fromObject(resultStr);
            String safeGasPrice = result.getString("SafeGasPrice");
            String proposeGasPrice = result.getString("ProposeGasPrice");
            String fastGasPrice = result.getString("FastGasPrice");
            Gas gas = getGasInfo(chainId);
            if(gas!=null){
                if(StringUtils.isNotBlank(safeGasPrice) && StringUtils.isNotBlank(proposeGasPrice)  && StringUtils.isNotBlank(fastGasPrice)){
                    gas.setSafe(Long.valueOf(safeGasPrice));
                    gas.setPropose(Long.valueOf(proposeGasPrice));
                    gas.setFast(Long.valueOf(fastGasPrice));
                    this.updateGas(gas);
                }else{
                    log.info("************查询gas接口返回数据异常************");
                }
            }else{
                Gas gas1 = new Gas();
                gas1.setChainId(chainId);
                gas1.setChainName(chainName);
                gas1.setSafe(Long.valueOf(safeGasPrice));
                gas1.setPropose(Long.valueOf(proposeGasPrice));
                gas1.setFast(Long.valueOf(fastGasPrice));
                this.createGas(gas1);
            }
        }
    }

    @Override
    public Gas getGasInfo(Integer chainId) {
        QueryWrapper<Gas> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("chainId",chainId);
        Gas gas = this.baseMapper.selectOne(queryWrapper);
        return gas;
    }
}
