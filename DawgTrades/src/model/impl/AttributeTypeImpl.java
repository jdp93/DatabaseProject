package edu.uga.dawgtrades.model.impl;

import edu.uga.dawgtrades.DTException;
import edu.uga.dawgtrades.model.Attribute;
import edu.uga.dawgtrades.model.AttributeType;
import edu.uga.dawgtrades.model.Category;

public class AttributeTypeImpl extends PersistableImpl implements AttributeType {
	
	//private attributes
	private String name;
	private long categoryId;
	
	//empty constructor
	protected AttributeTypeImpl(){
		super(-1);
	};
	
	//constructor setting some values
	protected AttributeTypeImpl (Category category, String name) throws DTException{
		super(-1);
		if (!category.isPersistent()){
			throw new DTException("The attributetype's category is not persistent.");
		}
		this.name = name;
		this.categoryId = category.getId();
	};
	
	//public getters and setters
	@Override
	public String getName(){
		return name;
	}

	@Override
	public void setName(String name){
		this.name = name;
	}
	
	@Override
	public long getCategoryId(){
		return categoryId;
	}
	
	@Override
	public void setCategoryId( long categoryId ){
		this.categoryId = categoryId;
	}
	
	@Override
	public String toString(){
        return "AttributeType[" + getId() + "] " + getName();
    }
	
	@Override
	public boolean equals(Object otherAttributeType){
    	
    	if (otherAttributeType == null)
    		return false;
    	if (otherAttributeType instanceof AttributeType)
    		return getName().equals(((AttributeType) otherAttributeType).getName());
    	return false;
    }
}
