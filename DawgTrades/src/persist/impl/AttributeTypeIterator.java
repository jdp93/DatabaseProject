package edu.uga.dawgtrades.persist.impl;

import java.sql.ResultSet;
import java.util.Iterator;
import java.util.NoSuchElementException;

import edu.uga.dawgtrades.DTException;
import edu.uga.dawgtrades.model.AttributeType;
import edu.uga.dawgtrades.model.Category;
import edu.uga.dawgtrades.model.ObjectModel;

public class AttributeTypeIterator 
	implements Iterator<AttributeType> {

	private ResultSet rs = null;
	private ObjectModel objectModel = null;
	private boolean more = false;
	
	public AttributeTypeIterator(ResultSet r, ObjectModel objModel)
		throws DTException {
		this.rs = r;
		this.objectModel = objModel;
		try {
			more = rs.next();
		} catch ( Exception e ) {
			throw new DTException( "AttributeTypeIterator: Cannot create AttributeType iterator; Root cause: " + e );
		}
	}

	@Override
	public boolean hasNext() {
		return more;
	}

	@Override
	public AttributeType next() {
		long id;
		long categoryId;
		String name;
		AttributeType attrType = null;
		
		if ( more ) {
			try {
				id = rs.getLong( 1 );
				categoryId = rs.getLong( 2 );
				name = rs.getString( 3 );
				
				more = rs.next();
			} catch ( Exception e ) {
				throw new NoSuchElementException( "AttributeTypeIterator: No next AttributeType object; Root cause: " + e );
			}
			
			Category cat = objectModel.createCategory();
			cat.setId( categoryId );
			
			try {
				attrType = objectModel.createAttributeType( cat, name );
				attrType.setId( id );
				attrType.setCategoryId( categoryId );
			} catch (DTException e ) {
				e.printStackTrace();
			}
			
			return attrType;
		} else {
			throw new NoSuchElementException( "AttributeTypeIterator: No next AttributeType object." );
		}
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

}
