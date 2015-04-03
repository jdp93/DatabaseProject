package edu.uga.dawgtrades.persist.impl;

import java.sql.Connection;
import java.util.Iterator;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.mysql.jdbc.PreparedStatement;

import edu.uga.dawgtrades.DTException;
import edu.uga.dawgtrades.model.ExperienceReport;
import edu.uga.dawgtrades.model.ObjectModel;

public class ExperienceReportManager {
	// Fields
	private ObjectModel objModel = null;
	private Connection conn = null;
	
	// Constructor
	public ExperienceReportManager( Connection conn, ObjectModel objectModel) {
		this.conn = conn;
		this.objModel = objectModel;
	}

	public void save(ExperienceReport expReport)
		throws DTException {
		String insertExpRepSql = "insert into ExperienceReport "
				+ "( ReviewerId, ReviewedId, Rating, Report, Date ) values ( ?, ?, ?, ?, ? )";
		PreparedStatement stmt = null;
		int inscnt;
		long id;
		
		if( expReport.getReviewer() == null || expReport.getReviewed() == null ) {
			throw new DTException( "ExperienceReportManager.save: Attempting to save a ExperienceReport with no Reviewer or Reviewed defined" );
		}
		if( !expReport.getReviewer().isPersistent() || !expReport.getReviewed().isPersistent() ) {
			throw new DTException( "ExperienceReportManager.save: Attempting to save a ExperienceReport with either Reviewer or Reviewed are not persistent." );
		}
		
		try {
			stmt = (PreparedStatement) conn.prepareStatement( insertExpRepSql );
			
			stmt.setLong( 1, expReport.getReviewer().getId() );
			stmt.setLong( 2, expReport.getReviewed().getId() );
			
			if( expReport.getRating() >= 1 || expReport.getRating() <= 5 ) {
				stmt.setLong( 3, expReport.getRating() );
			} else {
				throw new DTException( "ExperienceReportManager.save: Rating is null." );
			}
			
			if( expReport.getReport() != null) {
				stmt.setString( 4, expReport.getReport() );
			} else {
				throw new DTException("ExperienceReportManager.save: Report is null.");
			}
			
			if( expReport.getDate() != null ) {
				java.util.Date jDate = expReport.getDate();
				java.sql.Date sDate = new java.sql.Date( jDate.getTime() );
				stmt.setDate( 5, sDate );
			} else {
				throw new DTException("ExperienceReportManager.save: Date is null.");
			}
			
			inscnt = stmt.executeUpdate();
			
			if ( inscnt >= 1 ) {
				String sql = "select last_insert_id()";
				if( stmt.execute( sql ) ) {
					ResultSet rs = stmt.getResultSet();
					while( rs.next() ) {
						id = rs.getLong( 1 );
						if( id > 0 ) {
							expReport.setId( id );
						}
					}
				}
			} else {
				throw new DTException( "ExperienceReportManager.save: Failed to save an ExperienceReport." );
			}
		} catch ( SQLException e ) {
			e.printStackTrace();
			throw new DTException( "ExperienceReportManager.save: Failed to save an ExperienceReport: " + e );
		}
	}

	public Iterator<ExperienceReport> restore(ExperienceReport expReport)
		throws DTException {
		String selectExpRepSql = "select r1.Username, r1.FirstName, r1.LastName, r1.Password, r1.Email, r1.Phone, r1.IsAdmin, r1.CanText, "
									+ "e.Id, e.ReviewerId, e.ReviewedId, e.Rating, e.Report, e.Date, "
									+ "r2.Username, r2.FirstName, r2.LastName, r2.Password, r2.Email, r2.Phone, r2.IsAdmin, r2.CanText, "
									+ "from RegisteredUser r1, ExperienceReport e, RegisteredUser r2 where r1.Id = e.reviewerId and e.reviewedId = r2.Id";
		Statement stmt = null;
		StringBuffer query = new StringBuffer(100);
		StringBuffer condition = new StringBuffer(100);
		
		if( expReport.getReviewer() != null && !expReport.getReviewer().isPersistent() ) {
			throw new DTException( "ExperienceReportManager.restore: The argument expReport includes a non-persistent Reviewer object" );
		}
		if( expReport.getReviewed() != null && !expReport.getReviewed().isPersistent() ) {
			throw new DTException( "ExperienceReportManager.restore: The argument expReport includes a non-persistent Reviewed object" );
		}
		
		condition.setLength( 0 );
		
		query.append( selectExpRepSql );
		
		if( expReport != null ) {
			if( expReport.isPersistent() ) {
				query.append( " where e.Id = " + expReport.getId() );
			}
			else {
				if( expReport.getReviewer() != null ) {
					condition.append( " and e.ReviewerId " + expReport.getReviewer().getId() );
				}
				
				if( expReport.getReviewed() != null ) {
					condition.append( " and e.ReviewedId " + expReport.getReviewed().getId() );
				}
				
				if( expReport.getRating() >= 0) {
					condition.append( " and e.Rating = " + expReport.getRating() );
				}
				
				if( expReport.getReport() != null ) {
					condition.append( " and e.Report = '" + expReport.getReport() + "'" );
				}
				
				if( expReport.getDate() != null ) {
					condition.append( " and e.Date = '" + expReport.getDate() + "'" );
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
				return new ExperienceReportIterator( r, objModel );
			}
		} catch ( Exception e ) {
			throw new DTException( "ExperienceReportManager.restore: Could not restore persistent ExperienceReport object; Root cause: " + e );
		}
		
		throw new DTException( "ExperienceReportManager.restore: Could not restore persistent ExperienceReport object" );
	}

	public void delete(ExperienceReport expReport)
		throws DTException {
		String deleteExpRepSql = "delete from ExperienceReport where Id = ?";
		PreparedStatement stmt = null;
		int inscnt;
		
		if( !expReport.isPersistent() ) {
			return;
		}
		
		try {
			stmt = (PreparedStatement) conn.prepareStatement( deleteExpRepSql );
			stmt.setLong( 1, expReport.getId() );
			inscnt = stmt.executeUpdate();
			
			if( inscnt == 1 ) {
				return;
			} else {
				throw new DTException( "ExperienceReport.delete: Failed to delete an ExperienceReport" );
			}
		} catch (SQLException e ) {
			e.printStackTrace();
			throw new DTException( "ExperienceReport.delete: Failed to delete an ExperienceReport: " + e );
		}
	}
}
