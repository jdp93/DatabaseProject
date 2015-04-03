package edu.uga.dawgtrades.persist.impl;

import java.sql.ResultSet;
import java.util.Iterator;
import java.util.NoSuchElementException;

import edu.uga.dawgtrades.DTException;
import edu.uga.dawgtrades.model.ObjectModel;
import edu.uga.dawgtrades.model.RegisteredUser;

public class RegisteredUserIterator implements Iterator<RegisteredUser> {
	private ResultSet rs = null;
	private ObjectModel objectModel = null;
	private boolean more = false;
	
	public RegisteredUserIterator(ResultSet rs, ObjectModel objModel)
		throws DTException {
		this.rs = rs;
		this.objectModel = objModel;
		try {
			more = rs.next();
		} catch( Exception e ) {
			throw new DTException( "RegisteredUserIterator: Cannot create an iterator; root cause: " + e );
		}
	}

	@Override
	public boolean hasNext() {
		return more;
	}

	@Override
	public RegisteredUser next() {
		long userId;
		String username;
		String firstname;
		String lastname;
		String password;
		String email;
		String phone;
		boolean isAdmin;
		boolean canText;
		RegisteredUser user = null;
		
		if( more ) {
			try {
				userId = rs.getLong( 1 );
				username = rs.getString( 2 );
				password = rs.getString( 3 );
				firstname = rs.getString( 4 );
				lastname = rs.getString( 5 );
				email = rs.getString( 6 );
				phone = rs.getString( 7 );
				canText = rs.getBoolean( 8 );
				isAdmin = rs.getBoolean( 9 );
				
				
				more = rs.next();
			} catch ( Exception e ) {
				throw new NoSuchElementException( "RegisteredUserIterator: No next RegisteredUser object " + e );
			}
			
			try {
				user = objectModel.createRegisteredUser(username, firstname, lastname, password, isAdmin, email, phone, canText);
				user.setId( userId );
			} catch (DTException e) {
				//
			}
			
			return user;
		} else {
			throw new NoSuchElementException( "RegisteredUserIterator: No next RegisteredUser object" );
		}
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

}
