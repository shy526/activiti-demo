package top.ccxh;

import com.mysql.jdbc.StringUtils;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.impl.cfg.StandaloneProcessEngineConfiguration;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.apache.ibatis.jdbc.Null;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ActivitiHelp {
    private final  static  Logger LOGGER=LoggerFactory.getLogger(ActivitiHelp.class);
    private static ActivitiHelp activitiHelp=null;
    private  ProcessEngine engine=null;
    private  ActivitiConfig config=new ActivitiConfig();
    class ActivitiConfig{
        //URL
        private String url="jdbc:mysql://localhost:3306/weidian_activiti?serverTimezone=UTC&useUnicode=true&characterEncoding=utf8&useSSL=false";
       //用户名
        private String userName="root";
        //密码
        private String password="root";
        //驱动包
        private String driver="com.mysql.jdbc.Driver";
        //数据库创建方式
        private String schemaUpdate= ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE;
        private String fontNmae="宋体";

       public String getFontNmae() {
           return fontNmae;
       }

       public void setFontNmae(String fontNmae) {
           this.fontNmae = fontNmae;
       }

       public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getDriver() {
            return driver;
        }

        public void setDriver(String driver) {
            this.driver = driver;
        }

        public String getSchemaUpdate() {
            return schemaUpdate;
        }

        public void setSchemaUpdate(String schemaUpdate) {
            this.schemaUpdate = schemaUpdate;
        }
    }

    /**
     * 初始化引擎
     * @param config
     * @return
     */
    public  ProcessEngine init(ActivitiConfig config){
        ProcessEngineConfiguration cfg = new StandaloneProcessEngineConfiguration()
                .setJdbcUrl(config.url)
                .setJdbcUsername(config.userName)
                .setJdbcPassword(config.password)
                //     .setJdbcDriver("com.mysql.cj.jdbc.Driver".trim())
                .setJdbcDriver(config.driver)
                .setDatabaseSchemaUpdate(config.schemaUpdate);
        //获取引擎
        cfg.setActivityFontName(config.fontNmae);
        return cfg.buildProcessEngine();

    }

    /**
     * 获取一个帮助类
     * @return
     */
    public static ActivitiHelp buildActivitiHelp(){
       if (activitiHelp!=null){
           activitiHelp=new ActivitiHelp();
           activitiHelp.engine = activitiHelp.init(activitiHelp.config);
       }
        return activitiHelp;
    }

    /**
     * 根据配置生成一个帮助类
     * @param config
     * @return
     */
    public static ActivitiHelp buildActivitiHelp(ActivitiConfig config){
        if (activitiHelp!=null){
            activitiHelp=new ActivitiHelp();
            activitiHelp.engine = activitiHelp.init(config);
        }
        return activitiHelp;
    }

    /**
     * 直接给分配只一个引擎
     * @param engine
     * @return
     */
    public static ActivitiHelp buildActivitiHelp(ProcessEngine engine){
        if (activitiHelp!=null){
            activitiHelp=new ActivitiHelp();
            activitiHelp.engine = engine;
        }
        return activitiHelp;
    }

    /**
     * 加载资源
     * @param name resources/***
     */
    public void  addResource(String name)  {
        if (StringUtils.isNullOrEmpty(name)){
            throw new NullPointerException("name is null");
        }
        RepositoryService reService = engine.getRepositoryService();
        Deployment deploy = reService.createDeployment().addClasspathResource(name).deploy();
        ProcessDefinition processDefinition = reService.createProcessDefinitionQuery().deploymentId(deploy.getId()).singleResult();
        LOGGER.info("流程ID:{},流程名:{}",processDefinition.getId(),processDefinition.getName());
    }
}
