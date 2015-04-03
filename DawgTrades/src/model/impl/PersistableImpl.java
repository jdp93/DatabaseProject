package edu.uga.dawgtrades.model.impl;

import edu.uga.dawgtrades.model.Persistable;

/**
 * 
 * @author Joseph Browning
 *
 */
public abstract class PersistableImpl implements Persistable{

	protected long id;

	protected PersistableImpl(){
		id = -1;
	}
	
	
	protected PersistableImpl(long id){
		this.id = id;
	}
	
	@Override
	public long getId() {
		
		return id;
	}

	@Override
	public void setId(long id){
		this.id = id;
	}

	@Override
	public boolean isPersistent() {
		return id >= 0;
	}
}
