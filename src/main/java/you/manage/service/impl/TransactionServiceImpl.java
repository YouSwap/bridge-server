package you.manage.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.transaction.annotation.Transactional;
import org.web3j.protocol.core.methods.response.EthBlock;
import you.manage.dao.TransactionMapper;
import you.manage.model.Transaction;
import you.manage.service.TransactionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import java.util.*;


/**
 * @author zhBlock
 */
@Slf4j
@Service("transactionService")
public class TransactionServiceImpl extends ServiceImpl<TransactionMapper, Transaction> implements TransactionService {

    @Override
    public List<Transaction> findTransactions(Transaction Transaction) {
        QueryWrapper<Transaction> queryWrapper = new QueryWrapper<>();
        return this.baseMapper.selectList(queryWrapper);
    }

    @Override
    @Transactional
    public void createTransaction(Transaction Transaction) {
        Transaction.setCreated(new Date());
        this.save(Transaction);
    }

    @Override
    @Transactional
    public void updateTransaction(Transaction Transaction) {
        Transaction.setModified(new Date());
        this.baseMapper.updateById(Transaction);
    }

    @Override
    @Transactional
    public void deleteTransactions(String[] TransactionIds) {
        Arrays.stream(TransactionIds).forEach(TransactionId -> this.baseMapper.deleteById(TransactionId));
    }

    @Override
    public void addTransaction(EthBlock.TransactionObject transaction,Integer chainId) {
        Transaction tran = new Transaction();
        tran.setChainId(chainId);
        tran.setHash(transaction.getHash());
        tran.setBlockHash(transaction.getBlockHash());
        tran.setBlockNumber(Long.valueOf(transaction.getBlockNumber().toString()));
        tran.set_from(transaction.getFrom());
        tran.set_to(transaction.getTo());
        tran.setGas(Long.valueOf(transaction.getGas().toString()));
        tran.setGasPrice(Long.valueOf(transaction.getGasPrice().toString()));
        tran.setInput(transaction.getInput());
        tran.setTransactionIndex(Long.valueOf(transaction.getTransactionIndex().toString()));
        tran.setNonce(Long.valueOf(transaction.getNonce().toString()));
        this.createTransaction(tran);
    }
}
