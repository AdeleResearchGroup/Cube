package fr.liglab.adele.cube.util.tests;

import fr.liglab.adele.cube.AdministrationService;
import fr.liglab.adele.cube.AutonomicManager;
import fr.liglab.adele.cube.Configuration;
import fr.liglab.adele.cube.autonomicmanager.AutonomicManagerException;
import fr.liglab.adele.cube.autonomicmanager.NotFoundManagedElementException;
import fr.liglab.adele.cube.extensions.ExtensionConfig;
import fr.liglab.adele.cube.extensions.core.CoreExtensionFactory;
import fr.liglab.adele.cube.extensions.core.model.Component;
import fr.liglab.adele.cube.extensions.core.model.Node;
import fr.liglab.adele.cube.metamodel.InvalidNameException;
import fr.liglab.adele.cube.metamodel.PropertyExistException;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * User: debbabi
 * Date: 9/26/13
 * Time: 3:38 PM
 */
public class Test implements Runnable {


    Thread t;

    AdministrationService as;
    int testId;
    int param;

    public Test(AdministrationService as, int testId) {
        this.as = as;
        this.testId = testId;
        t = new Thread(this);
        t.start();
    }

    public Test(AdministrationService as, int testId, int param) {
        this.as = as;
        this.testId = testId;
        this.param = param;
        t = new Thread(this);
        t.start();
    }

    public void run() {
        switch (testId) {
            case 1: {
                Test1.test(as);
            } break;
            case 2: {
                Test2.test(as);
            }case 3: {
                Test3.test(as, param);
            }
        }
    }



}
