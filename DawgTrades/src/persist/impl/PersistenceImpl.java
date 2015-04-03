package edu.uga.dawgtrades.persist.impl;

import java.sql.Connection;
import java.util.Iterator;

import edu.uga.dawgtrades.DTException;
import edu.uga.dawgtrades.model.Attribute;
import edu.uga.dawgtrades.model.AttributeType;
import edu.uga.dawgtrades.model.Auction;
import edu.uga.dawgtrades.model.Bid;
import edu.uga.dawgtrades.model.Category;
import edu.uga.dawgtrades.model.ExperienceReport;
import edu.uga.dawgtrades.model.Item;
import edu.uga.dawgtrades.model.Membership;
import edu.uga.dawgtrades.model.RegisteredUser;
import edu.uga.dawgtrades.model.ObjectModel;
import edu.uga.dawgtrades.persist.Persistence;

public class PersistenceImpl
	implements Persistence {
	private AttributeManager attrManager = null;
	private AttributeTypeManager attrtypeManager = null;
	private AuctionManager auctManager = null;
	private BidManager bidManager = null;
	private CategoryManager categManager = null;
	private ItemManager itemManager = null;
	private MembershipManager membershipManager = null;
	private RegisteredUserManager reguserManager = null;
	private ExperienceReportManager exprepManager = null;

	public PersistenceImpl( Connection conn, ObjectModel objModel) {
		attrManager = new AttributeManager( conn, objModel );
		attrtypeManager = new AttributeTypeManager( conn, objModel );
		auctManager = new AuctionManager( conn, objModel );
		bidManager = new BidManager( conn, objModel );
		categManager = new CategoryManager( conn, objModel );
		itemManager = new ItemManager( conn, objModel );
		membershipManager = new MembershipManager( conn, objModel );
		reguserManager = new RegisteredUserManager( conn, objModel );
		exprepManager = new ExperienceReportManager( conn, objModel );
	}
	
	//Attribute
	public void saveAttribute(Attribute attribute) throws DTException {
		attrManager.save( attribute );
	}

	public Iterator<Attribute> restoreAttribute(Attribute attribute)
			throws DTException {
		return attrManager.restore( attribute );
	}

	public void deleteAttribute(Attribute attribute) throws DTException {
		attrManager.delete( attribute );
	}

	//AttributeType
	public void saveAttributeType(AttributeType attributeType)
			throws DTException {
		attrtypeManager.save( attributeType );
	}

	public Iterator<AttributeType> restoreAttributeType(
			AttributeType attributeType) throws DTException {
		return attrtypeManager.restore( attributeType );
	}

	public void deleteAttributeType(AttributeType attributeType)
			throws DTException {
		attrtypeManager.delete( attributeType );
	}

	//Auction
	public void saveAuction(Auction auction) throws DTException {
		auctManager.save( auction );
	}

	public Iterator<Auction> restoreAuction(Auction auction) throws DTException {
		return auctManager.restore( auction );
	}
	
	public Iterator<RegisteredUser> restoreBidders( Auction auction ) throws DTException {
		return auctManager.restoreBidders( auction);
	}

	public Iterator<Bid> restoreBids(Auction auction) throws DTException {
		return auctManager.restoreBids( auction );
	}

	public void deleteAuction(Auction auction) throws DTException {
		auctManager.delete( auction );
	}

	//Bid
	public void saveBid(Bid bid) throws DTException {
		bidManager.save( bid );
	}

	public Iterator<Bid> restoreBid(Bid bid) throws DTException {
		return bidManager.restore( bid );
	}

	public void deleteBid(Bid bid) throws DTException {
		bidManager.delete( bid );
	}

	//Category
	public void saveCategory(Category category) throws DTException {
		categManager.save( category );
	}

	public Iterator<Category> restoreCategory(Category category)
			throws DTException {
		return categManager.restore( category );
	}

	public void deleteCategory(Category category) throws DTException {
		categManager.delete( category );
	}

	//Item
	public void saveItem(Item item) throws DTException {
		itemManager.save( item );
	}

	public Iterator<Item> restoreItem(Item item) throws DTException {
		return itemManager.restore( item );
	}

	public void deleteItem(Item item) throws DTException {
		itemManager.delete( item );
	}

	//Membership
	public void saveMembership(Membership membership) throws DTException {
		membershipManager.save( membership );
	}

	public Membership restoreMembership(Membership membership)
			throws DTException {
		return membershipManager.restore( membership );
	}

	public void deleteMembership(Membership membership) throws DTException {
		membershipManager.delete( membership );
	}

	//RegisteredUser
	public void saveRegisteredUser(RegisteredUser registeredUser)
			throws DTException {
		reguserManager.save( registeredUser );
	}

	public Iterator<RegisteredUser> restoreRegisteredUser(
			RegisteredUser registeredUser) throws DTException {
		return reguserManager.restore( registeredUser );
	}
	
	public Iterator<Auction> restoreAuctionsBidOn( RegisteredUser registeredUser ) throws DTException {
		return reguserManager.restoreAuctionsBidOn( registeredUser );
	}
	
	public Iterator<RegisteredUser> restoreReviewers(
			RegisteredUser registeredUser) throws DTException {
		return reguserManager.restoreReviewers( registeredUser );
	}

	public Iterator<RegisteredUser> restoreReviewed(
			RegisteredUser registeredUser) throws DTException {
		return reguserManager.restoreReviewed( registeredUser );
	}
	
	public void deleteRegisteredUser(RegisteredUser registeredUser)
			throws DTException {
		reguserManager.delete( registeredUser );
	}

	//ExperienceReport
	public void saveExperienceReport(ExperienceReport expReport)
			throws DTException {
		exprepManager.save( expReport);
	}

	public Iterator<ExperienceReport> restoreExperienceReport(
			ExperienceReport expReport) throws DTException {
		return exprepManager.restore( expReport );
	}

	public void deleteExperienceReport(ExperienceReport expReport)
			throws DTException {
		exprepManager.delete( expReport );
	}
}
