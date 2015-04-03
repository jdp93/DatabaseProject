package edu.uga.dawgtrades.logic.impl;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import edu.uga.dawgtrades.DTException;
import edu.uga.dawgtrades.logic.Logic;
import edu.uga.dawgtrades.model.Attribute;
import edu.uga.dawgtrades.model.Auction;
import edu.uga.dawgtrades.model.Item;
import edu.uga.dawgtrades.model.ObjectModel;
import edu.uga.dawgtrades.model.Persistable;

/**
 * 
 * @author Joseph Browning
 *
 */

public class LogicImpl implements Logic{

	private ObjectModel om = null;
	GeneralServices gs = null;
	AdministratorServices as = null;
	RegisteredUserServices rus = null;
	TimerServices ts = null;
	
	public LogicImpl( ObjectModel om){
		this.om = om;
		gs = new GeneralServices(om);
		as = new AdministratorServices(om);
		rus = new RegisteredUserServices(om);
		ts = new TimerServices(om);
	}
	
	@Override
	public void login(String username, String password) throws DTException {
		gs.login(username, password);
	}

	@Override
	public void logout() throws DTException {
		gs.logout();
	}

	@Override
	public List<Persistable> browseCategory(String catName) throws DTException {
		return gs.browseCategory(catName);
	}
	
	@Override
	public List<Persistable> browseRoot() throws DTException {
		return gs.browseRoot();
	}

	@Override
	public List<Item> findItems(String catName, List<String> attributes)
			throws DTException {
		return gs.findItems(catName, attributes);
	}

	@Override
	public void register(String username, String password, String firstName,
			String lastName, String email, String phone, boolean canText)
			throws DTException {
		gs.register(username, password, firstName, lastName, email, phone, canText);
	}

	@Override
	public void defineCategory(String name, String parentName,
			LinkedList<String> attributeTypes) throws DTException {
		as.defineCategory(name, parentName, attributeTypes);
	}
	
	@Override
	public void defineRoot(String name,
			LinkedList<String> attributeTypes) throws DTException {
		as.defineRoot(name, attributeTypes);
	}

	@Override
	public void updateCategory(String name, String newName,
			String newParentName, LinkedList<String> newAttributeTypes)
			throws DTException {
		as.updateCategory(name, newName, newParentName, newAttributeTypes);
	}

	@Override
	public void deleteCategory(String name) throws DTException {
		as.deleteCategory(name);
	}

	@Override
	public void deleteItem(long itemId) throws DTException {
		as.deleteItem(itemId);
	}

	@Override
	public String printReport(Date from, Date to) throws DTException {
		return as.printReport(from, to);
	}

	@Override
	public void setMembershipPrice(float price) throws DTException {
		as.setMembershipPrice(price);
	}

	@Override
	public void deleteUser(String username) throws DTException {
		as.deleteUser(username);
	}

	@Override
	public List<String> viewProfile(String username) throws DTException {
		return rus.viewProfile(username);
	}

	@Override
	public void updateProfile(String username, String password,
			String firstName, String lastName, String email, String phone,
			boolean canText) throws DTException {
		rus.updateProfile(username, password, firstName, lastName, email, phone, canText);
	}

	@Override
	public void resetPassword(String username, String email) throws DTException {
		rus.resetPassword(username, email);
	}

	@Override
	public void unregister(String username) throws DTException {
		rus.unregister(username);
	}

	@Override
	public long auctionItem(String username, String catName, String itemName,
			String description, float minPrice, Date expiration,
			List<String> attributes) throws DTException {
		return rus.auctionItem(username, catName, itemName, description, minPrice, expiration, attributes);
	}

	@Override
	public List<Auction> viewMyAuctions(String username) throws DTException {
		return rus.viewMyAuctions(username);
	}

	@Override
	public void reportOnTransaction(long auctionId, int rating,
			String description) throws DTException {
		rus.reportOnTransaction(auctionId, rating, description);
	}

	@Override
	public void bidOnItem(String username, long auctionId, float amount)
			throws DTException {
		rus.bidOnItem(username, auctionId, amount);
	}

	@Override
	public List<Auction> trackBids(String username) throws DTException {
		return rus.trackBids(username);
	}

	@Override
	public void reAuctionItem(int auctionId, Date expiration)
			throws DTException {
		rus.reAuctionItem(auctionId, expiration);
	}

	@Override
	public void autoLogout() {
		ts.autoLogout();
	}

	@Override
	public void closeAuction() throws DTException{
		ts.closeAuction();
	}

}
