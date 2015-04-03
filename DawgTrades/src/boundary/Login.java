package edu.uga.dawgtrades.boundary;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;

import edu.uga.dawgtrades.DTException;
import edu.uga.dawgtrades.authentication.*;
import edu.uga.dawgtrades.model.ObjectModel;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class Login extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	static String templateDir = "WEB-INF/templates";
	static String resultTemplateName = "RegisteredUserHome.ftl";
	
	private Configuration cfg;
	
	public void init() {
		// Prep the FreeMarker configuration;
		// - Load templates from WEB-INF/templates directory of the Web app
		cfg = new Configuration();
		cfg.setServletContextForTemplateLoading( getServletContext(), templateDir );
	}
	
	public void doPost( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {
		Template resultTemplate = null;
		HttpSession httpSession = null;
		BufferedWriter toClient = null;
		String username = null;
		String password = null;
		String ssid = null;
		Session session = null;
		ObjectModel objectModel = null;
		
		// Load templates from the WEB-INF/templates directory of the Web app
		try {
			resultTemplate = cfg.getTemplate( resultTemplateName );
		} catch ( IOException e ) {
			throw new ServletException( "Login.doPost: Can't load template in: " + templateDir + ": " + e.toString() );
		}
		
		httpSession = req.getSession();
		ssid = ( String ) httpSession.getAttribute( "ssid" );
		if( ssid != null ) {
			System.out.println( "Already have ssid: " + ssid );
			session = SessionManager.getSessionById( ssid );
			System.out.println( "Conneciton: " + session.getConnection() );
		} else {
			System.out.println( "ssid is null" );
		}
		
		// Prep the HTTP Response:
		// - use the charset of the template for the output
		// - use text/html MIME-type
		toClient = new BufferedWriter( new OutputStreamWriter( res.getOutputStream(), resultTemplate.getEncoding() ) );
		
		res.setContentType( "text/html; charset=" + resultTemplate.getEncoding() );
		
		// Get Parameters
		username = req.getParameter( "username" );
		password = req.getParameter( "password" );
		
		if( username == null || password == null ) {
			DTError.error( cfg, toClient, "Unknown user name or password" );
			return;
		}
		
		try {
			ssid = SessionManager.login( username, password );
			System.out.println( "Obtained ssid: " + ssid );
			httpSession.setAttribute( "ssid", ssid );
			session = SessionManager.getSessionById( ssid );
			System.out.println( "Connection: " + session.getConnection() );
		} catch ( Exception e ) {
			DTError.error( cfg, toClient, e );
			return;
		}
		
		objectModel = session.getObjectModel();
		if( objectModel == null ) {
			DTError.error( cfg, toClient, "Session expired" );
			return;
		}
		
		
		
		// Get isAdmin info
		String isAdmin;
		if( session.getRegisteredUser().getIsAdmin() ){
			isAdmin = "true";
		} else {
			isAdmin = "false";
		}
		
		// Setup the data-model
		Map<String, String> root = new HashMap<String, String>();
		
		// Build the data-model
		root.put( "isAdmin", isAdmin );
		
		// Merge the data-model and the template
		try {
			resultTemplate.process( root, toClient );
			toClient.flush();
		} catch ( TemplateException e ) {
			throw new ServletException( "Error while processing FreeMarker template", e );
		}
		
		toClient.close();
	}
}
