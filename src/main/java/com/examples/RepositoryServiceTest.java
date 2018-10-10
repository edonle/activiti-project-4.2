package com.examples;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.HashMap;
import java.util.Map;

import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.test.ActivitiRule;
import org.junit.Rule;
import org.junit.Test;

public class RepositoryServiceTest extends AbstractTest {
	
	@Rule
	public ActivitiRule activitiRule = new ActivitiRule("activiti.cfg-mem.xml");

	@Test
	public void deleteDeployment() {
		
		RepositoryService repositoryService = activitiRule.getRepositoryService();
		
		//Deploys new process definition
		String deploymentID = repositoryService.createDeployment().addClasspathResource("bookorder.bpmn20.xml")
				.deploy().getId();
		
		//Queries engine for deployments
		Deployment deployment = repositoryService.createDeploymentQuery().singleResult();
		assertNotNull(deployment);
		assertEquals(deploymentID, deployment.getId());
		System.out
				.println("Found deployment " + deployment.getId() + ", deployed at " + deployment.getDeploymentTime());
		
		//Retrieves the deployed
		ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().latestVersion()
				.singleResult();
		assertNotNull(processDefinition);
		assertEquals("bookorder", processDefinition.getKey());
		System.out.println("Found process definition " + processDefinition.getId());
		
		RuntimeService runtimeService = activitiRule.getRuntimeService();
		Map<String, Object> variableMap = new HashMap<String, Object>();
		variableMap.put("isbn", "123456");
		
		//Starts new process instance
		runtimeService.startProcessInstanceByKey("bookorder", variableMap);
		ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().singleResult();
		assertNotNull(processInstance);
		assertEquals(processDefinition.getId(), processInstance.getProcessDefinitionId());
		
		//Deletes process definition and instances
		repositoryService.deleteDeployment(deploymentID, true);
		deployment = repositoryService.createDeploymentQuery().singleResult();
		assertNull(deployment);
		processDefinition = repositoryService.createProcessDefinitionQuery().singleResult();
		assertNull(processDefinition);
		processInstance = runtimeService.createProcessInstanceQuery().singleResult();
		assertNull(processInstance);
	}
}