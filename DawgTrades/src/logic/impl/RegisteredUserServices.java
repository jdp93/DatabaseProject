package edu.uga.dawgtrades.logic.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import edu.uga.dawgtrades.DTException;
import edu.uga.dawgtrades.model.Attribute;
import edu.uga.dawgtrades.model.AttributeType;
import edu.uga.dawgtrades.model.Auction;
import edu.uga.dawgtrades.model.Bid;
import edu.uga.dawgtrades.model.Category;
import edu.uga.dawgtrades.model.ExperienceReport;
import edu.uga.dawgtrades.model.Item;
import edu.uga.dawgtrades.model.ObjectModel;
import edu.uga.dawgtrades.model.RegisteredUser;

/*
 * @author Seong Wang 
 */

public class RegisteredUserServices{

	private ObjectModel om = null;
	
	public RegisteredUserServices(ObjectModel om) {
		this.om = om;
	}
	
	public List<String> viewProfile(String username) throws DTException {
		// Create empty user
		RegisteredUser model = om.createRegisteredUser();
		
		// Assign username to empty user
		model.setName( username );
		
		// (calls) ObjectModelImpl -> PersistenceImpl -> RegisteredUserManager -> RegisteredUserIterator
		//     which ultimately RETURNS Iterator<RegisteredUser> iter containing the correct profile if it exists
		Iterator<RegisteredUser> iter = om.findRegisteredUser( model );
		
		// User does not exist/is not persistent, throw exception
		if( !iter.hasNext() ) {
			throw new DTException( "RegisteredUserServices.viewProfile: " + username + " does not exist" );
		}
		// User exists, return profile
		else {
			RegisteredUser user = iter.next();
			List<String> profile = new ArrayList<String>();
			
			profile.add( user.getName() );
			profile.add( user.getPassword() );
			profile.add( user.getFirstName() );
			profile.add( user.getLastName() );
			profile.add( user.getEmail() );
			profile.add( user.getPhone() );
			
			if( user.getCanText() ) {
				profile.add( "true" );
			}
			else {
				profile.add( "false" );
			}
			
			return profile;
		}
	}

	public void updateProfile(String username, String password,
			String firstName, String lastName, String email, String phone,
			boolean canText) throws DTException {
		// Create empty user
		RegisteredUser model = om.createRegisteredUser();
		
		// Assign username to empty user
		model.setName( username );
		
		// (calls)ObjectModelImpl -> PersistenceImpl -> RegisteredUserManager -> RegisteredUserIterator
		//     which ultimately RETURNS Iterator<RegisteredUser> iter containing the correct profile if it exists
		Iterator<RegisteredUser> iter = om.findRegisteredUser( model );
		
		// User does not exist, throw exception
		if( !iter.hasNext() ) {
			throw new DTException( "RegisteredUserServices.updateProfile: " + username + " does not exist" );
		}
		// User exists, update profile
		else {
			RegisteredUser user = iter.next();
			
			user.setPassword( password );
			user.setFirstName( firstName );
			user.setLastName( lastName );
			user.setEmail( email );
			user.setPhone( phone );
			user.setCanText( canText );
			
			
			om.storeRegisteredUser( user );
		}
	}

	// Might have to return boolean for email was valid and linked to given username and deal with emailing HTML page with servlet
	public void resetPassword(String username, String email) throws DTException {
		// Create empty user
		RegisteredUser model = om.createRegisteredUser();
		
		// Assign username to empty user
		model.setName( username );
		
		// (calls)->PersistenceImpl.java -> RegisteredUserManager -> RegisteredUserIterator
		//     which ultimately RETURNS Iterator<RegisteredUser> containing the correct profile if it exists
		Iterator<RegisteredUser> iter = om.findRegisteredUser( model );
		
		// User does not exist, throw exception
		if( !iter.hasNext() ) {
			throw new DTException( "RegisteredUserServices.resetPassword: " + username + " does not exist" );
		}
		// User exists, check if email matches given username
		else {
			RegisteredUser user = iter.next();
			
			// Given email does not match user's email in DB, throw exception
			if( !(user.getEmail().equals( email )) ) {
				throw new DTException( "RegisteredUserServices.resetPassword: " + email + " does not match the username " + username );
			}
			// Given email MATCHES user's email in DB, check validity of email
			else {
				Pattern pattern = Pattern.compile( "[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,4}" );
				Matcher matcher = pattern.matcher( email.toUpperCase() );
				
				// Invalid email, throw exception
				if( !matcher.matches() ) {
					throw new DTException( "RegisteredUserServices.resetPassword: " + email + " is not a valid email" );
				}
				// Valid email, email password reset to user
				else {
					// http://stackoverflow.com/questions/16212026/how-to-add-javamail-api-source-code-in-eclise-project
					// http://www.tutorialspoint.com/javamail_api/javamail_api_gmail_smtp_server.htm
					String host = "smtp.gmail.com";
					String to = email;
					String from = "tradedawgs@gmail.com";
					final String nameuser = "tradedawgs";
					final String password = "brainjuice";
					
					Properties props = System.getProperties();
					props.put( "mail.smtp.auth", "true" );				// authenticate user via username and password
					props.put( "mail.smtp.starttls.enable", "true" );	// switch connection to TLS-protected connection before login
					props.put( "mail.smtp.host", host );				// host, Google provides free SMTP mail servers
					props.put( "mail.smtp.port", "587" );				// port number
					
					// Get session object
					Session session = Session.getInstance( props, new javax.mail.Authenticator() {
							protected PasswordAuthentication getPasswordAuthentication() {
								return new PasswordAuthentication( nameuser, password );
							}
					} );
					
					try {
						// Create a default MimeMessage object
						Message message = new MimeMessage( session );
						
						// Set From: header field of the header
						message.setFrom( new InternetAddress( from ) );
						
						// Set To: header field of the header
						message.setRecipients( Message.RecipientType.TO, InternetAddress.parse( to ) );
						
						// Set Subject: header field
						message.setSubject( "DawgTrades: Password Reset Request" );
						
						// Set Message: body field
						message.setText( "Hello " + username + ", your password is: " + user.getPassword() );
						
						// Send email!!!
						Transport.send( message );
						
						System.out.println("Password Reset successfully delivered!");
						
					} catch ( MessagingException e ) {
						System.out.println("RegisteredUserServices.resetPassword: Password Reset to user: " + username + " at email address: " + email + " was not delivered");
						throw new RuntimeException( e );
					}
				}
			}
		}
	}

	public void unregister(String username) throws DTException {
		// Create empty user
		RegisteredUser model = om.createRegisteredUser();
		// Assign username to empty user
		model.setName( username );
		// (calls)ObjectModelImpl -> PersistenceImpl -> RegisteredUserManager -> RegisteredUserIterator
		//     which ultimately RETURNS Iterator<RegisteredUser> iter containing the correct profile if it exists
		Iterator<RegisteredUser> iter = om.findRegisteredUser( model );
		
		// User does not exist, throw exception
		if( !iter.hasNext() ) {
			throw new DTException( "RegisteredUserServices.unregister: " + username + " does not exist" );
		}
		
		RegisteredUser user = iter.next();
		
		// Get items owned by user
		Iterator<Item> itemIter = om.getItem( user );

		while( itemIter.hasNext() ) {
			// Active auction, throw exception
			if( !om.getAuction( itemIter.next() ).getIsClosed() ) {
				throw new DTException( "RegisteredUserServices.unregister: " + username + " has active auctions" );
			}
		}
			
		om.deleteRegisteredUser( user );
	}

	// Assuming ordered LIST (i.e. that the List<Attributes> attributes corresponds in order to Category's AttributeTypes one to one
	public long auctionItem(String username, String catName, String itemName, String description,
			float minPrice, Date expiration, List<String> attributes)
			throws DTException {
		// Create empty Category
		Category cat = om.createCategory();		
		
		// Assign catName to empty Category
		cat.setName( catName );
		
		// Check existence of given catName
	 	Iterator<Category> catIter = om.findCategory( cat );
		
	 	// Category does not exist, throw exception
	 	if( !catIter.hasNext() ) {
	 		throw new DTException( "RegisteredUserServices.auctionItem: " + catName + " does not exist" );
	 	}
	 	// Category exists, check validity of required item attributes
	 	else {
	 		Date date = new Date();
	 		
	 		// minPrice negative, throw exception
	 		if( minPrice < 0 ) {
	 			throw new DTException( "RegisteredUserServices.auctionItem: " + minPrice + " is negative" );
	 		}
	 		// expiration not a future date, throw exception
	 		else if( expiration.before( date ) ) {
	 			throw new DTException( "RegisteredUserServices.auctionItem: " + expiration + " is not a future date" );
	 		}
	 		
	 		Category gory = catIter.next();
	 		Iterator<AttributeType> types = om.getAttributeType( gory );
	 		// No if( !types.hasNext() ) { ..test.. } administered b/c it is possible for no additional admin-made attributetypes to be had on a valid category 
	 		
	 		// Iterate through Item's Attributes (List<Attributes> attributes) and Category's AttributeTypes
	 		//      and see if they match up, specifically compare Attribute's AttributeTypeIDs with catName Category AttributeTypeIDs
	 		//      throw exception if something does not match up
	 		
	 		/*for( Attribute a : attributes ) {
	 			if( a.getAttributeType() != types.next().getId() ) {
	 				throw new DTException( "RegisteredUserServices.auctionItem: Given attribute(s) do not match up with given Category's AttributeTypes" );
	 			}
	 		}*/
	 		
	 		// We know this user exists since he/she is auctioning an item but how to get the RegisteredUser that owns the item
	 		//      that needs to be created and then auctioned
	 		RegisteredUser seller = om.createRegisteredUser();
	 		seller.setName( username );
	 		Iterator<RegisteredUser> user = om.findRegisteredUser( seller );
	 		
	 		// Hopefully this never triggers, but it doesn't hurt to have it
	 		if( !user.hasNext() ) {
	 			throw new DTException( "RegisteredUserServices.auctionItem: " + username + " does not exist" );
	 		}
	 		
		 	// Create Item with proper params
		 	Item it = om.createItem( gory, user.next(), itemName, description );
		 	// Store Item in DB
		 	om.storeItem( it );
	 		Attribute attr;
	 		for( String s : attributes ){
	 			attr = om.createAttribute(types.next(), it, s);
	 			om.storeAttribute(attr);
	 		}
		 	// Create Auction with proper params
		 	Auction auction = om.createAuction( it, minPrice, expiration );
		 	// Store Auction in DB
		 	om.storeAuction( auction );
		 	
		 	return auction.getId();
	 	}
	}

	// According to page 33 of the ReqDoc, return Auctions user is selling
	public List<Auction> viewMyAuctions(String username) throws DTException {
		// Find user via username
		RegisteredUser model = om.createRegisteredUser();
		model.setName( username );
		Iterator<RegisteredUser> modelIter = om.findRegisteredUser( model );
		
		// User does not exist, throw exception
		if( !modelIter.hasNext() ) {
			throw new DTException( "RegisteredUserServices.viewMyAuction: User does not exist" );
		}
		
		// User exists, grep details
		RegisteredUser user = modelIter.next();
		
		//User is admin, throw exception
		if( user.getIsAdmin() ) {
			throw new DTException( "RegisteredUserServices.viewMyauction: User is admin" );
		}
		
		// Get items owned by user
		Iterator<Item> itemIter = om.getItem( user );
		List<Auction> myAuctions = new ArrayList<Auction>();
		
		// Add auctions of items owned by user to list
		while( itemIter.hasNext() ) {
			myAuctions.add( om.getAuction( itemIter.next() ) );
		}
		
		return myAuctions;
	}

	public void reportOnTransaction(long auctionId, int rating,
			String description) throws DTException {
		// Find the auction via auctionId
		Auction model = om.createAuction();
		model.setId( auctionId );
		Iterator<Auction> modelIter = om.findAuction( model );
		
		// Auction does not exist, throw exception
		if( !modelIter.hasNext() ) {
			throw new DTException( "RegisteredUserServices.reportOnTransaction: Auction does not exist" );
		}
		
		// Auction exists, grep details
		Auction auction = modelIter.next();

 		// Auction active, throw exception
		if( !auction.getIsClosed() ){
			throw new DTException( "RegisteredUserServices.reportOnTransaction: Auction is active" );
		}
		
		// Find buyer/reviewer via who has the winning bid
		RegisteredUser reviewer = om.createRegisteredUser();
		Iterator<Bid> bids = om.findBids( auction );
		while( bids.hasNext() ) {
			Bid bid = bids.next();
			if( bid.isWinning() ) {
				reviewer = bid.getRegisteredUser();
			}
		}
		
		// Create item for finding owner/seller/reviewed
		Item item = om.getItem( auction );
		item.getId();
		
		// Create and store the ExperienceReport
		ExperienceReport exp = om.createExperienceReport(reviewer, om.getRegisteredUser( item ), rating, description, auction.getExpiration());
		om.storeExperienceReport( exp );
	}

	public void bidOnItem(String username, long auctionId, float amount) throws DTException {
		// Amount is negative, throw exception
		if( amount < 0 ) {
			throw new DTException( "RegisteredUserServices.bidOnItem: Amount was negative" );
		}
		
		// Find the auction via auctionId
		Auction auction = om.createAuction();
		auction.setId( auctionId );
		Iterator<Auction> auctionIter = om.findAuction( auction );
		
		// Auction does not exist, throw exception
		if( !auctionIter.hasNext() ) {
			throw new DTException( "RegisteredUserServices.bidOnItem: Auction does not exist" );
		}
		Auction auc = auctionIter.next();
 		// Auction not active, throw exception
		if( auc.getIsClosed() ) {
			throw new DTException( "RegisteredUserServices.bidOnItem: Auction not active" );
		}
		if( auc.getMinPrice() > amount ){
			throw new DTException( "RegisteredUserServices.bidOnItem: Bid Amount is too low" );
		}
		// Find the bidder
		RegisteredUser user = om.createRegisteredUser();
		user.setName( username );
		Iterator<RegisteredUser> userIter = om.findRegisteredUser( user );
		
		// Is the bidder persistent?
		if( !userIter.hasNext() ) {
			throw new DTException( "RegisteredUserServices.bidOnItem: " + username + " does not exist" );
		}
		
		// Create and store the bid
		Bid bid = om.createBid(auc, userIter.next(), amount);
		om.storeBid( bid );
	}

	// If there are no auctions this buyer has bids on then it should not throw an exception
	public List<Auction> trackBids(String username) throws DTException {
		// Find user via username
		RegisteredUser buyer = om.createRegisteredUser();
		buyer.setName( username );
		Iterator<RegisteredUser> rUsers = om.findRegisteredUser(buyer);
		if(!rUsers.hasNext()){
			throw new DTException( "RegisteredUserservices.trackBids: Username can not be found" );
		}
		buyer = rUsers.next();
		
		// Get Auctions bid on (user in role of buyer)
		Iterator<Auction> auctionIter = om.findAuctionsBidOn( buyer );
		
		List<Auction> trackedBids = new ArrayList<Auction>();
		
		// Auction(s) do not exist, return empty List
		if( !auctionIter.hasNext() ) {
			// throw new DTException( "RegisteredUserServices.trackBids: Auction do not exist" );
			return trackedBids;
		}
		
		while( auctionIter.hasNext() ) {
			// Auction not active, skip
			if( auctionIter.next().getIsClosed() ) {
				//
			}
			else {
				trackedBids.add( auctionIter.next() );
			}
		}
		return trackedBids;
	}

	// not all too sure on my check for auction sold
	public void reAuctionItem(int auctionId, Date expiration)
			throws DTException {
		Auction model = om.createAuction();
		model.setId( auctionId );
		Iterator<Auction> auctionIter = om.findAuction( model );
		
		// Auction does not exist, throw exception
		if( !auctionIter.hasNext() ) {
			throw new DTException( "RegisteredUserServices.reAuctionItem: Auction does not exist" );
		}
		
		Auction auction = auctionIter.next();
		
		// Auction is active, throw exception
		if( !auction.getIsClosed() ) {
			throw new DTException( "RegisteredUserServices.reAuctionItem: Auction is active" );
		}
		// Auction sold, throw exception
		else if (auction.getSellingPrice() > auction.getMinPrice() ) {
			throw new DTException( "RegisteredUserServices.reAuctionItem: Auction already sold" );
		}
		
		auction.setExpiration( expiration );
		om.storeAuction( auction );
	}

}
