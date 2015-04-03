package edu.uga.dawgtrades.persist.impl;

import java.sql.Connection;
import java.util.Iterator;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import com.mysql.jdbc.PreparedStatement;

import edu.uga.dawgtrades.DTException;
import edu.uga.dawgtrades.model.Auction;
import edu.uga.dawgtrades.model.Bid;
import edu.uga.dawgtrades.model.ObjectModel;
import edu.uga.dawgtrades.model.RegisteredUser;

public class AuctionManager {
	// Fields
	private ObjectModel objModel = null;
	private Connection conn = null;
	
	// Constructor
	public AuctionManager( Connection conn, ObjectModel objectModel) {
		this.conn = conn;
		this.objModel = objectModel;
	}

	public void save(Auction auction)
		throws DTException {
		String insertAuctionSql = "insert into Auction ( ItemId, MinPrice, Expiration ) values ( ?, ?, ? )";
		String updateAuctionSql = "update Auction set ItemId = ?, MinPrice = ?, Expiration = ? where Id = ?";
		PreparedStatement stmt = null;
		int inscnt;
		long auctionId;
		
		if( auction.getItemId() == -1 ) {
			throw new DTException( "AuctionManager.save: Attempting to save an Auction without an Item." );
		}
		
		try {
			if( !auction.isPersistent() ) {
				stmt = (PreparedStatement) conn.prepareStatement( insertAuctionSql );
			} else {
				stmt = (PreparedStatement) conn.prepareStatement( updateAuctionSql );
			}
			
			if( auction.getItemId() >= 0 ) {
				stmt.setLong( 1, auction.getItemId() );
			} else {
				throw new DTException( "AuctionManager.save: Can't save an Auction; Item is undefined." );
			}
			
			if( auction.getMinPrice() >= 0 ) {
				stmt.setFloat( 2, auction.getMinPrice() );
			} else {
				throw new DTException("AuctionManager.save: MinPrice is null.");
			}
			
			if( auction.getExpiration() != null ) {
				java.util.Date jDate = auction.getExpiration();
				java.sql.Date sDate = new java.sql.Date( jDate.getTime() );
				stmt.setDate( 3, sDate );
			} else {
				//stmt.setNull( 3, java.sql.Types.DATE );
				throw new DTException( "AuctionManager.save: Can't save an Auction; Expiration is undefined." );
			}
			
			if( auction.isPersistent() ) {
				stmt.setLong( 4, auction.getId() );
			}
			
			inscnt = stmt.executeUpdate();
			
			if( !auction.isPersistent() ) {
				if( inscnt >= 1 ) {
					String sql = "select last_insert_id()";
					if( stmt.execute( sql ) ) {
						ResultSet r = stmt.getResultSet();
						while( r.next() ) {
							auctionId = r.getLong( 1 );
							if( auctionId >= 0 ) {
								auction.setId( auctionId );
							}
						}
					}
				} else {
					throw new DTException( "AuctionManager.save: Failed to save an Auction. " );
				}
			} else {
				if( inscnt < 1 ) {
					throw new DTException( "AuctionManager.save: Failed to save an Auction." );
				}
			}
		} catch ( SQLException e ) {
			e.printStackTrace();
			throw new DTException( "AuctionManager.save: Failed to save an Auction: " + e );
		}
	}

	public Iterator<Auction> restore(Auction auction)
		throws DTException {
		String selectAuctionSql = "select Id, ItemId, MinPrice, Expiration from Auction";
		Statement stmt = null;
		StringBuffer query = new StringBuffer(100);
		StringBuffer condition = new StringBuffer(100);
		
		condition.setLength( 0 );
		
		query.append( selectAuctionSql );
		
		if( auction != null ) {
			if( auction.getId() > 0 ) {
				query.append( " where Id = " + auction.getId() );
			}
			else if ( auction.getItemId() > 0 ) {
				query.append( " where ItemId = '" + auction.getItemId() + "'" );
			}
			else {
				if( auction.getMinPrice() > 0 ) {
					condition.append( " MinPrice = '" + auction.getMinPrice() + "'" );
				}
				if( auction.getExpiration() != null ) {
					if( condition.length() > 0 ) {
						condition.append( " and");
					}
					condition.append( " Expiration = '" + auction.getExpiration() + "'" );
				}
				
				if( condition.length() > 0 ) {
					query.append( " where " );
					query.append( condition );
				}
			}
		}
		
		try {
			stmt = conn.createStatement();
			if( stmt.execute( query.toString() ) ) {
				ResultSet rs = stmt.getResultSet();
				return new AuctionIterator( rs, objModel );
			}
		} catch ( Exception e ) {
			throw new DTException( "AuctionManager.restore: Could not restore persistent Auction object; Root cause: " + e );
		}
		
		throw new DTException( "AuctionManager.restore: Could not restore persistent Auction object." );
	}

	public Iterator<RegisteredUser> restoreBidders(Auction auction)
		throws DTException {
		String selectRegUserSql = "select ru.Id, ru.Username, ru.FirstName, ru.LastName, ru.Password,"
				+ " ru.Email, ru.Phone, ru.Cantext, ru.IsAdmin "
				+ "from RegisteredUser ru, Auction a , Bid b where ru.Id = b.UserId and b.AuctionId = a.Id";
		Statement stmt = null;
		StringBuffer query = new StringBuffer( 100 );
		StringBuffer condition = new StringBuffer( 100 );
		
		condition.setLength( 0 );
		
		query.append( selectRegUserSql );
		
		if( auction != null ) {
			if( auction.getId() >= 0) {
				query.append( " and a.Id = " + auction.getId() );
			}
			else if( auction.getItemId() >= 0 ) {
				query.append( " and a.ItemId = '" + auction.getItemId() + "'" );
			}
			else {
				if( auction.getMinPrice() >= 0 ) {
					condition.append( " and a.MinPrice = '" + auction.getMinPrice() + "'" );
				}
				if( auction.getExpiration() != null ) {
					condition.append( " and a.Expiration = '" + auction.getExpiration() + "'" );
				}
				
				if( condition.length() > 0 ) {
					query.append( condition );
				}
			}
		}
		
		try {
			stmt = conn.createStatement();
			
			if( stmt.execute( query.toString() ) ) {
				ResultSet rs = stmt.getResultSet();
				return new RegisteredUserIterator( rs, objModel );
			}
		} catch ( Exception e ) {
			throw new DTException( "AuctionManager.restoreBidders: Could not restore persistent RegisteredUser objects; Root cause: " + e );
		}
		
		throw new DTException( "AuctionManager.restoreBidders: Could not restore persistent RegisteredUser objects." );
	}
	
	public Iterator<Bid> restoreBids(Auction auction) 
		throws DTException {
		String selectBidSql = "select a.ItemId, a.MinPrice, a.Expiration, " +
				"b.Id, b.UserId, b.AuctionId, b.Amount, " +
				"ru.Username, ru.Password, ru.FirstName, ru.LastName, " +
				"ru.Email, ru.Phone, ru.CanText, ru.IsAdmin " +
				"from Auction a, Bid b, RegisteredUser ru "
				+ "where a.Id = b.AuctionId and b.UserId = ru.Id";
		Statement stmt = null;
		StringBuffer query = new StringBuffer( 100 );
		StringBuffer condition = new StringBuffer( 100 );
		
		condition.setLength( 0 );
		
		query.append( selectBidSql );
		
		if( auction != null ) {
			if( auction.getId() >= 0 ) {
				query.append( " and a.Id = " + auction.getId() );
			}
			else {
				if( auction.getItemId() >= 0 ) {
					condition.append( " and a.ItemId = '" + auction.getItemId() + "'" );
				}
				
				if( auction.getMinPrice() >= 0 ) {
					condition.append( " and a.MinPrice = '" + auction.getMinPrice() + "'" );
				}
				
				if( auction.getExpiration() != null ) {
					condition.append( " and a.Expiration = '" + auction.getExpiration() + "'" );
				}
				
				if( condition.length() > 0 ) {
					query.append( condition );
				}
			}
		}
		
		try {
			stmt = conn.createStatement();
			
			if( stmt.execute( query.toString() ) ) {
				ResultSet rs = stmt.getResultSet();
				return new BidIterator( rs, objModel );
			}
		} catch ( Exception e ) {
			throw new DTException( "AuctionManager.restoreBids: Could not restore persistent Bid objects; root cause: " + e );
		}
		
		throw new DTException( "AuctionManager.restoreBids: Could not restore persistent" );
	}

	public void delete(Auction auction)
		throws DTException {
		String deleteAuctionSql = "delete from Auction where Id = ?";
		PreparedStatement stmt = null;
		int inscnt;
		
		if( !auction.isPersistent() ) {
			return;
		}
		
		try {
			stmt = (PreparedStatement) conn.prepareStatement( deleteAuctionSql );
			stmt.setLong( 1, auction.getId() );
			inscnt = stmt.executeUpdate();
			if(inscnt == 1) {
				return;
			}
			else {
				throw new DTException( "AuctionManager.delete: Failed to delete this Auction." );
			}
		} catch ( SQLException e ) {
			throw new DTException( "AuctionManager.delete: Failed to delete this Auction: " + e );
		}
	}
}
