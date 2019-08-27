package fdmc.web.servlets;

import fdmc.domain.entities.Cat;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@WebServlet("/cats/all")
public class CatAllServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        StringBuilder htmlOutput = new StringBuilder();

        if (req.getSession().getAttribute("cats") == null) {
            htmlOutput.append("<h1>All Cats</h1>")
                    .append(System.lineSeparator()).append("<hr/>")
                    .append(System.lineSeparator())
                    .append("<h2>There are no cats.<a href=\"/cats/create\">Create some!</a></h2>")
                    .append(System.lineSeparator()).append("<br/>")
                    .append(System.lineSeparator())
                    .append("<a href=\"/\">Back To Home</a>");
        } else {
            htmlOutput
                    .append("<h1>All Cats</h1>")
                    .append(System.lineSeparator())
                    .append("<hr/>")
                    .append(System.lineSeparator());

            ((Map<String, Cat>) req.getSession().getAttribute("cats"))
                    .forEach((key, value) ->
                            htmlOutput
                                    .append(
                                            String.format("<h3><a href=\"/cats/profile?catName=%s\">%s</a></h3>",
                                            key, key))
                                    .append(System.lineSeparator()));
            htmlOutput
                    .append("<br/>")
                    .append("<a href=\"/\">Back To Home</a>");
        }

        resp.getWriter().println(htmlOutput.toString());
    }
}
