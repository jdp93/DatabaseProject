package edu.uga.dawgtrades.test;

import java.sql.Connection;
import java.util.Iterator;
import java.util.LinkedList;

import edu.uga.dawgtrades.DTException;
import edu.uga.dawgtrades.model.Category;
import edu.uga.dawgtrades.model.Membership;
import edu.uga.dawgtrades.model.ObjectModel;
import edu.uga.dawgtrades.model.RegisteredUser;
import edu.uga.dawgtrades.model.Attribute;
import edu.uga.dawgtrades.model.AttributeType;
import edu.uga.dawgtrades.model.Auction;
import edu.uga.dawgtrades.model.Bid;
import edu.uga.dawgtrades.model.ExperienceReport;
import edu.uga.dawgtrades.model.Item;
import edu.uga.dawgtrades.model.Persistable;
import edu.uga.dawgtrades.model.ObjectModel;
import edu.uga.dawgtrades.model.impl.ObjectModelImpl;
import edu.uga.dawgtrades.persist.Persistence;
import edu.uga.dawgtrades.persist.impl.DbUtils;
import edu.uga.dawgtrades.persist.impl.PersistenceImpl;
//will return exceptions if there are classes that do not exist
public class DetailedReadTest {
	public static void main(String[] args)
    {
         Connection  conn = null;
         ObjectModel om = null;
         Persistence persistence = null;

         // get a database connection
         try {
             conn = DbUtils.connect();
         } 
         catch (Exception seq) {
             System.err.println( "DetailedReadTest: Unable to obtain a database connection" );
         }
         
         // obtain a reference to the ObjectModel module      
         om = new ObjectModelImpl();
         // obtain a reference to Persistence module and connect it to the ObjectModel        
         persistence = new PersistenceImpl( conn, om ); 
         // connect the ObjectModel module to the Persistence module
         om.setPersistence( persistence ); 
         
         try{
        	 //search to get Iterators with all objects
        	 Iterator<Auction> aIter = om.findAuction(null);
        	 Iterator<Category> cIter = om.findCategory(null);
        	 Iterator<Item> iIter = om.findItem(null);
        	 Iterator<RegisteredUser> rIter = om.findRegisteredUser(null);
        	 //read all objects
        	 Auction a;
        	 Iterator<Bid> bIter;
        	 Iterator<RegisteredUser> bidders;
        	 System.out.println("\nAuctions:");
        	 while(aIter != null && aIter.hasNext()){
        		 a = aIter.next();
        		 System.out.print("\n" + a);
        		 bIter = om.findBids(a);
        		 System.out.print(" bidded on by: ");
        		 while(bIter != null && bIter.hasNext()){
        			 System.out.print(bIter.next().getRegisteredUser().getName() + " ");
        		 }
        		 bidders = om.findBidders(a);
        		 System.out. print(" confirmed by findBidders: ");
        		 while(bidders != null && bidders.hasNext()){
        			 System.out.print(bidders.next().getName() + " ");
        		 }
        	 }
        	 Category c;
        	 Iterator<AttributeType> aTIter;
        	 System.out.println("\nCategories:");
        	 while(cIter != null && cIter.hasNext()){
        		 c = cIter.next();
        		 System.out.print("\n" + c);
        		 aTIter = om.getAttributeType(c);
        		 System.out.print(" with Attribute Types: ");
        		 while(aTIter != null && aTIter.hasNext()){
        			 System.out.print(aTIter.next().getName() + " ");
        		 }
        	 }
        	 Item i;
        	 Iterator<Attribute> attrIter;
        	 System.out.println("\nItems:");
        	 while(iIter != null && iIter.hasNext()){
        		 i = iIter.next();
        		 System.out.print("\n" + i);
        		 attrIter = om.getAttribute(i);
        		 System.out.print(" with Attributes: ");
        		 while(attrIter !=null && attrIter.hasNext()){
        			 System.out.print(attrIter.next().getValue() + " ");
        		 }
        	 }
        	 RegisteredUser r;
        	 Iterator<RegisteredUser> reviewedIter;
        	 Iterator<RegisteredUser> reviewerIter;
        	 Iterator<Auction> auctions;
        	 System.out.println("\nRegistered Users:");
        	 while(rIter != null && rIter.hasNext()){
        		 r = rIter.next();
        		 System.out.print("\n" + r);
        		 reviewedIter = om.findReviewers(r);
        		 reviewerIter = om.findReviewed(r);
        		 auctions = om.findAuctionsBidOn(r);
        		 System.out.print(" review by: ");
        		 while(reviewedIter != null && reviewedIter.hasNext()){
        			 System.out.print(reviewedIter.next().getName() + " ");
        		 }
        		 System.out.print(" reviewed: ");
        		 while(reviewerIter != null && reviewerIter.hasNext()){
        			 System.out.print(reviewerIter.next().getName() + " ");
        		 }
        		 System.out.println("\nBid on Auctions:");
        		 while(auctions != null && auctions.hasNext()){
        			 System.out.println(auctions.next());
        		 }
        	 }
        	 
         }
         catch(Exception e){
        	 System.err.println( "Exception: " + e );
             e.printStackTrace();
         }
         finally{
        	// close the connection
             try {
                 conn.close();
             }
             catch( Exception e ) {
                 System.err.println( "Exception: " + e );
                 e.printStackTrace();
             }
         }
    }
}
