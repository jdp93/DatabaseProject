package edu.uga.dawgtrades.logic;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import edu.uga.dawgtrades.DTException;
import edu.uga.dawgtrades.model.Attribute;
import edu.uga.dawgtrades.model.Auction;
import edu.uga.dawgtrades.model.Item;
import edu.uga.dawgtrades.model.Persistable;

public interface Logic {

	/**
	 * Login to the dawgtrades system.  Throws a DTException
	 * if the username doesn't exist
	 * or if the supplied password doesn't match the stored password.
	 * 
	 * @param username
	 * @param password
	 * @throws DTException
	 */
	public void login(String username, String password)
		throws DTException;
	
	/**
	 * Logout of the dawgtrades system.
	 * 
	 * @throws DTException
	 */
	public void logout() throws DTException;
	
	/**
	 * Browse a Category in the dawgtrades system.  Returns a list of 
	 * subcategories and Items in the Category.  Throws a DTException
	 * if the Category doesn't exist.
	 * 
	 * @param catName
	 * @return a list of subcategories and items
	 * @throws DTException
	 */
	public List<Persistable> browseCategory(String catName)
		throws DTException;
	
	/**
	 * Browse a root Category in the dawgtrades system.  Returns a list of 
	 * subcategories and Items in the Category.  Throws a DTException
	 * if the Category doesn't exist.
	 * 
	 * @return a list of subcategories and items
	 * @throws DTException
	 */
	public List<Persistable> browseRoot()
		throws DTException;
	
	/**
	 * Find items by their category name and a subset of values for
	 * that Category's attributeTypes.  Throws a DTException
	 * if the Category doesn't exist.
	 * 
	 * @param catName
	 * @param attributes
	 * @return a list of Items
	 * @throws DTException
	 */
	public List<Item> findItems(String catName, 
			List<String> attributes) throws DTException;
	
	/**
	 * Register a new User with the dawgtrades system. Throws a DTException
	 * if the username already exists.
	 * 
	 * @param username
	 * @param password
	 * @param firstName
	 * @param lastName
	 * @param email
	 * @param phone
	 * @param canText
	 * @throws DTException
	 */
	public void register(String username, String password, String firstName,
			String lastName, String email, String phone, boolean canText)
			throws DTException;
	
	/**
	 * Define a new Category.  Throws a DTException 
	 * if the Category already exists
	 * or the parent Category does NOT already exist.
	 * 
	 * @param name
	 * @param parentName
	 * @param attributeTypes
	 * @return
	 * @throws DTException
	 */
	public void defineCategory(String name, String parentName, 
			LinkedList<String> attributeTypes) throws DTException;
	
	/**
	 * Define a new root Category.  Throws a DTException 
	 * if the Category already exists
	 * 
	 * @param name
	 * @param attributeTypes
	 * @return
	 * @throws DTException
	 */
	public void defineRoot(String name, 
			LinkedList<String> attributeTypes) throws DTException;
	
	/**
	 * Update an already existing Category.  Throws a DTException
	 * if the Category doesn't exist,
	 * or if the new Category name already exists,
	 * or if the new parent Category doesn't exist,
	 * or if there are any items in the Category 
	 * 
	 * @param name
	 * @param newName
	 * @param newParentName
	 * @param newAttributeTypes
	 * @throws DTException
	 */
	public void updateCategory(String name, String newName, String newParentName, 
			LinkedList<String> newAttributeTypes) throws DTException;
	
	/**
	 * Delete an already existing Category.  Throws a DTException
	 * if the Category doesn't exist
	 * or if there are any Items in the Category.
	 * 
	 * @param name
	 * @return
	 * @throws DTException
	 */
	public void deleteCategory(String name) throws DTException;
	
	/**
	 * Delete an already existing Item, and also the associated auction
	 * if it exists.  Throws a DTException
	 * if the Item doesn't exist.
	 * 
	 * @param itemId
	 * @throws DTException
	 */
	public void deleteItem(long itemId) throws DTException;
	
	/**
	 * Print a report on the system including:
	 * number of members,
	 * total of paid memebership fees,
	 * number of completed auctions,
	 * total value of completed auctions,
	 * number of ongoing auctions
	 * 
	 * @param from
	 * @param to
	 * @return a String of various statistics about the dawgtrades system
	 * @throws DTException
	 */
	public String printReport(Date from, Date to) throws DTException;
	
	/**
	 * Set the memberhsip price for using the dawgtrades system.  Throws
	 * a DTException
	 * if price is non-negative or non-numeric
	 * 
	 * @param price
	 * @throws DTException
	 */
	public void setMembershipPrice(float price) throws DTException;

	/**
	 * Delete a RegisteredUser from the dawgtrades system.  Throws
	 * a DTException
	 * if the username doesn't exist.
	 * 
	 * @param username
	 * @throws DTException
	 */
	public void deleteUser(String username) throws DTException;
	
	/**
	 * View a user's profile. Throws a DTException 
	 * if the username doesn't exist.
	 * 
	 * @param username
	 * @return username, password, firstName, lastName, email, phone, canText
	 * @throws DTException
	 */
	public List<String> viewProfile(String username) throws DTException;
	
	/**
	 * Update the user's profile.  Throws a DTException
	 * if the username doesn't exist.
	 * 
	 * @param username
	 * @param password
	 * @param firstName
	 * @param lastName
	 * @param email
	 * @param phone
	 * @param canText
	 * @throws DTException
	 */
	public void updateProfile(String username, String password, 
			String firstName, String lastName, String email, 
			String phone, boolean canText) throws DTException;
	
	/**
	 * Send an email to the user's email with his password.
	 * Throws a DTException 
	 * if the username doesn't exist,
	 * or if the email doesn't match the username,
	 * or if the email isn't a valid email.
	 * 
	 * @param email
	 * @throws DTException
	 */
	public void resetPassword(String username, String email) throws DTException;
	
	/**
	 * Unregister the user.  Throws a DTException 
	 * if the username doesn't exist,
	 * or if the user has active auctions.
	 * 
	 * @param username
	 * @throws DTException
	 */
	public void unregister(String username) throws DTException;
	
	/**
	 * Auction an Item on the dawgtrades system.  Throws a DTException
	 * if the Category doesn't exist,
	 * or if the minPrice is negative or non-numeric,
	 * or if the expiration is not a future date,
	 * or if the attributes don't match the Category's attributeTypes
	 * 
	 * @param username
	 * @param catName
	 * @param itemName
	 * @param description
	 * @param minPrice
	 * @param expiration
	 * @param attributes
	 * @return the auctionId of the created Auction
	 * @throws DTException
	 */
	public long auctionItem(String username, String catName, String itemName, 
			String description, float minPrice, Date expiration,
			List<String> attributes) throws DTException;
	
	/**
	 * View a user's active auctions. Throws a DTException
	 * if the username doesn't exist, 
	 * or if the user is an admin.
	 * 
	 * @param username
	 * @return a list of the user's auctions
	 */
	public List<Auction> viewMyAuctions(String username) throws DTException;
	
	/**
	 * Report on a transaction between two registeredUsers.  
	 * Throws a DTException
	 * if the auction doesn't exist,
	 * or if the auction isn't over yet.
	 * 
	 * @param auctionId
	 * @param rating
	 * @param description
	 * @throws DTException
	 */
	public void reportOnTransaction(long auctionId, int rating,
			String description) throws DTException;
	
	/**
	 * Bid on an active auction in the dawgtrades system.  
	 * Throws a DTException 
	 * if the Auction doesn't exist,
     * or if the auction is not active,
	 * or if the amount is negative or non-numeric.
	 * 
	 * @param username
	 * @param auctionId
	 * @param amount
	 * @throws DTException
	 */
	public void bidOnItem(String username, long auctionId, float amount) throws DTException;
	
	/**
	 * Tracks auctions that the user has bid on in the dawgtrades system.
	 * Throws a DTException 
	 * if the auction doesn't exist
	 * 
	 * @param auctionId
	 * @throws DTException
	 */
	public List<Auction> trackBids(String username) throws DTException;
	
	/**
	 * Re-Auction an item that has been previously auctioned.
	 * Throws a DTException 
	 * If the auction is active,
	 * or if the item was sold(there was a bid higher than the minPrice).
	 * 
	 * @param auctionId
	 * @param expiration
	 * @throws DTException
	 */
	public void reAuctionItem(int auctionId, Date expiration)
			throws DTException;
	
	/**
	 * Logs the user out of the system after 15 minutes 
	 * of inactivity
	 */
	public void autoLogout();
	
	/**
	 * Close every Auction for which its time has expired.
	 * Notify the seller.
	 * If a bid was placed, notify the highest bidder.
	 * 
	 */
	public void closeAuction() throws DTException;
}
