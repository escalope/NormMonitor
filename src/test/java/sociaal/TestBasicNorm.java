/*
    Copyright (C) 2015 Jorge Gomez Sanz

    This file is part of NormMonitor a monitoring solution system for PHAT infrastructure, 
    and availabe at https://github.com/escalope/NormMonitor. 

    The NormMonitor is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 3 of the License, or
    (at your option) any later version.

    INGENIAS Agent Framework is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with NormMonitor; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

 */
package sociaal;

import static org.junit.Assert.*;
import jade.core.AID;
import jade.domain.introspection.Event;
import jade.lang.acl.ACLMessage;
import jade.wrapper.StaleProxyException;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.cdi.KSession;
import org.kie.api.event.process.ProcessEventListener;
import org.kie.api.event.process.ProcessNodeLeftEvent;
import org.kie.api.event.process.ProcessNodeTriggeredEvent;
import org.kie.api.event.process.ProcessStartedEvent;
import org.kie.api.event.process.ProcessVariableChangedEvent;
import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.event.rule.AgendaGroupPoppedEvent;
import org.kie.api.event.rule.AgendaGroupPushedEvent;
import org.kie.api.event.rule.BeforeMatchFiredEvent;
import org.kie.api.event.rule.DebugAgendaEventListener;
import org.kie.api.event.rule.DebugRuleRuntimeEventListener;
import org.kie.api.event.rule.MatchCancelledEvent;
import org.kie.api.event.rule.MatchCreatedEvent;
import org.kie.api.event.rule.ObjectDeletedEvent;
import org.kie.api.event.rule.ObjectInsertedEvent;
import org.kie.api.event.rule.ObjectUpdatedEvent;
import org.kie.api.event.rule.RuleFlowGroupActivatedEvent;
import org.kie.api.event.rule.RuleFlowGroupDeactivatedEvent;
import org.kie.api.event.rule.RuleRuntimeEventListener;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;
import org.testng.Assert;

import com.jme3.math.Vector3f;

import phat.agents.AgentPHATEvent;
import phat.body.BodyUtils.BodyPosture;
import phat.world.MonitorEventQueue;
import phat.world.MonitorEventQueueImp;
import phat.world.PHATCalendar;
import phat.world.RemotePHATEvent;
import sociaal.ontology.Action;
import sociaal.ontology.ActionPerformed;
import sociaal.ontology.AgentPartner;
import sociaal.ontology.Assisting;
import sociaal.ontology.CurrentTime;
import sociaal.ontology.GoingToFail;
import sociaal.ontology.OnTheGround;
import sociaal.ontology.Partner;

public class TestBasicNorm {

	static MonitorEventQueueImp meq=new MonitorEventQueueImp();

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		meq.startServer(MonitorEventQueue.DefaultName);
	}

	public void pause(long millis){
		try {
			Thread.currentThread().sleep(millis);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	@Test
	public void testNorms() throws StaleProxyException, IOException{
		
		System.err.println(new Date().getTime());
		System.err.println(new PHATCalendar().getTimeInMillis());
		
		KieSession ksession = getSession("src/test/resources/Norms.drl");
		//ksession.addEventListener( new DebugRuleRuntimeEventListener());

		AgentPHATEvent ape=new AgentPHATEvent("patient",new Vector3f(0f,0f,0f),new PHATCalendar(),BodyPosture.Falling,"walking");
		ksession.insert(ape);
		FactHandle timeHandle = ksession.insert(new CurrentTime(new Date().getTime()));
		printWorkingMemory(ksession);
		ksession.fireAllRules();
		printWorkingMemory(ksession);
		pause(2000);
		ksession.update(timeHandle, new CurrentTime(new Date().getTime()));
		ksession.fireAllRules();
		printWorkingMemory(ksession);
		pause(2000);
		ksession.update(timeHandle, new CurrentTime(new Date().getTime()));
		ksession.fireAllRules();
		printWorkingMemory(ksession);
	}

	@Test
	public void testToleratedFailure() throws StaleProxyException, IOException{
		
		System.err.println(new Date().getTime());
		System.err.println(new PHATCalendar().getTimeInMillis());
		
		
		KieSession ksession = getSession("src/test/resources/Norms.drl");
			ksession.insert(new Assisting("AndroidTVRemote1","E3Patient","TV"));
		ksession.fireAllRules();
		checkInstancesCount(ksession,ToleratedFailure.class,0);
		checkInstancesCount(ksession,Action.class,0);

		ACLMessage acl=new ACLMessage(ACLMessage.INFORM);
		acl.setSender(new AID("AndroidTVRemote1@localhost",true));
		acl.setContent("C1");
		FactHandle aclMessageHandler = ksession.insert(acl);
		ksession.fireAllRules();
		checkInstancesCount(ksession,Action.class,1);
		checkInstancesCount(ksession,ToleratedFailure.class,0);

		AgentPHATEvent ape=new AgentPHATEvent("E3Patient",new Vector3f(0f,0f,0f),new PHATCalendar(),BodyPosture.Sitting,"sitting"+".");
		ape.setScope("TV");
		ape.setFailure(true);
		FactHandle apeHandler = ksession.insert(ape);
		ksession.fireAllRules();
		checkInstancesCount(ksession,Action.class,1);
		checkInstancesCount(ksession,GoingToFail.class,1);

		ksession.delete(aclMessageHandler);
		ksession.fireAllRules();
		checkInstancesCount(ksession,Action.class,0);
		checkInstancesCount(ksession,ToleratedFailure.class,1);

		acl=new ACLMessage(ACLMessage.INFORM);
		acl.setSender(new AID("AndroidTVRemote1@localhost",true));
		acl.setContent("C1");
		aclMessageHandler = ksession.insert(acl);
		ksession.fireAllRules();
		checkInstancesCount(ksession,Action.class,1);
		checkInstancesCount(ksession,ToleratedFailure.class,0);		

		ksession.delete(aclMessageHandler);
		ksession.fireAllRules();
		checkInstancesCount(ksession,Action.class,0);
		checkInstancesCount(ksession,ToleratedFailure.class,1);

		ksession.delete(apeHandler);

		
		ksession.fireAllRules();
		
		checkInstancesCount(ksession,Action.class,0);
		checkInstancesCount(ksession,GoingToFail.class,0);
		checkInstancesCount(ksession,ToleratedFailure.class,0);
	}

	private void checkInstancesCount(KieSession ksession, Class controlledClass, int expectedInstancesCount) {
		Assert.assertTrue(countInstanceTypes(ksession, controlledClass)==expectedInstancesCount, 
				"There should be "+expectedInstancesCount+" "+controlledClass.getName()+" instance and there are "+countInstanceTypes(ksession, controlledClass)+"."+
						"Working memory:\n"+getWorkingMemory(ksession));
	}
	
	

	@Test
	public void testFallingGroundFactCreation() throws StaleProxyException, IOException{
	
		System.err.println(new Date().getTime());
		System.err.println(new PHATCalendar().getTimeInMillis());
		KieSession ksession = getSession("src/test/resources/Norms.drl");
		AgentPHATEvent ape=new AgentPHATEvent("patient",new Vector3f(0f,0f,0f),new PHATCalendar(),BodyPosture.Sitting,"walking"+".");
		ksession.insert(ape);
		ksession.fireAllRules();
		Assert.assertTrue(countInstanceTypes(ksession, OnTheGround.class)==0, 
				"There should be no ontheground instance and there are "+countInstanceTypes(ksession, OnTheGround.class)+".");
		ape=new AgentPHATEvent("patient",new Vector3f(0f,0f,0f),new PHATCalendar(),BodyPosture.Falling,"walking"+".");
		ksession.insert(ape);
		ksession.fireAllRules();
		Assert.assertTrue(countInstanceTypes(ksession, OnTheGround.class)==1, 
				"There should be one ontheground instance and there are "+countInstanceTypes(ksession, OnTheGround.class)+".");
		ape=new AgentPHATEvent("patient",new Vector3f(0f,0f,0f),new PHATCalendar(),BodyPosture.Falling,"walking"+".");
		ksession.insert(ape);
		ksession.fireAllRules();
		Assert.assertTrue(countInstanceTypes(ksession, OnTheGround.class)==1, 
				"There should be one ontheground instance and there are "+countInstanceTypes(ksession, OnTheGround.class)+".");
		ape=new AgentPHATEvent("patient",new Vector3f(0f,0f,0f),new PHATCalendar(),BodyPosture.Standing,"walking"+".");
		ksession.insert(ape);
		ksession.fireAllRules();
		Assert.assertTrue(countInstanceTypes(ksession, OnTheGround.class)==0, 
				"There should be no ontheground instance and there are "+countInstanceTypes(ksession, OnTheGround.class)+".");

	}

	@Test
	public void testTimeModification() throws StaleProxyException, IOException{
		
		System.err.println(new Date().getTime());
		System.err.println(new PHATCalendar().getTimeInMillis());
		KieSession ksession = getSession("src/test/resources/Norms.drl");

		RemotePHATEvent ape=new AgentPHATEvent("patient",new Vector3f(0f,0f,0f),new PHATCalendar(),BodyPosture.Falling,"walking"+".");
		ksession.insert(ape);
		ksession.insert(ksession.getSessionClock());
		ksession.fireAllRules();
		Assert.assertTrue(countInstanceTypes(ksession, Partner.class)==2, 
				"There should be two partner instance only. There are "+countInstanceTypes(ksession, Partner.class)+".");
		Assert.assertTrue(countInstanceTypes(ksession, OnTheGround.class)==1, 
				"There should be one ontheground instance only. There are "+countInstanceTypes(ksession, OnTheGround.class)+".");

		FactHandle timeHandle = ksession.insert(new CurrentTime(System.currentTimeMillis()+10000));
		ape=new AgentPHATEvent("patient",new Vector3f(0f,0f,0f),new PHATCalendar(),BodyPosture.Falling,"walking");
		ksession.insert(ape);
		ksession.fireAllRules();
		Assert.assertTrue(countInstanceTypes(ksession, Partner.class)==2, 
				"There should be two partner instance only. There are "+countInstanceTypes(ksession, Partner.class)+".");
		Assert.assertTrue(countInstanceTypes(ksession, PunishOnTheGround.class)==1, 
				"There should be one  PunishOnTheGround instance only. There are "+countInstanceTypes(ksession, PunishOnTheGround.class)+".");
		ksession.update(timeHandle,new CurrentTime(System.currentTimeMillis()+10001));
		ksession.fireAllRules();
	}


	@Test
	public void testInstitutionalFactsCreation() throws StaleProxyException, IOException{
		
		System.err.println(new Date().getTime());
		System.err.println(new PHATCalendar().getTimeInMillis());
		KieSession ksession = getSession("src/test/resources/Norms.drl");
		
		RemotePHATEvent ape=new AgentPHATEvent("patient",new Vector3f(0f,0f,0f),new PHATCalendar(),BodyPosture.Falling,"walking"+".");
		ksession.insert(ape);
		ksession.fireAllRules();
		Assert.assertTrue(countInstanceTypes(ksession, Partner.class)==2, 
				"There should be two partner instance only. There are "+countInstanceTypes(ksession, Partner.class)+".");
		Assert.assertTrue(countInstanceTypes(ksession, OnTheGround.class)==1, 
				"There should be one ontheground instance only. There are "+countInstanceTypes(ksession, OnTheGround.class)+".");

		ksession.insert(new CurrentTime(System.currentTimeMillis()+10000));
		ape=new AgentPHATEvent("patient",new Vector3f(0f,0f,0f),new PHATCalendar(),BodyPosture.Falling,"walking");
		ksession.insert(ape);
		ksession.fireAllRules();
		Assert.assertTrue(countInstanceTypes(ksession, Partner.class)==2, 
				"There should be two partner instance only. There are "+countInstanceTypes(ksession, Partner.class)+".");
		Assert.assertTrue(countInstanceTypes(ksession, PunishOnTheGround.class)==1, 
				"There should be one  PunishOnTheGround instance only. There are "+countInstanceTypes(ksession, PunishOnTheGround.class)+".");

		ape=new AgentPHATEvent("patient",new Vector3f(0f,0f,0f),new PHATCalendar(),BodyPosture.Standing,"walking");
		ksession.insert(ape);
		ksession.fireAllRules();
		Assert.assertTrue(countInstanceTypes(ksession, OnTheGround.class)==0, 
				"There should none ontheground instance only. There are "+countInstanceTypes(ksession, OnTheGround.class)+".");

		ape=new AgentPHATEvent("patient1",new Vector3f(0f,0f,0f),new PHATCalendar(),BodyPosture.Falling,"walking");
		ksession.insert(ape);
		ksession.fireAllRules();
		Assert.assertTrue(countInstanceTypes(ksession, Partner.class)==3, 
				"There should be three partner instance only. There are "+countInstanceTypes(ksession, Partner.class)+".");


		ACLMessage me=new ACLMessage(ACLMessage.INFORM);
		me.setSender(new AID("sample@localhost",true));
		me.setContent("");
		ksession.insert(me);
		ksession.fireAllRules();
		Assert.assertTrue(countInstanceTypes(ksession, AgentPartner.class)==1, 
				"There should be one AgentPartner instance only. There are "+countInstanceTypes(ksession, AgentPartner.class)+".");

		me=new ACLMessage(ACLMessage.INFORM);
		me.setSender(new AID("sample@localhost",true));
		me.setContent("");
		ksession.insert(me);
		ksession.fireAllRules();
		Assert.assertTrue(countInstanceTypes(ksession, AgentPartner.class)==1, 
				"There should be one AgentPartner instance only. There are "+countInstanceTypes(ksession, AgentPartner.class)+".");

		me=new ACLMessage(ACLMessage.INFORM);
		me.setSender(new AID("sample1@localhost",true));
		me.setContent("");
		ksession.insert(me);
		ksession.fireAllRules();
		Assert.assertTrue(countInstanceTypes(ksession, AgentPartner.class)==2, 
				"There should be two AgentPartner instances only. There are "+countInstanceTypes(ksession, AgentPartner.class)+".");
		//pause(30000);
	}

	private int countInstanceTypes(KieSession ksession, Class instanceClass){
		int count=0;
		for (Object obj:ksession.getObjects())
			if (obj.getClass().equals(instanceClass))
				count++;
		return count;
	}

	private static void printWorkingMemory(KieSession ksession) {
		System.out.println("--------BEGIN Working Memory-------------");
		for (Object obj:ksession.getObjects())
			System.out.println(obj);
		System.out.println("--------END Working Memory-------------");
	}

	private static String getWorkingMemory(KieSession ksession) {
		String result=("--------BEGIN Working Memory-------------\n");
		for (Object obj:ksession.getObjects())
			result=result+obj.toString()+"\n";
		result=result+("--------END Working Memory-------------\n");
		return result;
	}

	private KieSession getSession(String ruleFile){
		KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
		
		KieServices kServices = KieServices.Factory.get();
		FileInputStream fis=null;
		try {
			fis = new FileInputStream(ruleFile);
			kbuilder.add(ResourceFactory.newInputStreamResource(fis), ResourceType.DRL);
			System.out.println( "Loading file: " + ruleFile );
			
			return kbuilder.newKnowledgeBase().newKieSession();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}



	public static void main(String[] args){
		System.out.println(Calendar.getInstance().getTimeInMillis());
	}

}
