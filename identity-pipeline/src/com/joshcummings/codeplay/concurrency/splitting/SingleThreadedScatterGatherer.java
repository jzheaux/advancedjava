package com.joshcummings.codeplay.concurrency.splitting;

import com.joshcummings.codeplay.concurrency.Identity;

public class SingleThreadedScatterGatherer implements ScatterGatherer {
	@Override
	public Identity go(Scatterer scatterer, Gatherer gatherer) {
		while ( scatterer.hasNext() && gatherer.needsMore() ) {
			try {
				gatherer.gatherResult(scatterer.next().call());
			} catch ( Exception e ) {
				e.printStackTrace();
				// log and move on to the next result
			}
		}
		return gatherer.getFinalResult();
	}
}
