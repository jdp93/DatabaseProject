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
public class AdditionalReadTest {
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
             System.err.println( "AdditionalReadTest: Unable to obtain a database connection" );
         }
         
         // obtain a reference to the ObjectModel module      
         om = new ObjectModelImpl();
         // obtain a reference to Persistence module and connect it to the ObjectModel        
         persistence = new PersistenceImpl( conn, om ); 
         // connect the ObjectModel module to the Persistence module
         om.setPersistence( persistence ); 
         
         try{
        	 //search to get Iterators with all objects
        	 Iterator<Category> cIter = om.findCategory(null);
        	 Iterator<Item> iIter = om.findItem(null);
        	 Iterator<RegisteredUser> rIter = om.findRegisteredUser(null);
        	 //read all objects
        	 Category c;
        	 Category parent;
        	 Iterator<Category> childIter;
        	 Iterator<Item> items;
        	 System.out.println("\nCategories:");
        	 while(cIter != null && cIter.hasNext()){
        		 c = cIter.next();
        		 System.out.print("\n" + c);
        		 parent = om.getParent(c);
        		 items = om.getItem(c);
        		 if(parent != null){
        			 System.out.print(" with parent: " + parent.getName() + " ");
        		 }
        		 childIter = om.getChild(c);
        		 System.out.print(" with children: ");
        		 while(childIter != null && childIter.hasNext()){
        			 System.out.print(childIter.next().getName() + " ");
        		 }
        		 System.out.println("\nWith items:");
        		 while(items != null && items.hasNext()){
        			 System.out.println(items.next().getName());
        		 }
        	 }
        	 Item i;
        	 Iterator<Attribute> attrIter;
        	 AttributeType attrType;
        	 Category catOfAttrType;
        	 Category catOfItem;
        	 Attribute attr;
        	 System.out.println("\nItems:");
        	 while(iIter != null && iIter.hasNext()){
        		 i = iIter.next();
        		 System.out.print("\n" + i);
        		 catOfItem = om.getCategory(i);
        		 System.out.print(" in Category: " + catOfItem.getName());
        		 attrIter = om.getAttribute(i);
        		 System.out.print(" with Attributes of Type: ");
        		 while(attrIter !=null && attrIter.hasNext()){
        			 attr = attrIter.next();
        			 attrType = om.getAttributeType(attr);
        			 catOfAttrType = om.getCategory(attrType);
        			 System.out.println(attrType.getName() + "from category " + catOfAttrType.getName());
        			 System.out.println("\nTest of getItem(Attribute) " + om.getItem(attr).getName());
        		 }
        	 }
        	 RegisteredUser r;
        	 Iterator<Item> myItems;
        	 Item myItem;
        	 RegisteredUser owner;
        	 Auction itemAuction;
        	 System.out.println("\nRegistered Users:");
        	 while(rIter != null && rIter.hasNext()){
        		 r = rIter.next();
        		 System.out.print("\n" + r);
        		 myItems = om.getItem(r);
        		 System.out.println("\nOwns:");
        		 while(myItems != null && myItems.hasNext()){
        			 myItem = myItems.next();
        			 itemAuction = om.getAuction(myItem);
        			 owner = om.getRegisteredUser(myItem);
        			 if(itemAuction != null){
        				 System.out.println(myItem.getName() + "which is owned by " + owner.getName() + " and in auction " + itemAuction + " which is auctioning " + om.getItem(itemAuction).getName());
        			 }
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
