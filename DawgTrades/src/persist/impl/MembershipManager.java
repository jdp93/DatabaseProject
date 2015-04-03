package edu.uga.dawgtrades.persist.impl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import com.mysql.jdbc.PreparedStatement;
import java.util.Iterator;

import edu.uga.dawgtrades.DTException;
import edu.uga.dawgtrades.model.Membership;
import edu.uga.dawgtrades.model.ObjectModel;

public class MembershipManager {
	// Fields
	private ObjectModel objModel = null;
	private Connection conn = null;
	
	// Constructor
	public MembershipManager( Connection conn, ObjectModel objectModel) {
		this.conn = conn;
		this.objModel = objectModel;
	}

	public void save(Membership membership)
		throws DTException{
		String insertMembershipSql = "insert into Membership ( Price, Date ) values ( ?, ? )";
		String updateMembershipSql = "update Membership set Price = ?, Date = ? where Id = ?";
		PreparedStatement stmt = null;
		int inscnt;
		long membershipid;
		
		try {
			if( !membership.isPersistent() ) {
				stmt = (PreparedStatement) conn.prepareStatement( insertMembershipSql );
			} else {
				stmt = (PreparedStatement) conn.prepareStatement( updateMembershipSql );
			}
			
			if( membership.getPrice() >= 0 ) {
				stmt.setFloat( 1, membership.getPrice() );
			} else {
				//stmt.setNull( 1, java.sql.Types.FLOAT );
				throw new DTException( "MembershipManager.save: Can't save a Membership: Price negative or undefined" );
			}
			
			if( membership.getDate() != null ) {
				java.util.Date jDate = membership.getDate();
				java.sql.Date sDate = new java.sql.Date( jDate.getTime() );
				stmt.setDate( 2, sDate );
			} else {
				//stmt.setNull( 2, java.sql.Types.DATE );
				throw new DTException( "MembershipManager.save: Can't save a Membership: Date is undefined" );
			}
			
			if( membership.isPersistent() ) {
				stmt.setLong( 3, membership.getId() );
			}
			
			inscnt = stmt.executeUpdate();
			
			if( !membership.isPersistent() ) {
				if( inscnt >= 1 ) {
					String sql = "select last_insert_id()";
					if( stmt.execute( sql ) ) {
						ResultSet r = stmt.getResultSet();
						while( r.next() ) {
							membershipid = r.getLong( 1 );
							if( membershipid > 0 ) {
								membership.setId( membershipid );
							}
						}
					}
				} else {
					throw new DTException( "MembershipManager.save: Failed to save an Membership" );
				}
			} else {
				if( inscnt < 1 ) {
					throw new DTException( "MembershipManager.save: Failed to save an Membership" );
				}
			}
		} catch ( SQLException e ) {
			e.printStackTrace();
			throw new DTException( "MembershipManager.save: Failed to save an Membership " + e );
		}
	}

	public Membership restore(Membership membership) 
		throws DTException {
		String selectMembershipSql = "select Id, Price, Date from Membership";
		Statement stmt = null;
		StringBuffer query = new StringBuffer(100);
		StringBuffer condition = new StringBuffer(100);
		
		condition.setLength( 0 );
		
		query.append( selectMembershipSql );
		
		if( membership != null ) {
			if( membership.getId() >= 0 ) {
				query.append( " where Id = " + membership.getId() );
			}
			else {
				if( membership.getPrice() >= 0 ) {
					condition.append( " and Price = " + membership.getPrice() );
				}
				
				if( membership.getDate() != null ) {
					condition.append( " and Date = " + membership.getDate() );
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
				Iterator<Membership> membershipIter = new MembershipIterator( r, objModel);
				if( membershipIter != null && membershipIter.hasNext() ) {
					return membershipIter.next();
				}
				else {
					return null;
				}
			}
		} catch ( Exception e ) {
			throw new DTException( "MembershipManager.restore: Could not restore persistent Membership object; Root cause: " + e );
		}
		
		throw new DTException( "MembershipManager.restore: Could not restore persistent Membership object" );
	}

	public void delete(Membership membership) 
		throws DTException {
		String deleteMembershipSql = "delete from Membership where Id = ?";
		PreparedStatement stmt = null;
		int inscnt;
		
		if( !membership.isPersistent() ) {
			return;
		}
		
		try {
			stmt = (PreparedStatement) conn.prepareStatement( deleteMembershipSql );
			stmt.setLong( 1, membership.getId() );
			inscnt = stmt.executeUpdate();
			
			if( inscnt == 1 ) {
				return;
			} else {
				throw new DTException( "MembershipManager.delete: Failed to delete a Membership" );
			}
		} catch ( SQLException e ) {
			e.printStackTrace();
			throw new DTException( "MembershipManager.delete: Failed to delete a Membership: " + e );
		}
	}
}
