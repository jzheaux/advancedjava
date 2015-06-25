package com.joshcummings.jms.borrower;

import java.io.File;
import java.math.BigDecimal;
import java.util.Random;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.util.FileSystemUtils;

@SpringBootApplication
@EnableJms
@ComponentScan(basePackages="com.joshcummings.jms.borrower")
public class BorrowerApplication {

    public static void main(String[] args) {
        // Clean out any ActiveMQ data from a previous run
    	FileSystemUtils.deleteRecursively(new File("activemq-data"));

        // Launch the application
        ConfigurableApplicationContext context = SpringApplication.run(BorrowerApplication.class, args);

        BorrowingService bs = context.getBean(BorrowingService.class);
        Random r = new Random();
        for ( int i = 0; i < 100; i++ ) {
        	bs.borrow(new BigDecimal(r.nextInt(1000)));
        }
        
        /*JmsTemplate jmsTemplate = context.getBean(JmsTemplate.class);
        System.out.println("Sending a new message.");
        jmsTemplate.send("mailbox-destination", messageCreator);*/
    }

}
