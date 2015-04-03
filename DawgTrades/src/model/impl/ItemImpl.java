/*
* Author: NeelRana
*/

package edu.uga.dawgtrades.model.impl;

import edu.uga.dawgtrades.DTException;
import edu.uga.dawgtrades.model.Attribute;
import edu.uga.dawgtrades.model.Category;
import edu.uga.dawgtrades.model.Item;
import edu.uga.dawgtrades.model.RegisteredUser;

public class ItemImpl extends PersistableImpl implements Item {
	
	//Private attributes
	private String name;
	private String description;
	private long ownerId;
	private long categoryId;
	
	//Protected empty constructor
	protected ItemImpl(){
		super(-1);
	}
	
	//Protected constructor
	protected ItemImpl(Category category, RegisteredUser user,
			String name, String description) throws DTException{
		super(-1);
		if (!category.isPersistent()){
			throw new DTException("Item's Category is not persistent.");
		}
		if (!user.isPersistent()){
			throw new DTException("Item's RegisteredUser is not persistent.");
		}
		this.categoryId = category.getId();
		this.ownerId = user.getId();
		this.name = name;
		this.description = description;
	}
	
	/******************************
	** public getters and setters
	*******************************/
	
	@Override
	public String getName(){
		return name;
	}
	
	@Override
	public void setName(String name){
		this.name=name;
	}
	
	@Override
	public String getDescription(){
		return description;
	}
	
	@Override
	public void setDescription(String description){
		this.description = description;
	}
	
	@Override
	public long getOwnerId(){
		return ownerId;
	}
	
	@Override
	public void setOwnerId(long ownerId){
		this.ownerId = ownerId;
	}
	
	@Override
	public long getCategoryId(){
		return categoryId;
	}
	
	@Override
	public void setCategoryId(long categoryId){
		this.categoryId = categoryId;
	}
	
	@Override
	public String toString(){
        return "Item[" + getId() + "] " + getName() + " " + getDescription();
    }
    
	//two items are equal if their name and owners are equal
	@Override
    public boolean equals(Object otherItem){
    	
    	if (otherItem == null)
    		return false;
    	if (otherItem instanceof Item)
    		return (getName().equals(((Item) otherItem).getName()))
    				&& (getOwnerId() == ((Item)otherItem).getOwnerId());
    	return false;
    }
	
}