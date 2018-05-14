package top.ccxh;

import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.activiti.engine.task.Task;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 注册一个service
 */
public class TestService implements JavaDelegate,Serializable {

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        System.out.println("----------------------------------------监听器被调用------------------------------------------");
        Integer t = execution.getVariable("t", Integer.class);
        Integer t1 =execution.getVariable("t1",Integer.class);
        System.out.println("-------------------------------------------监听器结果:(".concat(t+t1+"").concat(")"));
        TaskService taskService = execution.getEngineServices().getTaskService();
        Task task = taskService.createTaskQuery().executionId(execution.getId()).singleResult();
        Map<String,Object> p=new HashMap<>();
        p.put("t",1);
        taskService.complete(task.getId(),p);

    }

    /**
     * 模拟自定分组
     * @param mpAlias
     * @return
     */
    public List<String> getGroup(String mpAlias){
        System.out.println("mpAlias = " + mpAlias);
        System.out.println("----------------------------------------获取分组------------------------------------------");
        List<String> list=new ArrayList<>();
        list.add("ccxh");
        list.add("ccxh1");
        list.add("ccxh2");
        System.out.println("----------------------------------------获取分组------------------------------------------");
        return list;
    }
}
