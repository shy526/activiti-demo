package top.ccxh;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.junit.jupiter.api.Test;

import java.util.List;

public class activitiHelpTest {
    @Test
    public void test1(){
        ActivitiHelp activitiHelp = ActivitiHelp.buildActivitiHelp();
        activitiHelp.addAllResource();
        RepositoryService repositoryService = activitiHelp.getProcessEngine().getRepositoryService();
        List<Deployment> list = repositoryService.createDeploymentQuery().list();
        for (Deployment deployment:list){
            System.out.println("deployment = " + deployment.getName());
        }
    }
}
