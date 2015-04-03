package edu.uga.dawgtrades.model.impl;

import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import edu.uga.dawgtrades.DTException;
import edu.uga.dawgtrades.model.Attribute;
import edu.uga.dawgtrades.model.AttributeType;
import edu.uga.dawgtrades.model.Auction;
import edu.uga.dawgtrades.model.Bid;
import edu.uga.dawgtrades.model.Category;
import edu.uga.dawgtrades.model.ExperienceReport;
import edu.uga.dawgtrades.model.Item;
import edu.uga.dawgtrades.model.Membership;
import edu.uga.dawgtrades.model.ObjectModel;
import edu.uga.dawgtrades.model.RegisteredUser;
import edu.uga.dawgtrades.persist.Persistence;

/**
 * 
 * @author Joseph Browning
 *
 */
public class ObjectModelImpl implements ObjectModel {
	
	Persistence persistence = null;
	
	public ObjectModelImpl(){
        this.persistence = null;
    }
    
    public ObjectModelImpl( Persistence persistence ){
        this.persistence = persistence;
    }
    
    @Override
    public void setPersistence(Persistence persistence){
    	this.persistence = persistence;
    }

	@Override
	public Category createCategory(Category parent, String name)
			throws DTException {
		return new CategoryImpl(parent, name);
	}

	@Override
	public Category createCategory() {
		return new CategoryImpl();
	}

	@Override
	public Iterator<Category> findCategory(Category modelCategory) throws DTException {
		return persistence.restoreCategory(modelCategory);
	}

	@Override
	public void storeCategory(Category category) throws DTException {
		persistence.saveCategory(category);
	}

	@Override
	public void deleteCategory(Category category) throws DTException {
		persistence.deleteCategory(category);
	}

	@Override
	public AttributeType createAttributeType(Category category, String name) throws DTException {
		return new AttributeTypeImpl(category, name);
	}

	@Override
	public AttributeType createAttributeType() {
		return new AttributeTypeImpl();
	}

	@Override
	public void storeAttributeType(AttributeType attributeType) throws DTException {
		persistence.saveAttributeType(attributeType);
	}

	@Override
	public void deleteAttributeType(AttributeType attributeType) throws DTException {
		persistence.deleteAttributeType(attributeType);
	}

	@Override
	public Item createItem(Category category, RegisteredUser user,
			String name, String description) throws DTException {
		return new ItemImpl(category, user, name, description);
	}

	@Override
	public Item createItem() {
		return new ItemImpl();
	}

	@Override
	public Iterator<Item> findItem(Item modelItem) throws DTException {
		return persistence.restoreItem(modelItem);
	}

	@Override
	public void storeItem(Item item) throws DTException {
		persistence.saveItem(item);
	}

	@Override
	public void deleteItem(Item item) throws DTException {
		persistence.deleteItem(item);
	}

	@Override
	public Attribute createAttribute(AttributeType attributeType, Item item,
			String value) throws DTException {
		return new AttributeImpl(attributeType, item, value);
	}

	@Override
	public Attribute createAttribute() {
		return new AttributeImpl();
	}

	@Override
	public void storeAttribute(Attribute attribute) throws DTException {
		persistence.saveAttribute(attribute);
	}

	@Override
	public void deleteAttribute(Attribute attribute) throws DTException {
		persistence.deleteAttribute(attribute);
	}

	@Override
	public Auction createAuction(Item item, float minPrice, Date expiration)
			throws DTException {
		return new AuctionImpl(item, minPrice, expiration);
	}

	@Override
	public Auction createAuction() {
		return new AuctionImpl();
	}

	@Override
	public Iterator<Auction> findAuction(Auction modelAuction) throws DTException {
		return persistence.restoreAuction(modelAuction);
	}

	@Override
	public void storeAuction(Auction auction) throws DTException {
		persistence.saveAuction(auction);
	}

	@Override
	public void deleteAuction(Auction auction) throws DTException {
		persistence.deleteAuction(auction);
	}

	@Override
	public RegisteredUser createRegisteredUser(String name, String firstName,
			String lastName, String password, boolean isAdmin, String email,
			String phone, boolean canText) throws DTException {
		return new RegisteredUserImpl(name, firstName, lastName, password,
				isAdmin, email, phone, canText);
	}

	@Override
	public RegisteredUser createRegisteredUser() {
		return new RegisteredUserImpl();
	}

	@Override
	public Iterator<RegisteredUser> findRegisteredUser(RegisteredUser modelRegisteredUser)
			throws DTException {
		return persistence.restoreRegisteredUser(modelRegisteredUser);
	}

	@Override
	public void storeRegisteredUser(RegisteredUser registeredUser) throws DTException {
		persistence.saveRegisteredUser(registeredUser);
	}

	@Override
	public void deleteRegisteredUser(RegisteredUser registeredUser) throws DTException {
		persistence.deleteRegisteredUser(registeredUser);
	}

	@Override
	public Bid createBid(Auction auction, RegisteredUser user, float price) throws DTException {
		return new BidImpl(auction, user, price);
	}

	@Override
	public Bid createBid() {
		return new BidImpl();
	}

	@Override
	public Iterator<Bid> findBid(Bid modelBid) throws DTException {
		return persistence.restoreBid(modelBid);
	}

	@Override
	public void storeBid(Bid bid) throws DTException {
		persistence.saveBid(bid);
	}

	@Override
	public void deleteBid(Bid bid) throws DTException {
		persistence.deleteBid(bid);
	}

	@Override
	public ExperienceReport createExperienceReport(RegisteredUser reviewer,
			RegisteredUser reviewed, int rating, String report, Date date)
			throws DTException {

		return new ExperienceReportImpl(reviewer, reviewed, rating, report, date);
	}

	@Override
	public ExperienceReport createExperienceReport() {
		return new ExperienceReportImpl();
	}

	@Override
	public Iterator<ExperienceReport> findExperienceReport(
			ExperienceReport modelExperienceReport) throws DTException {
		return persistence.restoreExperienceReport(modelExperienceReport);
	}

	@Override
	public void storeExperienceReport(ExperienceReport experienceReport)
			throws DTException {
		persistence.saveExperienceReport(experienceReport);
	}

	@Override
	public void deleteExperienceReport(ExperienceReport experienceReport)
			throws DTException {
		persistence.deleteExperienceReport(experienceReport);
	}

	@Override
	public Membership createMembership(float price, Date date)
			throws DTException {
		return MembershipImpl.getMembership(price, date);
	}

	@Override
	public Membership createMembership() {
		return MembershipImpl.getMembership();
	}

	@Override
	public Membership findMembership() throws DTException {
		return persistence.restoreMembership(MembershipImpl.getMembership());
	}

	@Override
	public void storeMembership(Membership membership) throws DTException {
		persistence.saveMembership(MembershipImpl.getMembership());
	}

	
	////////////////////////////////////////////////////////////////////////////////////////////////
	//Associations (excluding many-to-many associations, which are handled by their own classes)////
	////////////////////////////////////////////////////////////////////////////////////////////////
	
	@Override
	public Category getParent(Category category) throws DTException {
		
		if (!category.isPersistent())
			throw new DTException("The child category is not persistent.");
		//restore the Iterator of categories given the model parent
		Category modelParent = createCategory();
		modelParent.setId(category.getParentId());
		Iterator<Category> catIt = persistence.restoreCategory(modelParent);
		if(catIt.hasNext()){
			return catIt.next();
		}
		return null;
	}

	@Override
	public Iterator<Category> getChild(Category category) throws DTException {
		
		if (!category.isPersistent())
			throw new DTException("The parent category is not persistent.");
		//restore the Iterator of categories given the model child
		Iterator<Category> catIt = persistence.restoreCategory(createCategory(category, null));
		return catIt;
	}

	@Override
	public Category getCategory(AttributeType attributeType) throws DTException {
		
		if (!attributeType.isPersistent())
			throw new DTException("The Category's attributeType is not persistent.");
		//create the model using getCategoryId from the attributeType
		Category modelCat = createCategory();
		modelCat.setId(attributeType.getCategoryId());
		//restore the category iterator using the model category
		Iterator<Category> catIt = persistence.restoreCategory(modelCat);
		if (!catIt.hasNext())
			throw new DTException("No Categories with specified AttributeType were found.");
		return catIt.next();
	}

	@Override
	public Iterator<AttributeType> getAttributeType(Category category) throws DTException {
		
		if (!category.isPersistent())
			throw new DTException("The category to find AttributeTypes by is not persistent.");
		//restore the AttributeTypes given the model AttributeType
		Iterator<AttributeType> attTypeIt = persistence.restoreAttributeType(createAttributeType(category, null));
		return attTypeIt;
	}

	@Override
	public Category getCategory(Item item) throws DTException {
		
		if (!item.isPersistent())
			throw new DTException("The Item to find a Category by is not persistent.");
		//create the model Category
		Category modelCat = createCategory();
		modelCat.setId(((ItemImpl)item).getCategoryId());
		//use the model Category to restore the category of this item
		Iterator<Category> catIt = persistence.restoreCategory(modelCat);
		if (!catIt.hasNext())
			throw new DTException("The Category for this Item was not found.");
		return catIt.next();
	}

	@Override
	public Iterator<Item> getItem(Category category) throws DTException {
		
		if (!category.isPersistent())
			throw new DTException("The Category to find Items by is not persistent.");
		//restore the items given the model item
		Item modItem = createItem();
		modItem.setCategoryId(category.getId());
		modItem.setOwnerId(-1);
		Iterator<Item> itemIt = persistence.restoreItem(modItem);
		return itemIt;
	}

	@Override
	public Item getItem(Attribute attribute) throws DTException {
		
		if (!attribute.isPersistent())
			throw new DTException("The attribute to find an Item by is not persistent.");
		//create the model Item
		Item modelItem = createItem();
		modelItem.setId(attribute.getItemId());
		//use the model Item to restore
		Iterator<Item> itemIt = persistence.restoreItem(modelItem);
		if (!itemIt.hasNext())
			throw new DTException("The Item for this Attribute was not found.");
		return itemIt.next();
	}

	@Override
	public Iterator<Attribute> getAttribute(Item item) throws DTException {

		if (!item.isPersistent())
			throw new DTException("The Item to find Attributes by is not persistent.");
		//restore the Attributes given the model Attribute
		Attribute modAttribute = createAttribute();
		modAttribute.setItemId(item.getId());
		modAttribute.setAttributeType(-1);
		Iterator<Attribute> attIt = persistence.restoreAttribute(modAttribute);
		return attIt;
	}

	@Override
	public RegisteredUser getRegisteredUser(Item item) throws DTException {
		
		if (!item.isPersistent())
			throw new DTException("The Item to find a RegisteredUser by is not persistent.");
		//create the model RegisteredUser with item's ownerId
		RegisteredUser ruModel = createRegisteredUser();
		ruModel.setId(item.getOwnerId());
		//restore the RegisteredUser using the model
		Iterator<RegisteredUser> ruIt = persistence.restoreRegisteredUser(ruModel);
		if (!ruIt.hasNext())
			throw new DTException("The RegisteredUser for the specified Item was not found.");
		return ruIt.next();
	}

	@Override
	public Iterator<Item> getItem(RegisteredUser registeredUser) throws DTException {
		
		if (!registeredUser.isPersistent())
			throw new DTException("The RegisteredUser to find Items by is not persistent.");
		//restore Items using a model Item
		Item modItem = createItem();
		modItem.setOwnerId(registeredUser.getId());
		modItem.setCategoryId(-1);
		Iterator<Item> itemIt = persistence.restoreItem(modItem);
		return itemIt;
	}

	@Override
	public Item getItem(Auction auction) throws DTException {

		if (!auction.isPersistent())
			throw new DTException("The Auction to find an Item by is not persistent.");
		//create the model Item using auction's itemId
		Item modelItem = createItem();
		modelItem.setId(auction.getItemId());
		//restore the Item using the model
		Iterator<Item> itemIt = persistence.restoreItem(modelItem);
		if (!itemIt.hasNext())
			throw new DTException("The item for the specified Auction was not found.");
		return itemIt.next();
	}

	@Override
	public Auction getAuction(Item item) throws DTException {
		
		if (!item.isPersistent())
			throw new DTException("The Item to find an Auction by is not persistent.");
		//restore the Auction using a model
		Auction modelAuction = createAuction();
		modelAuction.setItemId(item.getId());
		Iterator<Auction> auctionIt = persistence.restoreAuction(modelAuction);
		if(auctionIt.hasNext()){
			return auctionIt.next();
		}
		return null;
	}

	@Override
	public AttributeType getAttributeType(Attribute attribute) throws DTException {
		
		if (!attribute.isPersistent())
			throw new DTException("The AttributeType's attribute is not persistent.");
		//restore the AttributeType using a model
		AttributeType modelAT = createAttributeType();
		modelAT.setId(attribute.getAttributeType());
		Iterator<AttributeType> attTypeIt = persistence.restoreAttributeType(modelAT);
		if (!attTypeIt.hasNext())
			throw new DTException("The Attribute's AttributeType was not found.");
		return attTypeIt.next();
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////
	//Methods for traversing the many-to-many ExperienceReport association//////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	@Override
	public Iterator<RegisteredUser> findReviewers(RegisteredUser modelRegisteredUser) throws DTException{
	
		if (!modelRegisteredUser.isPersistent())
			throw new DTException("The RegisteredUser to find reviewers by is not persistent.");
		Iterator<RegisteredUser> ruIt = persistence.restoreReviewers(modelRegisteredUser);
		return ruIt;
	}
	
	@Override
	public Iterator<RegisteredUser> findReviewed(RegisteredUser modelRegisteredUser) throws DTException{
		
		if (!modelRegisteredUser.isPersistent())
			throw new DTException("The RegisteredUser to find ppl he reviewed is not persistent.");
		Iterator<RegisteredUser> ruIt = persistence.restoreReviewed(modelRegisteredUser);
		return ruIt;
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////
	//Methods for traversing the many-to-many Bid association between Auction and RegisteredUser/////////////
	/////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	@Override
	public Iterator<Auction> findAuctionsBidOn(RegisteredUser registeredUser) throws DTException{
		
		if (!registeredUser.isPersistent())
			throw new DTException("The RegisteredUser to find Auctions by is not persistent.");
		Iterator<Auction> auctionIt = persistence.restoreAuctionsBidOn(registeredUser);
		return auctionIt;
	}
	
	@Override
	public Iterator<RegisteredUser> findBidders(Auction auction) throws DTException{
		
		if (!auction.isPersistent())
			throw new DTException("The Auction to find RegisteredUsers by is not persistent.");
		Iterator<RegisteredUser> ruIt = persistence.restoreBidders(auction);
		return ruIt;
	}
	
	@Override
	public Iterator<Bid> findBids(Auction auction) throws DTException{
		
		if(!auction.isPersistent())
			throw new DTException("The auction to find Bids by is not persistent.");
		Iterator<Bid> bidIt = persistence.restoreBids(auction);
		return bidIt;
	}
}
