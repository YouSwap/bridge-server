package you.tools.generator;

import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class AutoMybatis {

    //作者
    private static final String AUTHOR = "zhBlock";
    private static final String URL = "jdbc:mysql://localhost:3306/chain_bridge?useSSL=false&serverTimezone" +
            "=Hongkong&useUnicode=true&characterEncoding=utf-8";
    private static final String DRIVERNAME = "com.mysql.cj.jdbc.Driver";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "zh2020";
    //模块名
    private static final String MODULENAME = "manage";
    //表名
    private static final String TABLENAME = "gas";
    //实体名首字母小写
    private static final String ENTITY_NAME_LOWERCASE = "Gas";

    private static final String BASE_PATH = "you";


    public static void main(String[] args) {
        // 代码生成器
        AutoGenerator mpg = new AutoGenerator();

        // 全局配置
        GlobalConfig gc = new GlobalConfig();
        String projectPath = System.getProperty("user.dir");
        gc.setOutputDir(projectPath + "/src/main/java");
        gc.setAuthor(AUTHOR);
        gc.setOpen(false);
        mpg.setGlobalConfig(gc);
        gc.setServiceName("%sService");

        // 数据源配置
        DataSourceConfig dsc = new DataSourceConfig();
        dsc.setUrl(URL);
        dsc.setDriverName(DRIVERNAME);
        dsc.setUsername(USERNAME);
        dsc.setPassword(PASSWORD);
        mpg.setDataSource(dsc);

        // 包配置
        PackageConfig pc = new PackageConfig();
        pc.setModuleName(MODULENAME);
        pc.setParent("you");
        mpg.setPackageInfo(pc);
        pc.setEntity("model");
        pc.setXml("mapper");

        // 自定义配置
        InjectionConfig cfg = new InjectionConfig() {
            @Override
            public void initMap() {
                Map<String, Object> map = new HashMap<>(16);
                map.put("tablename", TABLENAME);
                map.put("entity_name_lowercase", ENTITY_NAME_LOWERCASE);
                map.put("base_path", BASE_PATH);
                this.setMap(map);
            }
        };

        String templatePath = "";
        List<FileOutConfig> focList = new ArrayList<>();


        templatePath = "/templates/dao.java";
        focList.add(new FileOutConfig(templatePath) {
            @Override
            public String outputFile(TableInfo tableInfo) {
                return projectPath + "/src/main/java/you/" + pc.getModuleName()
                        + "/dao/" + tableInfo.getEntityName() + "Mapper" + StringPool.DOT_JAVA;
            }
        });
        templatePath = "/templates/model.java";
        focList.add(new FileOutConfig(templatePath) {
            @Override
            public String outputFile(TableInfo tableInfo) {
                return projectPath + "/src/main/java/you/" + pc.getModuleName()
                        + "/model/" + tableInfo.getEntityName() + StringPool.DOT_JAVA;
            }
        });

        templatePath = "/templates/service.java";
        focList.add(new FileOutConfig(templatePath) {
            @Override
            public String outputFile(TableInfo tableInfo) {
                return projectPath + "/src/main/java/you/" + pc.getModuleName()
                        + "/service/" + tableInfo.getEntityName() + "Service" + StringPool.DOT_JAVA;
            }
        });

        templatePath = "/templates/serviceImpl.java";
        focList.add(new FileOutConfig(templatePath) {
            @Override
            public String outputFile(TableInfo tableInfo) {
                return projectPath + "/src/main/java/you/" + pc.getModuleName()
                        + "/service/impl/" + tableInfo.getEntityName() + "ServiceImpl" + StringPool.DOT_JAVA;
            }
        });

        templatePath = "/templates/controller.java";
        focList.add(new FileOutConfig(templatePath) {
            @Override
            public String outputFile(TableInfo tableInfo) {
                return projectPath + "/src/main/java/you/" + pc.getModuleName()
                        + "/controller/" + tableInfo.getEntityName() + "Controller" + StringPool.DOT_JAVA;
            }
        });


        cfg.setFileOutConfigList(focList);
        mpg.setCfg(cfg);
        // 策略配置
        StrategyConfig strategy = new StrategyConfig();
        strategy.setNaming(NamingStrategy.underline_to_camel);
        strategy.setColumnNaming(NamingStrategy.underline_to_camel);
        //strategy.setSuperEntityClass("com.hry.basemodel.BaseModel");
        strategy.setRestControllerStyle(true);
        strategy.setSuperMapperClass("com.baomidou.mybatisplus.core.mapper.BaseMapper");
        strategy.setEntityLombokModel(true);
        strategy.setInclude(TABLENAME);
        strategy.setSuperEntityColumns("id");
        strategy.setControllerMappingHyphenStyle(true);
        strategy.setEntityTableFieldAnnotationEnable(true);
        //strategy.setSuperControllerClass("zh.block.manage.utils.controller.BaseController");
        mpg.setStrategy(strategy);
        mpg.setTemplateEngine(new FreemarkerTemplateEngine());
        mpg.execute();
    }
}
