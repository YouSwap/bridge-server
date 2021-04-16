package ${package.Service};

import ${package.Entity}.${entity};
import ${superServiceClassPackage};


import java.util.List;

/**
 * @author ${author}
 */
<#if kotlin>
interface ${table.serviceName} : ${superServiceClass}<${entity}>
<#else>
public interface ${table.serviceName} extends ${superServiceClass}<${entity}> {

    List<${entity}> find${entity}s(${entity} ${cfg.entity_name_lowercase});

    void create${entity}(${entity} ${cfg.entity_name_lowercase});

    void update${entity}(${entity} ${cfg.entity_name_lowercase});

    void delete${entity}s(String[] ${cfg.entity_name_lowercase}Ids);
}
</#if>
