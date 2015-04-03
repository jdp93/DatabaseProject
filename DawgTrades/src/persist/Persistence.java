package edu.uga.dawgtrades.persist;

import java.util.Iterator;

import edu.uga.dawgtrades.DTException;
import edu.uga.dawgtrades.model.Attribute;
import edu.uga.dawgtrades.model.AttributeType;
import edu.uga.dawgtrades.model.Auction;
import edu.uga.dawgtrades.model.Bid;
import edu.uga.dawgtrades.model.Category;
import edu.uga.dawgtrades.model.Item;
import edu.uga.dawgtrades.model.Membership;
import edu.uga.dawgtrades.model.RegisteredUser;
import edu.uga.dawgtrades.model.ExperienceReport;

public interface Persistence {
	public void						saveAttribute(Attribute attribute) throws DTException;
	public Iterator<Attribute>		restoreAttribute( Attribute attribute ) throws DTException;
	public void						deleteAttribute( Attribute attribute ) throws DTException;

	public void						saveAttributeType(AttributeType attributeType) throws DTException;
	public Iterator<AttributeType>	restoreAttributeType( AttributeType attributeType ) throws DTException;
	public void						deleteAttributeType(AttributeType attributeType ) throws DTException;
	
	public void						saveAuction( Auction auction ) throws DTException;
	public Iterator<Auction>		restoreAuction( Auction auction ) throws DTException;
	public Iterator<RegisteredUser>	restoreBidders( Auction auction ) throws DTException;
	public Iterator<Bid>			restoreBids(Auction auction) throws DTException;
	public void						deleteAuction( Auction auction ) throws DTException;

	public void						saveBid(Bid bid ) throws DTException;
	public Iterator<Bid>			restoreBid( Bid bid ) throws DTException;
	public void						deleteBid(Bid bid ) throws DTException;
	
	public void						saveCategory( Category category ) throws DTException;
	public Iterator<Category>		restoreCategory( Category category ) throws DTException;
	public void						deleteCategory( Category category ) throws DTException;

	public void						saveItem( Item item ) throws DTException;
	public Iterator<Item>			restoreItem( Item item ) throws DTException;
	public void						deleteItem( Item item ) throws DTException;

	public void						saveMembership( Membership membership ) throws DTException;
	public Membership				restoreMembership( Membership membership ) throws DTException;
	public void						deleteMembership( Membership membership ) throws DTException;

	public void						saveRegisteredUser( RegisteredUser registeredUser ) throws DTException;
	public Iterator<RegisteredUser>	restoreRegisteredUser( RegisteredUser registeredUser ) throws DTException;
	public void						deleteRegisteredUser( RegisteredUser registeredUser ) throws DTException;
	public Iterator<Auction>		restoreAuctionsBidOn( RegisteredUser registeredUser ) throws DTException;
	public Iterator<RegisteredUser>	restoreReviewers( RegisteredUser registeredUser ) throws DTException;
	public Iterator<RegisteredUser>	restoreReviewed( RegisteredUser registeredUser ) throws DTException;

	public void						saveExperienceReport( ExperienceReport expReport ) throws DTException;
	public Iterator<ExperienceReport>	restoreExperienceReport( ExperienceReport expReport ) throws DTException;
	public void						deleteExperienceReport( ExperienceReport expReport ) throws DTException;

};