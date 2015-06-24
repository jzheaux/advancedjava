package com.joshcummings.jpa.order;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.joshcummings.jpa.profile.Profile;

@Entity

/**
 * ACK! Order is a keyword in SQL, so I need to specify a different name for it so I don't
 * have a conflict.
 * 
 * @author jzheaux
 *
 */
@Table(name="USER_ORDER")
public class Order {
	@Id
	@GeneratedValue
	private Long id;
	
	@ManyToOne
	@JoinColumn(name="PROFILE_ID")
	private Profile orderer;
	
	@Column
	private BigDecimal shipping;
	
	@Column(name="CREATED_DATE")
	private Instant createdDate = Instant.now();
	
	@Column(name="UPDATED_DATE")
	private Instant updatedDate = Instant.now();
	
	@OneToMany(fetch=FetchType.EAGER, cascade={CascadeType.ALL}, orphanRemoval=true, mappedBy="order")
	private Set<OrderItem> items = new HashSet<OrderItem>();
	
	public Order() {}
	
	public Order(Profile orderer, BigDecimal shipping) {
		this.orderer = orderer;
		this.shipping = shipping;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Profile getOrderer() {
		return orderer;
	}

	public void setOrderer(Profile orderer) {
		this.orderer = orderer;
	}

	public BigDecimal getShipping() {
		return shipping;
	}

	public void setShipping(BigDecimal shipping) {
		this.shipping = shipping;
	}

	public Instant getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Instant createdDate) {
		this.createdDate = createdDate;
	}

	public Instant getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(Instant updatedDate) {
		this.updatedDate = updatedDate;
	}
	
	/**
	 * Don't give direct access to relationships!
	 * 
	 * @param i
	 * @param qty
	 */
	public void addItem(Item i, Long qty) {
		OrderItem oi = new OrderItem(this, i, qty);
		items.add(oi);
	}
	
	public void removeItem(Item i) {
		items.stream()
			.filter((oi) -> oi.getItem().equals(i))
			.findAny().ifPresent(items::remove);
	}
	
	public Set<OrderItem> getItems() {
		return Collections.unmodifiableSet(items);
	}
}
