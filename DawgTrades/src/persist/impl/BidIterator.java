package edu.uga.dawgtrades.persist.impl;

import java.sql.ResultSet;
import java.util.Date;
import java.util.Iterator;
import java.util.NoSuchElementException;

import edu.uga.dawgtrades.DTException;
import edu.uga.dawgtrades.model.Auction;
import edu.uga.dawgtrades.model.Bid;
import edu.uga.dawgtrades.model.Item;
import edu.uga.dawgtrades.model.ObjectModel;
import edu.uga.dawgtrades.model.RegisteredUser;

public class BidIterator implements Iterator<Bid> {

	private ResultSet rs = null;
	private ObjectModel modelFactory = null;
	private boolean more = false;
	
	public BidIterator(ResultSet r, ObjectModel objModel) 
		throws DTException {
		this.rs = r;
		this.modelFactory = objModel;
		try {
			more = rs.next();
		} catch ( Exception e ) {
			throw new DTException( "BidIterator: Cannot create an iterator; root cause: " + e );
		}
	}

	@Override
	public boolean hasNext() {
		return more;
	}

	@Override
	public Bid next() {
		long id;
		float amount;
		long auctionId;
		long itemId;
		float minPrice;
		Date expiration;
		long registeredUserId;
		String userName;
		String firstName;
		String lastName;
		String passWord;
		boolean isAdmin;
		String email;
		String phone;
		boolean canText;
		
		Auction auction = null;
		RegisteredUser registeredUser = null;
		
		if( more ) {
			try {
				itemId = rs.getLong( 1 );
				minPrice = rs.getFloat( 2 );
				System.out.println(itemId + " " + minPrice);
				expiration = rs.getDate( 3 );
				
				id = rs.getLong( 4 );
				registeredUserId = rs.getLong( 5 );
				auctionId = rs.getLong( 6 );
				amount = rs.getFloat( 7 );
				
				userName = rs.getString( 8 );
				passWord = rs.getString( 9 );
				firstName = rs.getString( 10 );
				lastName = rs.getString( 11);
				email = rs.getString( 12 );
				phone = rs.getString( 13 );
				canText = rs.getBoolean( 14 );
				isAdmin = rs.getBoolean( 15 );
				
				
				
				more = rs.next();
			} catch ( Exception e ) {
				throw new NoSuchElementException( "BidIterator: No next Bid object; root cause: " + e );
			}
			
			Item item = modelFactory.createItem();
			item.setId( itemId );
			
			try {
				auction = modelFactory.createAuction(item, minPrice, expiration);
				auction.setId( auctionId );
			} catch (DTException e) {
				e.printStackTrace();
				System.out.println( e );
			}
			
			try {
				registeredUser = modelFactory.createRegisteredUser(userName, firstName, lastName, passWord, isAdmin, email, phone, canText);
				registeredUser.setId( registeredUserId );
			} catch (DTException e) {
				e.printStackTrace();
				System.out.println( e );
			}
			
			Bid bid = null;
			try {
				bid = modelFactory.createBid( auction, registeredUser, amount );
				bid.setId( id );
			} catch (DTException e) {
				e.printStackTrace();
				System.out.println( e );
			}
			
			return bid;
			
		} else {
			throw new NoSuchElementException( "BidIterator: No next Bid object" );
		}
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

}
