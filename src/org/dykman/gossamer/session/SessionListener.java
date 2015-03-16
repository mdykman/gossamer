package org.dykman.gossamer.session;

import javax.servlet.http.HttpSessionActivationListener;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;
import javax.servlet.http.HttpSessionEvent;

public class SessionListener implements HttpSessionActivationListener, HttpSessionAttributeListener, HttpSessionBindingListener
{

	public void sessionDidActivate(HttpSessionEvent arg0)
    {
//		HttpSession session = arg0.getSession();
System.out.println("        sessionDidActivate");		
    }

	public void sessionWillPassivate(HttpSessionEvent arg0)
    {
System.out.println("        sessionWillPassivate");		
	    
    }

	public void attributeAdded(HttpSessionBindingEvent arg0)
    {
System.out.println("        attributeAdded");		
    }

	public void attributeRemoved(HttpSessionBindingEvent arg0)
    {
System.out.println("        attributeRemoved");		
    }

	public void attributeReplaced(HttpSessionBindingEvent arg0)
    {
System.out.println("        attributeReplaced");		
    }

	public void valueBound(HttpSessionBindingEvent arg0)
    {
System.out.println("        valueBound");		
    }

	public void valueUnbound(HttpSessionBindingEvent arg0)
    {
System.out.println("        valueUnbound");		
    }

}
