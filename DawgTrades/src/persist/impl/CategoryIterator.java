package edu.uga.dawgtrades.persist.impl;

import java.sql.ResultSet;
import java.util.Iterator;
import java.util.NoSuchElementException;

import edu.uga.dawgtrades.DTException;
import edu.uga.dawgtrades.model.Category;
import edu.uga.dawgtrades.model.ObjectModel;

public class CategoryIterator implements Iterator<Category> {
	private ResultSet rs = null;
	private ObjectModel objectModel = null;
	private boolean more = false;
	
	public CategoryIterator(ResultSet r, ObjectModel objModel)
		throws DTException {
		this.rs = r;
		this.objectModel = objModel;
		try {
			more = rs.next();
		} catch ( Exception e ) {
			throw new DTException( "CategoryIterator: Cannot create Category iterator; Root cause: " + e );
		}
	}

	@Override
	public boolean hasNext() {
		return more;
	}

	@Override
	public Category next() {
		long id;
		long parentId;
		String name;
		Category category = null;
		
		if( more ) {
			try {
				id = rs.getLong( 1 );
				parentId = rs.getLong( 2 );
				name = rs.getString( 3 );
				
				more = rs.next();
			} catch ( Exception e ) {
				throw new NoSuchElementException( "CategoryIterator: No next Category object; Root cause " + e );
			}
			
			Category parent = objectModel.createCategory();
			parent.setId( parentId );
			
			try {
				category = objectModel.createCategory( parent, name );
				category.setId( id );
				category.setParentId( parentId );
			} catch ( DTException e ) {
				e.printStackTrace();
			}
			
			return category;
		} else {
			throw new NoSuchElementException( "CategoryIterator: No next Category object" );
		}
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

}
