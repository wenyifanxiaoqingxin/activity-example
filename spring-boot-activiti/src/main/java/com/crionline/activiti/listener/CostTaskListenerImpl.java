package com.crionline.activiti.listener;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;

/**
 * Created by fx on 2019/2/14.
 */
public class CostTaskListenerImpl implements TaskListener {

    //指定个人任务和组任务办理人
    @Override
    public void notify(DelegateTask delegateTask) {
        String assignee= "王一一";
        //指定
        delegateTask.setAssignee(assignee);
    }
}
