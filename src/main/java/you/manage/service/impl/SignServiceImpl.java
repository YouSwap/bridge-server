package you.manage.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.transaction.annotation.Transactional;
import you.manage.dao.SignMapper;
import you.manage.model.Sign;
import you.manage.service.SignService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import java.util.*;


/**
 * @author zhBlock
 */
@Slf4j
@Service("signService")
public class SignServiceImpl extends ServiceImpl<SignMapper, Sign> implements SignService {

    @Override
    public List<Sign> findSigns(Sign Sign) {
        QueryWrapper<Sign> queryWrapper = new QueryWrapper<>();
        return this.baseMapper.selectList(queryWrapper);
    }

    @Override
    @Transactional
    public void createSign(Sign Sign) {
        Sign.setCreated(new Date());
        this.save(Sign);
    }

    @Override
    @Transactional
    public void updateSign(Sign Sign) {
        Sign.setModified(new Date());
        this.baseMapper.updateById(Sign);
    }

    @Override
    @Transactional
    public void deleteSigns(String[] SignIds) {
        Arrays.stream(SignIds).forEach(SignId -> this.baseMapper.deleteById(SignId));
    }


}
