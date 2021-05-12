import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
@WebServlet("/gpa")
public class GPACalculator extends HttpServlet {
	
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

		response.setContentType("text/html");
		PrintWriter out = response.getWriter();

		// Write the response message, in an HTML page
		out.println("<!DOCTYPE html>");
		out.println("<html>");
		out.println("<head><title>GPA Calculator</title></head>");
		out.println("<body style=background-color:#333333; >"
				+ "<font color=#EEEEEE>");
		out.println("<font color=#00FF00><h1>Simple way to calculate your GPA</h1></font>");
		out.println("<form method=get>"
				+ "<legend>Fill in the informaton below:</legend></br>");
		
		for(int i = 0; i < 8; i++) {
			out.append(generateInputFields());
			out.append("<input type=hidden value=courseNums>");
		}
		
		out.append("<input type=submit value=SEND>"
				+ "&nbsp;<input type=reset value=CLEAR>"
				+ "</form>");
		processInput(request, response, out);
		out.println("</font>"
				+ "</body></html>");
	}
	
	// Calculate the GPA by parsing request Parameters.
	public void processInput(HttpServletRequest request, HttpServletResponse response, PrintWriter out) throws IOException {
		String[] courses = new String[5];
		String grades[] = new String[5];
		String creditHours[] = new String[5];
		courses = request.getParameterValues("course");
		grades = request.getParameterValues("grade");
		creditHours = request.getParameterValues("ch");
		
		out.append("<h2>******************************************************************</h2>");
		
		int i = 0;
		NumberFormat formatter = new DecimalFormat("#0.000");
		double creditsEarned = 0F, gpa = 0F, totalCredits = 0F;
		if (grades != null && courses != null && creditHours != null) {
			while(i < 5) {
				if(!(courses[i].isEmpty()) && !(grades[i].isEmpty()) && !(creditHours[i].isEmpty())) {
					out.append("<h3>* Course: " + courses[i] + ", Credit Hours: " + 
								creditHours[i] + ", Grade: " 
							+ grades[i] + "</h3>");
					creditsEarned += Double.parseDouble(creditHours[i]) * Double.parseDouble(grades[i]);
					totalCredits += Double.parseDouble(creditHours[i]);
				}
				i++;
			}
			gpa = creditsEarned / totalCredits;
			if (gpa < 3F) {
				out.append("<h3>* Cumulative GPA: <font color=#FF0000>" + formatter.format(gpa) + "</font></h3>");
				out.append("<h3>*<font color=#FF0000> A five year old could do better than you <3</font></h3>");
			} else out.append("<h3>* Cumulative GPA: <font color=#00FF00>" + formatter.format(gpa) + "</font></h3>");
		} else out.append("<h3>* <font color=#FF0000>No data provided!</font></h3>");
		out.append("<h2>******************************************************************</h2>");
	}
	
	// Return some HTML when called. Just to have concise code.
	public String generateInputFields() {
		return "Course Name: &nbsp;<input type=text name=course />"
		+ "&nbsp; Credit Hours: &nbsp;<input type=text name=ch />"
		+ "&nbsp;<input type=checkbox name=grade value=4.0 unchecked />A"
		+ "&nbsp;<input type=checkbox name=grade value=3.7 unchecked />A-"
		+ "&nbsp;<input type=checkbox name=grade value=3.3 unchecked />B+"
		+ "&nbsp;<input type=checkbox name=grade value=3.0 unchecked />B"
		+ "&nbsp;<input type=checkbox name=grade value=2.7 unchecked />B-"
		+ "&nbsp;<input type=checkbox name=grade value=2.3 unchecked />C+"
		+ "&nbsp;<input type=checkbox name=grade value=2.0 unchecked />C"
		+ "&nbsp;<input type=checkbox name=grade value=1.7 unchecked />C-"
		+ "&nbsp;<input type=checkbox name=grade value=1.3 unchecked />D+"
		+ "&nbsp;<input type=checkbox name=grade value=1.0 unchecked />D"
		+ "&nbsp;<input type=checkbox name=grade value=0.0 unchecked />E/F </br></br>";
	}
}
