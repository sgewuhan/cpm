package com.sg.bpm.service;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.EntityManagerFactory;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.ResourceFactory;
import org.drools.io.impl.UrlResource;
import org.drools.persistence.jpa.JPAKnowledgeService;
import org.drools.runtime.Environment;
import org.drools.runtime.EnvironmentName;
import org.drools.runtime.StatefulKnowledgeSession;

import bitronix.tm.TransactionManagerServices;

public class BPMService {

	private Map<String, KnowledgeBase> knowledgeBaseMap = new ConcurrentHashMap<String, KnowledgeBase>();

	private Map<String, StatefulKnowledgeSession> sessionMap = new ConcurrentHashMap<String, StatefulKnowledgeSession>();

	private String guvnorRoot;

	private EntityManagerFactory emf;

	private String localBPMPath;

	public BPMService(String guvnorRoot, String localBPMPath) {

		this.guvnorRoot = guvnorRoot;
		
		this.localBPMPath = localBPMPath;
	}

	public void init(EntityManagerFactory emf) {

		this.emf = emf;
	}

	public KnowledgeBase getKnowledgeBase(String knowledgebaseName) {

		KnowledgeBase kbase = knowledgeBaseMap.get(knowledgebaseName);
		if (kbase == null) {
			KnowledgeBuilder

			kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

			registeResource(kbuilder, knowledgebaseName);

			kbase = kbuilder.newKnowledgeBase();

			knowledgeBaseMap.put(knowledgebaseName, kbase);
		}

		return kbase;

	}

	private void registeResource(KnowledgeBuilder knowledgebuilder, String knowledgebaseName) {

		boolean suc = true;
		try{//尝试从远程注册资源
			UrlResource urlResource = (UrlResource) ResourceFactory.newUrlResource(guvnorRoot + knowledgebaseName + "/LATEST/");
			urlResource.setBasicAuthentication("enabled");
			urlResource.setUsername("admin");
			urlResource.setPassword("admin");
			knowledgebuilder.add(urlResource, ResourceType.PKG);
			System.out.println("加载远程知识库成功");
		}catch(Exception e){
			suc = false;
			System.out.println("从远程知识库获得流程定义失败");
		}
		
		if(!suc){
			System.out.println("尝试从服务器本地目录资源");
			//尝试从服务器本地目录资源
			File root = new File(localBPMPath);
			String path = root + "/" +knowledgebaseName.replace(".", "/");
			File[] files = new File(path).listFiles();
			for(int i=0;i<files.length;i++){
				knowledgebuilder.add(ResourceFactory.newFileResource(files[i]), ResourceType.BPMN2);
			}
			System.out.println("加载本地知识库成功");
		}
		
		
	}
	
	public StatefulKnowledgeSession createSession(String knowledgebaseName) {

		StatefulKnowledgeSession session = sessionMap.get(knowledgebaseName);
		if (session == null) {
			KnowledgeBase kbase = getKnowledgeBase(knowledgebaseName);

			Environment env = KnowledgeBaseFactory.newEnvironment();
			env.set(EnvironmentName.ENTITY_MANAGER_FACTORY, emf);
//			env.set(EnvironmentName.TRANSACTION_MANAGER, TransactionManagerServices.getTransactionManager());
			session = JPAKnowledgeService.newStatefulKnowledgeSession(kbase, null, env);

			sessionMap.put(knowledgebaseName, session);
		}
		return session;
	}

	public StatefulKnowledgeSession getSession(String knowledgebaseName, int sid) {

		StatefulKnowledgeSession session = sessionMap.get(knowledgebaseName);
		if (session == null) {
			KnowledgeBase kbase = getKnowledgeBase(knowledgebaseName);

			Environment env = KnowledgeBaseFactory.newEnvironment();
			env.set(EnvironmentName.ENTITY_MANAGER_FACTORY, emf);
//			env.set(EnvironmentName.TRANSACTION_MANAGER, TransactionManagerServices.getTransactionManager());

			session = JPAKnowledgeService.loadStatefulKnowledgeSession(sid, kbase, null, env);

			if (session == null) {
				return null;
			}
			sessionMap.put(knowledgebaseName, session);
		}
		return session;
	}


	public void stop() {


	}

}
