package com.crionline.activiti.deploy;

import com.crionline.activiti.model.AuditInfo;
import com.crionline.activiti.model.SubmitInfo;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.IdentityLink;
import org.activiti.engine.task.Task;
import org.junit.Test;
import org.springframework.util.CollectionUtils;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by fx on 2019/2/11.
 */
public class groupProccess {
    ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();

    private String processInstanceId="155001";
    //部署
    @Test
    public void deploy() throws FileNotFoundException {

        InputStream inputStream = this.getClass().getResourceAsStream("/processes/groupProcess.bpmn");
        InputStream inputStreamPng = this.getClass().getResourceAsStream("/processes/groupProcess.png");

        Deployment deployment = engine.getRepositoryService()
                .createDeployment()
                .addInputStream("groupProcess.bpmn",inputStream)
                .addInputStream("groupProcess.png",inputStreamPng)
                .name("小组审批")
                .deploy();

        System.out.println(deployment.getId()+": "+deployment.getName());
    }

    //开启流程
    @Test
    public void startProcess(){
        String processId = "groupProcess";
        Map<String,Object> map = new HashMap<>();
        map.put("userIds","大大,小小");
       ProcessInstance processInstance = engine.getRuntimeService()
                .startProcessInstanceByKey(processId,map);
        processInstanceId = processInstance.getId();
        System.out.println("pid: "+processInstance.getId()+"activityId: "+processInstance.getActivityId()+processInstance.getProcessInstanceId());

    }
    //完成此任务
    @Test
    public void complete(){

        String tastId = "162505";
        engine.getTaskService().complete(tastId);

    }

    //查询用户任务
    @Test
    public void userTask(){
        //办理人
        String assigneeUser ="大大";
        List<Task> tasks = engine.getTaskService().createTaskQuery()
                .taskAssignee(assigneeUser)
                .orderByTaskCreateTime().desc()
                .list();
        if(!CollectionUtils.isEmpty(tasks)){
            for(Task task:tasks){

                System.out.println("taskId:"+task.getId());
                System.out.println("assigneeUser:"+task.getAssignee());
                System.out.println("executionId:"+task.getExecutionId());
            }

        }
    }

    //查询组用户任务
    @Test
    public void userGroupTask(){
        //办理人
        String assigneeUser ="大大";
        List<Task> tasks = engine.getTaskService().createTaskQuery()
                .taskCandidateUser(assigneeUser)
                .orderByTaskCreateTime().desc()
                .list();
        if(!CollectionUtils.isEmpty(tasks)){
            for(Task task:tasks){

                System.out.println("taskId:"+task.getId());
                System.out.println("assigneeUser:"+task.getAssignee());
                System.out.println("executionId:"+task.getExecutionId());
            }

        }
    }

    //查询任务组用户
    @Test
    public void groupTaskUsers(){
        //办理人
        String taskId ="162505";
        List<IdentityLink> links = engine.getTaskService()
                .getIdentityLinksForTask(taskId);
        if(!CollectionUtils.isEmpty(links)){
            for(IdentityLink task:links){

                System.out.println("userId:"+task.getUserId());
                System.out.println("taskId:"+task.getTaskId());
                System.out.println("pid:"+task.getProcessInstanceId());
            }

        }
    }

    //将组任务分配给个人任务，拾取任务,由一个人完成任务
    @Test
    public void claim(){
        //任务ID
        String taskId = "162505";
        //分配的办理人
        String userId = "大大";
        engine.getTaskService()//
                .claim(taskId, userId);
    }

    //有传递变量的执行完成任务
    @Test
    public void setVariable(){

        TaskService taskService = engine.getTaskService();
        //办理人
        String assigneeUser ="刘丽";

        //流程实例id

        Task task = taskService.createTaskQuery()  //创建任务查询
                .taskAssignee(assigneeUser)
                .processInstanceId(processInstanceId)        //流程实例id
                .singleResult();
        //此处为排他网关条件 大于一千走李四流程，小于一千走王五流程，map的key对应图中${money}
        Map map = new HashMap(2);
        map.put("money",2000);
        taskService.complete(task.getId(),map);
    }

    //接收传递变量的执行任务
    @Test
    public void getVariable(){

        TaskService taskService = engine.getTaskService();
        //办理人
        String assigneeUser ="王五";


        Task task = taskService.createTaskQuery()  //创建任务查询
                .taskAssignee(assigneeUser)
                .processInstanceId(processInstanceId)        //流程实例id
                .singleResult();

        SubmitInfo submitInfo = (SubmitInfo) taskService.getVariable(task.getId(),"请假信息");
        if(submitInfo!=null){
            System.out.println("**************");
            System.out.println("申请人："+submitInfo.getName());
            System.out.println("请假天数："+submitInfo.getDay());
            System.out.println("请假理由："+submitInfo.getInfo());
            System.out.println("**************");
        }
        AuditInfo auditInfos = (AuditInfo) taskService.getVariable(task.getId(),"审批理由");
        if(auditInfos!=null){
            System.out.println("**************");
            System.out.println("审批人："+auditInfos.getAuditUser());
            System.out.println("审批意见："+auditInfos.getAuditMessage());
            System.out.println("**************");
        }
        AuditInfo auditInfo = new AuditInfo();
        auditInfo.setAuditMessage("同意啦！");
        auditInfo.setAuditUser(assigneeUser);
        taskService.setVariable(task.getId(),"审批理由",auditInfo);

        taskService.complete(task.getId());
    }

    @Test
    //历史流程实例（按照某个规则中一共执行多少次流程）
    public  void findHistoricProcessInstance(){

        List<HistoricProcessInstance> historicProcessInstances = engine.getHistoryService().createHistoricProcessInstanceQuery()
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
    }

    @Test
    //查询历史活动(某一次流程的执行一共经历了多少个活动)

    public void queryHistoryActivityInstance(){

        String processInstanceIds = "45001";
        List<HistoricActivityInstance> historicActivityInstances = engine.getHistoryService()
                .createHistoricActivityInstanceQuery()
                .processInstanceId(processInstanceIds)
                .orderByHistoricActivityInstanceEndTime().asc()
                .list();
        for(HistoricActivityInstance historicActivityInstance:historicActivityInstances){
                System.out.println("activityId:"+historicActivityInstance.getActivityId());
                System.out.println("name:"+historicActivityInstance.getActivityName());
                System.out.println("type:"+historicActivityInstance.getActivityType());
                System.out.println("pid:"+historicActivityInstance.getProcessInstanceId());
                System.out.println("assignee:"+historicActivityInstance.getAssignee());
            System.out.println("startTTime:"+historicActivityInstance.getStartTime());
            System.out.println("endTime:"+historicActivityInstance.getEndTime());
            System.out.println("duration:"+historicActivityInstance.getDurationInMillis());

        }
    }

    @Test
    //查询历史任务(某一次流程执行经历多少任务节点)
    public  void findHistoricTask(){
        String processInstanceIds = "45001";
        List<HistoricTaskInstance> historicTaskInstances = engine.getHistoryService().createHistoricTaskInstanceQuery()
                .processInstanceId(processInstanceIds)//流程定义key
                .orderByHistoricTaskInstanceEndTime().asc()//流程开始时间降序
                .list();//返回结果集
        for(HistoricTaskInstance historicTaskInstance:historicTaskInstances){

            System.out.println("taskId:"+historicTaskInstance.getId());
            System.out.println("name:"+historicTaskInstance.getName());
            System.out.println("pid:"+historicTaskInstance.getProcessInstanceId());
            System.out.println("assignee:"+historicTaskInstance.getAssignee());
            System.out.println("startTTime:"+historicTaskInstance.getStartTime());
            System.out.println("endTime:"+historicTaskInstance.getEndTime());
            System.out.println("duration:"+historicTaskInstance.getDurationInMillis());
        }
    }
    @Test
    //查询历史流程变量(某一次流程执行经历多少任务节点)
    public  void findHistoricVariables(){
        String processInstanceIds = "35001";
        List<HistoricVariableInstance> historicVariableInstances = engine.getHistoryService().createHistoricVariableInstanceQuery()
                .processInstanceId(processInstanceIds)//流程定义key
                .orderByProcessInstanceId().asc()//流程开始时间降序
                .list();//返回结果集
        if(!CollectionUtils.isEmpty(historicVariableInstances)){
            for(HistoricVariableInstance historicVariableInstance:historicVariableInstances){

                System.out.println("pid:"+historicVariableInstance.getProcessInstanceId());
                System.out.println("VariableName:"+historicVariableInstance.getVariableName());
                System.out.println("VariableValue:"+historicVariableInstance.getValue());
            }

        }

    }
}
