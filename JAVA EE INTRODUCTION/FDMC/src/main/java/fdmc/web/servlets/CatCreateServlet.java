package fdmc.web.servlets;

import fdmc.domain.entities.Cat;
import fdmc.util.HtmlReader;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/cats/create")
public class CatCreateServlet extends HttpServlet {
    private final static String CAT_CREATE_HTML_PATH =
            "G:\\SoftUni\\SoftUni-source-code\\Java-Web-Module-January-2019\\JAVA EE INTRODUCTION\\FDMC\\src\\resources\\views\\cats-create.html";

    private final HtmlReader htmlReader;

    @Inject
    public CatCreateServlet(HtmlReader htmlReader) {
        this.htmlReader = htmlReader;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        resp.getWriter().println(this.htmlReader.readHtmlFile(CAT_CREATE_HTML_PATH));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Cat cat = new Cat();
        cat.setName(req.getParameter("name"));
        cat.setBreed(req.getParameter("breed"));
        cat.setColor(req.getParameter("color"));
        cat.setAge(Integer.parseInt(req.getParameter("age")));
    }
}
