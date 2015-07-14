package com.joshcummings.jms.borrower;

import java.math.BigDecimal;

import javax.inject.Inject;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;


@Service
public class LendingService {

	private final JmsTemplate jmsTemplate;
	private volatile BigDecimal money = new BigDecimal("12356.34");
	
	@Inject
	public LendingService(JmsTemplate jmsTemplate) {
		this.jmsTemplate = jmsTemplate;
	}
	
    @JmsListener(destination = "com.joshcummings.jms.borrower.request",
    		concurrency="6")
    public void moneyRequested(BigDecimal amount) {
    	BigDecimal toLend = amount;
    	synchronized ( money ) {
    		money = money.subtract(amount);
    		if ( money.compareTo(BigDecimal.ZERO) < 0 ) {
    			toLend = amount.add(money);
    			money = BigDecimal.ZERO;
    		}
    	}
    	
    	final BigDecimal lent = toLend;
    	String message = "Yay!";
    	if ( lent.compareTo(BigDecimal.ZERO) <= 0 ) {
	        message = "Insufficient Funds for your"
					+ " request for $" + amount;
    	}
    	LendingResponse response = new LendingResponse(message, lent);
		jmsTemplate.send("com.joshcumings.jms.borrower.response", 
        		(session) ->
        			session.createObjectMessage(response));
    	
    }

    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
}
