package edu.uga.dawgtrades.persist.impl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;

//http://stackoverflow.com/questions/12065464/what-is-the-difference-between-package-com-mysql-jdbc-preparedstatement-and-jav
//import com.mysql.jdbc.PreparedStatement;
import com.mysql.jdbc.PreparedStatement;

import edu.uga.dawgtrades.DTException;
import edu.uga.dawgtrades.model.Attribute;
import edu.uga.dawgtrades.model.ObjectModel;

public class AttributeManager {
	// Fields
	private ObjectModel objModel = null;
	private Connection conn = null;
	
	// Constructor
	public AttributeManager( Connection conn, ObjectModel objectModel) {
		this.conn = conn;
		this.objModel = objectModel;
	}
	
	public void save (Attribute attribute)
		throws DTException {
		String insertAttrSql = "insert into Attribute ( ItemId, AttributeTypeId, Value ) "
				+ "values ( ?, ?, ? )";
		String updateAttrSql = "update Attribute "
				+ "set AttributeTypeId = ?, ItemId = ?, Value = ? "
				+ "where Id = ?";
		
		PreparedStatement stmt = null;
		int inscnt;
		long attrId;
		
		if( attribute.getAttributeType() == -1 ) {
			throw new DTException( "AttributeManager.save: Attempting to save an Attribute without an AttributeType." );
		}
		if( attribute.getItemId() == -1) {
			throw new DTException( "AttributeManager.save: Attempting to save an Attribute without an Item." );
		}
		
		try {
			if( !attribute.isPersistent() ) {
				stmt = (PreparedStatement) conn.prepareStatement( insertAttrSql );
			} else {
				stmt = (PreparedStatement) conn.prepareStatement( updateAttrSql );
			}
			
			if( attribute.getItemId() >= 0 ) {
				stmt.setLong( 1, attribute.getItemId() );
			} else {
				throw new DTException( "AttributeManager.save: Can't save an Attribute: Item is undefined");
			}
			
			if( attribute.getAttributeType() >= 0 ) {
				stmt.setLong( 2, attribute.getAttributeType() );
			} else {
				throw new DTException( "AttributeManager.save: Can't save an Attribute: AttributeType is undefined.");
			}
			
			if( attribute.getValue() != null ) {
				stmt.setString( 3, attribute.getValue() );
			} else {
				throw new DTException( "AttributeManager.save: Can't save an Attribute: value undefined." );				
			}
			
			if( attribute.isPersistent() ) {
				stmt.setLong( 4, attribute.getId() );
			}
			
			inscnt = stmt.executeUpdate();
			
			if( !attribute.isPersistent() ) {
				if( inscnt >= 1) {
					String sql = "select last_insert_id()";
					if( stmt.execute( sql ) ) {	// Statement returned a result
						// Retrieve the result
						ResultSet r = stmt.getResultSet();
						// We will use only the first row
						while( r.next() ) {
							// Retrieve the last insert auto_increment value
							attrId = r.getLong( 1 );
							if( attrId >= 0) {
								attribute.setId( attrId );	// Set this Attribute's db id (proxy object)
							}
						}
					}
				} else {
					throw new DTException( "AttributeManager.save: Failed to save an Attribute." );
				}
			} else {
				if ( inscnt < 1 ) {
					throw new DTException( "AttributeManager.save: Failed to save an Attribute." );
				}
			}
		} catch ( SQLException e ) {
			e.printStackTrace();
			throw new DTException(" AttributeManager.save: Failed to save an Attribute: " + e);
		} 
	}

	public Iterator<Attribute> restore(Attribute attribute)
		throws DTException{
		String selectAttrSql = "select Id, ItemId, AttributeTypeId, Value from Attribute";
		Statement stmt = null;
		StringBuffer query = new StringBuffer(100);
		StringBuffer condition = new StringBuffer(100);
		
		condition.setLength( 0 );
		
		// Form the query based on the given Attribute object instance
		query.append( selectAttrSql );
		if( attribute != null ) {
			if( attribute.getId() >= 0 ) {	// AttributeID is unique, so it is sufficient to get Attribute
				query.append( " where Id = " + attribute.getId() );
			}
			else if( attribute.getAttributeType() > 0 ) {	// AttributeTypeID is unique, so it is sufficient to get Attribute
				query.append( " where AttributeTypeId = " + attribute.getAttributeType() );
			}
			else if( attribute.getItemId() > 0 ) {	// ItemID is unique, so it is sufficient to get Attribute
				query.append( " where ItemId = " + attribute.getItemId() );
			}
			else {
				if( attribute.getValue() != null ) {
					condition.append( " Value = '" + attribute.getValue() + "'" );
				}
				
				if( condition.length() > 0 ) {
					query.append( " where " );
					query.append( condition );
				}
			}
		}
		
		try {
			stmt = conn.createStatement();
			
			// Retrieve the persistent Attribute object
			if( stmt.execute( query.toString()) ) {	// Statement returned a result
				ResultSet r = stmt.getResultSet();
				return new AttributeIterator( r, objModel );
			}
		} catch( Exception e ) {	// Just in case...
			throw new DTException( "AttributeManager.restore: Could not restore persistent Attribute object; Root cause: " + e );
		} 
		throw new DTException( "AttributeManager.restore: Could not restore persistent Attribute object." );
	}
	
	public void delete(Attribute attribute) 
		throws DTException {
		String deleteAttributeSql = "delete from Attribute where Id = ?";
		PreparedStatement stmt = null;
		int inscnt;
		
		if( !attribute.isPersistent() ) {	// Is the Attribute object persistent? If not, nothing to actually delete
			return;
		}
		
		try {
			stmt = (PreparedStatement) conn.prepareStatement( deleteAttributeSql );
			stmt.setLong( 1, attribute.getId() );
			inscnt = stmt.executeUpdate();
			if( inscnt == 1 ) {
				return;
			} else {
				throw new DTException( "AttributeManager.delete: Failed to delete an Attribute." );
			}
		} catch ( SQLException e ) {
			e.printStackTrace();
			throw new DTException( "AttributeManager.delete: Failed to delete an Attribute: " + e);
		}
	}
}
