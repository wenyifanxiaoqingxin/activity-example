package com.crionline.activiti.listener;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;

/*
* 描述 ： 监听TASL1的监听器，用于添加任务接收组
* 作者 ： csg
* 时间 ： 2017-11-13
*/
public class Task1ListenerImpl_4 implements TaskListener {


    /**指定个人任务和组任务的办理人*/
    public void notify(DelegateTask delegateTask) {
        String userId1 = "玉皇大帝";
        String userId2 = "佛祖";
        //指定组任务
        delegateTask.addCandidateUser(userId1);
        delegateTask.addCandidateUser(userId2);
    }

}
