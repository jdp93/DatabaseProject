package edu.uga.dawgtrades.persist.impl;

import java.sql.Connection;
import java.util.Iterator;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import com.mysql.jdbc.PreparedStatement;

import edu.uga.dawgtrades.DTException;
import edu.uga.dawgtrades.model.AttributeType;
import edu.uga.dawgtrades.model.ObjectModel;

public class AttributeTypeManager {
	// Fields
	private ObjectModel objModel = null;
	private Connection conn = null;
	
	// Constructor
	public AttributeTypeManager( Connection conn, ObjectModel objectModel) {
		this.conn = conn;
		this.objModel = objectModel;
	}

	public void save(AttributeType attributeType)
		throws DTException {
		String insertAttrTypeSql = "insert into AttributeType ( CategoryId, Name ) "
				+ "values ( ?, ? )";
		String updateAttrTypeSql = "update AttributeType "
				+ "set CategoryId = ?, Name = ? "
				+ "where Id = ?";
		PreparedStatement stmt = null;
		int inscnt;
		long attrtypeId;
		
		if( attributeType.getCategoryId() == -1 ) {
			throw new DTException( "AttributeTypeManager.save: Attempting to save an AttributeType without a Category.");
		}
		
		try {
			if ( !attributeType.isPersistent() ) {
				stmt = (PreparedStatement) conn.prepareStatement( insertAttrTypeSql );
			} else {
				stmt = (PreparedStatement) conn.prepareStatement( updateAttrTypeSql );
			}
			
			if ( attributeType.getCategoryId() >= 0 ) {
				stmt.setLong( 1, attributeType.getCategoryId() );
			} else {
				throw new DTException( "AttributeTypeManager.save: Can't save an AttributeType: Category is undefiend.");
			}
			
			if ( attributeType.getName() != null ) {
				stmt.setString( 2, attributeType.getName() );
			} else {
				throw new DTException( "AttributeTypeManager.save: Can't save an AttributeType: Name is undefined." );
			}
			
			if ( attributeType.isPersistent() ) {
				stmt.setLong( 3, attributeType.getId() );
			}
			
			inscnt = stmt.executeUpdate();
			
			if ( !attributeType.isPersistent() ) {
				if ( inscnt >= 1 ) {
					String sql = "select last_insert_id()";
					if ( stmt.execute( sql ) ) {
						ResultSet r = stmt.getResultSet();
						while( r.next() ) {
							attrtypeId = r.getLong( 1 );
							if ( attrtypeId >= 0 ) {
								attributeType.setId( attrtypeId );
							}
						}
					}
				}
			} else {
				if ( inscnt < 1 ) {
					throw new DTException( "AttributeTypeManager.save: Failed to save an AttributeType.");
				}
			}
		}
		catch ( SQLException e ) {
			e.printStackTrace();
			throw new DTException( "AttributeTypeManager.save: Failed to save an AttributeType: " + e );
		} 
	}

	public Iterator<AttributeType> restore(AttributeType attributeType)
		throws DTException {
		String selectAttrTypeSql = "select Id, CategoryId, Name from AttributeType";
		Statement stmt = null;
		StringBuffer query = new StringBuffer(100);
		StringBuffer condition = new StringBuffer(100);
		
		condition.setLength(0);
		
		query.append( selectAttrTypeSql );
		
		if ( attributeType != null ) {
			if ( attributeType.getId() >= 0 ) {
				query.append( " where Id = " + attributeType.getId() );
			}
			else if ( attributeType.getCategoryId() >= 0 ) {
				query.append( " where CategoryId = " + attributeType.getCategoryId() );
			}
			else {
				if ( attributeType.getName() != null ) {
					condition.append( " Name = '" + attributeType.getName() + "'" );
				}
				
				if ( condition.length() > 0 ) {
					query.append( " where " );
					query.append( condition );
				}
			}
		}
		
		try {
			stmt = conn.createStatement();
			
			if ( stmt.execute( query.toString() ) ) {
				ResultSet r = stmt.getResultSet();
				return new AttributeTypeIterator( r, objModel );
			}
		} catch ( Exception e ) {
			throw new DTException( "AttributeTypeManager.restore: Could not restore persistent AttributeType object; Root cause: " + e);
		}
		throw new DTException( "");
	}

	public void delete(AttributeType attributeType)
		throws DTException {
		String deleteAttributeTypeSql = "delete from AttributeType where Id = ?";
		PreparedStatement stmt = null;
		int inscnt;
		
		if ( !attributeType.isPersistent() ) {
			return;
		}
		
		try {
			stmt = (PreparedStatement) conn.prepareStatement( deleteAttributeTypeSql );
			stmt.setLong( 1, attributeType.getId() );
			inscnt = stmt.executeUpdate();
			if ( inscnt == 1) {
				return;
			} else {
				throw new DTException( "AttributeTypeManager.delete: Failed to delete an AttributeType." );
			}
		} catch ( SQLException e ) {
			e.printStackTrace();
			throw new DTException( "AttributeTypeManager.delete: Failed to delete an AttributeType: " + e );
		}
	}
}
