package edu.uga.dawgtrades.model.impl;

import java.sql.Connection;
import java.util.Date;
import java.util.Iterator;

import edu.uga.dawgtrades.DTException;
import edu.uga.dawgtrades.model.Attribute;
import edu.uga.dawgtrades.model.Auction;
import edu.uga.dawgtrades.model.Bid;
import edu.uga.dawgtrades.model.ObjectModel;
import edu.uga.dawgtrades.model.RegisteredUser;
import edu.uga.dawgtrades.persist.Persistence;
import edu.uga.dawgtrades.persist.impl.DbUtils;
import edu.uga.dawgtrades.persist.impl.PersistenceImpl;

/**
 *  @author Seong Wang
 */
public class BidImpl
	extends PersistableImpl
	implements Bid {

	// Fields
	private float amount;
	private Date date;
	private Auction auction;
	private RegisteredUser user;
	
	// Constructors
	public BidImpl() {
		super(-1);
	}
	
	public BidImpl(Auction auction, RegisteredUser user, float amount)
		throws DTException {
		super(-1);
		if ( !auction.isPersistent() ) {
			throw new DTException( "The bid's auction is not persistent.");
		}
		if ( !user.isPersistent() ) {
			throw new DTException( " The bid's user is not persistent.");
		}
		if ( amount < 0 ) {
			throw new DTException ( "The Bid price is negative" );
		}
		
		this.auction = auction;
		this.user = user;
		this.amount = amount;
		this.date = new java.util.Date();
	}
	
	public float getAmount() {
		return amount;
	}

	public void setAmount(float amount) {
		this.amount = amount;
	}

	public Date getDate() {		
		return date;
	}

	public void setDate(Date date) { 
		this.date = date;			
	}

	// Methods for Association/Relationship
	public boolean isWinning() throws DTException {

		Connection  conn = null;
        ObjectModel objectModel = null;
        Persistence persistence = null;

        // get a database connection
        try {
            conn = DbUtils.connect();
        } 
        catch (Exception seq) {
            System.err.println( "Bid.isWinning: Unable to obtain a database connection" );
        }
        
        // obtain a reference to the ObjectModel module      
        objectModel = new ObjectModelImpl();
        // obtain a reference to Persistence module and connect it to the ObjectModel        
        persistence = new PersistenceImpl( conn, objectModel ); 
        // connect the ObjectModel module to the Persistence module
        objectModel.setPersistence( persistence );
        
        //find all the bids on this bid's Auction in the database
        Iterator<Bid> bidIt = objectModel.findBids(this.getAuction());
        
        //look through all the bids and return false if this bid is beat, otherwise return true
        while(bidIt.hasNext()){
        	if (bidIt.next().getAmount() > this.getAmount())
        		return false;
        }
		return true;
	}

	public Auction getAuction() {
		return auction;
	}

	public RegisteredUser getRegisteredUser() {
		return user;
	}

	@Override
	public String toString(){
        return "Bid[" + getId() + "] " + getAmount() + " " + getDate();
    }
    
	//Bids are equal if their amounts, dates, auctions, and registeredusers are all equal
	@Override
    public boolean equals(Object otherBid){
    	
    	if (otherBid == null)
    		return false;
    	if (otherBid instanceof Bid){
    		return (getAmount() == (((Bid) otherBid).getAmount()))
    				&& (getDate().equals(((Bid)otherBid).getDate()))
    				&& (getAuction().equals(((Bid)otherBid).getAuction()))
    				&& (getRegisteredUser().equals(((Bid)otherBid).getRegisteredUser()));
    	}
    	return false;
    }
}
