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
public class AdministratorServicesTest {
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
             System.err.println( "AdministratorServicesTest: Unable to obtain a database connection" );
         }
         
         // obtain a reference to the ObjectModel module      
         om = new ObjectModelImpl();
         // obtain a reference to Persistence module and connect it to the ObjectModel        
         persistence = new PersistenceImpl( conn, om ); 
         // connect the ObjectModel module to the Persistence module
         om.setPersistence( persistence ); 
         //create new RegisteredUser services
         AdministratorServices aServices = new AdministratorServices(om);
         try{
        	 //define Category test
        	 LinkedList<String> attributeTypes = new LinkedList<String>();
        	 attributeTypes.add("sscAttrType1");
        	 attributeTypes.add("sscAttrType2");
        	 aServices.defineCategory("ssc", "sc", attributeTypes);
        	 
        	 //update Category test
        	 LinkedList<String> diffAttrTypes = new LinkedList<String>();
        	 diffAttrTypes.add("blahAttrType1");
        	 diffAttrTypes.add("blahAttrType2");
        	 aServices.updateCategory("ssc", "blahCategory", "c", diffAttrTypes);
        	 
        	 //delete Category test
        	 aServices.deleteCategory("blahCategory");
        	 
        	 //delete Item test
        	 aServices.deleteItem(1);
        	 
        	 //delete User
        	 aServices.deleteUser("joe");
        	 
        	 //set membership price
        	 aServices.setMembershipPrice(20);
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
