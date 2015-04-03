package edu.uga.dawgtrades.boundary;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

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
import edu.uga.dawgtrades.model.ObjectModel;
import freemarker.template.Configuration;
import freemarker.template.Template;

public class UpdateProfile extends HttpServlet{
	private static final long serialVersionUID = 1L;
	
	static String templateDir = "WEB-INF/templates";
	static String resultTemplateName = "Profile.ftl";
	
	private Configuration cfg;
	
	public void init() {
		// Prep the FreeMarker configuration;
		// - Load templates from WEB-INF/templates directory of the Web app
		cfg = new Configuration();
		cfg.setServletContextForTemplateLoading( getServletContext(), templateDir );
	}
	
	public void doPost( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {
		Template resultTemplate = null;
		BufferedWriter toClient = null;
		ObjectModel objectModel = null;
		Logic logic = null;
		HttpSession httpSession;
		Session session;
		String ssid;
		String username = null;
		String password = null;
		String firstname = null;
		String lastname = null;
		String email = null;
		String phone = null;
		String canText = null;
		httpSession = req.getSession();
		
		ssid = (String) httpSession.getAttribute( "ssid" );
		
		if( ssid == null ) {
			DTError.error( cfg, toClient, "Session expired or illgal; please log in" );
			return;
		}

        // Load templates from the WEB-INF/templates directory of the Web app.
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
        username = req.getParameter( "username" );
        password = req.getParameter( "password" );
        firstname = req.getParameter( "firstname" );
        lastname = req.getParameter( "lastname" );
        email = req.getParameter( "email" );
        phone = req.getParameter( "phone" );
        canText = req.getParameter( "canText" );
        
        // validate the input data
        if( username == null || password == null || lastname == null ||	firstname  == null || email == null || phone == null || canText == null ) {
            DTError.error( cfg, toClient, "Input cannot be null" );
            return;
        }
        
        boolean text = false;
        if( canText.equals("true") ) {
        	text = true;
        }
        
		session = SessionManager.getSessionById( ssid );
        objectModel = session.getObjectModel();
        if( objectModel == null ) {
            DTError.error( cfg, toClient, "Session expired or illegal; please log in" );
            return; 
        }
        
        logic = new LogicImpl( objectModel );
        try {
			logic.updateProfile(username, password, firstname, lastname, email, phone, text);
		} catch (DTException e) {
			DTError.error(cfg, toClient, e);
		}    
	}
}
