package edu.uga.dawgtrades.persist.impl;

import java.sql.Date;
import java.sql.ResultSet;
import java.util.Iterator;
import java.util.NoSuchElementException;

import edu.uga.dawgtrades.DTException;
import edu.uga.dawgtrades.model.Membership;
import edu.uga.dawgtrades.model.ObjectModel;

public class MembershipIterator implements Iterator<Membership> {
	private ResultSet rs = null;
	private ObjectModel objectModel = null;
	private boolean more = false;
	
	public MembershipIterator(ResultSet r, ObjectModel objModel)
		throws DTException {
		this. rs = r;
		this.objectModel = objModel;
		try {
			more = rs.next();
		} catch ( Exception e ) {
			throw new DTException( "MembershipIterator: Cannot create Membership iterator; root cause: " + e );
		}
	}

	@Override
	public boolean hasNext() {
		return more;
	}

	@Override
	public Membership next() {
		long membershipId;
		float price;
		Date date;
		
		if( more ) {
			try {
				membershipId = rs.getLong( 1 );
				price = rs.getFloat( 2 );
				date = rs.getDate( 3 );
				
				more = rs.next();
			} catch ( Exception e ) {
				throw new NoSuchElementException( "MembershipIterator: No next Membership object; root cause: " + e );
			}
			
			Membership membership = null;
			try {
				membership = objectModel.createMembership(price, date);
			} catch (DTException e) {
				e.printStackTrace();
				System.out.println( e );
			}
			membership.setId( membershipId );
			
			return membership;
		} else {
			throw new NoSuchElementException( "MembershipIterator: No next Membership object" );
		}
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}
}
