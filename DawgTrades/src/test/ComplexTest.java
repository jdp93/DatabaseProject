package edu.uga.dawgtrades.test;

import java.sql.Connection;
import java.util.Date;

import edu.uga.dawgtrades.model.impl.*;
import edu.uga.dawgtrades.model.*;
import edu.uga.dawgtrades.persist.Persistence;
import edu.uga.dawgtrades.persist.impl.DbUtils;
import edu.uga.dawgtrades.persist.impl.PersistenceImpl;

public class ComplexTest {
	public static void main(String[] args){
		Connection  conn = null;
    	ObjectModel om = null;
    	Persistence persistence = null;

    	//get a database connection
    	try {
    		conn = DbUtils.connect();
    	} 
    	catch (Exception seq) {
        	System.err.println( "ComplexTest: Unable to obtain a database connection" );
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
       	 	
       	 	Category sc = om.createCategory(c, "sc");
       	 	om.storeCategory(sc);
       	 	
       	 	AttributeType attrType1 = om.createAttributeType(c, "Parent Attribute Type");
       	 	AttributeType attrType2 = om.createAttributeType(sc, "Attribute Type");
       	 	om.storeAttributeType(attrType1);
       	 	om.storeAttributeType(attrType2);
       	 	
       	 	Item i = om.createItem(sc, ru1, "i", "this is an item");
       	 	om.storeItem(i);
       	 	
       	 	Attribute attr1 = om.createAttribute(attrType1, i, "Attribute1");
       	 	Attribute attr2 = om.createAttribute(attrType2, i, "Attribute2");
       	 	om.storeAttribute(attr1);
       	 	om.storeAttribute(attr2);
       	 	
       	 	Auction a = om.createAuction(i, 1, new Date());
       	 	om.storeAuction(a);
       	 		
       	 	Bid b1 = om.createBid(a, ru2, 2);
       	 	om.storeBid(b1);
       	 	
       	 	ExperienceReport e1 = om.createExperienceReport(ru2, ru1, 5, "Report", new Date());
       	 	ExperienceReport e2 = om.createExperienceReport(ru1, ru2, 4, "Different Report", new Date());
       	 	om.storeExperienceReport(e1);
       	 	om.storeExperienceReport(e2);
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
