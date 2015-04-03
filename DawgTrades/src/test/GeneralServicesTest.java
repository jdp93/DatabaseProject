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
public class GeneralServicesTest {
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
             System.err.println( "GeneralServicesTest: Unable to obtain a database connection" );
         }
         
         // obtain a reference to the ObjectModel module      
         om = new ObjectModelImpl();
         // obtain a reference to Persistence module and connect it to the ObjectModel        
         persistence = new PersistenceImpl( conn, om ); 
         // connect the ObjectModel module to the Persistence module
         om.setPersistence( persistence ); 
         //create new RegisteredUser services
         GeneralServices gServices = new GeneralServices(om);
         try{
        	 //browse Category test
        	 System.out.println(gServices.browseCategory("c"));
        	 System.out.println(gServices.browseCategory("sc"));
        	 
        	 //register test
        	 gServices.register("bob", "weed", "bob", "marley", "chill@beach.edu", "420-2474", true);
        	 
        	 //find items test
        	 LinkedList<String> attributes = new LinkedList<String>();
        	 attributes.add("Attribute1");
        	 attributes.add("Attribute2");
        	 System.out.println(gServices.findItems("sc", attributes));
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
