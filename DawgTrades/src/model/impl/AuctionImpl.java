
package edu.uga.dawgtrades.model.impl;

import java.sql.Connection;

import edu.uga.dawgtrades.persist.impl.DbUtils;
import edu.uga.dawgtrades.persist.impl.PersistenceImpl;

import java.util.Date;
import java.util.Iterator;

import edu.uga.dawgtrades.DTException;
import edu.uga.dawgtrades.model.Auction;
import edu.uga.dawgtrades.model.Bid;
import edu.uga.dawgtrades.model.Item;
import edu.uga.dawgtrades.model.ObjectModel;
import edu.uga.dawgtrades.persist.Persistence;

public class AuctionImpl extends PersistableImpl implements Auction {
	
	//private attributes
	private float minPrice;
	private Date expiration;
	private long itemId;
	
	//empty constructor
	protected AuctionImpl(){
		super(-1);
	};
	
	//constructor setting some values
	protected AuctionImpl(Item item, float minPrice, java.util.Date expiration)
							throws DTException{
		super(-1);
		if (!item.isPersistent())
			throw new DTException("Auction's Item is not persistent.");
		this.itemId = item.getId();
		this.minPrice = minPrice;
		this.expiration = expiration;		
	};
	
	//public getters and setters
	@Override
    public float getMinPrice(){
    	return minPrice;
    }
    
	@Override
    public void setMinPrice( float minPrice ){
		this.minPrice = minPrice;
	}
	
	@Override
    public Date getExpiration(){
		return expiration;
	}
    
	@Override
    public void setExpiration( Date expiration ){
    	this.expiration = expiration;
    }
    
	@Override
    public boolean getIsClosed(){
		return expiration.before(new java.util.Date());
	}
    
	@Override
    public float getSellingPrice() throws DTException{
		
		Connection  conn = null;
        ObjectModel objectModel = null;
        Persistence persistence = null;

        // get a database connection
        try {
            conn = DbUtils.connect();
        } 
        catch (Exception seq) {
            System.err.println( "Auction.getSellingPrice(): Unable to obtain a database connection" );
        }
        
        // obtain a reference to the ObjectModel module      
        objectModel = new ObjectModelImpl();
        // obtain a reference to Persistence module and connect it to the ObjectModel        
        persistence = new PersistenceImpl( conn, objectModel ); 
        // connect the ObjectModel module to the Persistence module
        objectModel.setPersistence( persistence );
        
        //find all the bids on this Auction in the database
        Iterator<Bid> bidIt = objectModel.findBids(this);
        
        //find the highest Bid and return its amount
        float amount = getMinPrice();
        while (bidIt.hasNext()){
        	if (bidIt.next().getAmount() > amount){
        		amount = bidIt.next().getAmount();
        	}
        }
		return amount;
	}
    
    @Override
    public long getItemId(){
    	return itemId;
    }
    
    @Override
    public void setItemId( long itemId ){
    	this.itemId = itemId;
    }
    
    @Override
    public String toString(){
        return "Auction[" + getId() + "] " + getMinPrice() + " " + getExpiration();
    }
    
    //Auctions are equal if their itemId's and expiration's are equal
    @Override
    public boolean equals(Object otherAuction){
    	
    	if (otherAuction == null)
    		return false;
    	if (otherAuction instanceof Auction){
    		return (getExpiration().equals(((Auction) otherAuction).getExpiration()))
    				&& (getItemId() == ((Auction)otherAuction).getItemId());
    	}
    	return false;
    }
}
