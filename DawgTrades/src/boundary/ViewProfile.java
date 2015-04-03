package edu.uga.dawgtrades.boundary;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import edu.uga.dawgtrades.authentication.Session;
import edu.uga.dawgtrades.authentication.SessionManager;
import edu.uga.dawgtrades.model.RegisteredUser;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class ViewProfile extends HttpServlet {
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
	
	// doGET starts the execution of the ViewProfile Use Case
	public void doGet( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {
		Template resultTemplate = null;
		HttpSession httpSession = null;
		BufferedWriter toClient = null;
		String username = null;
		String ssid = null;
		Session session = null;
		
		try {
			resultTemplate = cfg.getTemplate( resultTemplateName );
		} catch ( IOException e ) {
			throw new ServletException( "ViewProfile.doGet: Can't load template" );
		}
		
		toClient = new BufferedWriter(new OutputStreamWriter( res.getOutputStream(), resultTemplate.getEncoding() ) );
		res.setContentType( "text/html; charset=" + resultTemplate.getEncoding() );
		
        httpSession = req.getSession();
        if( httpSession == null ) {       // not logged in!
            DTError.error( cfg, toClient, "Session expired or illegal; please log in" );
            return;
        }
        
        ssid = (String) httpSession.getAttribute( "ssid" );
        if( ssid == null ) {       // assume not logged in!
            DTError.error( cfg, toClient, "Session expired or illegal; please log in" );
            return;
        }

        session = SessionManager.getSessionById( ssid );
        if( session == null ){
            DTError.error( cfg, toClient, "Session expired or illegal; please log in" );
            return;
        }
        
        RegisteredUser person = session.getRegisteredUser();
        if( person == null ) {
            DTError.error( cfg, toClient, "Session expired or illegal; please log in" );
            return;   
        }
        
        // Setup the data-model
        Map<String, String> root = new HashMap<String, String>();
        
        // Build the data-model
        root.put( "username", person.getName() );
        root.put( "password", person.getPassword() );
        root.put( "firstname", person.getFirstName() );
        root.put( "lastname", person.getLastName() );
        root.put( "email", person.getEmail() );
        if ( person.getPhone() != null) {
            root.put( "phone", person.getPhone() );	
        } else {
        	root.put( "phone", "" );
        }
        if( person.getCanText() ) {
            root.put( "canText", "true" );
        } else {
        	root.put( "canText", "false" );
        }
        
        // Merge the data-model and the template
        try {
        	resultTemplate.process( root, toClient );
        	toClient.flush();
        } catch ( TemplateException e ) {
        	throw new ServletException( "Error while processing FreeMarker", e );
        }
        toClient.close();
	}
}
