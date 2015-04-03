package edu.uga.dawgtrades.persist.impl;

import java.sql.ResultSet;
import java.util.Date;
import java.util.Iterator;
import java.util.NoSuchElementException;

import edu.uga.dawgtrades.DTException;
import edu.uga.dawgtrades.model.Auction;
import edu.uga.dawgtrades.model.Item;
import edu.uga.dawgtrades.model.ObjectModel;

public class AuctionIterator implements Iterator<Auction> {

	private ResultSet rs = null;
	private ObjectModel objectModel = null;
	private boolean more = false;
	
	public AuctionIterator(ResultSet rs, ObjectModel objModel)
		throws DTException {
		this.rs = rs;
		this.objectModel = objModel;
		try {
			more = rs.next();
		} catch ( Exception e ) {
			throw new DTException( "AuctionIterator: Cannot create Auction iterator; Root cause: " + e );
		}
	}

	@Override
	public boolean hasNext() {
		return more;
	}

	@Override
	public Auction next() {
		long id;
		long itemId;
		float minPrice;
		Date expiration;
		Auction auction = null;
		
		if( more ) {
			try {
				id = rs.getLong( 1 );
				itemId = rs.getLong( 2 );
				minPrice = rs.getFloat( 3 );
				expiration = rs.getDate( 4 );
				
				more = rs.next();
			} catch ( Exception e ) {
				throw new NoSuchElementException( "AuctionIterator: No next Auction object; Root cause: " + e );
			}
			
			Item item = objectModel.createItem();
			item.setId( itemId );
			
			try {
				auction = objectModel.createAuction( item, minPrice, expiration );
				auction.setId( id );
				auction.setItemId( itemId );
			} catch ( DTException e ) {
				e.printStackTrace();
			}
			
			return auction;
		} else {
			throw new NoSuchElementException( "AuctionIterator: No next Auction object." );
		}
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

}
