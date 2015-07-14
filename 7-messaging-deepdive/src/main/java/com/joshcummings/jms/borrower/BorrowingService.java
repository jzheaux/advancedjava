package com.joshcummings.jms.borrower;

import java.math.BigDecimal;

import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;

@Service
public class BorrowingService {
	private final JmsTemplate jmsTemplate;
    
	@Inject
	public BorrowingService(JmsTemplate jmsTemplate) {
		this.jmsTemplate = jmsTemplate;
	}

	public void borrow(BigDecimal money) {
		this.jmsTemplate.send("com.joshcummings.jms.borrower.request", new MessageCreator() {
            public Message createMessage(Session session) throws JMSException {
            	System.out.println("Trying to borrow $" + money);
                return session.createObjectMessage(money);
            }
        });
	}
	
	@JmsListener(destination = "com.joshcumings.jms.borrower.response")
    public void moneyLent(LendingResponse response, Session session) {
		if ( response.wasLoanSuccessful() ) {
			System.out.println("Received money: " + response.getAmount());
		} else {
			System.out.println("Did NOT receive money: " + response.getMessage());
		}
	}	
	
	
	
	
	
	
	
	
	
	
	
	
}
