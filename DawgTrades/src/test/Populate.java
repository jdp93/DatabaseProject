package edu.uga.dawgtrades.test;

import java.sql.Connection;
import java.util.Date;
import java.util.Calendar;

import edu.uga.dawgtrades.model.impl.*;
import edu.uga.dawgtrades.model.*;
import edu.uga.dawgtrades.persist.Persistence;
import edu.uga.dawgtrades.persist.impl.DbUtils;
import edu.uga.dawgtrades.persist.impl.PersistenceImpl;

public class Populate {
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
       	 	//create administrators
        	RegisteredUser joseph = om.createRegisteredUser( "jdp", "Joe", "Pallansch", "joepass", true, "jdp93@uga.edu", "333-4456", true );
            RegisteredUser tyler = om.createRegisteredUser( "browning", "Tyler", "Browning", "password", true, "browning@mail.com", "444-9876", true );
            RegisteredUser seong = om.createRegisteredUser( "seong", "Seong", "Wong", "rainbows", true, "seong@uga.edu", "111-1234", true );
            RegisteredUser neel = om.createRegisteredUser( "rana", "neel", "rana", "taylorswift", true, "rana@uga.edu", "234-5678", true );
       	 	om.storeRegisteredUser(joseph);
       	 	om.storeRegisteredUser(tyler);
       	 	om.storeRegisteredUser(seong);
       	 	om.storeRegisteredUser(neel);
       	 	
       	 	//create root categories
       	 	Category computers = om.createCategory(null, "Computers");
       	 	om.storeCategory(computers);
       	 	Category instruments = om.createCategory(null, "Instruments");
       	 	om.storeCategory(instruments);
       	 	
       	 	//create computer subCategories
       	 	Category laptops = om.createCategory(computers, "Laptops");
       	 	om.storeCategory(laptops);
       	 	Category desktops = om.createCategory(computers, "Desktops");
       	 	om.storeCategory(desktops);
       	 	
       	 	//create desktop leaf Categories
       	 	Category fastDesktops = om.createCategory(desktops, "Fast Desktops");
       	 	om.storeCategory(fastDesktops);
       	 	Category slowDesktops = om.createCategory(desktops, "Slow Desktops");
       	 	om.storeCategory(slowDesktops);
       	 	
       	 	//create laptop leaf Categories
       	 	Category bigLaptops = om.createCategory(laptops, "Big Laptops");
       	 	om.storeCategory(bigLaptops);
       	 	Category smallLaptops = om.createCategory(laptops, "Small Laptops");
       	 	om.storeCategory(smallLaptops);
       	 	
       	 	//create instrument subcategories
       	 	Category guitars = om.createCategory(instruments, "Guitars");
       	 	om.storeCategory(guitars);
       	 	Category drums = om.createCategory(instruments, "Drums");
       	 	om.storeCategory(drums);
       	 	
       	 	//create guitar leaf categories
       	 	Category bigGuitars = om.createCategory(guitars, "Big Guitars");
       	 	om.storeCategory(bigGuitars);
       	 	Category smallGuitars = om.createCategory(guitars, "Small Guitars");
       	 	om.storeCategory(smallGuitars);
       	 	
       	 	//create drum leaf categories
       	 	Category loudDrums = om.createCategory(drums, "Loud Drums");
       	 	om.storeCategory(loudDrums);
       	 	Category quietDrums = om.createCategory(drums, "Quiet Drums");
       	 	om.storeCategory(quietDrums);
       	 	
       	 	//create and store attribute types
       	 	AttributeType portabilityFD = om.createAttributeType(fastDesktops, "Portability");
       	 	AttributeType speedFD = om.createAttributeType(fastDesktops, "Speed");
       	 	AttributeType portabilitySD = om.createAttributeType(slowDesktops, "Portability");
    	 	AttributeType speedSD = om.createAttributeType(slowDesktops, "Speed");
    	 	
    	 	AttributeType portabilityBL = om.createAttributeType(bigLaptops, "Portability");
       	 	AttributeType sizeBL = om.createAttributeType(bigLaptops, "Size");
       	 	AttributeType portabilitySL = om.createAttributeType(smallLaptops, "Portability");
    	 	AttributeType sizeSL = om.createAttributeType(smallLaptops, "Size");
    	 	
    	 	om.storeAttributeType(portabilityFD);
    	 	om.storeAttributeType(portabilitySD);
    	 	om.storeAttributeType(portabilityBL);
    	 	om.storeAttributeType(portabilitySL);
    	 	
    	 	om.storeAttributeType(speedFD);
    	 	om.storeAttributeType(speedSD);
    	 	
    	 	om.storeAttributeType(sizeBL);
    	 	om.storeAttributeType(sizeSL);
    	 	
       	 	AttributeType typeBG = om.createAttributeType(bigGuitars, "Type");
       	 	AttributeType iSizeBG = om.createAttributeType(bigGuitars, "Size");
       	 	AttributeType typeSG = om.createAttributeType(smallGuitars, "Type");
       	 	AttributeType iSizeSG = om.createAttributeType(smallGuitars, "Size");
       	 	
       	 	AttributeType typeLD = om.createAttributeType(loudDrums, "Type");
       	 	AttributeType loudnessLD = om.createAttributeType(loudDrums, "Loudness");
       	 	AttributeType typeQD = om.createAttributeType(quietDrums, "Type");
       	 	AttributeType loudnessQD = om.createAttributeType(quietDrums, "Loudness");
       	 	
       	 	om.storeAttributeType(typeBG);
       	 	om.storeAttributeType(typeSG);
       	 	om.storeAttributeType(typeLD);
    	 	om.storeAttributeType(typeQD);
       	 	
    	 	om.storeAttributeType(iSizeBG);
       	 	om.storeAttributeType(iSizeSG);
       	 
       	 	om.storeAttributeType(loudnessLD);
    	 	om.storeAttributeType(loudnessQD);
       	 	
    	 	//create items
    	 	Item racingComp = om.createItem(fastDesktops, joseph, "Racing Computer", "Racing Computer with Racing Stripe");
    	 	Item dyingComp = om.createItem(slowDesktops, tyler, "Dying Computer", "Any day now...");
    	 	om.storeItem(racingComp);
    	 	om.storeItem(dyingComp);
    	 	
    	 	Item airLaptop = om.createItem(smallLaptops, seong, "Laptop Air", "The new mac laptop");
    	 	Item mountainLaptop = om.createItem(bigLaptops, neel, "iPhone278", "Bigger and more powerful than necessary");
    	 	om.storeItem(airLaptop);
    	 	om.storeItem(mountainLaptop);
    	 	
       	 	Item bGuitar = om.createItem(bigGuitars, tyler, "Big Guitar", "It's a tub with strings");
       	 	Item sGuitar = om.createItem(smallGuitars, joseph, "Small Guitar", "It's a shoebox with strings");
       	 	om.storeItem(bGuitar);
       	 	om.storeItem(sGuitar);
       	 	
       	 	Item lDrum = om.createItem(loudDrums, seong, "Bass Drum", "When will the bass drop?");
       	 	Item qDrum = om.createItem(quietDrums, neel, "Snare Drum", "Drumroll Please!");
       	 	om.storeItem(lDrum);
       	 	om.storeItem(qDrum);
       	 	
       	 	//create attributes
       	 	Attribute compFast = om.createAttribute(speedFD, racingComp, "Fast!");
       	 	Attribute compSlow = om.createAttribute(speedSD, dyingComp, "...slow");
       	 	Attribute portFD = om.createAttribute(portabilityFD, racingComp, "Not!");
       	 	Attribute portSD = om.createAttribute(portabilitySD, dyingComp, "Not!");
       	 	om.storeAttribute(compFast);
       	 	om.storeAttribute(compSlow);
       	 	om.storeAttribute(portFD);
       	 	om.storeAttribute(portSD);
       	 	
       	 	Attribute compBig = om.createAttribute(sizeBL, mountainLaptop, "BIG!");
       	 	Attribute compSmall = om.createAttribute(sizeSL, airLaptop, "small");
       	 	Attribute portBL = om.createAttribute(portabilityBL, mountainLaptop, "Very!");
       	 	Attribute portSL = om.createAttribute(portabilitySL, airLaptop, "Very!");
       	 	om.storeAttribute(compBig);
       	 	om.storeAttribute(compSmall);
       	 	om.storeAttribute(portBL);
       	 	om.storeAttribute(portSL);
       	 	
       	 	Attribute instBig = om.createAttribute(iSizeBG, bGuitar, "BIG!");
       	 	Attribute instSmall = om.createAttribute(iSizeSG, sGuitar, "small");
       	 	Attribute instTypeBG = om.createAttribute(typeBG, bGuitar, "String");
       	 	Attribute instTypeSG = om.createAttribute(typeSG, sGuitar, "String");
       	 	om.storeAttribute(instBig);
    	 	om.storeAttribute(instSmall);
    	 	om.storeAttribute(instTypeBG);
    	 	om.storeAttribute(instTypeSG);
       	 	
    	 	Attribute loudAttr = om.createAttribute(loudnessLD, lDrum, "loud");
    	 	Attribute quietAttr = om.createAttribute(loudnessQD, qDrum, "QUIET!");
    	 	Attribute instTypeLD = om.createAttribute(typeLD, lDrum, "Percussion");
    	 	Attribute instTypeQD = om.createAttribute(typeQD, qDrum, "Percussion");
    	 	om.storeAttribute(loudAttr);
    	 	om.storeAttribute(quietAttr);
    	 	om.storeAttribute(instTypeLD);
    	 	om.storeAttribute(instTypeQD);
    	 	
    	 	//create new date for auctions
    	 	Calendar c = Calendar.getInstance();
    	 	Date d = new Date();
    	 	c.setTime(d);
    	 	c.add(Calendar.DATE, 2);
    	 	d = c.getTime();
    	 	
    	 	//create auctions
       	 	Auction racingCompAuction = om.createAuction(racingComp, 1, d);
       	 	Auction dyingCompAuction = om.createAuction(dyingComp, 1, d);
       	 	Auction mountainLaptopAuction = om.createAuction(mountainLaptop, 200, new Date());
       	 	Auction bigGuitarAuction = om.createAuction(bGuitar, 2, d);
       	 	Auction quietDrumAuction = om.createAuction(qDrum, 2, new Date());
       	 	om.storeAuction(racingCompAuction);
       	 	om.storeAuction(dyingCompAuction);
       	 	om.storeAuction(mountainLaptopAuction);
       	 	om.storeAuction(bigGuitarAuction);
       	 	om.storeAuction(quietDrumAuction);
       	 	//create bids
       	 	Bid b1 = om.createBid(racingCompAuction, neel, 2);
       	 	om.storeBid(b1);
       	 	Bid b2 = om.createBid(racingCompAuction, tyler, 3);
    	 	om.storeBid(b2);
    	 	Bid b3 = om.createBid(bigGuitarAuction, seong, 3);
       	 	om.storeBid(b3);
       	 	Bid b4 = om.createBid(dyingCompAuction, joseph, 2);
    	 	om.storeBid(b4);
       	 	
    	 	RegisteredUser taylor = om.createRegisteredUser("taylor", "taylor", "swift", "neelrana", false, "taylor@mail", "000-0000", false);
    	 	om.storeRegisteredUser(taylor);
    	 	
       	 	ExperienceReport e1 = om.createExperienceReport(joseph, tyler, 2, "Blah", new Date());
       	 	ExperienceReport e2 = om.createExperienceReport(neel, taylor, 5, "The BEST!!", new Date());
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
