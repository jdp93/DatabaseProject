package edu.uga.dawgtrades.model.impl;

import edu.uga.dawgtrades.DTException;
import edu.uga.dawgtrades.model.Attribute;
import edu.uga.dawgtrades.model.Category;

/**
 *  @author Seong Wang
 */
public class CategoryImpl
	extends PersistableImpl
	implements Category {

	// Fields
	private String name;
	private long parentId;
	
	// Constructors
	public CategoryImpl() {
		super(-1);
	}
	
	public CategoryImpl(Category parent, String name)
		throws DTException {
		super(-1);
		if (parent != null){
			if ( parent.isPersistent() ) {
				this.parentId = parent.getId();
			}
			else{
				throw new DTException( "The parent category is not persistent.");
			}
		}
		this.name = name;
	}
	
	public String getName() {
		return name;
	}

    public void setName(String name) {
		this.name = name;
	}

	// Methods for Association/Relationship
	public long getParentId() {
		return parentId;
	}

	public void setParentId(long parentId){
		this.parentId = parentId;
	}

	@Override
	public String toString(){
        return "Category[" + getId() + "] " + getName();
    }
    
	//two categories are equal if their names are equal
	@Override
    public boolean equals(Object otherCategory){
    	
    	if (otherCategory == null)
    		return false;
    	if (otherCategory instanceof Category)
    		return getName().equals(((Category) otherCategory).getName());
    	return false;
    }
}
