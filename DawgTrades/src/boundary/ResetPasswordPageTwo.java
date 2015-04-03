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

import edu.uga.dawgtrades.DTException;
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

public class ResetPasswordPageTwo extends HttpServlet {
		
	private static final long serialVersionUID = 1L;
	    static  String         templateDir = "WEB-INF/templates";
	    static  String         resultTemplateName = "ResetPasswordPageTwo-Result.ftl";

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
	    	
	    	Template       resultTemplate = null;
	        BufferedWriter toClient = null;
	       
	        Persistable	p = null;
	        
	        Connection conn = null;
	        ObjectModel objectModel = null;
	        Persistence persistence = null;
	        Logic logic = null;
	        
	        try {
	            conn = DbUtils.connect();
	        } catch (Exception seq) {
	            throw new ServletException( "ResetPasswordPageTwo: Unable to get a database connection" );
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
	        String username = req.getParameter( "username" );
	        String email = req.getParameter( "email" );
	        
	        if(username == null){
	        	DTError.error(cfg, toClient, "Unspecified username.");
	        	return;
	        }
	        
	        if( email == null ) {
	            DTError.error( cfg, toClient, "Unspecified email." );
	            return;
	        }
	        //check email is in database
	        
	        
	        
	        try {
				logic.resetPassword(username, email);
			} catch (DTException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	        // Setup the data-model
	        //
	        Map<String,Object> root = new HashMap<String,Object>();
	        
	        // Build the data-model
	        //
	        	
	        
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
