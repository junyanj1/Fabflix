package edu.uci.ics.junyanj1.service.movies.core;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.junyanj1.service.movies.MovieService;
import edu.uci.ics.junyanj1.service.movies.logger.ServiceLogger;
import edu.uci.ics.junyanj1.service.movies.models.*;
import org.glassfish.jersey.jackson.JacksonFeature;

import javax.ws.rs.client.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class MovieRecords {
    public static int checkPrivilegeFromIDM(String email, int privilege) {
        ServiceLogger.LOGGER.info("Verifying privilege level with IDM...");

        // Create a new Client
        ServiceLogger.LOGGER.info("Building client...");
        Client client = ClientBuilder.newClient();
        client.register(JacksonFeature.class);

        ServiceLogger.LOGGER.info("Building URI...");
        String IDM_URI = MovieService.getMovieConfigs().getIdmUri();
        ServiceLogger.LOGGER.info("Setting path to endpoint...");
        String IDM_ENDPOINT_PATH = MovieService.getMovieConfigs().getIdmPrivilege();

        ServiceLogger.LOGGER.info("Building WebTarget...");
        WebTarget webTarget = client.target(IDM_URI).path(IDM_ENDPOINT_PATH);
        System.out.println(webTarget.toString());

        ServiceLogger.LOGGER.info("Starting invocation builder...");
        Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);

        ServiceLogger.LOGGER.info("Setting payload of the request.");
        IDMPrivilegeRequestModel requestModel = new IDMPrivilegeRequestModel(email, privilege);

        ServiceLogger.LOGGER.info("Sending request...");
        Response response = invocationBuilder.post(Entity.entity(requestModel,MediaType.APPLICATION_JSON));
        ServiceLogger.LOGGER.info("Sent!");

        // Check the status code
        if (response.getStatus() == 200) {
            ServiceLogger.LOGGER.info("Received status 200!");
            String jsonText = response.readEntity(String.class);
            ServiceLogger.LOGGER.info("jsonText: " + jsonText);
            IDMPrivilegeResponseParseModel getModel;

            try {
                ObjectMapper mapper = new ObjectMapper();
                getModel = mapper.readValue(jsonText, IDMPrivilegeResponseParseModel.class);
                if (getModel.getResultCode() == 140) {
                    ServiceLogger.LOGGER.info("User has sufficient privilege.");
                    return 1;
                } else if (getModel.getResultCode() == 141) {
                    ServiceLogger.LOGGER.info("User has insufficient privilege.");
                    return 0;
                } else {
                    ServiceLogger.LOGGER.info("User not found.");
                    return -1;
                }
            } catch (IOException e) {
                e.printStackTrace();
                if (e instanceof JsonMappingException) {
                    ServiceLogger.LOGGER.warning("Unable to map JSON to POJO.");
                    return -1;
                }
                else if (e instanceof JsonParseException) {
                    ServiceLogger.LOGGER.warning("Unable to parse JSON.");
                    return -1;
                }
                else {
                    ServiceLogger.LOGGER.warning("Other IOException.");
                    return -1;
                }
            }
        } else {
            ServiceLogger.LOGGER.warning("Failed to retrieve the privilege.");
            return -1;
        }
    }

    public static ArrayList<MovieModel> searchForMoviesFromDb(SearchRequestModel requestModel, String email) {
        ServiceLogger.LOGGER.info("Retrieving search result from database...");
        int cpl = checkPrivilegeFromIDM(email,3);
        boolean show;
        if (cpl == -1) {
            return null;
        } else if (cpl == 1) {
            show = true;
        } else {
            show = false;
        }

        try {
            String queryCondition = "";
            if (requestModel.getTitle() != null) {
                queryCondition += "  AND title LIKE '%" + requestModel.getTitle() +"%'\n";
            }
            if (requestModel.getGenre() != null) {
                queryCondition += "  AND genres.name LIKE '%" + requestModel.getGenre() +"%'\n";
            }
            if (requestModel.getYear() != 0) {
                queryCondition += "  AND year = " + requestModel.getYear() +"\n";
            }
            if (requestModel.getDirector() != null) {
                queryCondition += "  AND director LIKE '%" + requestModel.getDirector() +"%'\n";
            }
            if (show==true && requestModel.isHidden()!=null) {
                if (requestModel.isHidden() == true)
                    queryCondition += "  AND hidden = 1\n";
                else
                    queryCondition += "  AND hidden = 0\n";
            }
            if (show==false) {
                queryCondition += "  AND hidden = 0\n";
            }

            String query =
                    String.format("SELECT DISTINCT movies.id AS movieId,title,director,year,backdrop_path,budget,overview,poster_path,revenue,rating,numVotes,hidden\n" +
                            "FROM movies, ratings, genres, genres_in_movies\n" +
                            "WHERE movies.id = ratings.movieId AND movies.id = genres_in_movies.movieId\n" +
                            "  AND genres_in_movies.genreId = genres.id\n" +
                            "%s" +
                            "ORDER BY %s %s, RATING DESC\n" +
                            "LIMIT  ?,?;", queryCondition,requestModel.getDirection(),requestModel.getOrderby());

            if (requestModel.getDirection().toUpperCase().equals("RATING")) {
                query =
                        String.format("SELECT DISTINCT movies.id AS movieId,title,director,year,backdrop_path,budget,overview,poster_path,revenue,rating,numVotes,hidden\n" +
                                "FROM movies, ratings, genres, genres_in_movies\n" +
                                "WHERE movies.id = ratings.movieId AND movies.id = genres_in_movies.movieId\n" +
                                "  AND genres_in_movies.genreId = genres.id\n" +
                                "%s" +
                                "ORDER BY %s %s, TITLE ASC\n" +
                                "LIMIT  ?,?;", queryCondition,requestModel.getDirection(),requestModel.getOrderby());
            }
            PreparedStatement ps = MovieService.getCon().prepareStatement(query);

            ps.setInt(1,requestModel.getOffset());
            ps.setInt(2,requestModel.getLimit());

            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Query successful.");

            ArrayList<MovieModel> movies = new ArrayList<MovieModel>();
            while (rs.next()) {
                int h = rs.getInt("hidden");
                Boolean hidden;
                if (show == false) {
                    hidden = null;
                } else {
                    if (h==0)
                        hidden = false;
                    else
                        hidden = true;
                }

                movies.add(new MovieModel(rs.getString("movieId"),
                        rs.getString("title"),
                        rs.getString("director"),
                        rs.getInt("year"),
                        rs.getFloat("rating"),
                        rs.getInt("numVotes"),
                        hidden
                ));
            }
            return movies;

        } catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Unable to perform search.");
            e.printStackTrace();
            return null;
        }
    }

    public static FullMovieModel searchForMoviesByIdInDb(String email, String id) {
        ServiceLogger.LOGGER.info("Retrieving movie by id from database...");
        int cpl = checkPrivilegeFromIDM(email,4);
        boolean check;
        if (cpl == -1) {
            return null;
        } else if (cpl == 1) {
            check = false;
        } else {
            check = true;
        }

        try {
            String queryCondition = "";

            queryCondition += "  AND movies.id = '"+ id +"'\n;";


            String query =
                    String.format("SELECT DISTINCT movies.id AS movieId,title,director,year,backdrop_path,budget,overview,poster_path,revenue,rating,numVotes,hidden\n" +
                            "FROM movies, ratings, genres, genres_in_movies\n" +
                            "WHERE movies.id = ratings.movieId AND movies.id = genres_in_movies.movieId\n" +
                            "  AND genres_in_movies.genreId = genres.id\n" +
                            "%s", queryCondition);
            PreparedStatement ps = MovieService.getCon().prepareStatement(query);

            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Query successful.");

            FullMovieModel movie = new FullMovieModel();
            movie.setMovieId("Not found.");
            if (rs.next()) {
                int h = rs.getInt("hidden");
                Boolean hidden;
                if (check == false) {
                    hidden = null;
                } else {
                    if (h==0)
                        hidden = null;
                    else
                        hidden = true;
                }

                movie.setMovieId(rs.getString("movieId"));
                movie.setTitle(rs.getString("title"));
                movie.setDirector(rs.getString("director"));
                movie.setYear(rs.getInt("year"));
                movie.setBackdrop_path(rs.getString("backdrop_path"));
                movie.setBudget(rs.getInt("budget"));
                movie.setOverview(rs.getString("overview"));
                movie.setPoster_path(rs.getString("poster_path"));
                movie.setRevenue(rs.getInt("revenue"));
                movie.setRating(rs.getFloat("rating"));
                movie.setNumVotes(rs.getInt("numVotes"));
                movie.setHidden(hidden);
            }
            return movie;

        } catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Unable to perform search.");
            e.printStackTrace();
            return null;
        }
    }

    public static ArrayList<GenreModel> searchMovieGenreInDb(FullMovieModel movieModel) {
        ServiceLogger.LOGGER.info("Search for genre for the given movie...");
        try {
            String query =
                    "SELECT distinct genres.id AS id, genres.name AS name\n" +
                            "FROM genres, movies, genres_in_movies\n" +
                            "WHERE movies.id = genres_in_movies.movieId AND genres.id = genres_in_movies.genreId AND movies.id = ?;";
            PreparedStatement ps = MovieService.getCon().prepareStatement(query);

            ps.setString(1,movieModel.getMovieId());

            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Query successful.");

            ArrayList<GenreModel> genres = new ArrayList<GenreModel>();
            while (rs.next()) {
                genres.add(new GenreModel(rs.getInt("id"),
                        rs.getString("name")));
            }
            if (genres.isEmpty())
                return null;
            return genres;
        } catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Unable to get genres.");
            e.printStackTrace();
            return null;
        }
    }

    public static ArrayList<StarModel> searchMovieStarByIdInDb(FullMovieModel movieModel) {
        ServiceLogger.LOGGER.info("Search for stars for the given movie id...");
        try {
            String query =
                    "SELECT distinct stars.id AS id, stars.name AS name, stars.birthYear AS birthYear\n" +
                            "FROM stars, stars_in_movies, movies\n" +
                            "WHERE movies.id = stars_in_movies.movieId AND stars_in_movies.starId = stars.id AND movies.id = ?;";

            PreparedStatement ps = MovieService.getCon().prepareStatement(query);
            ps.setString(1,movieModel.getMovieId());

            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Query successful.");

            ArrayList<StarModel> stars = new ArrayList<StarModel>();
            while (rs.next()) {
                stars.add(new StarModel(rs.getString("id"),
                        rs.getString("name"),
                        rs.getInt("birthYear")));
            }
            
            return stars;
        } catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Unable to get genres.");
            e.printStackTrace();
            return null;
        }
    }

    public static Boolean isMovieInDb(String title, String director, int year) {
        ServiceLogger.LOGGER.info("Checking if the movie is in database...");
        try {
            String query =
                    "SELECT *\n" +
                            "FROM movies\n" +
                            "WHERE title = ? AND director = ? AND year = ? AND hidden = 0;";
            PreparedStatement ps = MovieService.getCon().prepareStatement(query);
            ps.setString(1,title);
            ps.setString(2,director);
            ps.setInt(3,year);

            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Query successful.");

            if (rs.next())
                return true;
            else
                return false;

        } catch (SQLException e) {
            ServiceLogger.LOGGER.info("Unable to perform check.");
            e.printStackTrace();
            return null;
        }
    }

    public static String insertMovieToDb(AddRequestModel requestModel) {
        ServiceLogger.LOGGER.info("Inserting movie to Database...");
        for (GenreModel g : requestModel.getGenres()) {
            if (!genreInDb(g.getName().substring(0,1).toUpperCase()+g.getName().substring(1).toLowerCase())) {
                boolean success = insertGenreToDb(g.getName().substring(0,1).toUpperCase()+g.getName().substring(1).toLowerCase());
                if(!success)
                    return "error";
            }
        }
        try {
            String query =
                    "INSERT INTO movies(id, title,year,director,backdrop_path,budget,overview,poster_path,revenue,hidden)\n" +
                            "VALUES (?,?,?,?,?,?,?,?,?,0);";
            PreparedStatement ps = MovieService.getCon().prepareStatement(query);
            int cs = checkWhatIsLastMovieIdInDb();
            if (cs < 0)
                return "error";
            String id = String.format("cs%07d",cs+1);
            ServiceLogger.LOGGER.info("id: " + id);
            ps.setString(1,id);
            ps.setString(2,requestModel.getTitle());
            ps.setInt(3,requestModel.getYear());
            ps.setString(4,requestModel.getDirector());
            ps.setString(5,requestModel.getBackdrop_path());
            ps.setInt(6,requestModel.getBudget());
            ps.setString(7,requestModel.getOverview());
            ps.setString(8,requestModel.getPoster_path());
            ps.setInt(9,requestModel.getRevenue());


            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            ps.execute();
            ServiceLogger.LOGGER.info("Query successful.");

            return id;

        } catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Unable to perform insertion.");
            e.printStackTrace();
            return "error";
        }
    }

    public static Boolean genreInDb(String name) {
        ServiceLogger.LOGGER.info("Checking if genre is in database...");
        try {
            String query =
                    "SELECT *\n" +
                            "FROM genres\n" +
                            "WHERE name = ?;";
            PreparedStatement ps = MovieService.getCon().prepareStatement(query);
            ps.setString(1,name);

            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Query successful.");

            if (rs.next())
                return true;
            else
                return false;

        } catch (SQLException e) {
            ServiceLogger.LOGGER.info("Unable to perform check.");
            e.printStackTrace();
            return null;
        }
    }

    public static boolean insertGenreToDb(String name) {
        ServiceLogger.LOGGER.info("Inserting genre to Db...");

        name = name.substring(0,1).toUpperCase()+name.substring(1).toLowerCase();

        try {
            String query =
                    "INSERT INTO genres(name)\n" +
                            "VALUES (?);";
            PreparedStatement ps = MovieService.getCon().prepareStatement(query);
            ps.setString(1,name);

            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            ps.execute();
            ServiceLogger.LOGGER.info("Query successful.");

            return true;

        } catch (SQLException e) {
            ServiceLogger.LOGGER.info("Unable to perform insertion.");
            e.printStackTrace();
            return false;
        }
    }

    public static int checkWhatIsLastMovieIdInDb() {
        ServiceLogger.LOGGER.info("Checking last cs%% movie ID in database...");
        try {
            String query =
                    "SELECT max(id) AS max\n" +
                            "FROM movies\n" +
                            "WHERE id LIKE 'cs%';";
            PreparedStatement ps = MovieService.getCon().prepareStatement(query);

            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Query successful.");

            if (rs.next()) {
                String maxId = rs.getString("max");
                ServiceLogger.LOGGER.info("last cs%% ID: " + maxId);
                if (maxId == null)
                    maxId = "cs0000000";
                int result = Integer.parseInt(maxId.substring(2));
                ServiceLogger.LOGGER.info("cs%%: " + result);
                return result;
            }
            else
                return 0;

        } catch (SQLException e) {
            ServiceLogger.LOGGER.info("Unable to perform check.");
            e.printStackTrace();
            return -1;
        }
    }

    public static boolean initiateGenresInMovies(String movieId, GenreModel[] genres) {
        ServiceLogger.LOGGER.info("Initialize the genres for a new movie.");

        Map<String,Integer> genreMap = whatIsTheGenreListInDb();
        if (genreMap == null) {
            ServiceLogger.LOGGER.info("GenreMap retrieve failed.");
            return false;
        }

        try {
            String query =
                    "INSERT INTO genres_in_movies(genreId,movieId)\n" +
                            "VALUES (?,?);";
            PreparedStatement ps = MovieService.getCon().prepareStatement(query);

            for (GenreModel g : genres) {
                Integer id = genreMap.get(g.getName().substring(0,1).toUpperCase()+g.getName().substring(1).toLowerCase());
                ps.setInt(1,id);
                ps.setString(2,movieId);
                ps.addBatch();
            }
            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            int[] r = ps.executeBatch();
            ServiceLogger.LOGGER.info("Query successful.");

            return true;

        } catch (SQLException e) {
            ServiceLogger.LOGGER.info("Unable to initiate genre-movie relation.");
            e.printStackTrace();
            return false;
        }
    }

    public static boolean initiateNewMovieRatings(String movieId) {
        ServiceLogger.LOGGER.info("Initiate the rating of a given movie.");

        try {
            String query =
                    "INSERT INTO ratings(movieId,rating,numVotes)\n" +
                            "VALUES (?,?,0);";
            PreparedStatement ps = MovieService.getCon().prepareStatement(query);
            ps.setString(1,movieId);
            ps.setFloat(2,0.0f);

            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            ps.execute();
            ServiceLogger.LOGGER.info("Query successful.");

            return true;

        } catch (SQLException e) {
            ServiceLogger.LOGGER.info("Unable to update ratings.");
            e.printStackTrace();
            return false;
        }
    }

    public static Map<String,Integer> whatIsTheGenreListInDb() {
        ServiceLogger.LOGGER.info("Receiving the request to get every id-genre combination.");

        try {
            String query =
                    "SELECT id, name\n" + "FROM genres\n";
            PreparedStatement ps = MovieService.getCon().prepareStatement(query);

            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Query successful.");

            Map<String,Integer> genreMap = new HashMap<String, Integer>();
            while (rs.next()) {
                String name = rs.getString("name");
                name = name.substring(0,1).toUpperCase()+name.substring(1).toLowerCase();
                genreMap.put(name,rs.getInt("id"));
            }

            return genreMap;

        } catch (SQLException e) {
            ServiceLogger.LOGGER.info("Unable to get genre map.");
            e.printStackTrace();
            return null;
        }
    }

    public static Boolean checkHidden(String id) {
        ServiceLogger.LOGGER.info("Check if hidden...");
        try {
            String query =
                    "SELECT hidden FROM movies WHERE id LIKE ?;";
            PreparedStatement ps = MovieService.getCon().prepareStatement(query);
            ps.setString(1,id);

            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Query successful.");

            if (rs.next()) {
                int hidden = rs.getInt("hidden");
                if (hidden == 0)
                    return false;
                else
                    return true;
            }

            return null;
        } catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Unable to delete the movie.");
            e.printStackTrace();
            return null;
        }
    }

    public static Boolean deleteMovieByIdFromDb(String id) {
        ServiceLogger.LOGGER.info("Find movie of that specific");

        Boolean h = checkHidden(id);
        if (h == null)
            return null;
        else if (h == true)
            return false;

        try {
            String query =
                    "UPDATE movies\n" +
                            "SET hidden = 1\n" +
                            "WHERE id LIKE ?;";
            PreparedStatement ps = MovieService.getCon().prepareStatement(query);
            ps.setString(1,id);

            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            ps.execute();
            ServiceLogger.LOGGER.info("Query successful.");

            return true;

        } catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Unable to delete the movie.");
            e.printStackTrace();
            return null;
        }
    }

    public static ArrayList<GenreModel> getGenresFromDb() {
        ServiceLogger.LOGGER.info("Get all genres from Database in order...");

        try {
            String query =
                    "SELECT id, name\n" +
                            "FROM genres\n";
            PreparedStatement ps = MovieService.getCon().prepareStatement(query);

            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Query successful.");

            ArrayList<GenreModel> genres = new ArrayList<GenreModel>();

            while (rs.next()) {
                genres.add(new GenreModel(rs.getInt("id"),rs.getString("name")));
            }

            return genres;
        } catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Unable to delete the movie.");
            e.printStackTrace();
            return null;
        }
    }

    public static Boolean isGenreInDb(String name) {
        ServiceLogger.LOGGER.info("Check if the given genre is in database...");

        try {
            String query =
                    "SELECT *\n" +
                            "FROM genres\n" +
                            "WHERE name = ?;";
            PreparedStatement ps = MovieService.getCon().prepareStatement(query);
            ps.setString(1,name);

            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Query successful.");

            if (rs.next())
                return true;

            return false;
        } catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Unable to delete the movie.");
            e.printStackTrace();
            return null;
        }
    }

    public static Boolean addGenreToDb(String name){
        ServiceLogger.LOGGER.info("Trying to insert given genre into the database...");

        name = name.substring(0,1).toUpperCase()+name.substring(1).toLowerCase();
        Boolean check = isGenreInDb(name);
        if (check == null)
            return null;
        else if (check == true)
            return false;
        else {
            try {
                String query =
                        "INSERT INTO genres(name)\n" +
                                "VALUES (?);";
                PreparedStatement ps = MovieService.getCon().prepareStatement(query);
                ps.setString(1,name);

                ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
                int rs = ps.executeUpdate();
                ServiceLogger.LOGGER.info("Query successful.");

                return true;
            } catch (SQLException e) {
                ServiceLogger.LOGGER.warning("Unable to delete the movie.");
                e.printStackTrace();
                return null;
            }
        }
    }

    public static ArrayList<GenreModel> checkGenresByIdInDb(String id) {
        ServiceLogger.LOGGER.info("Retrieving genres by movieId from database...");

        try {
            String query =
                    "SELECT genres.id AS id, name\n" +
                        "FROM genres, genres_in_movies\n" +
                            "WHERE genres_in_movies.movieId = ? AND genres.id = genres_in_movies.genreId;";
            PreparedStatement ps = MovieService.getCon().prepareStatement(query);
            ps.setString(1,id);

            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Query successful.");

            ArrayList<GenreModel> genres = new ArrayList<GenreModel>();

            while (rs.next()) {
                genres.add(new GenreModel(rs.getInt("id"),rs.getString("name")));
            }
            return genres;
        } catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Unable to delete the movie.");
            e.printStackTrace();
            return null;
        }
    }

    public static ArrayList<StarModel> searchStarsInDb(SearchForStarRequestModel requestModel) {
        ServiceLogger.LOGGER.info("Starting to search for stars in database...");
        try {
            String queryCondition = "";
            if (requestModel.getName() != null) {
                queryCondition += "  AND stars.name LIKE '%" + requestModel.getName() + "%'\n";
            }
            if (requestModel.getBirthYear() != 0) {
                queryCondition += "  AND stars.birthYear = " + requestModel.getBirthYear() + "\n";
            }
            if (requestModel.getMovieTitle() != null) {
                queryCondition += "  AND movies.title LIKE '%" + requestModel.getMovieTitle() + "%'\n";
            }

            String query =
                    String.format("SELECT distinct stars.id AS id, stars.birthYear AS birthYear, stars.name AS name\n" +
                            "FROM stars, movies, stars_in_movies\n" +
                            "WHERE movies.id = stars_in_movies.movieId AND stars_in_movies.starId = stars.id\n" +
                            "%s" +
                            "ORDER BY %s %s\n" +
                            "LIMIT ?,?;", queryCondition,requestModel.getOrderby(),requestModel.getDirection()
                    );
            if (requestModel.getOrderby().toUpperCase().equals("NAME")) {
                query =
                    String.format("SELECT distinct stars.id AS id, stars.birthYear AS birthYear, stars.name AS name\n" +
                            "FROM stars, movies, stars_in_movies\n" +
                            "WHERE movies.id = stars_in_movies.movieId AND stars_in_movies.starId = stars.id\n" +
                            "%s" +
                            "ORDER BY %s %s, BIRTHYEAR ASC\n" +
                            "LIMIT ?,?;", queryCondition,requestModel.getOrderby(),requestModel.getDirection()
                    );
            }
            PreparedStatement ps = MovieService.getCon().prepareStatement(query);
            ps.setInt(1,requestModel.getOffset());
            ps.setInt(2,requestModel.getLimit());

            ArrayList<StarModel> stars = new ArrayList<StarModel>();
            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Query successful.");

            while (rs.next()) {
                stars.add(new StarModel(rs.getString("id"),
                        rs.getString("name"),
                        rs.getInt("birthYear")));
            }
            return stars;

        } catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Unable to perform search.");
            e.printStackTrace();
            return null;
        }
    }

    public static StarModel searchForStarsByIdInDb(String id) {
        ServiceLogger.LOGGER.info("Starting to search for star by id in database...");
        try {
            String queryCondition = "";
            queryCondition += "  AND stars.id = '" + id + "'\n";


            String query =
                    String.format("SELECT distinct stars.id AS id, stars.birthYear AS birthYear, stars.name AS name\n" +
                            "FROM stars, movies, stars_in_movies\n" +
                            "WHERE movies.id = stars_in_movies.movieId AND stars_in_movies.starId = stars.id\n" +
                            "%s;", queryCondition
                    );
            PreparedStatement ps = MovieService.getCon().prepareStatement(query);

            StarModel star = new StarModel();
            star.setId("Not found.");
            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Query successful.");

            if (rs.next()) {
                star.setId(rs.getString("id"));
                star.setName(rs.getString("name"));
                star.setBirthYear(rs.getInt("birthYear"));
            }
            return star;

        } catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Unable to perform search.");
            e.printStackTrace();
            return null;
        }
    }

    public static int checkWhatIsLastStarIdInDb() {
        ServiceLogger.LOGGER.info("Checking last ss%% star ID in database...");
        try {
            String query =
                    "SELECT max(id) AS max\n" +
                            "FROM stars\n" +
                            "WHERE id LIKE 'ss%';";
            PreparedStatement ps = MovieService.getCon().prepareStatement(query);

            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Query successful.");

            if (rs.next()) {
                String maxId = rs.getString("max");
                ServiceLogger.LOGGER.info("last ss%% ID: " + maxId);
                if (maxId == null)
                    maxId = "ss0000000";
                int result = Integer.parseInt(maxId.substring(2));
                ServiceLogger.LOGGER.info("ss%%: " + result);
                return result;
            }
            else
                return 0;

        } catch (SQLException e) {
            ServiceLogger.LOGGER.info("Unable to perform check.");
            e.printStackTrace();
            return -1;
        }
    }

    public static Boolean doesStarExist(String name, int birthYear) {
        ServiceLogger.LOGGER.info("Check if the star is already in database...");
        try {
            String query =
                    "SELECT *\n" +
                        "FROM stars\n" +
                        "WHERE name = ? AND birthYear = ?;";
            PreparedStatement ps = MovieService.getCon().prepareStatement(query);
            ps.setString(1,name);
            ps.setInt(2,birthYear);

            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Query successful.");

            if(rs.next())
                return true;
            return false;
        } catch (SQLException e) {
            ServiceLogger.LOGGER.info("Unable to perform check.");
            e.printStackTrace();
            return null;
        }
    }

    public static Boolean insertStarToDb(AddStarRequestModel requestModel) {
        ServiceLogger.LOGGER.info("Inserting star to Database...");
        Boolean isDuplicate = doesStarExist(requestModel.getName(),requestModel.getBirthYear());
        if (isDuplicate == null)
            return null;
        else if (isDuplicate)
            return false;
        else {
            try {
                String query =
                        "INSERT INTO stars(id,name,birthYear)\n" +
                                "VALUES(?,?,?);";
                PreparedStatement ps = MovieService.getCon().prepareStatement(query);
                int ss = checkWhatIsLastStarIdInDb();
                if (ss < 0)
                    return null;
                String id = String.format("ss%07d",ss+1);
                ServiceLogger.LOGGER.info("id: " + id);
                ps.setString(1,id);
                ps.setString(2,requestModel.getName());
                int year = requestModel.getBirthYear();
                int currentYear = Calendar.getInstance().get(Calendar.YEAR);
                if (year > currentYear) {
                    ps.setNull(3,java.sql.Types.INTEGER);
                } else {
                    ps.setInt(3, requestModel.getBirthYear());
                }

                ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
                ps.execute();
                ServiceLogger.LOGGER.info("Query successful.");

                return true;
            } catch (SQLException e) {
                ServiceLogger.LOGGER.info("Unable to perform insertion.");
                e.printStackTrace();
                return null;
            }
        }
    }

    public static Boolean isMovieIdInDb(String movieId) {
        ServiceLogger.LOGGER.info("Checking if the movieId is in database...");
        try {
            String query =
                    "SELECT *\n" +
                            "FROM movies\n" +
                            "WHERE id = ?;";
            PreparedStatement ps = MovieService.getCon().prepareStatement(query);
            ps.setString(1,movieId);

            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Query successful.");

            if (rs.next())
                return true;
            else
                return false;

        } catch (SQLException e) {
            ServiceLogger.LOGGER.info("Unable to perform check.");
            e.printStackTrace();
            return null;
        }
    }

    public static Boolean insertStarIntoMovieInDb(String starId, String movieId) {
        ServiceLogger.LOGGER.info("Inserting star into movie in database...");
        try {
            String query =
                    "INSERT INTO stars_in_movies(starId,movieId)\n" +
                            "VALUES (?,?);";
            PreparedStatement ps = MovieService.getCon().prepareStatement(query);
            ps.setString(1,starId);
            ps.setString(2,movieId);

            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            int rs = ps.executeUpdate();
            ServiceLogger.LOGGER.info("Query successful.");

            return true;

        } catch (SQLException e) {
            if (e instanceof SQLIntegrityConstraintViolationException) {
                ServiceLogger.LOGGER.info("The combination is already in the database.");
                return false;
            }
            ServiceLogger.LOGGER.info("Unable to perform check.");
            e.printStackTrace();
            return null;
        }
    }

    public static RatingModel retrieveCurrentRatingFromDb(String id) {
        ServiceLogger.LOGGER.info("Retrieving the current rating of the movie.");
        try {
            String query =
                    "SELECT movieId,rating,numVotes\n" +
                            "FROM ratings\n" +
                            "WHERE movieId = ?;";
            PreparedStatement ps = MovieService.getCon().prepareStatement(query);
            ps.setString(1,id);

            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Query successful.");

            RatingModel rating = new RatingModel("Not found.");
            if (rs.next()) {
                rating.setMovieId(id);
                rating.setRating(rs.getFloat("rating"));
                rating.setNumVotes(rs.getInt("numVotes"));
            }

            return rating;
        } catch (SQLException e) {
            ServiceLogger.LOGGER.info("Unable to perform retrieval.");
            e.printStackTrace();
            return null;
        }
    }

    public static Boolean updateRatingInDb(String id, float rating) {
        ServiceLogger.LOGGER.info("Update the movie rating in database...");
        RatingModel previousRating = retrieveCurrentRatingFromDb(id);
        if (previousRating == null)
            return null;
        else if (previousRating.getMovieId().equals("Not found."))
            return false;

        try {
            String query =
                    "UPDATE ratings\n" +
                            "SET rating = ?, numVotes = ?\n" +
                            "WHERE movieId = ?;";
            PreparedStatement ps = MovieService.getCon().prepareStatement(query);
            int newNumVotes = previousRating.getNumVotes() + 1;
            float newRating = (previousRating.getNumVotes()*previousRating.getRating()+rating)/newNumVotes;
            ps.setFloat(1,newRating);
            ps.setInt(2,newNumVotes);
            ps.setString(3,id);

            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            int rs = ps.executeUpdate();
            ServiceLogger.LOGGER.info("Query successful.");

            if (rs == 0)
                return false;
            return true;
        } catch (SQLException e) {
            ServiceLogger.LOGGER.info("Unable to perform update.");
            e.printStackTrace();
            return null;
        }
    }
}
