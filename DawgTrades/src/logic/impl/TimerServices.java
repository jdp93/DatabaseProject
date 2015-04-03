package edu.uga.dawgtrades.logic.impl;

import java.util.Iterator;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.uga.dawgtrades.DTException;
import edu.uga.dawgtrades.model.*;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class TimerServices{

    private ObjectModel om = null;
    
    public TimerServices(ObjectModel om){
    	this.om = om;
    }
    
    public void autoLogout(){}
	
	public void closeAuction() throws DTException{
		// Create empty auction
		Auction model = om.createAuction();
		//returns a list of auctions
		Iterator<Auction> auctionIter = om.findAuction( model );
		
		while( auctionIter.hasNext() ) {
			Auction auction = auctionIter.next();
			if (auction.getIsClosed()){
				Item item = om.getItem(auction);
				RegisteredUser seller = om.getRegisteredUser(item);
				Iterator<Bid> bids = om.findBids(auction);
				RegisteredUser buyer = null;
				Bid winningBid = null;
				
				//loop through the Auction's Bids and see if fone of them won
				while (bids.hasNext()){
					Bid currentBid = bids.next();
					if (currentBid.isWinning()){
						winningBid = currentBid;
						buyer = currentBid.getRegisteredUser();
					}
				}
				
				//prepare to send out the emails
				String email = seller.getEmail();
				// Given email MATCHES user's email in DB, check validity of email
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
				
				//if no bidders then send out email to the seller only
				if(buyer == null) {
					
					try {
						// Create a default MimeMessage object
						Message message = new MimeMessage( session );
						// Set From: header field of the header
						message.setFrom( new InternetAddress( from ) );
						// Set To: header field of the header
						message.setRecipients( Message.RecipientType.TO, InternetAddress.parse( seller.getEmail() ) );
						// Set Subject: header field
						message.setSubject( "DawgTrades: Auction Expired" );
						// Set Message: body field
						message.setText( "Hello, Your auction " + auction + " has expired with no winning bidders" +
								" and has been taken off the system."
								+ "\n\tYou can re-auction your item if you wish to do so.");
						// Send email
						Transport.send( message );
						System.out.println("Notification to seller successful!");
						
					} catch ( MessagingException e ) {
						System.out.println("TimerServices.closeAuction: notification to seller: " + seller.getName() + 
								" at email address: " + seller.getEmail() + " was not successful");
						throw new RuntimeException( e );
					}
				}
				
				//else if the auction sold then send out email to the seller and buyer.
				else{
					
					try {
						// Create a default MimeMessage object
						Message message = new MimeMessage( session );
						// Set From: header field of the header
						message.setFrom( new InternetAddress( from ) );
						// Set To: header field of the header
						message.setRecipients( Message.RecipientType.TO, InternetAddress.parse( seller.getEmail() ) );
						// Set Subject: header field
						message.setSubject( "DawgTrades: Auction Sold" );
						// Set Message: body field
						message.setText( "Hello, Your auction " + auction + " has been sold for " 
								+ winningBid.getAmount() +"\n\n" 
								+ "Please contact " + buyer.getName() + " at " + buyer.getEmail() 
								+ "to complete the transaction.");
						// Send email
						Transport.send( message );
						System.out.println("Notification to seller successful!");
						
					} catch ( MessagingException e ) {
						System.out.println("TimerServices.closeAuction: notification to seller: " + seller.getName() + 
								" at email address: " + seller.getEmail() + " was not successful");
						throw new RuntimeException( e );
					}
					
					try {
						// Create a default MimeMessage object
						Message message = new MimeMessage( session );
						// Set From: header field of the header
						message.setFrom( new InternetAddress( from ) );
						// Set To: header field of the header
						message.setRecipients( Message.RecipientType.TO, InternetAddress.parse( buyer.getEmail() ) );
						// Set Subject: header field
						message.setSubject( "DawgTrades: Auction Won" );
						// Set Message: body field
						message.setText( "Hello, Your bid for " + auction + " has won at " 
								+ winningBid.getAmount() +"\n\n" 
								+ "Please contact " + seller.getName() + " at " + seller.getEmail() 
								+ "to complete the transaction.");
						// Send email
						Transport.send( message );
						System.out.println("Notification to buyer successful!");
						
					} catch ( MessagingException e ) {
						System.out.println("TimerServices.closeAuction: notification to buyer: " + buyer.getName() + 
								" at email address: " + buyer.getEmail() + " was not successful");
						throw new RuntimeException( e );
					}
				}
				
				// delete the non-active auction
				om.deleteAuction( auction );
				}
			}
		}
	}
}