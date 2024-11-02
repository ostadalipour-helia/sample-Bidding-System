package server.ws;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html");
        HttpSession session = request.getSession();
        String target = "/WSC.jsp";
        
        String checkedCode = request.getParameter("checked");
        Item selectedItem = DatabaseUtil.getItemByCode(checkedCode);
        
        if (selectedItem != null) {
            session.setAttribute("fchecked", checkedCode);
            session.setAttribute("selectedItem", selectedItem);
        }
        
        if (session.getAttribute("firstTime") == null) {
            session.setAttribute("firstTime", 1);
        }
        
        request.getRequestDispatcher(target).forward(request, response);
        session.setAttribute("firstTime", null);
    }
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}