package top.ccxh.testx;

import top.ccxh.ActivitiHelp;

import javax.swing.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class KefuTest implements Serializable {

    public List<String> selevtgroup(String group){
        System.out.println("group = " + group);
        if (group.equals("a")){
            List<String> list=new ArrayList<>();
            list.add("小王");
            list.add("小红");
            return list;
        }else {
            List<String> list=new ArrayList<>();
            list.add("大王");
            list.add("大红");
            return list;
        }

    }
}
