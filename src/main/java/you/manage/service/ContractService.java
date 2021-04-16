package you.manage.service;

import you.manage.model.Contract;
import com.baomidou.mybatisplus.extension.service.IService;


import java.util.List;

/**
 * @author zhBlock
 */
public interface ContractService extends IService<Contract> {

    List<Contract> findContracts(Contract Contract);

    void createContract(Contract Contract);

    void updateContract(Contract Contract);

    void deleteContracts(String[] ContractIds);

    Contract getContractInfo(String funcName);
}
