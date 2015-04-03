package edu.uga.dawgtrades.boundary;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import edu.uga.dawgtrades.DTException;
import edu.uga.dawgtrades.authentication.Session;
import edu.uga.dawgtrades.authentication.SessionManager;
import edu.uga.dawgtrades.logic.Logic;
import edu.uga.dawgtrades.logic.impl.LogicImpl;
import edu.uga.dawgtrades.model.Category;
import edu.uga.dawgtrades.model.Item;
import edu.uga.dawgtrades.model.ObjectModel;
import edu.uga.dawgtrades.model.Persistable;
import edu.uga.dawgtrades.model.impl.ObjectModelImpl;
import edu.uga.dawgtrades.persist.Persistence;
import edu.uga.dawgtrades.persist.impl.DbUtils;
import edu.uga.dawgtrades.persist.impl.PersistenceImpl;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class BrowseCategory extends HttpServlet{

	private static final long serialVersionUID = 1L;
    static  String         templateDir = "WEB-INF/templates";
    static  String         resultTemplateName = "BrowseCategory-Result.ftl";

    private Configuration  cfg;

    public void init()
    {
        // Prepare the FreeMarker configuration;
        // - Load templates from the WEB-INF/templates directory of the Web app.
        //
        cfg = new Configuration();
        cfg.setServletContextForTemplateLoading(
                getServletContext(), 
                "WEB-INF/templates"
                );
    }
    
    public void doGet( HttpServletRequest req, HttpServletResponse res )
            throws ServletException, IOException{
    	
    	Template               resultTemplate = null;
        BufferedWriter         toClient = null;
        List<List<Object>>     categories = null;
        List<Persistable>      persistables = null;
        List<Object>			category = null;
        List<List<Object>>     items = null;
        List<Object>			item = null;
        Persistable				p = null;
        
        Connection conn = null;
        ObjectModel objectModel = null;
        Persistence persistence = null;
        Logic logic = null;
        
        try {
            conn = DbUtils.connect();
        } catch (Exception seq) {
            throw new ServletException( "BrowseCategory: Unable to get a database connection" );
        }
        
        objectModel = new ObjectModelImpl();
        persistence = new PersistenceImpl( conn, objectModel );
        objectModel.setPersistence(persistence);
        logic = new LogicImpl(objectModel);
        
        // Load templates from the WEB-INF/templates directory of the Web app.
        //
        try {
            resultTemplate = cfg.getTemplate( resultTemplateName );
        } 
        catch (IOException e) {
            throw new ServletException( 
                    "Can't load template in: " + templateDir + ": " + e.toString());
        }

        // Prepare the HTTP response:
        // - Use the charset of template for the output
        // - Use text/html MIME-type
        //
        toClient = new BufferedWriter(
                new OutputStreamWriter( res.getOutputStream(), resultTemplate.getEncoding() )
                );

        res.setContentType("text/html; charset=" + resultTemplate.getEncoding());
        
        // Setup the data-model
        //
        Map<String,Object> root = new HashMap<String,Object>();
        
        // Build the data-model
        //
        categories = new LinkedList<List<Object>>();
        items = new LinkedList<List<Object>>();

        try {
            persistables = logic.browseRoot();
        } 
        catch( Exception e ) {
            DTError.error( cfg, toClient, e );
            return;
        }
        
        root.put( "categories", categories );
        root.put( "items", items );

        //populate the categories and items from the persistable objects
        for( int i = 0; i < persistables.size(); i++ ) {
            p = (Persistable) persistables.get( i );
            if (p instanceof Category){
            	category = new LinkedList<Object>();
                category.add( ((Category) p).getName());
                categories.add(category);
            }
            else{
            	item = new LinkedList<Object>();
            	item.add(new Long( p.getId()));
            	item.add(((Item)p).getName());
            	item.add(((Item)p).getDescription() );
                try {
					item.add(objectModel.getCategory((Item)p).getName());
					item.add(objectModel.getRegisteredUser((Item)p).getName());
					if (objectModel.getAuction((Item)p) != null){
						item.add(objectModel.getAuction((Item)p).getId());
					}
					else{
						item.add("NA");
					}
				} catch (DTException e) {
					DTError.error( cfg, toClient, e );
		            return;
				}
                items.add(item);
            }
        }
        
        // Merge the data-model and the template
        //
        try {
            resultTemplate.process( root, toClient );
            toClient.flush();
        } 
        catch (TemplateException e) {
            throw new ServletException( "Error while processing FreeMarker template", e);
        }

        toClient.close();
        
        //close the connection
        try {
            conn.close();
        }
        catch( Exception e ) {
            System.err.println( "Exception: " + e );
            e.printStackTrace();
        }
    }
}

