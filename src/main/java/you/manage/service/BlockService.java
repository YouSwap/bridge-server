package you.manage.service;

import org.web3j.protocol.core.methods.response.EthBlock;
import you.manage.model.Block;
import com.baomidou.mybatisplus.extension.service.IService;
import you.manage.model.Config;


import java.util.List;

/**
 * @author zhBlock
 */
public interface BlockService extends IService<Block> {

    List<Block> findBlocks(Block Block);

    void createBlock(Block Block);

    void updateBlock(Block Block);

    void deleteBlocks(String[] BlockIds);

    Config getConfigBlock(Integer chainId);

    void addBlock(EthBlock.Block block,Integer num,Integer chainId);
}
