package edu.uga.dawgtrades.persist.impl;

import java.sql.ResultSet;
import java.util.Date;
import java.util.Iterator;
import java.util.NoSuchElementException;

import edu.uga.dawgtrades.DTException;
import edu.uga.dawgtrades.model.ExperienceReport;
import edu.uga.dawgtrades.model.ObjectModel;
import edu.uga.dawgtrades.model.RegisteredUser;

public class ExperienceReportIterator implements Iterator<ExperienceReport> {
	private ResultSet rs = null;
	private ObjectModel modelFactory = null;
	private boolean more = false;
	
	public ExperienceReportIterator(ResultSet r, ObjectModel objModel)
		throws DTException {
		this.rs = r;
		this.modelFactory = objModel;
		try {
			more = rs.next();
		} catch( Exception e ) {
			throw new DTException( "ExperienceReportIterator: Cannot create an iterator; root cause: " + e );
		}
	}

	@Override
	public boolean hasNext() {
		return more;
	}

	@Override
	public ExperienceReport next() {
		long id;
		long reviewerId;
		long reviewedId;
		int rating;
		String report;
		Date date;
		
		String username1;
		String username2;
		String firstname1;
		String firstname2;
		String lastname1;
		String lastname2;
		String password1;
		String password2;
		boolean isAdmin1;
		boolean isAdmin2;
		String email1;
		String email2;
		String phone1;
		String phone2;
		boolean canText1;
		boolean canText2;
		
		ExperienceReport expRep = null;
		RegisteredUser reviewer = null;
		RegisteredUser reviewed = null;
		
		if ( more ) {
			try {
				
				username1 = rs.getString( 1 );
				firstname1 = rs.getString( 2 );
				lastname1 = rs.getString( 3 );
				password1 = rs.getString( 4 );
				email1 = rs.getString( 5 );
				phone1 = rs.getString( 6 );
				isAdmin1 = rs.getBoolean( 7 );
				canText1 = rs.getBoolean( 8 );
				
				id = rs.getLong( 9 );
				reviewerId = rs.getLong( 10 );
				reviewedId = rs.getLong( 11 );
				rating = rs.getInt( 12 );
				report = rs.getString( 13 );
				date = rs.getDate( 14 );
				
				username2 = rs.getString( 15 );
				firstname2 = rs.getString( 16 );
				lastname2 = rs.getString( 17 );
				password2 = rs.getString( 18 );
				email2 = rs.getString( 19 );
				phone2 = rs.getString( 20 );
				isAdmin2 = rs.getBoolean( 21 );
				canText2 = rs.getBoolean( 22 );
				
				more = rs.next();
			} catch ( Exception e) {
				throw new NoSuchElementException( "ExperienceReportIterator: No next ExperienceReport object; root cause: " + e );
			}
			
			try {
				reviewer = modelFactory.createRegisteredUser(username1, firstname1, lastname1, password1, isAdmin1, email1, phone1, canText1);
				reviewer.setId( reviewerId );
			} catch (DTException e) {
				e.printStackTrace();
				System.out.println( e );
			}
			
			try {
				reviewed = modelFactory.createRegisteredUser(username2, firstname2, lastname2, password2, isAdmin2, email2, phone2, canText2);
				reviewed.setId( reviewedId );
			} catch (DTException e) {
				e.printStackTrace();
				System.out.println( e );
			}
			
			try {
				expRep = modelFactory.createExperienceReport(reviewer, reviewed, rating, report, date);
				expRep.setId( id );
			} catch (DTException e) {
				e.printStackTrace();
				System.out.println( e );
			}
			
			return expRep;
		} else {
			throw new NoSuchElementException( "ExperienceReportIterator: No next ExperienceReport object" );
		}
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

}
