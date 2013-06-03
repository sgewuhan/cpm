package com.sg.bpm.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.EntityManagerFactory;

import org.drools.SystemEventListenerFactory;
import org.jbpm.task.Group;
import org.jbpm.task.User;
import org.jbpm.task.service.TaskClient;
import org.jbpm.task.service.TaskService;
import org.jbpm.task.service.TaskServiceSession;
import org.jbpm.task.service.mina.MinaTaskClientConnector;
import org.jbpm.task.service.mina.MinaTaskClientHandler;
import org.jbpm.task.service.mina.MinaTaskServer;
import org.jbpm.task.service.persistence.TaskPersistenceManager;

public class HTService {

//	private static final String localhost = "127.0.0.1";
//
	private static final int port = 9123;//5445;

	private TaskServiceSession defaultTaskSession;

	private Map<String, TaskClient> taskClients = new ConcurrentHashMap<String, TaskClient>();

	private org.jbpm.task.service.TaskService _taskService;

//	private MinaTaskServer taskServer;

	public HTService() {

	}

	public void init(EntityManagerFactory emf) {

		
		_taskService = new TaskService(emf, SystemEventListenerFactory.getSystemEventListener());
		defaultTaskSession = _taskService.createSession();
		defaultTaskSession.getTaskPersistenceManager();
		MinaTaskServer taskServer = new MinaTaskServer(_taskService, port);
		Thread thread = new Thread(taskServer);
		thread.start();
		
		
	}
	

	
//	public TaskServiceSession getTaskSession(){
//		return _taskService.createSession();
//	}


//	public WorkItemHandler getWorkItemHandler(StatefulKnowledgeSession ksession) {
//		MinaHTWorkItemHandler handler = new MinaHTWorkItemHandler(ksession);
//		return handler;
//	}
	
//	public HornetQHTWorkItemHandler getHTWorkItemHandler(StatefulKnowledgeSession ksession) {
//
//		HornetQHTWorkItemHandler humanTaskHandler = new HornetQHTWorkItemHandler(ksession);
//		humanTaskHandler.setIpAddress(localhost);
//		humanTaskHandler.setPort(port);
//		return humanTaskHandler;
//	}

//	public HTTaskClient getTaskClient(String clientId) {
//		clientId = "server";
//		HTTaskClient client = taskClients.get(clientId);
//		if (client == null) {
//			client = HTTaskClient.getClient(clientId);
//			client.connect(localhost, port);
//			taskClients.put(clientId, client);
//		}
//		return client;
//	}
//	
	
	public TaskClient createTaskClient(String clientId){
		TaskClient client = new TaskClient(new MinaTaskClientConnector(clientId,
		new MinaTaskClientHandler(SystemEventListenerFactory.getSystemEventListener())));
		client.connect("127.0.0.1", port);
		return client;
	}
	
	public TaskClient getTaskClient(String clientId) {
		TaskClient client = taskClients.get(clientId);
		if(client == null ){
			client = new TaskClient(new MinaTaskClientConnector(clientId,
			new MinaTaskClientHandler(SystemEventListenerFactory.getSystemEventListener())));
			client.connect("127.0.0.1", port);
			taskClients.put(clientId, client);
		}
		
		return client;
	}
	
//	public HTTaskService getLocalTaskService(){
//		HTTaskService hts = new HTTaskService(_taskService);
//		return hts;
//	}
	
//	public HTTaskClient createTaskClient(String clientId){
//		
//		
//		
//		HTTaskClient client = HTTaskClient.getClient(clientId);
//		client.connect(localhost, port);
//		
//		taskClients.put(clientId, client);
//		
//		return client;
//	}

//	public HTTaskService getTaskService() {
////		if(ht ==null){
////			ht = new HTTaskService(_taskService);
////		}
//		
//		return new HTTaskService(_taskService);
//	}
	

	public void stop() {
//		if (taskClients != null) {
//			for (String key : taskClients.keySet()) {
//				try {
//					taskClients.get(key).disconnect();
//				} catch (Exception ex) {
//				}
//			}
//			taskClients = null;
//		}

			defaultTaskSession.dispose();
//		try {
//
//			taskServer.stop();
//
//		} catch (Exception ex) {
//		}
//		taskServer = null;
	}

	/**
	 * <p>
	 * <strong>trong>Add a user to task server</strong>
	 * </p>
	 * 
	 * 
	 * @param userId
	 */
	public void addParticipateUser(String userId) {

		if (!defaultTaskSession.getTaskPersistenceManager().userExists(userId)) {
			defaultTaskSession.addUser(new User(userId));
		}
	}

	public void addParticipateGroup(String groupId) {

		if (!defaultTaskSession.getTaskPersistenceManager().groupExists(groupId)) {
			defaultTaskSession.addGroup(new Group(groupId));
		}
	}

	public void removeParticipateUser(String userId) {

		TaskPersistenceManager manager = defaultTaskSession.getTaskPersistenceManager();
		User user = (User) manager.findEntity(User.class, userId);
		if (user != null) {
			manager.beginTransaction();
			manager.deleteEntity(user);
			manager.endTransaction(true);
		}
	}

	public void removeParticipateGroup(String groupId) {

		TaskPersistenceManager manager = defaultTaskSession.getTaskPersistenceManager();
		Object group = manager.findEntity(Group.class, groupId);
		if (group != null) {
			manager.beginTransaction();
			manager.deleteEntity(group);
			manager.endTransaction(true);
		}
	}

//	public void initUser() {
//
//		defaultTaskSession.addUser(new User("Administrator"));
//	}


	public TaskServiceSession getDefualtSession() {

		return defaultTaskSession;
	}


}
