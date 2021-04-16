package you.manage.thread;

import org.web3j.protocol.core.methods.response.EthBlock;
import you.manage.service.TransactionService;
import you.tools.utils.SpringContextUtil;

public class TransactionRunnable implements Runnable {

    private EthBlock.TransactionObject transaction;
    private Integer chainId;

    public TransactionRunnable(EthBlock.TransactionObject transaction,Integer chainId) {
        this.transaction = transaction;
        this.chainId = chainId;
    }
    @Override
    public void run() {
        TransactionService transactionService = (TransactionService) SpringContextUtil.getBean("transactionService");
        transactionService.addTransaction(transaction,chainId);
    }

}
