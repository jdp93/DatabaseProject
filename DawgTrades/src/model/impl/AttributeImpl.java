package edu.uga.dawgtrades.model.impl;

import edu.uga.dawgtrades.DTException;
import edu.uga.dawgtrades.model.Attribute;
import edu.uga.dawgtrades.model.AttributeType;
import edu.uga.dawgtrades.model.Item;

public class AttributeImpl extends PersistableImpl implements Attribute {
	
	//private attributes
	private String value;
	private long itemId;
	private long attributeTypeId;
	
	//protected empty constructor
	protected AttributeImpl(){
		super(-1);
	};
	
	//protected constructor setting some values
	protected AttributeImpl(AttributeType attributeType, Item item, String value)
							throws DTException{
		super(-1);
		if (!attributeType.isPersistent())
			throw new DTException("Attribute's AttributeType is not persistent.");
		if (!item.isPersistent())
			throw new DTException("Attribute's Item is not persistent.");
		this.itemId = item.getId();
		this.attributeTypeId = attributeType.getId();
		this.value = value;
	};
	
	//public getters and setters
	@Override
    public String getValue(){
		return value;
	}

	@Override
    public void setValue(String value){
    	this.value = value;
	}
	
	@Override
    public long getItemId(){
		return itemId;
	}
	
    @Override
    public void setItemId( long itemId ){
    	this.itemId = itemId;
    }
    
    @Override
    public long getAttributeType(){
    	return attributeTypeId;
    }
    
    @Override
    public void setAttributeType( long attributeId ){
    	this.attributeTypeId = attributeId;
    }
    
    @Override
    public String toString(){
        return "Attribute[" + getId() + "] " + getValue();
    }
    
    @Override
    public boolean equals(Object otherAttribute){
    	
    	if (otherAttribute == null)
    		return false;
    	if (otherAttribute instanceof Attribute)
    		return getValue().equals(((Attribute) otherAttribute).getValue());
    	return false;
    }
}
