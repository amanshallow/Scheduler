import javax.servlet.http.HttpServlet;
import java.io.*;
import java.net.CookieHandler;
import java.net.CookieManager;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@SuppressWarnings("serial")
@WebServlet("/scheduler")
public class ScheduleBuilder extends HttpServlet {
	
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		
		out.println("<!DOCTYPE html>"
				+ "<html>"
				+ "<head><title>Scheduler</title></head>"
				+ "<body style=background-color:#333333;>"
				+ "<font color=#FFFFFF>"
				+ "<h1>Build your schedule!</h1>"
				+ "<h2><form method=get>"
				+ "&nbsp; First name: &nbsp;<input type=text name=userName />");
		
		String[] cookieValue = new String[1];
		cookieValue = request.getParameterValues("userName");
		if (cookieValue != null) {
			Cookie cookie = new Cookie("scheduler", cookieValue[0]);
		    cookie.setMaxAge(999 * 999);
		    response.addCookie(cookie);
		    response.sendRedirect("/ProjectOne/scheduler");
		}
		
	    Cookie[] cookies = request.getCookies();
	    if (cookies != null) {
		    for (Cookie cookie1 : cookies) {
		        if (cookie1.getName().equals("scheduler")) {
		           out.append("&nbsp; &nbsp; Welcome " + cookie1.getValue() + "!");
		        }
		    }
	    }
		out.println("</h2></form>"
				+ "<h3>*** This page uses cookies to demonstrate a stateful servlet. ***</h3>"
				+ "</font>"
				+ "</body></html>");
	}

}
