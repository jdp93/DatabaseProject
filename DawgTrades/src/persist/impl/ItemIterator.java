package edu.uga.dawgtrades.persist.impl;

import java.sql.ResultSet;
import java.util.Iterator;
import java.util.NoSuchElementException;

import edu.uga.dawgtrades.DTException;
import edu.uga.dawgtrades.model.Category;
import edu.uga.dawgtrades.model.Item;
import edu.uga.dawgtrades.model.ObjectModel;
import edu.uga.dawgtrades.model.RegisteredUser;

public class ItemIterator implements Iterator<Item> {
	private ResultSet rs = null;
	private ObjectModel objectModel = null;
	private boolean more = false;
	
	public ItemIterator(ResultSet r, ObjectModel objModel)
		throws DTException {
		this.rs = r;
		this.objectModel = objModel;
		try {
			more = rs.next();
		} catch ( Exception e ) {
			throw new DTException( "ItemIterator: Cannot create Item iterator; root cause: " + e );
		}
	}

	@Override
	public boolean hasNext() {
		return more;
	}

	@Override
	public Item next() {
		long id;
		long categoryId;
		long ownerId;
		String name;
		String description;
		if( more ) {
			try {
				id = rs.getLong( 1 );
				ownerId = rs.getLong( 2 );
				categoryId = rs.getLong( 3 );
				name = rs.getString( 4 );
				description = rs.getString( 5 );
				
				more = rs.next();
			} catch ( Exception e ) {
				throw new NoSuchElementException( "ItemIterator: No next Item object; root cause: " + e );
			}
			
			Category category = objectModel.createCategory();
			category.setId( categoryId );
			
			RegisteredUser owner = objectModel.createRegisteredUser();
			owner.setId(ownerId);

			Item item = null;
			try {
				item = objectModel.createItem(category, owner, name, description);
				item.setId( id );
			} catch (DTException e) {
				e.printStackTrace();
				System.out.println( e );
			}
			
			return item;
		} 
		else {
			throw new NoSuchElementException( "ItemIterator: No next Item object" );
		}
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

}
