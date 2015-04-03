package edu.uga.dawgtrades.persist.impl;

import java.sql.Connection;
import java.util.Iterator;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import com.mysql.jdbc.PreparedStatement;

import edu.uga.dawgtrades.DTException;
import edu.uga.dawgtrades.model.Auction;
import edu.uga.dawgtrades.model.ObjectModel;
import edu.uga.dawgtrades.model.RegisteredUser;
import edu.uga.dawgtrades.persist.Persistence;

public class RegisteredUserManager{
	// Fields
	private ObjectModel objModel = null;
	private Connection conn = null;
	
	// Constructor
	public RegisteredUserManager( Connection conn, ObjectModel objectModel) {
		this.conn = conn;
		this.objModel = objectModel;
	}

	public void save(RegisteredUser registeredUser)
		throws DTException {
		String insertUserSql = "insert into RegisteredUser ( Username, Password, FirstName, LastName, Email, Phone, CanText, IsAdmin) values ( ?, ?, ?, ?, ?, ?, ?, ? )";
		String updateUserSql = "update RegisteredUser set Username = ?, Password = ?, FirstName = ?, LastName = ?, Email = ?, Phone = ?, CanText = ?, IsAdmin = ? where Id = ?";
		PreparedStatement stmt = null;
		int inscnt;
		long userId;
		
		try {
			if( !registeredUser.isPersistent() ) {
				stmt = (PreparedStatement) conn.prepareStatement( insertUserSql );
			} else {
				stmt = (PreparedStatement) conn.prepareStatement( updateUserSql );
			}
			
			if( registeredUser.getName() != null) {
				stmt.setString( 1, registeredUser.getName() );
			} else {
				throw new DTException( "RegisteredUserManager.save: Can't save a RegisteredUser: name undefined" );
			}
			
			if( registeredUser.getPassword() != null ) {
				stmt.setString( 2, registeredUser.getPassword() );
			} else {
				//stmt.setNull( 2, java.sql.Types.VARCHAR );
				throw new DTException( "RegisteredUserManager.save: Can't save a RegisteredUser: password undefined" );
			}
			
			if( registeredUser.getFirstName() != null ) {
				stmt.setString( 3, registeredUser.getFirstName() );
			} else {
				//stmt.setNull( 3, java.sql.Types.VARCHAR );
				throw new DTException( "RegisteredUserManager.save: Can't save a RegisteredUser: firstname undefined" );
			}
			
			if( registeredUser.getLastName() != null ) {
				stmt.setString( 4, registeredUser.getLastName() );
			} else {
				//stmt.setNull( 4, java.sql.Types.VARCHAR );
				throw new DTException( "RegisteredUserManager.save: Can't save a RegisteredUser: lastname undefined" );
			}
			
			if( registeredUser.getEmail() != null ) {
				stmt.setString( 5, registeredUser.getEmail() );
			} else {
				//stmt.setNull( 5, java.sql.Types.VARCHAR );
				throw new DTException( "RegisteredUserManager.save: Can't save a RegisteredUser: email undefined" );
			}
			
			stmt.setString( 6, registeredUser.getPhone() );
			
			stmt.setBoolean( 7, registeredUser.getCanText() );
			
			stmt.setBoolean( 8, registeredUser.getIsAdmin() );
			
			
			
			if( registeredUser.isPersistent() ) {
				stmt.setLong( 9, registeredUser.getId() );
			}
			
			inscnt = stmt.executeUpdate();
			
			if( !registeredUser.isPersistent() ) {
				if( inscnt >= 1 ) {
					String sql = "select last_insert_id()";
					if( stmt.execute( sql ) ) {
						ResultSet r = stmt.getResultSet();
						while( r.next() ) {
							userId = r.getLong( 1 );
							if( userId >= 0 ) {
								registeredUser.setId( userId );
							}
						}
					}
				} else {
					throw new DTException( "RegisteredUserManager.save: Failed to save a RegisteredUser" );
				}
			} else {
				if( inscnt < 1 ) {
					throw new DTException( "RegisteredUserManager.save: Failed to save a RegisteredUser" );
				}
			}
			
		} catch ( SQLException e ) {
			e.printStackTrace();
			throw new DTException( "RegisteredUserManager.save: Failed to save a RegisteredUser: " + e );
		}
	}

	public Iterator<RegisteredUser> restore(RegisteredUser registeredUser)
		throws DTException {
		String selectUserSql = "select Id, Username, Password, FirstName, " +
				"LastName, Email, Phone, CanText, IsAdmin from RegisteredUser";
		Statement stmt = null;
		StringBuffer query = new StringBuffer( 100 );
		StringBuffer condition = new StringBuffer(100);
		
		condition.setLength(0);
		
		query.append( selectUserSql );
		
		if( registeredUser != null ) {
			if( registeredUser.getId() >= 0 ) {
				query.append( " where Id = " + registeredUser.getId() );
			}
			else if( registeredUser.getName() != null ) {
				query.append( " where Username = '" + registeredUser.getName() + "'");
			}
			else {
				if( registeredUser.getFirstName() != null ) {
					condition.append( " FirstName = '" + registeredUser.getFirstName() + "'" );
				}
				
				if( registeredUser.getLastName() != null ) {
					if( condition.length() > 0 ) {
						condition.append( " and");
					}
					condition.append( " LastName = '" + registeredUser.getLastName() + "'" );
				}
				
				if( registeredUser.getPassword() != null ) {
					if( condition.length() > 0 ) {
						condition.append( " and");
					}
					condition.append( " Password = '" + registeredUser.getPassword() + "'" );
				}
				
				if( registeredUser.getEmail() != null ) {
					if( condition.length() > 0 ) {
						condition.append( " and");
					}
					condition.append( " Email = '" + registeredUser.getEmail() + "'" );
				}
				
				if( registeredUser.getPhone() != null ) {
					if( condition.length() > 0 ) {
						condition.append( " and");
					}
					condition.append( " Phone = '" + registeredUser.getPhone() + "'" );
				}
				
				condition.append( " and IsAdmin = " + registeredUser.getIsAdmin() + " and CanText = " + registeredUser.getCanText() );
				
				query.append( " where " );
				query.append( condition );
			}
		}
		
		try {
			stmt = conn.createStatement();
			
			if( stmt.execute( query.toString() ) ) {
				ResultSet r = stmt.getResultSet();
				return new RegisteredUserIterator( r, objModel );
			}
		} catch ( Exception e ) {
			throw new DTException( "RegisteredUserManager.restore: Could not restore persistent RegisteredUser object; Root cause: " + e );
		}
		
		throw new DTException( "RegisteredUserManager.restore: Could not restore persistent RegisteredUser object" );
	}

	// Given registeredUser:
	//	-> registerUser.id == bid.id
	//  -> bid.id == auction.id
	// Return Iterator<Auction>
	public Iterator<Auction> restoreAuctionsBidOn(RegisteredUser registeredUser)
		throws DTException {
		String selectAuctionSql = "select a.Id, a.ItemId, a.MinPrice, a.Expiration " 
				+ "from Auction a, Bid b, RegisteredUser r where a.Id = b.AuctionId and b.UserId = r.Id";
		Statement stmt = null;
		StringBuffer query = new StringBuffer(100);
		StringBuffer condition = new StringBuffer(100);
		
		condition.setLength( 0 );
		
		query.append( selectAuctionSql );
		
		if( registeredUser != null ) {
			if( registeredUser.getId() >= 0 ) {
				query.append( " and r.Id = " + registeredUser.getId() );
			}
			else if( registeredUser.getName() != null ) {
				query.append( " and r.Username = '" + registeredUser.getName() + "'" );
			}
			else {
				if( registeredUser.getPassword() != null ) {
					condition.append( " and r.Password = '" + registeredUser.getPassword() + "'" );
				}
				
				if( registeredUser.getFirstName() != null ) {
					condition.append( " and r.FirstName = '" + registeredUser.getFirstName() + "'" );
				}
				
				if( registeredUser.getLastName() != null ) {
					condition.append( " and r.LastName = '" + registeredUser.getLastName() + "'" );
				}
				
				if( registeredUser.getEmail() != null ) {
					condition.append( " and r.Email = '" + registeredUser.getEmail() + "'" );
				}
				
				if( registeredUser.getPhone() != null ) {
					condition.append( " and r.Phone = '" + registeredUser.getPhone() + "'" );
				}
				
				condition.append( " and r.CanText = " + registeredUser.getCanText()
								+ " and r.IsAdmin = " + registeredUser.getIsAdmin() );
				
				query.append( condition );
			}
		}
		
		try {
			stmt = conn.createStatement();
			
			if( stmt.execute( query.toString() ) ) {
				ResultSet r = stmt.getResultSet();
				return new AuctionIterator( r, objModel );
			}
		} catch ( Exception e ) {
			throw new DTException( "RegisteredUserManager.restoreAuctionsBidOn: Could not restore persistent Auction objects. Root cause: " + e );
		}
		
		throw new DTException( "RegisteredUserManager.restoreAuctionsBidOn Could not restore persistent Auction objects" );
	}
	
	// Given registeredUser
	// -> registeredUser.id == reviewer.id
	// -> reviewer.id == expReport.id
	// -> expReport.id == reviewer.id
	// -> reviewer.id == registeredUser.id
	// Return Iterator<RegisteredUser> (of Reviewers)
	public Iterator<RegisteredUser> restoreReviewers(
			RegisteredUser registeredUser) throws DTException {
		String selectAuctionSql = "select r1.Id, r1.Username, r1.Password, " +
				"r1.FirstName, r1.LastName, r1.Email, r1.Phone, r1.CanText, r1.IsAdmin " 
				+ "from RegisteredUser r1, RegisteredUser r2, ExperienceReport er " +
				"where r1.Id = er.ReviewerId and r2.Id = er.ReviewedId";
		Statement stmt = null;
		StringBuffer query = new StringBuffer(100);
		StringBuffer condition = new StringBuffer(100);
		
		condition.setLength( 0 );
		
		query.append( selectAuctionSql );
		
		if( registeredUser != null ) {
			if( registeredUser.getId() >= 0 ) {
				query.append( " and r2.Id = " + registeredUser.getId() );
			}
			else if( registeredUser.getName() != null ) {
				query.append( " and r2.Username = '" + registeredUser.getName() + "'" );
			}
			else {
				if( registeredUser.getPassword() != null ) {
					condition.append( " and r2.Password = '" + registeredUser.getPassword() + "'" );
				}
				
				if( registeredUser.getFirstName() != null ) {
					condition.append( " and r2.FirstName = '" + registeredUser.getFirstName() + "'" );
				}
				
				if( registeredUser.getLastName() != null ) {
					condition.append( " and r2.LastName = '" + registeredUser.getLastName() + "'" );
				}
				
				if( registeredUser.getEmail() != null ) {
					condition.append( " and r2.Email = '" + registeredUser.getEmail() + "'" );
				}
				
				if( registeredUser.getPhone() != null ) {
					condition.append( " and r2.Phone = '" + registeredUser.getPhone() + "'" );
				}
				
				condition.append( " and r2.CanText = " + registeredUser.getCanText()
								+ " and r2.IsAdmin = " + registeredUser.getIsAdmin() );
				
				query.append( condition );
			}
		}
		
		try {
			stmt = conn.createStatement();
			
			if( stmt.execute( query.toString() ) ) {
				ResultSet r = stmt.getResultSet();
				return new RegisteredUserIterator( r, objModel );
			}
		} catch ( Exception e ) {
			throw new DTException( "RegisteredUserManager.restoreReviewers: Could not restore persistent RegisteredUser; Root cause: " + e );
		}
		
		throw new DTException( "RegisteredUserManager.restoreAuctionsBidOn Could not restore persistent RegisteredUser" );
	}

	// Given registeredUser
	// -> registeredUser.id == reviewer.id
	// -> reviewer.id == expReport.id
	// -> expReport.id == reviewer.id
	// -> reviewer.id == registeredUser.id
	// Return Iterator<RegisteredUser> (of the Reviewed)
	public Iterator<RegisteredUser> restoreReviewed(
			RegisteredUser registeredUser) throws DTException{
		String selectAuctionSql = "select r2.Id, r2.Username, r2.Password, " +
				"r2.FirstName, r2.LastName, r2.Email, r2.Phone, r2.CanText, r2.IsAdmin " 
				+ "from RegisteredUser r1, RegisteredUser r2, ExperienceReport er " +
				"where r1.Id = er.ReviewerId and r2.Id = er.ReviewedId";
		Statement stmt = null;
		StringBuffer query = new StringBuffer(100);
		StringBuffer condition = new StringBuffer(100);
		
		condition.setLength( 0 );
		
		query.append( selectAuctionSql );
		
		if( registeredUser != null ) {
			if( registeredUser.getId() >= 0 ) {
				query.append( " and r1.Id = " + registeredUser.getId() );
			}
			else if( registeredUser.getName() != null ) {
				query.append( " and r1.Username = '" + registeredUser.getName() + "'" );
			}
			else {
				if( registeredUser.getPassword() != null ) {
					condition.append( " and r1.Password = '" + registeredUser.getPassword() + "'" );
				}
				
				if( registeredUser.getFirstName() != null ) {
					condition.append( " and r1.FirstName = '" + registeredUser.getFirstName() + "'" );
				}
				
				if( registeredUser.getLastName() != null ) {
					condition.append( " and r1.LastName = '" + registeredUser.getLastName() + "'" );
				}
				
				if( registeredUser.getEmail() != null ) {
					condition.append( " and r1.Email = '" + registeredUser.getEmail() + "'" );
				}
				
				if( registeredUser.getPhone() != null ) {
					condition.append( " and r1.Phone = '" + registeredUser.getPhone() + "'" );
				}
				
				condition.append( " and r1.CanText = " + registeredUser.getCanText()
								+ " and r1.IsAdmin = " + registeredUser.getIsAdmin() );
				
				query.append( condition );
			}
		}
		
		try {
			stmt = conn.createStatement();
			
			if( stmt.execute( query.toString() ) ) {
				ResultSet r = stmt.getResultSet();
				return new RegisteredUserIterator( r, objModel );
			}
		} catch ( Exception e ) {
			throw new DTException( "RegisteredUserManager.restoreReviewers: Could not restore persistent RegisteredUser; Root cause: " + e );
		}
		
		throw new DTException( "RegisteredUserManager.restoreAuctionsBidOn Could not restore persistent RegisteredUser" );
	}

	public void delete(RegisteredUser registeredUser)
		throws DTException {
		String deleteUserSql = "delete from RegisteredUser where Id = ?";
		PreparedStatement stmt = null;
		int inscnt;
		
		if( !registeredUser.isPersistent() ) {
			return;
		}
		
		try {
			stmt = (PreparedStatement) conn.prepareStatement( deleteUserSql );
			stmt.setLong( 1, registeredUser.getId() );
			inscnt = stmt.executeUpdate();
			if( inscnt == 1 ) {
				return;
			} else {
				throw new DTException( "RegisteredUserManager.delete: Failed to delete a RegisteredUser" );
			}
		} catch ( SQLException e ) {
			e.printStackTrace();
			throw new DTException( "RegisteredUserManager.delete: Failed to delete a RegisteredUser: " + e );
		}
	}
}
