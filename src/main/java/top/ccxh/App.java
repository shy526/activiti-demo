package top.ccxh;

import org.activiti.engine.*;
import org.activiti.engine.form.FormData;
import org.activiti.engine.form.FormProperty;
import org.activiti.engine.form.TaskFormData;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricTaskInstanceQuery;
import org.activiti.engine.impl.cfg.StandaloneProcessEngineConfiguration;
import org.activiti.engine.impl.form.DateFormType;
import org.activiti.engine.impl.form.LongFormType;
import org.activiti.engine.impl.form.StringFormType;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Hello world!
 */
public class App {
    private Logger logger = LoggerFactory.getLogger(App.class);
    private ProcessEngine engine = null;

    @Before
    public void init() {
        //做初始化
        ProcessEngineConfiguration cfg = new StandaloneProcessEngineConfiguration()
                .setJdbcUrl("jdbc:mysql://localhost:3306/weidian_activiti?serverTimezone=UTC&useUnicode=true&characterEncoding=utf8&useSSL=false")
                .setJdbcUsername("root")
                .setJdbcPassword("root")
           //     .setJdbcDriver("com.mysql.cj.jdbc.Driver".trim())
                .setJdbcDriver("com.mysql.jdbc.Driver".trim())
                .setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);
        //获取引擎
        cfg.setActivityFontName("宋体");
        engine = cfg.buildProcessEngine();
        logger.info("引擎名:{},版本:{}",engine.getName(),ProcessEngine.VERSION);
    }

    /**
     * 加载资源
     * @param name
     */
    public void  addResource(String name) throws Throwable {
        if (engine==null){
            throw new Throwable("engine is null");
        }
        RepositoryService reService = engine.getRepositoryService();
        Deployment deploy = reService.createDeployment().addClasspathResource(name).deploy();
        ProcessDefinition processDefinition = reService.createProcessDefinitionQuery().deploymentId(deploy.getId()).singleResult();
        logger.info("流程ID:{},流程名:{}",processDefinition.getId(),processDefinition.getName());
    }

    /**
     * 测试加载资源
     */
    @Test
    public void addResource(){
        try {
            System.out.println("engine = " + engine);
            addResource("test.bpmn20.xml");
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }
    @Test
    public void findResource(){
        List<Deployment> list = engine.getRepositoryService().createDeploymentQuery().list();
        for (Deployment d: list) {
            logger.info("模型名称:{},Id:{}",d.getName(),d.getId());
        }
    }

    /**
     * 模拟请假流程
     */
    @Test
    public void askForleave(){
        //1. 小猪发起请假
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("user", "小猪");
        variables.put("t", 1);
        //获取流程定义
        ProcessDefinition processDefinition = engine.getRepositoryService().createProcessDefinitionQuery().list().get(0);
        ProcessInstance processInstance = engine.getRuntimeService().startProcessInstanceById(processDefinition.getId(), variables);
        System.out.println("processInstance = " + processInstance);
        Map<String, Object> variables1 = new HashMap<String, Object>();
        variables1.put("wt", "PPD");
        variables1.put("t", 1);
        Task task = engine.getTaskService().createTaskQuery().taskCandidateOrAssigned("小猪").list().get(0);
        engine.getTaskService().complete(task.getId(),variables1);
    }

    @Test
    public void bailor(){
        // 委托人  //转交任务
        Task task = engine.getTaskService().createTaskQuery().taskCandidateOrAssigned("PPD").list().get(0);
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("t", 1);
        engine.getTaskService().complete(task.getId(),variables);

    }
    @Test
    public  void departmen(){
        //部门主管
        Task task = engine.getTaskService().createTaskQuery().taskCandidateOrAssigned("小王").list().get(0);
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("t", 1);
        engine.getTaskService().complete(task.getId(),variables);
    }

    @Test
    public  void manage(){
        Task task = engine.getTaskService().createTaskQuery().taskCandidateOrAssigned("老张").list().get(0);
        engine.getTaskService().complete(task.getId());

    }
    @Test
    public void history(){
        HistoryService historyService = engine.getHistoryService();
        System.out.println("historyService = " + historyService);
    }

    @Test
    public void test(){
        Map<String,Object> variables=new HashMap<>();
        TestService testService = new TestService();
        variables.put("testService",testService);
        variables.put("mpAlias","xx");
        variables.put("namn","c1");
        variables.put("tradeNumber",100);
        ProcessDefinition processDefinition = engine.getRepositoryService().createProcessDefinitionQuery().list().get(0);
        ProcessInstance processInstance = engine.getRuntimeService().startProcessInstanceById(processDefinition.getId(), variables);
        Map<String,String> form=new HashMap<>();

        //ccxh线同意
        Task ccxh = engine.getTaskService().createTaskQuery().taskCandidateGroup("ccxh").singleResult();


        TaskService taskService = engine.getTaskService();
        Map<String,Object> variables2=new HashMap<>();
        variables2.put("t", 100);
        variables2.put("t1",200);
        variables2.put("TestService",testService);
        taskService.complete(ccxh.getId(),variables2);
    }
    @Test
    public void test22(){
        Task ccxh1 = engine.getTaskService().createTaskQuery().taskCandidateGroup("ccxh1").singleResult();
        System.out.println("ccxh1 = " + ccxh1);
    }

}
