package you.manage.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.web3j.protocol.core.methods.response.EthBlock;
import you.manage.dao.BlockMapper;
import you.manage.model.Block;
import you.manage.model.Config;
import you.manage.service.BlockService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import you.manage.service.ConfigService;

import java.math.BigInteger;
import java.util.*;


/**
 * @author zhBlock
 */
@Slf4j
@Service("blockService")
public class BlockServiceImpl extends ServiceImpl<BlockMapper, Block> implements BlockService {

    @Autowired
    private ConfigService configService;

    @Override
    public List<Block> findBlocks(Block Block) {
        QueryWrapper<Block> queryWrapper = new QueryWrapper<>();
        return this.baseMapper.selectList(queryWrapper);
    }

    @Override
    @Transactional
    public void createBlock(Block Block) {
        Block.setCreated(new Date());
        this.save(Block);
    }

    @Override
    @Transactional
    public void updateBlock(Block Block) {
        Block.setModified(new Date());
        this.baseMapper.updateById(Block);
    }

    @Override
    @Transactional
    public void deleteBlocks(String[] BlockIds) {
        Arrays.stream(BlockIds).forEach(BlockId -> this.baseMapper.deleteById(BlockId));
    }

    @Override
    public Config getConfigBlock(Integer chainId) {
        //查找默认区块高度
        QueryWrapper<Config> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("chainId",chainId);
        Config config = configService.getOne(queryWrapper);
        Block block = new Block();
        block.setChainId(chainId);
        Block bk = this.baseMapper.findOneBlock(block);
        if(bk!=null){
            //最新区块高度
            config.setInitHeight(bk.getHeight()+1);
        }
        return config;
    }

    @Override
    public void addBlock(EthBlock.Block block, Integer num,Integer chainId) {
        //查询上一个区块高度的交易hash 比较
        BigInteger upHeight = block.getNumber().subtract(new BigInteger("1"));
        QueryWrapper<Block> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("height",upHeight);
        queryWrapper.eq("chainId",chainId);
        queryWrapper.orderByDesc("id");
        List<Block> bk = this.baseMapper.selectList(queryWrapper);//查询list防止回退区块入库多个相同区块高度信息
        Block blockItem  = new Block();
        boolean addFalse = true;
        if(bk.size()>0){
            Block blockInfo = bk.get(0);
            //当前区块parentHash和上个区块 hash不一致 继续保存上一个区块信息
            if(!blockInfo.getHash().equalsIgnoreCase(block.getParentHash())){
                System.out.println("11111");
                this.createBlock(blockInfo);
                addFalse = false;
            }
        }
        if(addFalse){
            blockItem.setHeight(Long.valueOf(block.getNumber().toString()));
            blockItem.setChainId(chainId);
            blockItem.setNumber(Long.valueOf(num));
            blockItem.setHash(block.getHash());
            blockItem.setParentHash(block.getParentHash());
            this.createBlock(blockItem);
        }
    }
}
