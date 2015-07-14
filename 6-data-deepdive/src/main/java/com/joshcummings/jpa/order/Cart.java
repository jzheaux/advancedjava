package com.joshcummings.jpa.order;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


public class Cart {
	private volatile Map<Long, ItemInCart> items = new HashMap<>();
	
	private BigDecimal shipping;
	
	public void addItem(Item i, Long qty) {
		ItemInCart iic;
		synchronized ( items ) {
			iic = items.get(i.getId());
		}
		if ( iic == null ) {
			iic = new ItemInCart(i, qty);
		} else {
			iic = new ItemInCart(i, iic.getQty() + qty);
		}

		synchronized ( items ) {
			items.put(i.getId(), iic);
		}
	}
	
	public void removeItem(Item i) {
		items.remove(i.getId());
	}
	
	public Collection<ItemInCart> getItems() {
		return items.values();
	}

	public BigDecimal getShipping() {
		return shipping;
	}

	public void setShipping(BigDecimal shipping) {
		this.shipping = shipping;
	}
}
