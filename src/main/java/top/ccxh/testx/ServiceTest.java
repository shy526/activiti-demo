package top.ccxh.testx;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import top.ccxh.pojo.Order;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 自动校验订单
 */
public class ServiceTest implements JavaDelegate,Serializable {
    @Override
    public void execute(DelegateExecution execution) throws Exception {
        Order order = (Order) execution.getVariable("order");
        int t=0;
        if (order.getDate() == null) {
            t=1;
        }
        if (order.getName()==null){
            t=1;
        }
        if (order.getPire()==null){
            t=1;
        }
       Map<String, Object> variables = new HashMap<>();
        variables.put("t",t);
        variables.put("order",order);
        variables.put("kefu",new KefuTest());
        variables.put("group","a");
        execution.setVariables(variables);
    }

}
