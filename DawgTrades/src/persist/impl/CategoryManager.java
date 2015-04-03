package edu.uga.dawgtrades.persist.impl;

import java.sql.Connection;
import java.util.Iterator;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.mysql.jdbc.PreparedStatement;

import edu.uga.dawgtrades.DTException;
import edu.uga.dawgtrades.model.Category;
import edu.uga.dawgtrades.model.ObjectModel;

public class CategoryManager {
	// Fields
	private ObjectModel objModel = null;
	private Connection conn = null;
	
	// Constructor
	public CategoryManager( Connection conn, ObjectModel objectModel) {
		this.conn = conn;
		this.objModel = objectModel;
	}

	public void save(Category category)
		throws DTException {
		String insertCategorySql = "insert into Category ( ParentId, name ) values ( ?, ? )";
		String updateCategorySql = "update Category set ParentId = ?, Name = ? where Id = ?";
		PreparedStatement stmt = null;
		int inscnt;
		long id;
		
		try {
			if( !category.isPersistent() ) {
				stmt = (PreparedStatement) conn.prepareStatement( insertCategorySql );
			} else {
				stmt = (PreparedStatement) conn.prepareStatement( updateCategorySql );
			}
			
			// category w/ parent
			if( category.getParentId() >= 0 ) {
				stmt.setLong( 1, category.getParentId() );
			}
			// root category
			else {
				stmt.setLong( 1, 0);
			}
			
			if( category.getName() != null ) {
				stmt.setString( 2, category.getName() );
			} else {
				//stmt.setNull( 2, java.sql.Types.VARCHAR );
				throw new DTException( "CategoryManager.save: Can't save a Category: Name is undefine." );
			}
			
			if( category.isPersistent() ) {
				stmt.setLong( 3,  category.getId() );
			}
			
			inscnt = stmt.executeUpdate();
			
			if( !category.isPersistent() ) {
				if( inscnt >= 1 ) {
					String sql = "select last_insert_id()";
					if( stmt.execute( sql ) ) {
						ResultSet r = stmt.getResultSet();
						while( r.next() ) {
							id = r.getLong( 1 );
							if( id >= 0 ) {
								category.setId( id );
							}
						}
					}
				} else {
					throw new DTException( "CategoryManager.save: Failed to save a Category" );
				}
			} else {
				if ( inscnt < 1 ) {
					throw new DTException( "CategoryManager.save: Failed to save a Category" );
				}
			}
		} catch ( SQLException e ) {
			e.printStackTrace();
			throw new DTException( "CategoryManager.save: Failed to save a Category: " + e );
		}
	}

	public Iterator<Category> restore(Category category)
		throws DTException {
		String selectCategorySql = "select Id, ParentId, Name from Category";
		Statement stmt = null;
		StringBuffer query = new StringBuffer(100);
		StringBuffer condition = new StringBuffer(100);
		
		condition.setLength( 0 );
		
		query.append( selectCategorySql );
		
		if( category != null) {
			if( category.getId() > 0 ) {
				query.append( " where Id = " + category.getId() );
			}
			else {
				if( category.getParentId() > 0 ) {
					query.append( " where ParentId = " + category.getParentId() );
				}
				
				if( category.getName() != null ) {
					condition.append( " Name = '" + category.getName() + "'" );
				}
				
				if( condition.length() > 0 ) {
					query.append( " where " );
					query.append( condition );
				}
			}
		}
		
		try {
			stmt = conn.createStatement();
			
			if( stmt.execute( query.toString() ) ) {
				ResultSet r = stmt.getResultSet();
				return new CategoryIterator( r, objModel );
			}
		} catch ( Exception e ) {
			throw new DTException( "CategoryManager.restore: Could not restore persistent Category object; Root cause: " + e );
		}
		
		throw new DTException( "CategoryManager.restore: Could not restore persistent Category object" );
	}

	public void delete(Category category)
		throws DTException {
		String deleteCategorySql = "delete from Category where Id = ?";
		PreparedStatement stmt = null;
		int inscnt;
		
		if( !category.isPersistent() ) {
			return;
		}
		
		try {
			stmt = (PreparedStatement) conn.prepareStatement( deleteCategorySql );
			stmt.setLong( 1, category.getId() );
			inscnt = stmt.executeUpdate();
			if( inscnt == 1 ) {
				return;
			} else {
				throw new DTException( "CategoryManager.delete: Failed to delete a Category" );
			}
		} catch ( SQLException e ) {
			throw new DTException( "CategoryManager.delete: Failed to delete a Category: " + e );
		}
	}
}
