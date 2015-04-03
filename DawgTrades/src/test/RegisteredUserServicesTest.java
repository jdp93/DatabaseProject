package edu.uga.dawgtrades.test;

import java.sql.Connection;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Calendar;

import edu.uga.dawgtrades.DTException;
import edu.uga.dawgtrades.logic.impl.*;
import edu.uga.dawgtrades.model.*;
import edu.uga.dawgtrades.model.impl.ObjectModelImpl;
import edu.uga.dawgtrades.persist.Persistence;
import edu.uga.dawgtrades.persist.impl.DbUtils;
import edu.uga.dawgtrades.persist.impl.PersistenceImpl;
//will return exceptions if there are classes that do not exist
public class RegisteredUserServicesTest {
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
             System.err.println( "RegisteredUserServicesTest: Unable to obtain a database connection" );
         }
         
         // obtain a reference to the ObjectModel module      
         om = new ObjectModelImpl();
         // obtain a reference to Persistence module and connect it to the ObjectModel        
         persistence = new PersistenceImpl( conn, om ); 
         // connect the ObjectModel module to the Persistence module
         om.setPersistence( persistence ); 
         //create new RegisteredUser services
         RegisteredUserServices ruServices = new RegisteredUserServices(om);
         try{
        	 System.out.println(ruServices.viewProfile("joe"));
        	 ruServices.updateProfile("mary", "mary", "mary", "scary", "jdp93@uga.edu", "999-9999", false);
        	 ruServices.updateProfile("joe", "joe", "joe", "shmoe", "jdp93@uga.edu", "999-9999", false);
        	 System.out.println(ruServices.viewProfile("mary"));
        	 //ruServices.resetPassword("mary", "jdp93@uga.edu");
        	 //ruServices.unregister("joe");
        	 LinkedList<String> attributes = new LinkedList<String>();
        	 attributes.add("blah");
        	 Calendar calendar = Calendar.getInstance();
        	 Date d = new Date();
        	 long time = d.getTime();
        	 d = new Date(time + 10);
        	 ruServices.auctionItem("joe", "c", "BigBlahBowl", "BlahBlahBlah", 1, d, attributes);
        	 calendar.setTime(d);
        	 calendar.add(Calendar.DATE, 1);
        	 d = calendar.getTime();
        	 ruServices.reAuctionItem(2, d);
        	 ruServices.bidOnItem("mary", 2, 2);
        	 ruServices.bidOnItem("mary", 2, 3);
        	 System.out.println(ruServices.trackBids("mary"));
        	 Auction auc = om.createAuction();
        	 auc.setId(2);
        	 Iterator<Auction> auctions = om.findAuction(auc);
        	 d = new Date();
        	 time = d.getTime();
        	 d = new Date(time + 10);
        	 Auction auction = auctions.next();
        	 auction.setExpiration(d);
        	 om.storeAuction(auction);
        	 ruServices.reportOnTransaction(auction.getId(), 4, "Blah!!!");
        	 
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
