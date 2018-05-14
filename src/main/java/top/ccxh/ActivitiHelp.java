package top.ccxh;

import com.mysql.jdbc.StringUtils;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.cfg.StandaloneProcessEngineConfiguration;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Activiti 帮助类
 * @author ccxh
 */
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

    private ActivitiHelp(){}
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
       if (activitiHelp==null){
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
        if (activitiHelp==null){
            activitiHelp=new ActivitiHelp();
            activitiHelp.engine = activitiHelp.init(config);
        }
        return activitiHelp;
    }

    /**
     * 直接分配一个引擎
     * @param engine
     * @return
     */
    public static ActivitiHelp buildActivitiHelp(ProcessEngine engine){
        if (activitiHelp==null){
            activitiHelp=new ActivitiHelp();
            activitiHelp.engine = engine;
        }
        return activitiHelp;
    }

    /**
     * 加载资源
     * @param name resources/***
     */
    public void  addResource(String name)   {
        isNotNull(name);
 /*       Deployment resource = findResource(name);
        if (resource!=null){
            LOGGER.debug("deployment.name:{},is exist ",resource.getName());
            return;
        }*/
        RepositoryService reService = engine.getRepositoryService();
        Deployment deploy = reService.createDeployment().addClasspathResource(name).name(name).deploy();
        ProcessDefinition processDefinition = reService.createProcessDefinitionQuery().deploymentId(deploy.getId()).singleResult();
        LOGGER.debug("process.id:{},process.name:{}",processDefinition.getId(),processDefinition.getName());
    }

    /**
     * 删除资源
     * @param name
     */
    public void deleteResource(String name){
        isNotNull(name);
        RepositoryService repositoryService = engine.getRepositoryService();
        try {
            Deployment deployment = repositoryService.createDeploymentQuery().deploymentName(name).singleResult();
            repositoryService.deleteDeployment(deployment.getId());
            LOGGER.debug("depoyment.name:{} delete success ",name);
        }catch (Exception e){
            LOGGER.info("depoyment.name:{} delete error",name);
        }
    }

    /**
     * 根据name查找某个模型
     * @param name
     * @return
     */
    public Deployment findResource(String name){
        isNotNull(name);
        Deployment deployment=null;
        try {
            deployment=engine.getRepositoryService().createDeploymentQuery().deploymentName(name).singleResult();
            LOGGER.debug("depoyment.id:{},depoyment.name:{} find success ",name);
        }catch (Exception e){
            LOGGER.info("depoyment.name:{} find error",name);
        }
        return deployment;
    }

    /**
     * .suffix
     * 加载所有resource下后缀为suffix
     * @param suffix
     */
    public void addAllResource(String suffix){
        isNotNull(suffix);
        URL url = ActivitiHelp.class.getClassLoader().getResource(".");
        File resources=new File(url.getPath());
        //过滤后缀
        File[] files = resources.listFiles((file) -> {
            if (file.isFile()){
                String name = file.getName();
                String fileSuffix = name.substring(name.indexOf(".")+1);
                if (StringUtils.isNullOrEmpty(fileSuffix)){
                    return false;
                }
                return suffix.equals(fileSuffix);
            }

            return false;
        });
        for (File resouce:files){
            addResource(resouce.getName());
        }
    }

    /**
     * 载所有resource下后缀为"bpmn20.xml"
     */
    public void addAllResource(){
        addAllResource("bpmn20.xml");
    }

    private void  isNotNull(String value){
        if (StringUtils.isNullOrEmpty(value)){
            throw new NullPointerException("name is null");
        }
    }

    /**
     * 得到一个引擎
     * @return
     */
    public ProcessEngine getProcessEngine(){
        if (engine!=null){
            return engine;
        }
        throw new NullPointerException("engine is null");
    }

    /**
     * 监控器获取当前 任务
     * @param execution
     * @return
     */
    public Task getTask(DelegateExecution execution){
        TaskService taskService = execution.getEngineServices().getTaskService();
        return taskService.createTaskQuery().executionId(execution.getId()).singleResult();
    }

    /**
     * 查询代理人为name的所有任务
     * @param name
     * @return
     */
    public List<Task> getTaskListByAssignedName(String name){
       return engine.getTaskService().createTaskQuery().taskCandidateOrAssigned(name).list();
    }

    /**
     * 查询代理人为name的任务id为tastId的任务
     * @param name
     * @param tastId
     * @return
     */
    public Task getTaskBySignedNameAndTastId(String name,String tastId){
        return engine.getTaskService().createTaskQuery().taskCandidateOrAssigned(name).taskId(tastId).singleResult();
    }

    /**
     * 返回指定group分组的所有任务
     * @param group
     * @return
     */
    public List<Task> getTaskListByGroupName(String group){
        return  engine.getTaskService().createTaskQuery().taskCandidateGroup(group).list();
    }

    /**
     * 返回指定group分组的所有任务id为tastId的任务
     * @param group
     * @param tastId
     * @return
     */
    public Task getTaskListByGroupNameAndTastId(String group,String tastId){
        return engine.getTaskService().createTaskQuery().taskCandidateGroup(group).taskId(tastId).singleResult();
    }


    /**
     * 开始一个模型名为name的流程
     * @param name
     * @return
     */
    public ProcessInstance startProcessByDeploymentName(String name){
       return startProcessByDeploymentName(name,null);
    }

    /**
     * 开始一个模型名为name的流程,并传入参数variables
     * @param name
     * @return
     */
    public ProcessInstance startProcessByDeploymentName(String name, Map<String,Object> variables){
        Deployment resource = findResource(name);
        ProcessDefinition processDefinition = engine.getRepositoryService().createProcessDefinitionQuery().deploymentId(resource.getId()).singleResult();
        ProcessInstance processInstance = engine.getRuntimeService().startProcessInstanceById(processDefinition.getId(), variables);
        return processInstance;
    }

    /**
     * 通过任务获取流程实例
     * @param task
     * @return
     */
    public ProcessInstance getProcessInstance(Task task){
        return engine.getRuntimeService().createProcessInstanceQuery().processInstanceId( task.getProcessInstanceId()).singleResult();
    }

    public Map<String,Object> getEmptyVariables(){
        return new HashMap<>();
    }

    /**
     * 获取流程id为id的活动任务
     * @param id
     * @return
     */
    public Task  getTaskByProcessInstanceId(String id){
        return engine.getTaskService().createTaskQuery().processInstanceId(id).active().singleResult();
    }

    /**
     * 完成指定任务并传递参数
     * @param task
     */
    public void taskComplete(Task task,Map<String,Object> variables){
        engine.getTaskService().complete(task.getId(),variables);
    }

    /**
     * 完成任务
     * @param task
     */
    public void taskComplete(Task task){
        taskComplete(task,null);
    }

    /**
     * 获取任务的流程变量集合
     * @param id
     * @return
     */
    public  Map<String, Object>  getTaskVariables(String id){
        return engine.getTaskService().getVariables(id);
    }

    /**
     * 流程中止
     * @param task 任务实例
     * @param message 中止原因
     * @return
     */
    public void  suspendProcessInstance(Task task,String message){
        ProcessInstance processInstance = getProcessInstance(task);
        if (processInstance==null){
            LOGGER.info("processInstance is voer");
        }
        engine.getRuntimeService().deleteProcessInstance(processInstance.getId(),message);
    }

    /**
     * 某个流程实例的历史任务流程
     * @param processInstanceId 流程实例
     * @return
     */
    public List<HistoricTaskInstance> findHisTaskList(String processInstanceId ){
        return engine.getHistoryService().createHistoricTaskInstanceQuery().processInstanceId(processInstanceId).list();
    }

    /**
     * 启动 key 中最新版本
     * @param key
     * @param variables
     * @return
     */
    public ProcessInstance startProcessByProcessDefinitionKeyMaxVersion(String key , Map<String,Object> variables){
        List<ProcessDefinition> list1 = engine.getRepositoryService().createProcessDefinitionQuery().processDefinitionKey(key).orderByProcessDefinitionVersion().desc().list();
        if (list1==null||list1.size()<1){
            throw new NullPointerException("Process not find");
        }
        return engine.getRuntimeService().startProcessInstanceById(list1.get(0).getId(), variables);
    }

}
