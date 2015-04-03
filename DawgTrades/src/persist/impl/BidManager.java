package edu.uga.dawgtrades.persist.impl;

import java.sql.Connection;
import java.util.Iterator;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.mysql.jdbc.PreparedStatement;

import edu.uga.dawgtrades.DTException;
import edu.uga.dawgtrades.model.Bid;
import edu.uga.dawgtrades.model.ObjectModel;

public class BidManager {
	// Fields
	private ObjectModel objModel = null;
	private Connection conn = null;

	// Constructor
	public BidManager( Connection conn, ObjectModel objectModel) {
		this.conn = conn;
		this.objModel = objectModel;
	}

	public void save(Bid bid) 
		throws DTException {
		String insertBidSql = "insert into Bid ( UserId, AuctionId, Amount ) values ( ?, ?, ? )";
		PreparedStatement stmt = null;
		int inscnt;
		long id;
		
		if( bid.getAuction() == null || bid.getRegisteredUser() == null ) {
			throw new DTException( "BidManager.save: Attempting to save a Bid with no Auction or RegisteredUser defined." );
		}
		if( !bid.getAuction().isPersistent() || !bid.getRegisteredUser().isPersistent() ) {
			throw new DTException( "BidManager.save: Attempting to save a Bid where either Auction or RegisteredUser are not persistent." );
		}
		
		try {
			stmt = (PreparedStatement) conn.prepareStatement( insertBidSql );
			
			stmt.setLong( 1, bid.getRegisteredUser().getId() );
			stmt.setLong( 2, bid.getAuction().getId() );
			
			if( bid.getAmount() >= 0 ) {
				stmt.setFloat( 3, bid.getAmount() );
			} else {
				throw new DTException( "BidManager.save: Can't save a Bid: Amount is negative." );
			}
			
			inscnt = stmt.executeUpdate();
			
			if( inscnt >= 1 ) {
				String sql = "select last_insert_id()";
				if( stmt.execute( sql ) ) {
					ResultSet rs = stmt.getResultSet();
					while( rs.next() ) {
						id = rs.getLong( 1 );
						if( id >= 0) {
							bid.setId( id );
						}
					}
				}
			} else {
				throw new DTException( "BidManager.save: Failed to save a Bid." );
			}
		} catch ( SQLException e ) {
			e.printStackTrace();
			throw new DTException( "BidManager.save: Failed to a Bid: " + e );
		}
	}

	public Iterator<Bid> restore(Bid bid) 
		throws DTException {
		String selectAuctionSql = 
				"select a.ItemId, a.MinPrice, a.Expiration, " +
				"b.Id, b.UserId, b.AuctionId, b.Amount, " +
				"ru.Username, ru.Password, ru.FirstName, ru.LastName, " +
				"ru.Email, ru.Phone, ru.CanText, ru.IsAdmin " +
				"from Auction a, Bid b, RegisteredUser ru "
				+ "where a.Id = b.AuctionId and b.UserId = ru.Id";
		Statement stmt = null;
		StringBuffer query = new StringBuffer( 100 );
		StringBuffer condition = new StringBuffer( 100 );
		
		if( bid.getAuction() != null && !bid.getAuction().isPersistent() ) {
			throw new DTException( "BidManager.restore: The argument bid includes a non-persistent Auction object" );
		}
		if( bid.getRegisteredUser() != null && ! bid.getRegisteredUser().isPersistent() ) {
			throw new DTException( "BidManager.restore: The argument bid includes a non-persistent RegisteredUser object" );
		}
		
		condition.setLength( 0 );
		
		query.append( selectAuctionSql );
		
		if( bid != null ) {
			if( bid.isPersistent() ) {
				query.append( " where Id = " + bid.getId()  );
			}
			else {
				if( bid.getAuction() != null ) {
					condition.append( " and b.AuctionId = " + bid.getAuction().getId() );
				}
				
				if( bid.getRegisteredUser() != null ) {
					condition.append( " and b.UserId = " + bid.getRegisteredUser().getId() );
				}
				
				if( bid.getAmount() >= 0 ) {
					condition.append( " and b.Amount = " + bid.getAmount() );
				}
				
				if( condition.length() > 0 ) {
					query.append( condition );
				}
			}
		}
		
		try {
			stmt = conn.createStatement();
			
			if( stmt.execute( query.toString() ) ) {
				ResultSet r = stmt.getResultSet();
				return new BidIterator( r, objModel );
			}
		} catch ( Exception e ) {
			throw new DTException( "BidManager.restore: Could not restore persistent Bid object; Root cause: " + e );
		}
		
		// If we reach this point, it is an error
		throw new DTException( "BidManager.restore: Could not restore persistent Bid object." );
	}

	public void delete(Bid bid) 
		throws DTException {
		String deleteBidSql = "delete from Bid where Id = ?";
		PreparedStatement stmt = null;
		int inscnt;
		
		if( !bid.isPersistent() ) {
			return;
		}
		
		try {
			stmt = (PreparedStatement) conn.prepareStatement( deleteBidSql );
			stmt.setLong( 1, bid.getId() );
			inscnt = stmt.executeUpdate();
			
			if( inscnt == 1 ) {
				return;
			} else {
				throw new DTException( "BidManager.delete: Failed to delete a Bid" );
			}
		} catch ( SQLException e) {
			e.printStackTrace();
			throw new DTException( "BidManager.delete: Failed to delete a Bid: " + e );
		}
	}
}
