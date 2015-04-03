package edu.uga.dawgtrades.boundary;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.sql.Connection;

import edu.uga.dawgtrades.DTException;
import edu.uga.dawgtrades.model.ObjectModel;
import edu.uga.dawgtrades.model.RegisteredUser;
import edu.uga.dawgtrades.model.impl.ObjectModelImpl;
import edu.uga.dawgtrades.persist.Persistence;
import edu.uga.dawgtrades.persist.impl.DbUtils;
import edu.uga.dawgtrades.persist.impl.PersistenceImpl;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import freemarker.template.Configuration;
import freemarker.template.Template;

public class Register extends HttpServlet {

private static final long serialVersionUID = 1L;

static  String  templateDir = "WEB-INF/templates";
static  String  resultTemplateName = "Register-Result.ftl";

private Configuration  cfg; 

public void init() {
    // Prepare the FreeMarker configuration;
    // - Load templates from the WEB-INF/templates directory of the Web app.
    //
    cfg = new Configuration();
    cfg.setServletContextForTemplateLoading(
            getServletContext(), 
            "WEB-INF/templates"
            );
}	
	// doPost starts the execution of the Register Use Case
	public void doPost( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {
		Template resultTemplate = null;
		BufferedWriter toClient = null;
		String username = null;
		String password = null;
		String firstname = null;
		String lastname = null;
		String email = null;
		String phone = null;
		String canText = null;
		
		// Load templates from the WEB-INF/templates directory of the Web app
		try {
			resultTemplate = cfg.getTemplate( resultTemplateName );
		} catch ( IOException e ) {
			throw new ServletException( "Login.doPost: Can't load template in: " + templateDir + ": " + e.toString() );
		}
		
		// Prep the HTTP Response:
		// - use the charset of the template for the output
		// - use text/html MIME-type
		toClient = new BufferedWriter( new OutputStreamWriter( res.getOutputStream(), resultTemplate.getEncoding() ) );
		
		res.setContentType( "text/html; charset=" + resultTemplate.getEncoding() );
		
		// Get Parameters
		username = req.getParameter( "username" );
		password = req.getParameter( "password" );
		firstname = req.getParameter( "firstname" );
		lastname = req.getParameter( "lastname" );
		email = req.getParameter( "email" );
		phone = req.getParameter( "phone" );
		canText = req.getParameter( "text" );
		
		// validate the input data
        if( username == null || password == null || firstname == null || lastname  == null || email == null ) {
            DTError.error( cfg, toClient, "Registration form cannot be null" );
            return;
        }
		
		RegisteredUser newUser = null;
		Connection conn = null;
		ObjectModel objectModel = null;
		Persistence persist = null;
		
		try {
			conn = DbUtils.connect();
		} catch (DTException e) {
			res.sendError( 400, e.getMessage() );
		}
		
		objectModel = new ObjectModelImpl();
		persist = new PersistenceImpl(conn, objectModel);
		
		boolean text = false;
		if( canText.equals("true") ) {
			text = true;
		}
		
		try {
			newUser = objectModel.createRegisteredUser(username, firstname, lastname, password, false, email, phone, text);
			objectModel.setPersistence( persist );
			objectModel.storeRegisteredUser( newUser );
		} catch (DTException e) {
			res.sendError( 400, e.getMessage() );
		}
		
		toClient.close();
	}
}
