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
import edu.uga.dawgtrades.model.*;
import edu.uga.dawgtrades.model.impl.ObjectModelImpl;
import edu.uga.dawgtrades.persist.Persistence;
import edu.uga.dawgtrades.persist.impl.DbUtils;
import edu.uga.dawgtrades.persist.impl.PersistenceImpl;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class FindItemsPageTwo extends HttpServlet{

	private static final long serialVersionUID = 1L;
    static  String         templateDir = "WEB-INF/templates";
    static  String         resultTemplateName = "FindItemsPageTwo-Result.ftl";

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
    
    public void doPost( HttpServletRequest req, HttpServletResponse res )
            throws ServletException, IOException{
    	
    	Template resultTemplate = null;
        BufferedWriter toClient = null;
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
        
        // Get the form parameters
        //
        String catNameString = req.getParameter( "category" );
        List<String> attributeString = new LinkedList<String>();
        if (!req.getParameter("attribute").equals(""))
        	attributeString.add(req.getParameter("attribute"));

        List<Item> itemObjects = null;
		try {
			itemObjects = logic.findItems(catNameString, attributeString);
		} catch (DTException e1) {
			DTError.error( cfg, toClient, "Couldn't find Items." );
            return;
		}

        // Setup the data-model
        //
        Map<String,Object> root = new HashMap<String,Object>();

        // Build the data-model
        //
        List<List<Object>> items = new LinkedList<List<Object>>();
        root.put( "items", items );
        
        for( int i = 0; i < itemObjects.size(); i++ ) {
            Item item = (Item) itemObjects.get( i );
            List<Object> itemList = new LinkedList<Object>();
            itemList.add( new Long( item.getId() ) );
            itemList.add( item.getName() );
            itemList.add( item.getDescription() );
            try {
				itemList.add( objectModel.getCategory(item).getName() );
				itemList.add( objectModel.getRegisteredUser(item).getName());
				itemList.add( objectModel.getAuction(item).getId());
			} catch (DTException e) {
				DTError.error( cfg, toClient, "Couldn't find the Item's Category." );
	            return;
			}
            items.add(itemList);
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
        
       // close the connection
             try {
                 conn.close();
                 
             }
             catch( Exception e ) {
                 System.err.println( "Exception: " + e );
                 e.printStackTrace();
             }

    }
}
