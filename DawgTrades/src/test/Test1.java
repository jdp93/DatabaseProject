package edu.uga.dawgtrades.test;

import java.sql.Connection;
import java.util.Date;

import edu.uga.dawgtrades.DTException;
import edu.uga.dawgtrades.model.ObjectModel;
import edu.uga.dawgtrades.model.RegisteredUser;
import edu.uga.dawgtrades.model.Category;
import edu.uga.dawgtrades.model.Item;
import edu.uga.dawgtrades.model.Attribute;
import edu.uga.dawgtrades.model.Auction;
import edu.uga.dawgtrades.model.Bid;
import edu.uga.dawgtrades.model.impl.ObjectModelImpl;
import edu.uga.dawgtrades.persist.Persistence;
import edu.uga.dawgtrades.persist.impl.DbUtils;
import edu.uga.dawgtrades.persist.impl.PersistenceImpl;

public class Test1 {
	public static void main(String[] args){
		Connection  conn = null;
    	ObjectModel om = null;
    	Persistence persistence = null;

    	//get a database connection
    	try {
    		conn = DbUtils.connect();
    	} 
    	catch (Exception seq) {
        	System.err.println( "Test1: Unable to obtain a database connection" );
    	}
    	// obtain a reference to the ObjectModel module      
        om = new ObjectModelImpl();
        // obtain a reference to Persistence module and connect it to the ObjectModel        
        persistence = new PersistenceImpl( conn, om ); 
        // connect the ObjectModel module to the Persistence module
        om.setPersistence( persistence ); 
        
        try {
       	 
        	RegisteredUser ru1 = om.createRegisteredUser( "joe", "Joe", "Doe", "joepass", false, "joe@mail.com", "333-4456", false );
            RegisteredUser ru2 = om.createRegisteredUser( "mary", "Mary", "Swift", "marypass", true, "mary@mail.com", "444-9876", true );
       	 	om.storeRegisteredUser(ru1);
       	 	om.storeRegisteredUser(ru2);
       	 	
       	 	Category c = om.createCategory(null, "c");
       	 	om.storeCategory(c);
       	 	
       	 	Item i = om.createItem(c, ru1, "i", "this is an item");
       	 	om.storeItem(i);
       	 	
       	 	Auction a = om.createAuction(i, 1, new Date());
       	 	om.storeAuction(a);
       	 	
       	 	Bid b = om.createBid(a, ru2, 2);
       	 	om.storeBid(b);
        }
        catch( Exception e)
        {
            System.err.println( "Exception: " + e );
            e.printStackTrace();
        }
        finally {
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
