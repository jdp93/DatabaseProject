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

public class TotalReadTest {
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
             System.err.println( "TotalReadTest: Unable to obtain a database connection" );
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
        	 System.out.println("Auctions:");
        	 while(aIter != null && aIter.hasNext()){
        		 System.out.println(aIter.next());
        	 }
        	 System.out.println("Categories:");
        	 while(cIter != null && cIter.hasNext()){
        		 System.out.println(cIter.next());
        	 }
        	 System.out.println("Items:");
        	 while(iIter != null && iIter.hasNext()){
        		 System.out.println(iIter.next());
        	 }
        	 System.out.println("Registered Users:");
        	 while(rIter != null && rIter.hasNext()){
        		 System.out.println(rIter.next());
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
