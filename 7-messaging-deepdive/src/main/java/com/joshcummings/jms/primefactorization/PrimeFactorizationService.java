package com.joshcummings.jms.primefactorization;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

@Service
public class PrimeFactorizationService {
	private final Map<Long, Factorization> primeFactorizations = new HashMap<>();
	
	private final JmsTemplate template;
	
	@Inject
	public PrimeFactorizationService(JmsTemplate template) {
		this.template = template;
	}

	protected Long findFactorCloseToSqrt(Long factorable) {
		for ( int i = (int)Math.sqrt(factorable); i >= 2; i-- ) {
			if ( factorable % i == 0 ) {
				return factorable;
			}
		}
		return 1L;
	}
	
	@JmsListener(destination="com.joshcummings.jms.primefactorization")
	public void factorize(Long toFactorize) {
		/*Factorization f;
		synchronized ( primeFactorizations ) {
			f = primeFactorizations.get(toFactorize);
			if ( f == null ) {
				f = new Factorization(toFactorize);
				primeFactorizations.put(toFactorize, f);
			}
		}
		if ( !f.isComplete() ) {
			Long closeFactor = findFactorCloseToSqrt(toFactorize);
			if ( closeFactor > 1 ) {
				
			} else {
				f.setFactors(closeFactor);
			}
		}*/ // sounds fascinating, do it later
	}
}
