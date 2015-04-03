package edu.uga.dawgtrades.persist.impl;

import java.sql.Connection;
import java.util.Iterator;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.mysql.jdbc.PreparedStatement;

import edu.uga.dawgtrades.DTException;
import edu.uga.dawgtrades.model.Item;
import edu.uga.dawgtrades.model.ObjectModel;

public class ItemManager {
	// Fields
	private ObjectModel objModel = null;
	private Connection conn = null;
	
	// Constructor
	public ItemManager( Connection conn, ObjectModel objectModel) {
		this.conn = conn;
		this.objModel = objectModel;
	}

	public void save(Item item) 
		throws DTException {
		String insertItemSql = "insert into Item ( OwnerId, CategoryId, Name, Description ) values ( ?, ?, ?, ? )";
		String updateItemSql = "update Item set OwnerId = ?, CategoryId = ?, Name = ?, Description = ? where Id = ?";
		PreparedStatement stmt = null;
		int inscnt;
		long itemId;
		
		if( item.getCategoryId() == -1 ) {
			throw new DTException( "ItemManager.save: Attempting to save an Item without a Category" );
		}
		if ( item.getOwnerId() == -1 ) {
			throw new DTException( "ItemManager.save: Attempting to save an Item without an Owner" );
		}
		
		try {
			if( !item.isPersistent() ) {
				stmt = (PreparedStatement) conn.prepareStatement( insertItemSql );
			} else {
				stmt = (PreparedStatement) conn.prepareStatement( updateItemSql );
			}
			
			if( item.getOwnerId() >= 0 ) {
				stmt.setLong( 1, item.getOwnerId() );
			} else {
				throw new DTException( "ItemManager.save: Can't save an Item: Owner is undefined" );
			}
			
			if( item.getCategoryId() >= 0 ) {
				stmt.setLong( 2, item.getCategoryId() );
			} else {
				throw new DTException( "ItemManager.save: Can't save an Item: Category is undefined" );
			}
			
			if( item.getName() != null ) {
				stmt.setString( 3, item.getName() );
			} else {
				//stmt.setNull( 3, java.sql.Types.VARCHAR );
				throw new DTException( "ItemManager.save: Can't save an Item: Name is undefined" );
			}
			
			if( item.getDescription() != null ) {
				stmt.setString( 4, item.getDescription() );
			} else {
				stmt.setNull( 4, java.sql.Types.VARCHAR );
			}
			
			if( item.isPersistent() ) {
				stmt.setLong( 5, item.getId() );
			}
			
			inscnt = stmt.executeUpdate();
			
			if( !item.isPersistent() ) {
				if( inscnt >= 1 ) {
					String sql = "select last_insert_id()";
					if( stmt.execute( sql ) ) {
						ResultSet r = stmt.getResultSet();
						
						while( r.next() ) {
							itemId = r.getLong( 1 );
							if( itemId >= 0 ) {
								item.setId( itemId );
							}
						}
					}
				} else {
					throw new DTException( "ItemManager.save: Failed to save an Item" );
				}
			} else {
				if( inscnt < 1 ) {
					throw new DTException( "ItemManager.save: Failed to save an Item" );
				}
			}
		} catch ( SQLException e ) {
			e.printStackTrace();
			throw new DTException( "ItemManager.save: Failed to save an Item: " + e );
		}
	}

	public Iterator<Item> restore(Item item) 
		throws DTException {
		String selectItemSql = "select Id, OwnerId, CategoryId, Name, Description from Item";
		Statement stmt = null;
		StringBuffer query = new StringBuffer(100);
		StringBuffer condition = new StringBuffer(100);
		
		condition.setLength( 0 );
		
		query.append( selectItemSql );
		
		if( item != null ) {
			if( item.getId() >= 0 ) {
				query.append( " where Id = " + item.getId() );
			}
			else if( item.getOwnerId() > 0 ) {
				query.append( " where OwnerId = " + item.getOwnerId() );
			}
				
			else if( item.getCategoryId() > 0 ) {
				query.append( " where CategoryId = " + item.getCategoryId() );
			}	
			else{
				if( item.getName() != null ) {
					condition.append( " Name = '" + item.getName() + "'" );
				}
				if( item.getDescription() != null ) {
					if(condition.length() > 0){
						condition.append( " and Description = '" + item.getDescription() + "'" );
					}
					else{
						condition.append( " Description = '" + item.getDescription() + "'" );
					}
				}
				if( condition.length() > 0 ) {
					query.append(" where ");
					query.append( condition );
				}
			}
		}
		try {
			stmt = conn.createStatement();
			
			if( stmt.execute( query.toString() ) ) {
				ResultSet r = stmt.getResultSet();
				return new ItemIterator( r, objModel );
			}
		} catch ( Exception e ) {
			throw new DTException( "ItemManager.restore: Could not restore persistent Item object; Root cause: " + e );
		}
		
		throw new DTException( "ItemManager.restore: Could not restore persistent Item object" );
	}

	public void delete(Item item)
		throws DTException {
		String deleteItemSql = "delete from Item where Id = ?";
		PreparedStatement stmt = null;
		int inscnt;
		
		if( !item.isPersistent() ) {
			return;
		}
		
		try {
			stmt = (PreparedStatement) conn.prepareStatement( deleteItemSql );
			stmt.setLong( 1, item.getId() );
			inscnt = stmt.executeUpdate();
			if( inscnt == 1 ) {
				return;
			} else {
				throw new DTException( "ItemManager.delete: Failed to delete an Item" );
			}
		} catch ( SQLException e ) {
			e.printStackTrace();
			throw new DTException( "ItemManager.delete: Failed to delete an Item: " + e );
		}
	}
}
