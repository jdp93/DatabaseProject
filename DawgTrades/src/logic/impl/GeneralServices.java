package edu.uga.dawgtrades.logic.impl;

//import java.text.AttributedCharacterIterator.Attribute;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import edu.uga.dawgtrades.DTException;
import edu.uga.dawgtrades.model.*;


public class GeneralServices{

    private ObjectModel om = null;
   
	public GeneralServices( ObjectModel om ){
		this.om = om;
	}
	
	public void login(String username, String password)
		throws DTException{
		//create the model RegisteredUser with username and search for it in the db
		RegisteredUser model = om.createRegisteredUser();
		model.setName(username);
		model.setPassword(password);
		Iterator<RegisteredUser> users = om.findRegisteredUser(model);
		if(!users.hasNext())
			throw new DTException ("GeneralServices.login: The RegisteredUser is not persistent.");
		RegisteredUser user = users.next();
		
		//complete login
	}
	
	public void logout() throws DTException{
		//complete logout
	}
	
	public LinkedList<Persistable> browseCategory(String catName)
		throws DTException{
		//create the model category and search for it in the db
		Category model = om.createCategory();
		model.setName(catName);
		Iterator<Category> categories = om.findCategory(model);
		if(!categories.hasNext())
			throw new DTException ("GeneralServices.browseCategory: The Category is not persistent.");
		Category cat = categories.next();
		//create list to be returned
		LinkedList<Persistable> contents = new LinkedList<Persistable>();
		//use the category to find subcategories
		categories = om.getChild(cat);
		while(categories.hasNext()){
			contents.add(categories.next());
		}
		//use the category to get items
		Iterator<Item> items = om.getItem(cat);
		while(items.hasNext()){
			contents.add(items.next());
		}
		//return list with objects
		return contents;
	}
	
	public LinkedList<Persistable> browseRoot()
			throws DTException{
		//create the model category and search for it in the db
		LinkedList<Persistable> contents = new LinkedList<Persistable>();
		Category model = om.createCategory();
		Iterator<Category> categories = om.findCategory(model);
		Category currentCategory = null;
		Iterator<Item> items = null;
		while(categories.hasNext()){
			currentCategory = categories.next();
			contents.add(currentCategory);
			items = om.getItem(currentCategory);
			while (items.hasNext()){
				contents.add(items.next());
			}
		}
		return contents;
	}
	
	public List<Item> findItems(String catName, 
			List<String> attributes) throws DTException{
		//create model Category and search for them in the db
		Category model = om.createCategory();
		Category c = null;
		model.setName(catName);
		Iterator<Category> cIter = om.findCategory(model);
		if(cIter != null && cIter.hasNext()){
			c = cIter.next();
		}
		else{
			throw new DTException("GeneralServices.findItems: The Category is not persistent.");
		}
		//creates Iterator to store results of getAtrrbiute(Item i) method in object model
		Iterator<Attribute> attrs;
		//create LinkedList to hold the values of attributes
		List<String> attrValues = new LinkedList<String>();
		//create list of items to be returned
		List<Item> items = new LinkedList<Item>();
		//create Iterator of items in category
		Iterator<Item> iIter = om.getItem(c);
		//check attributes of items in category
		Item i;
		Attribute a;
		String aName;
		//loop through all items in the category
		while(iIter != null && iIter.hasNext()){
			i = iIter.next();
			//get all attributes of the item
			attrs = om.getAttribute(i);
			//store all attribute values in a linked list
			while(attrs != null && attrs.hasNext()){
				a = attrs.next();
				aName = a.getValue();
				attrValues.add(aName);
			}
			//check that all the desired attribute values are in the items attributes
			if (attributes.isEmpty()){
				//no attributes to search by, so add all items in the category
				items.add(i);
			}
			else if(attrValues.containsAll(attributes)){
				//add item to list to be returned
				items.add(i);
			}
		}
		return items;
	}
	
	public void register(String username, String password, String firstName,
			String lastName, String email, String phone, boolean canText)
			throws DTException{
		//check the username by creating model with username and searching db
		RegisteredUser model = om.createRegisteredUser();
		model.setName(username);
		Iterator<RegisteredUser> users = om.findRegisteredUser(model);
		if(users.hasNext())
			throw new DTException ("GeneralServices.register: The username already exists.");
		//create new RegisteredUser
		RegisteredUser user = om.createRegisteredUser(username, firstName, lastName,
				password, false, email, phone, canText);
		om.storeRegisteredUser(user);
	}

}
