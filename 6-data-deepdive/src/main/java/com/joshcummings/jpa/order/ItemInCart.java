package com.joshcummings.jpa.order;

public class ItemInCart {
	private final Item item;
	private final Long qty;
	
	public ItemInCart(Item item, Long qty) {
		this.item = item;
		this.qty = qty;
	}
	
	public Item getItem() {
		return item;
	}
	
	public Long getQty() {
		return qty;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((item == null) ? 0 : item.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ItemInCart other = (ItemInCart) obj;
		if (item == null) {
			if (other.item != null)
				return false;
		} else if (!item.equals(other.item))
			return false;
		return true;
	}
}
