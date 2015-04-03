/**
* @author NeelRana
*/

package edu.uga.dawgtrades.model.impl;

import java.util.Date;

import edu.uga.dawgtrades.DTException;
import edu.uga.dawgtrades.model.Attribute;
import edu.uga.dawgtrades.model.ExperienceReport;
import edu.uga.dawgtrades.model.RegisteredUser;


public class ExperienceReportImpl extends PersistableImpl implements ExperienceReport {
	
	//Private attributes
	private int rating;
	private String report;
	private Date date;
	private RegisteredUser reviewer;
	private RegisteredUser reviewed;
	
	//protected empty constructor
	protected ExperienceReportImpl(){
		super(-1);
	}
	
	
	//protected constructor setting some values
	protected ExperienceReportImpl(RegisteredUser reviewer, RegisteredUser reviewed, 
            int rating, String report, java.util.Date date) throws DTException{
		super(-1);
		if (!reviewer.isPersistent())
			throw new DTException("ExperienceReport's reviewer is not persistent.");
		if (!reviewed.isPersistent())
			throw new DTException("ExperienceReport's reviewed is not persistent.");
		if ((rating < 1) || (rating > 5))
			throw new DTException("ExperienceReport's rating is not between 1 and 5.");
		this.reviewer = reviewer;
		this.reviewed = reviewed;
		this.rating = rating;
		this.report = report;
		this.date = date;
	}
	
	/******************************
	** public getters and setters
	*******************************/
	
	@Override
	public int getRating() {
	   return rating;
	}
	
	
	@Override
	public void setRating( int rating ) throws DTException {
		if ((rating < 1) || (rating > 5))
			throw new DTException("ExperienceReport's rating is not between 1 and 5.");
		this.rating = rating;
	}
	    
	@Override
	public String getReport() {
		return report;
	}
	   
	@Override
	public void setReport( String report ){
		this.report = report;
	}
	   
	@Override
	public Date getDate(){
		return date;
	}
	   
	@Override
	public void setDate( Date date ){
		this.date = date;
	}
	    
	@Override
	public RegisteredUser getReviewer(){
		return reviewer;
	}
	    
	@Override
	public void setReviewer( RegisteredUser reviewer ){
		this.reviewer = reviewer;
	}
	    
	@Override
	public RegisteredUser getReviewed(){
		return reviewed;
	}
	    
	@Override
	public void setReviewed( RegisteredUser reviewed ){
		this.reviewed = reviewed;
	}
	
	@Override
	public String toString(){
        return "ExperienceReport[" + getId() + "] " + getRating() + " " + getDate() + " " + getReport();
    }
    
	//two experiencereports are equal if their rating, date, and report are equal
	@Override
    public boolean equals(Object otherExperienceReport){
    	
    	if (otherExperienceReport == null)
    		return false;
    	if (otherExperienceReport instanceof ExperienceReport){
    		return (getRating() == ((ExperienceReport) otherExperienceReport).getRating())
    				&& (getDate().equals(((ExperienceReport)otherExperienceReport).getDate()))
    				&& (getReport().equals(((ExperienceReport)otherExperienceReport).getReport()));
    	}
    	return false;
    }
}
