package com.crionline.activiti.controller;

/**
 * Created by fx on 2016/11/23.
 */

import com.crionline.activiti.service.ActivitiService;
import org.activiti.engine.history.HistoricIdentityLink;
import org.activiti.engine.task.IdentityLink;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController

public class MyRestController {

	@Autowired
	private ActivitiService myService;
	
	//开启流程实例
	@RequestMapping(value = "/process/{personId}/{compId}", method = RequestMethod.GET)
	public void startProcessInstance(@PathVariable Long personId, @PathVariable Long compId) {
		myService.startProcess(personId, compId);
	}

	//获取当前人的任务
	@RequestMapping(value = "/tasks", method = RequestMethod.GET)
	public List<TaskRepresentation> getTasks(@RequestParam String assignee) {
		List<Task> tasks = myService.getTasks(assignee);
		List<TaskRepresentation> dtos = new ArrayList<TaskRepresentation>();
		for (Task task : tasks) {
			dtos.add(new TaskRepresentation(task.getId(), task.getName()));
		}
		return dtos;
	}
	//执行当前的任务
	@RequestMapping(value = "/sumbit", method = RequestMethod.GET)
	public String completeTask(@RequestParam String taskId) {
		myService.completeTask(taskId);
		return "ok";
	}


	//完成的任务
	@RequestMapping(value = "/taskHistory", method = RequestMethod.GET)
	public String taskHistory(@RequestParam String taskId) {
		myService.completeTask(taskId);
		return "ok";
	}

	//查询流程定义
	@RequestMapping(value = "/processHistoryInstance", method = RequestMethod.GET)
	public String findHistoricProcessInstance(@RequestParam String taskId) {
		myService.completeTask(taskId);
		return "ok";
	}
	@RequestMapping(value = "/test", method = RequestMethod.GET)
	public void gettest(@RequestParam String assignee) {

//		myService.startProcess("task1");


//		查询当前人所拥有的组任务列表
//		List<Task> taslList = myService.findGroupList(assignee);
//
//		//查询组任务的能够处理人的所有人列表
//		List<IdentityLink> tasks = myService.findGroupUser("155002");
//

		// 获得历史执行任务列表
//		List<HistoricIdentityLink> hisTasks = myService.findGroupHisUser("145002");
//		//拾取组任务 分配给组内的某一个人
//		myService.claimTask("175003","唐僧");
//		//完成分配给某一个人的组任务
//		myService.completeTask("170002");
//
//		myService.completeTasks("重要","172502");
		myService.completeTasks("175003");
//获得当 前人的历史任务列表
//		List  list =  myService.findGroupHisByUser("玉皇大帝");

	}



//	//完成任务
//	@RequestMapping(value = "/complete/{joinApproved}/{taskId}", method = RequestMethod.GET)
//	public String complete(@PathVariable Boolean joinApproved, @PathVariable String taskId) {
//		myService.completeTasks(joinApproved, taskId);
//		return "ok";
//	}

	//Task的dto
	static class TaskRepresentation

	{
		private String id;
		private String name;

		public TaskRepresentation(String id, String name) {
			this.id = id;
			this.name = name;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}
}