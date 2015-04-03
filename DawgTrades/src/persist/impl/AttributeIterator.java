package edu.uga.dawgtrades.persist.impl;

import java.sql.ResultSet;
import java.util.Iterator;
import java.util.NoSuchElementException;

import edu.uga.dawgtrades.DTException;
import edu.uga.dawgtrades.model.Attribute;
import edu.uga.dawgtrades.model.AttributeType;
import edu.uga.dawgtrades.model.Item;
import edu.uga.dawgtrades.model.ObjectModel;

public class AttributeIterator implements Iterator<Attribute> {

	private ResultSet rs = null;
	private ObjectModel objectModel = null;
	private boolean more = false;
	
	// These two will be used to create a new object
	public AttributeIterator(ResultSet r, ObjectModel objectModel)
		throws DTException {
		this.rs = r;
		this.objectModel = objectModel;
		try {
			more = rs.next();
		} catch ( Exception e ) {	// Just in case...
			throw new DTException( "AttributeIterator: Cannot create Attribute iterator: Root cause: " + e );
		}
	}

	public boolean hasNext() {
		return more;
	}

	public Attribute next() {
		long id;
		long attrTypeId;
		long itemId;
		String value;
		Attribute attr = null;
		
		if( more ) {
			try {
				id = rs.getLong( 1 );
				itemId = rs.getLong( 2 );
				attrTypeId = rs.getLong( 3 );
				value = rs.getString( 4 );
				
				more = rs.next();
			} catch ( Exception e ) {	// Just in case...
				throw new NoSuchElementException( "AttributeIterator: No next Attribute object; Root cause: " + e );
			}
			
			AttributeType attrtype = objectModel.createAttributeType();
			attrtype.setId( attrTypeId );
			Item item = objectModel.createItem();
			item.setId( itemId );
			
			try {
				attr = objectModel.createAttribute( attrtype, item, value);
				attr.setId( id );
				attr.setAttributeType( attrTypeId);
				attr.setItemId( itemId );
			} catch ( DTException e) {
				// Safe to ignore: We explicitly set the persistent id of the AttributeType object and Item object above
			}
			
			return attr;
		} else {
			throw new NoSuchElementException( "AttributeIterator: No next Attribute object" );
		}
	}

	public void remove() {
		throw new UnsupportedOperationException();
	}

}
