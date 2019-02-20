package com.crionline.activiti.service;

import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.history.HistoricIdentityLink;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.IdentityLink;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jery on 2016/11/23.
 */

@Service
@Transactional
public class ActivitiService {


    //是activiti的流程执行服务类。可以从这个服务类中获取很多关于流程执行相关的信息
    @Autowired
    private RuntimeService runtimeService;
    //是activiti的任务服务类。可以从这个类中获取任务的信息
    @Autowired
    private TaskService taskService;


    //是Activiti的仓库服务类。所谓的仓库指流程定义文档的两个文件：bpmn文件和流程图片
    @Autowired
    private RepositoryService repositoryService;

    //是activiti的查询历史信息的类。在一个流程执行完成后，这个对象为我们提供查询历史信息。
    @Autowired
    private HistoryService historyService;

    //代表流程定义的执行实例。如范冰冰请了一天的假，她就必须发出一个流程实例的申请。
    // 一个流程实例包括了所有的运行节点。我们可以利用这个对象来了解当前流程实例的进度等信息。
    // 流程实例就表示一个流程从开始到结束的最大的流程分支，即一个流程中流程实例只有一个。
//	@Autowired
//	private ProcessDefinition processDefinition;


    //开始流程，传入申请者的id以及公司的id

    public void startProcess(Long personId, Long compId) {
//
//		repositoryService.activateProcessDefinitionById("");
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("personId", personId);
        variables.put("compId", compId);
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("OneDemo", variables);
        System.out.println("id: "+processInstance.getId()+"activityId: "+processInstance.getActivityId());
    }

    //获得某个人的任务别表
    public List<Task> getTasks(String assignee) {
        return taskService.createTaskQuery().taskAssignee(assignee).list();
    }

//    //完成任务
//    public void completeTasks(Boolean joinApproved, String taskId) {
//        Map<String, Object> taskVariables = new HashMap<String, Object>();
//        taskVariables.put("joinApproved", joinApproved);
//        taskService.complete(taskId, taskVariables);
//    }

    public void startProcess(String taskName) {

        runtimeService.startProcessInstanceByKey(taskName);
    }


    //查询组任务列表
    public List<Task> findGroupList(String userId) {

        List<Task> list = taskService.createTaskQuery()//
                .taskCandidateUser(userId)//指定组任务查询
                .list();
        return list;
    }


    //查询组任务成员列表
    public List<IdentityLink> findGroupUser(String taskId) {
        List<IdentityLink> list = taskService.getIdentityLinksForTask(taskId);
        return list;
    }

    //查询组任务成员历史列表
    public List<HistoricIdentityLink> findGroupHisUser(String taskId) {
        List<HistoricIdentityLink> list = historyService.getHistoricIdentityLinksForTask(taskId);
        return list;
    }
    //将组任务分配给个人任务（认领任务）
    public void claimTask(String taskId, String userId){

        taskService.claim(taskId, userId);
    }
    //完成任务
    public void completeTask( String taskId){
        taskService.complete(taskId);
        System.out.println("完成任务");
    }

    //完成任务
    public void completeTasks(String joinApproved, String taskId) {
        Map<String, Object> taskVariables = new HashMap<String, Object>();
        taskVariables.put("message", joinApproved);
        taskService.complete(taskId, taskVariables);
    }

    public void completeTasks(String taskId) {

        taskService.complete(taskId);
    }

    //获取符合条件的审批人，演示这里写死，使用应用使用实际代码
    public List<String> findUsers(DelegateExecution execution) {

        return Arrays.asList("admin", "wtr");
    }
    //查询组任务成员历史列表
    public List<HistoricTaskInstance> findGroupHisByUser(String userName) {
        List<HistoricTaskInstance>   list =  historyService.createHistoricTaskInstanceQuery().taskAssignee(userName).finished().list();
        return list;
    }

    //历史流程实例（按照某个规则中一共执行多少次流程）
    public  List<HistoricProcessInstance> findHistoricProcessInstance(){

        List<HistoricProcessInstance> historicProcessInstances = historyService.createHistoricProcessInstanceQuery()
                .processDefinitionKey("OneDemo")//流程定义key
                .orderByProcessInstanceStartTime().desc()//流程开始时间降序
                .list();//返回结果集
        for(HistoricProcessInstance historicProcessInstance:historicProcessInstances){

            System.out.println("pid:"+historicProcessInstance.getId());
            System.out.println("pdid:"+historicProcessInstance.getProcessDefinitionId());
            System.out.println("starttime:"+historicProcessInstance.getStartTime());
            System.out.println("endTime:"+historicProcessInstance.getEndTime());
            System.out.println("duration:"+historicProcessInstance.getDurationInMillis());
        }
        return historicProcessInstances;
    }

}
