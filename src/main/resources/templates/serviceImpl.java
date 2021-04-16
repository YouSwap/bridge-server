package ${package.ServiceImpl};

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.transaction.annotation.Transactional;
import ${package.Entity}.${entity};
import ${package.Mapper}.${table.mapperName};
import ${package.Service}.${table.serviceName};
import ${superServiceImplClassPackage};
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import java.util.*;


/**
 * @author ${author}
 */
@Slf4j
@Service
<#if kotlin>
open class ${table.serviceImplName} : ${superServiceImplClass}<${table.mapperName}, ${entity}>(), ${table.serviceName} {

}
<#else>
public class ${table.serviceImplName} extends ${superServiceImplClass}<${table.mapperName}, ${entity}> implements ${table.serviceName} {

    @Override
    public List<${entity}> find${entity}s(${entity} ${cfg.entity_name_lowercase}) {
        QueryWrapper<${entity}> queryWrapper = new QueryWrapper<>();
        return this.baseMapper.selectList(queryWrapper);
    }

    @Override
    @Transactional
    public void create${entity}(${entity} ${cfg.entity_name_lowercase}) {
        ${cfg.entity_name_lowercase}.setCreated(new Date());
        this.save(${cfg.entity_name_lowercase});
    }

    @Override
    @Transactional
    public void update${entity}(${entity} ${cfg.entity_name_lowercase}) {
        ${cfg.entity_name_lowercase}.setModified(new Date());
        this.baseMapper.updateById(${cfg.entity_name_lowercase});
    }

    @Override
    @Transactional
    public void delete${entity}s(String[] ${cfg.entity_name_lowercase}Ids) {
        Arrays.stream(${cfg.entity_name_lowercase}Ids).forEach(${cfg.entity_name_lowercase}Id -> this.baseMapper.deleteById(${cfg.entity_name_lowercase}Id));
    }


}
</#if>
