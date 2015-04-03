package edu.uga.dawgtrades.boundary;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
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
import edu.uga.dawgtrades.model.Auction;
import edu.uga.dawgtrades.model.RegisteredUser;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class TrackBids extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	static String templateDir = "WEB-INF/templates";
	static String resultTemplateName = "TrackBids-Result.ftl";
	
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
        
        Logic logic = new LogicImpl( session.getObjectModel() );
        List<Auction> auctionObj = null;
        try {
			auctionObj = logic.trackBids( person.getName() );
		} catch (DTException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        
        // Setup the data-model
        Map<String, Object> root = new HashMap<String, Object>();
        
        // Build the data-model
        //if(session.getRegisteredUser().getIsAdmin()) { }
        List<List<Object>> auctions = new LinkedList<List<Object>>();
        root.put( "auction", auctions );
        
        for( int i = 0; i < auctionObj.size(); i++ ) {
        	Auction auction = (Auction) auctionObj.get( i );
        	List auctionList = new LinkedList<Object>();
        	auctionList.add( new Long( auction.getId() ) );
        	try {
				auctionList.add( session.getObjectModel().getItem( auction ).getName() );
			} catch (DTException e1) {
				DTError.error(cfg, toClient, "TrackBids: Could not get item name " + e1);
			}
        	auctionList.add( auction.getMinPrice() );
        	try {
				auctionList.add( auction.getSellingPrice() );
			} catch (DTException e) {
				DTError.error(cfg, toClient, "TrackBids: Could not get selling price of auction " + e);
				return;
			}
        	auctionList.add( auction.getExpiration() );
        	auctions.add( auctionList );
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
