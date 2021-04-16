package you.manage.service;

import jnr.ffi.annotations.In;
import you.manage.model.Gas;
import com.baomidou.mybatisplus.extension.service.IService;


import java.util.List;

/**
 * @author zhBlock
 */
public interface GasService extends IService<Gas> {

    List<Gas> findGass(Gas Gas);

    void createGas(Gas Gas);

    void updateGas(Gas Gas);

    void deleteGass(String[] GasIds);

    void addGasPrice(Integer chainId,String chainName,String gasStr);

    Gas getGasInfo(Integer chainId);
}
