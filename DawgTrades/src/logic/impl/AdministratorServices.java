package edu.uga.dawgtrades.logic.impl;

import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import edu.uga.dawgtrades.DTException;
import edu.uga.dawgtrades.model.*;

public class AdministratorServices{
	
    private ObjectModel om = null;

	public AdministratorServices(ObjectModel om){
		this.om = om;
	}

	public void defineCategory(String name, String parentName,
			LinkedList<String> attributeTypes) throws DTException {
		
		//create the model parent Category and search for it in the db
		Category model = om.createCategory();
		model.setName(parentName);
		Iterator<Category> categories = om.findCategory(model);
		Category parent = categories.next();
		
		//use the parent to create the desired Category
		//and then store it in the db
		Category category = om.createCategory(parent, name);
		model = om.createCategory();
		model.setName(name);
		categories = om.findCategory(model);
		if (categories.hasNext())
			throw new DTException("AdministratorService.defineCategory: The Category already exists.");
		om.storeCategory(category);
		
		//create the AttributeTypes and associate them with 
		//the Category
		for (String s : attributeTypes){
			AttributeType temp = om.createAttributeType(category, s);
			om.storeAttributeType(temp);
		}
	}
	
	public void defineRoot(String name,
			LinkedList<String> attributeTypes) throws DTException {
		
		//create the model Category and search for it in the db
		Category model = om.createCategory();
		model.setName(name);
		Iterator<Category> categories = om.findCategory(model);
		if (categories.hasNext())
			throw new DTException ("AdministratorServices.defineRoot: The Category already exists.");
		
		//use the model to create the desired Category
		//and then store it in the db
		Category category = om.createCategory(null, name);
		om.storeCategory(category);
		
		//create the AttributeTypes and associate them with 
		//the Category
		for (String s : attributeTypes){
			AttributeType temp = om.createAttributeType(category, s);
			om.storeAttributeType(temp);
		}
	}

	public void updateCategory(String name, String newName,
			String newParentName, LinkedList<String> newAttributeTypes) throws DTException {
		
		//check that the old Category exists
		Category model = om.createCategory();
		model.setName(name);
		Iterator<Category> categories = om.findCategory(model);
		if (!categories.hasNext())
			throw new DTException("AdministratorServices.updateCategory: The old Category isn't persistent.");
		Category category = categories.next();
		
		//check that the new Category doesn't exist
		model.setName(newName);
		categories = om.findCategory(model);
		if (categories.hasNext())
			throw new DTException("AdministratorServices.updateCategory: The new Category already exists.");
		
		//check that the new parent Category is persistent
		model.setName(newParentName);
		categories = om.findCategory(model);
		Category parent = categories.next();
		
		//check that there are no items in the Category
		Item modelItem = om.createItem();
		modelItem.setCategoryId(category.getId());
		Iterator<Item> items = om.findItem(modelItem);
		if (items.hasNext())
			throw new DTException("AdministratorServices.updateCategory: The Category has Items in it.");
		
		//update the category and store it
		om.deleteCategory(category);
		category = om.createCategory();
		category.setName(newName);
		category.setParentId(parent.getId());
		om.storeCategory(category);
		
		//create the AttributeTypes and associate them with 
		//the Category
		for (String s : newAttributeTypes){
			AttributeType temp = om.createAttributeType(category, s);
			om.storeAttributeType(temp);
		}
	}

	public void deleteCategory(String name) throws DTException {
		
		//check that the Category exists
		Category model = om.createCategory();
		model.setName(name);
		Iterator<Category> categories = om.findCategory(model);
		if (!categories.hasNext())
			throw new DTException("AdministratorServices.deleteCategory: The Category isn't persistent.");
		Category category = categories.next();
		
		//check that there are no items in the Category
		Item modelItem = om.createItem();
		modelItem.setCategoryId(category.getId());
		Iterator<Item> items = om.findItem(modelItem);
		if (items.hasNext())
			throw new DTException("AdministratorServices.updateCategory: The Category has Items in it.");
		
		//delete the category
		om.deleteCategory(category);
	}

	public void deleteItem(long itemId) throws DTException {
		
		//check that the Item exists
		Item model = om.createItem();
		model.setId(itemId);
		Iterator<Item> items = om.findItem(model);
		if (!items.hasNext())
			throw new DTException ("AdministratorServices.deleteItem: The Item isn't Persistent.");
		Item item = items.next();
		
		//delete the Bids, Auctions, and finally the Item
		Auction modelAuction = om.createAuction();
		modelAuction.setItemId(item.getId());
		Iterator<Auction> auctions = om.findAuction(modelAuction);
		Auction currentAuction = null;
		Iterator<Bid> bids = null;
		while (auctions.hasNext()){
			currentAuction = auctions.next();
			bids = om.findBids(currentAuction);
			while(bids.hasNext()){
				om.deleteBid(bids.next());
			}
			om.deleteAuction(currentAuction);
		}
		om.deleteItem(item);
	}

	public String printReport(Date from, Date to) throws DTException {
		// TODO Auto-generated method stub
		return "This is a fake report.  Will implement AdministratorServices.printReport if there is time.";
	}

	public void setMembershipPrice(float price) throws DTException {
		
		//check that the price is a positive number
		if (Float.isNaN(price))
			throw new DTException("AdministratorServices.setMembershipPrice: The price is not a number.");
		if (price < 0)
			throw new DTException("AdministratorServices.setMembershipPrice: The price is negative.");
		
		//create the membership and store it
		Membership membership = om.createMembership(price, new Date());
		om.storeMembership(membership);
	}

	public void deleteUser(String username) throws DTException {
		
		//check that the user exists
		RegisteredUser model = om.createRegisteredUser();
		model.setName(username);
		Iterator<RegisteredUser> rus = om.findRegisteredUser(model);
		if (!rus.hasNext())
			throw new DTException ("AdministratorServices.deleteuser: The RegisteredUser doesn't exist.");
		RegisteredUser ru = rus.next();
		
		//delete the user
		om.deleteRegisteredUser(ru);
	}

}
