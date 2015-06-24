package com.joshcummings.jpa.order;

import java.math.BigDecimal;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.joshcummings.jpa.profile.Profile;

public class OrderServiceIT {
	private static EntityManager em;
	private static EntityTransaction t;
	private static OrderService os;
	private Item i1, i2, i3;
	private Profile orderer;
	private Cart cart;
	
	@BeforeClass
	public static void setUp() {
		em = Persistence.createEntityManagerFactory("testPersistenceUnit").createEntityManager(); 
		t = em.getTransaction();
		os = new OrderService(em);
	}
	
	@Before
	public void openTransaction() {
		t.begin();
		
		// need to add a couple of items to database
		i1  = new Item("Toaster", new BigDecimal("12.99"));
		i2  = new Item("Chrome Toaster", new BigDecimal("18.99"));
		i3  = new Item("Silver-Plated Toaster", new BigDecimal("89.99"));
		em.persist(i1);
		em.persist(i2);
		em.persist(i3);
		
		// also a user
		orderer = new Profile("Phyllis", "Diller", "phyllis.diller@bugslife.com");
		em.persist(orderer);
		
		cart = new Cart();
		cart.addItem(i1, 2L);
		cart.addItem(i2, 4L);
		cart.setShipping(new BigDecimal("50.12"));
	}
	
	@After
	public void rollbackTransation() {
		t.rollback();
	}
	
	@Test
	public void testPlaceOrder() {
		Order o = os.placeOrder(orderer, cart);
		o = os.retreive(o.getId());
		
		Assert.assertNotNull(o);
		Assert.assertEquals(2, o.getItems().size());
		Assert.assertSame(orderer, o.getOrderer());
	}
	
	@Test
	public void testReorder() {
		// add this test
	}
	
	@Test
	public void testOrderTotal() {
		Order o = os.placeOrder(orderer, cart);
		
		BigDecimal total = os.getOrderTotal(o.getId());
		Assert.assertEquals(new BigDecimal("152.06"), total.setScale(2, BigDecimal.ROUND_HALF_UP));
	}
}
