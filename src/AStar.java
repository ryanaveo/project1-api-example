import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


// Declaring a WebServlet called AStarServlet, which maps to url "/aStar"
@WebServlet(name = "AStarServlet", urlPatterns = "/aStar")
public class AStar extends HttpServlet {
    private static final long serialVersionUID = 2L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public AStar() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String loginUser = "mytestuser";
        String loginPasswd = "mypassword";
        String loginUrl = "jdbc:mysql://localhost:3306/moviedb";

        response.setContentType("application/json"); // Response mime type

        // Retrieve parameter id from url request.
        String id = request.getParameter("id");
        System.out.println(id);

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        try {
            // Class.forName("org.gjt.mm.mysql.Driver");
            Class.forName("com.mysql.jdbc.Driver").newInstance();

            Connection dbcon = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);


            // Construct a query with parameter represented by "?"
            String query = "SELECT * from stars as s, stars_in_movies as sim, movies as m where m.id = sim.movieId and sim.starId = s.id and s.id = ?";

            // Declare our statement
            PreparedStatement statement = dbcon.prepareStatement(query);

            // Set the parameter represented by "?" in the query to the id we get from url, num 1 indicates the first "?" in the query
            statement.setString(1, id);

            // Perform the query
            ResultSet rs = statement.executeQuery();


            JsonArray jsonArray = new JsonArray();

            // Iterate through each row of rs
            while (rs.next()) {


                String starId = rs.getString("starId");
                String starName = rs.getString("name");
                String starDob = rs.getString("birthYear");

                String movieId = rs.getString("movieId");
                String movieTitle = rs.getString("title");
                String movieYear = rs.getString("year");
                String movieDirector = rs.getString("director");

                // Create a JsonObject based on the data we retrieve from rs

                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("star_id", starId);
                jsonObject.addProperty("star_name", starName);
                jsonObject.addProperty("star_dob", starDob);
                jsonObject.addProperty("movie_id", movieId);
                jsonObject.addProperty("movie_title", movieTitle);
                jsonObject.addProperty("movie_year", movieYear);
                jsonObject.addProperty("movie_director", movieDirector);

                jsonArray.add(jsonObject);
            }

            out.write(jsonArray.toString());

            rs.close();
            statement.close();
            dbcon.close();
        } catch (Exception e) {
            out.println("<HTML>" + "<HEAD><TITLE>" + "MovieDB: Error" + "</TITLE></HEAD>\n<BODY>"
                    + "<P>SQL error in doGet: " + e.getMessage() + "</P></BODY></HTML>");
            return;
        }
        out.close();

    }

}