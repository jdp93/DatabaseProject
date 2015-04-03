package edu.uga.dawgtrades.boundary;

import java.util.List;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Calendar;
import java.util.Date;
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

public class AuctionItemPageTwo extends HttpServlet{

	private static final long serialVersionUID = 1L;
    static  String         templateDir = "WEB-INF/templates";
    static  String         resultTemplateName = "AuctionItemPageTwo-Result.ftl";

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
        String category = req.getParameter( "category" );
        String itemName = req.getParameter( "itemName" );
        String description = req.getParameter( "description" );
        String minPriceString = req.getParameter( "minPrice" );
        String a0 = req.getParameter( "a0" );
        String a1 = req.getParameter( "a1" );
        String a2 = req.getParameter( "a2" );
        String a3 = req.getParameter( "a3" );
        String a4 = req.getParameter( "a4" );
        String expirationString = req.getParameter("expiration");
        float minPrice = 0;
        Date expiration = new Date();

        if( username == "" ) {
            DTError.error( cfg, toClient, "Unspecified Username." );
            return;
        }
        
        if( category == "" ) {
            DTError.error( cfg, toClient, "Unspecified Username." );
            return;
        }
        
        if( itemName == "" ) {
            DTError.error( cfg, toClient, "Unspecified Username." );
            return;
        }
        
        if( description == "" ) {
            DTError.error( cfg, toClient, "Unspecified Username." );
            return;
        }
        
        if( minPriceString == "" ) {
            DTError.error( cfg, toClient, "Unspecified Username." );
            return;
        }
        
        try {
            minPrice = Float.parseFloat( minPriceString );
        }
        catch( Exception e ) {
            DTError.error( cfg, toClient, "minrice should be a number and is: " + minPriceString );
            return;
        }

        if( minPrice <= 0 ) {
            DTError.error( cfg, toClient, "Non-positive minPrice: " + minPrice );
            return;
        }
        
        LinkedList<String> attributes = new LinkedList<String>();
        if(!a0.equals(""))
        	attributes.add(a0);
        if(!a1.equals(""))
        	attributes.add(a1);
        if(!a2.equals(""))
        	attributes.add(a2);
        if(!a3.equals(""))
        	attributes.add(a3);
        if(!a4.equals(""))
        	attributes.add(a4);
        
        Calendar cal = Calendar.getInstance();
        cal.setTime(expiration);
        if (expirationString.equals("1")){
             cal.add(Calendar.DATE, 1);
             expiration = cal.getTime();
        }
        else if (expirationString.equals("2")){
            cal.add(Calendar.DATE, 2);
            expiration = cal.getTime();
        }
        else{
            cal.add(Calendar.DATE, 3);
            expiration = cal.getTime();
        }
       
        	

        try {
        	logic.auctionItem(username, category, itemName, description, minPrice, expiration, attributes);
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
