package edu.uga.dawgtrades.boundary;

import java.util.List;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import edu.uga.dawgtrades.authentication.Session;
import edu.uga.dawgtrades.authentication.SessionManager;
import edu.uga.dawgtrades.logic.Logic;
import edu.uga.dawgtrades.logic.impl.LogicImpl;
import edu.uga.dawgtrades.model.ObjectModel;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class BidOnItemPageTwo extends HttpServlet{

	private static final long serialVersionUID = 1L;
    static  String         templateDir = "WEB-INF/templates";
    static  String         resultTemplateName = "BidOnItemPageTwo-Result.ftl";

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
            throws ServletException, IOException
    {
        Template       resultTemplate = null;
        BufferedWriter toClient = null;
        ObjectModel    objectModel = null;
        Logic          logic = null;
        HttpSession    httpSession;
        Session        session;
        String         ssid;

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

        httpSession = req.getSession();
        if( httpSession == null ) {       // assume not logged in!
            DTError.error( cfg, toClient, "Session expired or illegal; please log in" );
            return;
        }

        ssid = (String) httpSession.getAttribute( "ssid" );
        if( ssid == null ) {       // not logged in!
            DTError.error( cfg, toClient, "Session expired or illegal; please log in" );
            return;
        }

        session = SessionManager.getSessionById( ssid );
        objectModel = session.getObjectModel();
        if( objectModel == null ) {
            DTError.error( cfg, toClient, "Session expired or illegal; please log in" );
            return; 
        }

        logic = new LogicImpl( objectModel );
        
        // Get the form parameters
        //
        String username = req.getParameter( "username" );
        String auctionIdString = req.getParameter( "auctionId" );
        String amountString = req.getParameter( "amount" );
        float amount = 0;
        long auctionId = 0;

        if( username.equals("") ) {
            DTError.error( cfg, toClient, "Unspecified Username." );
            return;
        }
        
        if( auctionIdString.equals("") ) {
            DTError.error( cfg, toClient, "Unspecified Auction Id." );
            return;
        }
        
        try {
            auctionId = Long.parseLong(auctionIdString);
        }
        catch( Exception e ) {
            DTError.error( cfg, toClient, "AuctionId should be a number and is: " + auctionIdString );
            return;
        }

        if( auctionId <= 0 ) {
            DTError.error( cfg, toClient, "Non-positive Auction Id: " + auctionId );
            return;
        }
        
        try {
            amount = Float.parseFloat( amountString );
        }
        catch( Exception e ) {
            DTError.error( cfg, toClient, "Amount should be a number and is: " + amountString );
            return;
        }

        if( amount <= 0 ) {
            DTError.error( cfg, toClient, "Non-positive amount: " + amount );
            return;
        }

        try {
        	logic.bidOnItem(username, auctionId, amount);
        } 
        catch ( Exception e ) {
            DTError.error( cfg, toClient, e );
            return;
        }

        // Setup the data-model
        //
        Map<String,Object> root = new HashMap<String,Object>();

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
  }
}
