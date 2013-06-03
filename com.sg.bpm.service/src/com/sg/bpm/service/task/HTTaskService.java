package com.sg.bpm.service.task;

import java.util.List;

import org.jbpm.task.Status;
import org.jbpm.task.query.TaskSummary;
import org.jbpm.task.service.TaskService;
import org.jbpm.task.service.local.LocalTaskService;

public class HTTaskService extends LocalTaskService {

	private static final String language = "en-UK";// "zh-cn";

	public HTTaskService(TaskService taskService) {

		super(taskService);

	}

	public List<TaskSummary> getSubTasksAssignedAsPotentialOwner(long parentId, String userId) {

		return super.getSubTasksAssignedAsPotentialOwner(parentId, userId, language);
	}

	public List<TaskSummary> getTasksAssignedAsBusinessAdministrator(String userId) {

		return super.getTasksAssignedAsBusinessAdministrator(userId, language);
	}

	public List<TaskSummary> getTasksAssignedAsExcludedOwner(String userId) {

		return super.getTasksAssignedAsExcludedOwner(userId, language);
	}

	public List<TaskSummary> getTasksAssignedAsPotentialOwner(String userId) {

		return super.getTasksAssignedAsPotentialOwner(userId, language);
	}

	public List<TaskSummary> getTasksAssignedAsPotentialOwnerByStatus(String userId, List<Status> status) {

		return super.getTasksAssignedAsPotentialOwnerByStatus(userId, status, language);
	}

	public List<TaskSummary> getTasksAssignedAsPotentialOwnerByStatusByGroup(String userId, List<String> groupIds, List<Status> status) {

		return super.getTasksAssignedAsPotentialOwnerByStatusByGroup(userId, groupIds, status, language);
	}

	public List<TaskSummary> getTasksAssignedAsPotentialOwner(String userId, List<String> groupIds) {

		return super.getTasksAssignedAsPotentialOwner(userId, groupIds, language);
	}

	public List<TaskSummary> getTasksAssignedAsPotentialOwner(String userId, List<String> groupIds, int firstResult, int maxResult) {

		return super.getTasksAssignedAsPotentialOwner(userId, groupIds, language, firstResult, maxResult);
	}

	public List<TaskSummary> getTasksAssignedAsRecipient(String userId) {

		return super.getTasksAssignedAsRecipient(userId, language);
	}

	public List<TaskSummary> getTasksAssignedAsTaskInitiator(String userId) {

		return super.getTasksAssignedAsTaskInitiator(userId, language);
	}

	public List<TaskSummary> getTasksAssignedAsTaskStakeholder(String userId) {

		return super.getTasksAssignedAsTaskStakeholder(userId, language);
	}

	public List<TaskSummary> getTasksOwned(String userId) {

		return super.getTasksOwned(userId, language);
	}

	public List<TaskSummary> getTasksOwned(String userId, List<Status> status) {

		return super.getTasksOwned(userId, status, language);
	}

	public void claimNextAvailable(String userId) {

		super.claimNextAvailable(userId, language);

	}

	public void claimNextAvailable(String userId, List<String> groupIds) {

		super.claimNextAvailable(userId, groupIds, language);
	}

	public void complete(long taskId, String userId) {
		
		super.complete(taskId, userId, null);
	}

}
