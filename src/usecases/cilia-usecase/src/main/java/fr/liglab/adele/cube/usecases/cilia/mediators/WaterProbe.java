package fr.liglab.adele.cube.usecases.cilia.mediators;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import fr.liglab.adele.cilia.Data;
import fr.liglab.adele.cilia.framework.AbstractPullCollector;

public class WaterProbe extends AbstractPullCollector {

	public void delay(long iDelay) {
		super.delay(iDelay);
	}

	public void period(long lperiod) {
		super.period(lperiod);
	}
	
	public void start(){
		super.start();
	}

	public void stop(){
		super.stop();
	}
	/* (non-Javadoc)
	 * @see fr.liglab.adele.cilia.framework.AbstractPullCollector#pullData()
	 */
	@Override
	protected List pullData() throws IOException {
		System.out.println("WaterProbe is called");
		Random rn = new Random();
        long number = rn.nextInt() % 100;
        return Collections.singletonList(new Data(number, "water-probe"));
	}
}
