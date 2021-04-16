package you.manage.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.transaction.annotation.Transactional;
import you.manage.dao.ContractMapper;
import you.manage.model.Contract;
import you.manage.model.Gas;
import you.manage.service.ContractService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import java.util.*;


/**
 * @author zhBlock
 */
@Slf4j
@Service("contractService")
public class ContractServiceImpl extends ServiceImpl<ContractMapper, Contract> implements ContractService {

    @Override
    public List<Contract> findContracts(Contract Contract) {
        QueryWrapper<Contract> queryWrapper = new QueryWrapper<>();
        return this.baseMapper.selectList(queryWrapper);
    }

    @Override
    @Transactional
    public void createContract(Contract Contract) {
        Contract.setCreated(new Date());
        this.save(Contract);
    }

    @Override
    @Transactional
    public void updateContract(Contract Contract) {
        Contract.setModified(new Date());
        this.baseMapper.updateById(Contract);
    }

    @Override
    @Transactional
    public void deleteContracts(String[] ContractIds) {
        Arrays.stream(ContractIds).forEach(ContractId -> this.baseMapper.deleteById(ContractId));
    }

    @Override
    public Contract getContractInfo(String funcName) {
        QueryWrapper<Contract> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("funcName",funcName);
        Contract contract = this.baseMapper.selectOne(queryWrapper);
        return contract;
    }


}
