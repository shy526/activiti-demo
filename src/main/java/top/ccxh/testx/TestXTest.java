package top.ccxh.testx;

import org.activiti.engine.HistoryService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.history.*;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;
import top.ccxh.ActivitiHelp;
import top.ccxh.pojo.Order;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class TestXTest {
    private static ActivitiHelp activitiHelp = null;

    static {
        activitiHelp = ActivitiHelp.buildActivitiHelp();
        activitiHelp.addAllResource();
    }

    public static void main(String[] args) {
        Map<String, Object> variables = activitiHelp.getEmptyVariables();
        variables.put("userName", "李四");
        Order order = new Order();
        order.setName("啤酒");
        order.setPire(100);
        order.setDate(new Date());
        variables.put("order", order);
        ProcessInstance processInstance = activitiHelp.startProcessByDeploymentName("test-x.bpmn20.xml", variables);
        String id = activitiHelp.getTaskByProcessInstanceId(processInstance.getId()).getId();
        Task task = activitiHelp.getTaskByProcessInstanceId(processInstance.getId());
        TestXTest testXTest = new TestXTest();
        testXTest.userackOrder("李四", task.getId());
        activitiHelp.suspendProcessInstance(task,"李四强制结束流程");
        return;
     /*   task = activitiHelp.getTaskByProcessInstanceId(processInstance.getId());
        testXTest.kefuackOrder("小王", task.getId(), 0);

        task = activitiHelp.getTaskByProcessInstanceId(processInstance.getId());
        testXTest.fahuoOrder("大王", task.getId());*/
    }

    @Test
    public void test(){
        List<HistoricTaskInstance> hisTaskList = activitiHelp.findHisTaskList("12501");
        if(hisTaskList!=null && hisTaskList.size()>0){
            for(HistoricTaskInstance hti:hisTaskList){
                System.out.println(hti.getId()+"    "+hti.getName()+"   "+hti.getClaimTime());
            }
        }

    }

    /**
     * 模拟用户确认订单
     */
    public void userackOrder(String name, String id) {
        Task usertask = activitiHelp.getTaskBySignedNameAndTastId(name, id);
        Map<String, Object> orderinfo = activitiHelp.getTaskVariables(usertask.getId());
        Order order = (Order) orderinfo.get("order");
        System.out.println("order = " + order);
        orderinfo.put("t", 0);
        orderinfo.put("kefu", new KefuTest());
        orderinfo.put("service", new ServiceTest());
        orderinfo.put("group", "a");
        activitiHelp.taskComplete(usertask, orderinfo);
    }

    /**
     * 模拟客服确认订单
     */
    public void kefuackOrder(String name, String id, Integer t) {
        Task usertask = activitiHelp.getTaskListByGroupNameAndTastId(name, id);
        Map<String, Object> orderinfo = activitiHelp.getTaskVariables(usertask.getId());
        Order order = (Order) orderinfo.get("order");
        System.out.println("order = " + order);
        order.setPire(100000000);
        orderinfo.put("t", t);
        orderinfo.put("group", "c");
        activitiHelp.taskComplete(usertask, orderinfo);
    }

    /**
     * 去发货
     */
    public void fahuoOrder(String name, String id) {
        Task usertask = activitiHelp.getTaskListByGroupNameAndTastId(name, id);
        Map<String, Object> orderinfo = activitiHelp.getTaskVariables(usertask.getId());
        Order order = (Order) orderinfo.get("order");
        System.out.println("order = " + order);
        activitiHelp.taskComplete(usertask, orderinfo);
    }

    @Test
    public void h() {
        ProcessEngine processEngine = ActivitiHelp.buildActivitiHelp().getProcessEngine();
        HistoryService historyService = processEngine.getHistoryService();
        List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery().list();
        String taskDefinitionKey = list.get(0).getTaskDefinitionKey();
        List<HistoricVariableInstance> list23 = historyService.createHistoricVariableInstanceQuery().list();
        System.out.println("list23 = " + list23);
    }
}
