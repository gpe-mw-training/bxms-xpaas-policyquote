package com.redhat.gpte.policyquote.rules;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import com.redhat.gpte.policyquote.model.Driver;
import com.redhat.gpte.policyquote.model.Policy;

public class PolicyQuotePackageStatefulTest {
    
    private static KieContainer kieContainer;
    
    
    @BeforeClass
    public static void setupKieBase() {
        KieServices ks = KieServices.Factory.get();
        kieContainer = ks.newKieClasspathContainer(); 
        kieContainer.getKieBase();
    }
    
    @Test
    public void testSafeYouthNewVehicle() throws Exception {
        KieSession kSession = kieContainer.newKieSession("ksession.stateful");       
        
        Driver driver = new Driver("1");
        driver.setAge(24);
        driver.setNumberOfAccidents(0);
        driver.setNumberOfTickets(1);
        
        Policy policy = new Policy();
        policy.setDriver(driver.getId());
        policy.setPolicyType("AUTO");
        policy.setPrice(0);
        policy.setVehicleYear(new Integer(2004));
        
        kSession.insert(driver);
        kSession.insert(policy);
        kSession.startProcess("com.redhat.gpte.policyquote.ruleflow");
        kSession.fireAllRules();
        kSession.dispose();
        
        Assert.assertEquals(550, policy.getPrice().intValue());
        
    }    

}
