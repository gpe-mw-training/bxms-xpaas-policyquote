package com.redhat.gpte.policyquote.rules;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.command.BatchExecutionCommand;
import org.kie.api.command.Command;
import org.kie.api.command.KieCommands;
import org.kie.api.runtime.ExecutionResults;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.StatelessKieSession;

import com.redhat.gpte.policyquote.model.Driver;
import com.redhat.gpte.policyquote.model.Policy;

public class PolicyQuotePackageStatelessTest {
    
    private static KieContainer kieContainer;
    
    private static KieCommands commandsFactory;
    
    @BeforeClass
    public static void setupKieBase() {
        KieServices ks = KieServices.Factory.get();
        kieContainer = ks.newKieClasspathContainer(); 
        kieContainer.getKieBase();
        commandsFactory = KieServices.Factory.get().getCommands();
    }
    
    @Test
    public void testSafeYouthNewVehicle() throws Exception {
        StatelessKieSession kSession = kieContainer.newStatelessKieSession("ksession.stateless");       
        
        Driver driver = new Driver("1");
        driver.setAge(24);
        driver.setNumberOfAccidents(0);
        driver.setNumberOfTickets(1);
        
        Policy policy = new Policy();
        policy.setDriver(driver.getId());
        policy.setPolicyType("AUTO");
        policy.setPrice(0);
        policy.setVehicleYear(new Integer(2004));
        
        List<Command<?>> commands = new ArrayList<Command<?>>();
        BatchExecutionCommand executionCommand = commandsFactory.newBatchExecution(commands);
               
        commands.add(commandsFactory.newInsert(driver, "driver"));
        commands.add(commandsFactory.newInsert(policy, "policy"));
        commands.add(commandsFactory.newStartProcess("com.redhat.gpte.policyquote.ruleflow"));
        
        ExecutionResults results = kSession.execute(executionCommand);    
        Assert.assertNotNull(results);
        
        Policy result = (Policy) results.getValue("policy");
        Assert.assertNotNull(policy);
        
        Assert.assertEquals(550, result.getPrice().intValue());
        
    }    

}
