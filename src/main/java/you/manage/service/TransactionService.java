package you.manage.service;

import org.web3j.protocol.core.methods.response.EthBlock;
import you.manage.model.Transaction;
import com.baomidou.mybatisplus.extension.service.IService;


import java.util.List;

/**
 * @author zhBlock
 */
public interface TransactionService extends IService<Transaction> {

    List<Transaction> findTransactions(Transaction Transaction);

    void createTransaction(Transaction Transaction);

    void updateTransaction(Transaction Transaction);

    void deleteTransactions(String[] TransactionIds);

    void addTransaction(EthBlock.TransactionObject transaction,Integer chainId);
}
