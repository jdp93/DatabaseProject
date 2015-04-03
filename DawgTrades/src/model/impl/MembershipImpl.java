/*
* Author: NeelRana
*/

package edu.uga.dawgtrades.model.impl;
import java.util.Date;

import edu.uga.dawgtrades.model.Attribute;
import edu.uga.dawgtrades.model.Membership;

public class MembershipImpl extends PersistableImpl implements Membership {
	
	private static Membership membership;
	private static float price;
	private static Date date;
	
	private MembershipImpl(){
		super(-1);
	}
	
	/******************************
	** public getters and setters
	*******************************/
	
	public static Membership getMembership(){
		if (membership == null)
			membership = new MembershipImpl();
		return membership;
	}
	
	public static Membership getMembership(float price, Date date){
		if (membership == null)
			membership = new MembershipImpl();
		MembershipImpl.price = price;
		MembershipImpl.date = date;
		return membership;
	}
	
	@Override
	public float getPrice() {
		return price;
	}
	
	@Override
	public void setPrice(float price) {
		MembershipImpl.price = price;
	}
	
	@Override
	public Date getDate(){
		return date;
	}
	
	@Override
	public String toString(){
        return "Membership[" + getId() + "] " + getPrice() + " " + getDate();
    }
    
	//don't know if memebership even needs an equals() method since it is singleton, but
	//two memberships are equal if their prices and dates are equal
	@Override
    public boolean equals(Object otherMembership){
    	
    	if (otherMembership == null)
    		return false;
    	if (otherMembership instanceof Membership)
    		return (getPrice() == ((Membership) otherMembership).getPrice())
    				&& (getDate().equals(((Membership)otherMembership).getDate()));
    	return false;
    }
}
