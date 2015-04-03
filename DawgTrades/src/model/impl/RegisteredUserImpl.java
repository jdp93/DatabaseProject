package edu.uga.dawgtrades.model.impl;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import edu.uga.dawgtrades.model.Attribute;
import edu.uga.dawgtrades.model.ObjectModel;
import edu.uga.dawgtrades.model.RegisteredUser;
import edu.uga.dawgtrades.persist.Persistence;

/**
 * 
 * @author Joseph Browning
 *
 */
public class RegisteredUserImpl extends PersistableImpl implements RegisteredUser {
	
	//private attributes
	private String username;
	private String firstName;
	private String lastName;
	private String password;
	private String email;
	private String phone;
	private boolean canText;
	private boolean isAdmin;
	private Set<Long> reviewerIds;
	private Set<Long> reviewedIds;
	
	
	//protected empty constructor
	protected RegisteredUserImpl(){
		super(-1);
		reviewerIds = new HashSet<Long>(16);
		reviewedIds = new HashSet<Long>(16);
	};
	
	//protected constructor setting some values
	protected RegisteredUserImpl(String username, String firstName, String lastName, String password,
            boolean isAdmin, String email, String phone, boolean canText){
		super(-1);
		this.username = username;
		this.firstName = firstName;
		this.lastName = lastName;
		this.password = password;
		this.email = email;
		this.phone = phone;
		this.canText = canText;
		this.isAdmin = false;
		reviewerIds = new HashSet<Long>(16);
		reviewedIds = new HashSet<Long>(16);
	};

	//public getters and setters go here
	//
	//
	@Override
	public String getName() {
		return username;
	}

	@Override
	public void setName(String name) {
		this.username = name;
	}

	@Override
	public String getFirstName() {
		return firstName;
	}

	@Override
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	@Override
	public String getLastName() {
		return lastName;
	}

	@Override
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public boolean getIsAdmin() {
		return isAdmin;
	}

	@Override
	public void setIsAdmin(boolean isAdmin) {
		this.isAdmin = isAdmin;
	}

	@Override
	public String getEmail() {
		return email;
	}

	@Override
	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public String getPhone() {
		return phone;
	}

	@Override
	public void setPhone(String phone) {
		this.phone = phone;
	}

	@Override
	public boolean getCanText() {
		return canText;	}

	@Override
	public void setCanText(boolean canText) {
		this.canText = canText;
	}
	
	@Override
	public String toString(){
		return "RegisteredUser[" + getId() + "] " + getName() + " " + getFirstName() + " " + getLastName()
				+ " " + getEmail() + " " + getPhone() + " " + getIsAdmin();
	}
	    
	//two registeredusers are equal if their usernames are equal
	@Override
    public boolean equals(Object otherRegisteredUser){
    	
    	if (otherRegisteredUser == null)
    		return false;
    	if (otherRegisteredUser instanceof RegisteredUser)
    		return getName().equals(((RegisteredUser) otherRegisteredUser).getName());
    	return false;
    }
}
