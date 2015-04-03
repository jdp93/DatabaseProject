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

public class CreateCategoryPageTwo extends HttpServlet{

	private static final long serialVersionUID = 1L;
    static  String         templateDir = "WEB-INF/templates";
    static  String         resultTemplateName = "CreateCategoryPageTwo-Result.ftl";

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
        String name = req.getParameter( "name" );
        String parent = req.getParameter( "parent" );
        String at0 = req.getParameter( "at0" );
        String at1 = req.getParameter( "at1" );
        String at2 = req.getParameter( "at2" );
        String at3 = req.getParameter( "at3" );
        String at4 = req.getParameter( "at4" );

        if( name == null ) {
            DTError.error( cfg, toClient, "Unspecified Category Name." );
            return;
        }
        
        LinkedList<String> attrTypes = new LinkedList<String>();
        if(!at0.equals(""))
        	attrTypes.add(at0);
        if(!at1.equals(""))
        	attrTypes.add(at1);
        if(!at2.equals(""))
        	attrTypes.add(at2);
        if(!at3.equals(""))
        	attrTypes.add(at3);
        if(!at4.equals(""))
        	attrTypes.add(at4);

        try {
        	if (parent.equals("")){
        		logic.defineRoot(name, attrTypes);
        	}
        	else{
        		logic.defineCategory(name, parent, attrTypes);
        	}
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
