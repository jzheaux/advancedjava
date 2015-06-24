package com.joshcummings.jpa.order;

import java.math.BigDecimal;

import javax.persistence.EntityManager;

import com.joshcummings.jpa.common.EntityManagerFactory;
import com.joshcummings.jpa.profile.Profile;

public class OrderService {
	private final EntityManager em;
	
	public OrderService() {
		em = EntityManagerFactory.getInstance();
	}
	
	public OrderService(EntityManager em) {
		this.em = em;
	}
	
	/**
	 * This demonstrates a simple way to keep from having 
	 * consumers create an Order object directly.
	 * 
	 * @param orderer
	 * @param cart
	 * @return
	 */
	public Order placeOrder(Profile orderer, Cart cart) {
		Order o = new Order(orderer, cart.getShipping());
		cart.getItems().stream()
				.forEach((iic) -> o.addItem(iic.getItem(), iic.getQty()));
		em.persist(o);
		return o;
	}
	
	public Order reorder(Long id) {
		Order o = em.find(Order.class, id);
		Order redo = new Order(o.getOrderer(), o.getShipping());
		o.getItems().stream()
				.forEach((oi) -> redo.addItem(oi.getItem(), oi.getQty()));
		em.persist(o);
		return o;
	}
	
	public Order retreive(Long id) {
		return em.find(Order.class, id);
	}
	
	/**
	 * Where should this method go?
	 * 
	 * It does seem nice here, but it gives us a higher LCOM value, which may be an indication that
	 * there is a better home for it...
	 * 
	 * @param id
	 * @return
	 */
	public BigDecimal getOrderTotal(Long id) {
		BigDecimal total = BigDecimal.ZERO;
		Order o = retreive(id);
		for ( OrderItem oi : o.getItems() ) {
			BigDecimal bQty = new BigDecimal(oi.getQty());
			total = total.add(
					oi.getItem().getPrice().multiply(bQty));
		}
		return total.add(o.getShipping());
	}
}
