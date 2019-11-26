// Name : Thanirin Trironnarith
// ID : 6088122
// Section : 1A

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SimpleMovieRecommender implements BaseMovieRecommender {

    ////////////////////////// VARIABLES //////////////////////////////////
    public Map<Integer, Movie> movies = new HashMap<>();          // Map to keep all the movies
    public Map<Integer, User> users = new HashMap<>();            // Map to keep all the users

    public BiMap<Integer, Integer> mIndex;                              // 1-1 Map --> index to movie ID
    public BiMap<Integer, Integer> uIndex;                              // 1-1 Map --> index to user ID

    double[][] userRateMovie;               // Variable : 1st dimension -> every user in users // 2nd dimension -> the rating of a movie (every movie in movies) of the 1st dimension user
    double[][] similarity;                  // Variable : 1st dimension -> every user in users // 2nd dimension -> similarity of every user in users to the 1st dimension user
    //////////////////////////////////////////////////////////////////////

    @Override
    public Map<Integer, Movie> loadMovies(String movieFilename) {

        Map<Integer, Movie> returnThis = new HashMap<>();                                     // Map to store the data
        Pattern p = Pattern.compile("^([\\d]+),\"?([\\S ]+)\\s\\(([\\d]{4})\\)\"?,(.*)$");          // Create and Compile pattern

        ///////////////////// LOAD FILE //////////////////////

        LineIterator itr = null;                                        // We use this to read line, but it's more efficient than BufferedReader
        try{
            itr = FileUtils.lineIterator(new File(movieFilename), "UTF-8");
            while (itr.hasNext()) {
                String line = itr.nextLine();                           // Variable : the line read is called line (String type)
                Matcher m = p.matcher(line);

                if (m.matches()) {
                    int mID = Integer.parseInt(m.group(1));
                    int year = Integer.parseInt(m.group(3));
                    Movie avicii = new Movie(mID, m.group(2), year);    // Object : create object from the data read (naming it Avicii to honour Avicii's death)

                    for(String t : m.group(4).split("\\|")){     // Put tags in the last group of regex (after splitted) in tag of the movie
                        avicii.addTag(t);
                    }
                    returnThis.put(mID, avicii);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (itr != null) {
                LineIterator.closeQuietly(itr);
            }
        }
        return returnThis;
    }

    @Override
    public Map<Integer, User> loadUsers(String ratingFilename) {
        Map<Integer, User> returnThis = new HashMap<>();
        //////////////////// LOAD FILE ////////////////////////
        LineIterator itr_chan = null;
        try {
            itr_chan = FileUtils.lineIterator(new File(ratingFilename));
            itr_chan.nextLine();                                                   // To skip the first line
            while (itr_chan.hasNext()) {
                String line = itr_chan.nextLine();
                String[] splitted = line.split(",");
                int uID = Integer.parseInt(splitted[0]);
                int mID = Integer.parseInt(splitted[1]);
                double rating = Double.parseDouble(splitted[2]);
                long timeStamp = Long.parseLong(splitted[3]);

                User anon = new User(uID);
                if (movies.get(mID) != null && rating >= 0.5 && rating <= 5) {    // check if movie exists, and the rating in the range given
                    if (returnThis.containsKey(uID)) {
                        returnThis.get(uID).addRating(movies.get(mID), rating, timeStamp);
                    } else {
                        returnThis.put(uID, anon);
                        returnThis.get(uID).addRating(movies.get(mID), rating, timeStamp);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (itr_chan != null) {
                LineIterator.closeQuietly(itr_chan);
            }
        }
        return returnThis;
    }

    @Override
    public void loadData(String movieFilename, String userFilename) {
        movies = loadMovies(movieFilename);
        users = loadUsers(userFilename);
    }

    @Override
    public Map<Integer, Movie> getAllMovies() {
        return movies;
    }

    @Override
    public Map<Integer, User> getAllUsers() {
        return users;
    }

    @Override
    public void trainModel(String modelFilename) {
        /////////////////// Create Index BiMap /////////////////////
        int user_num = 0, movie_num = 0;            // Variable : user index (u), and movie index (m)

        uIndex = HashBiMap.create();                // Create the existing user index map
        mIndex = HashBiMap.create();                // Create the existing movie index map

        for (Integer i : users.keySet()) {          // looping each user id to the assigned index
            uIndex.put(user_num++, i);
        }

        for (Integer i : movies.keySet()) {         // looping each movie id to the assigned index
            mIndex.put(movie_num++, i);
        }
        ////////////////////////////////////////////////////////////
        System.out.println("@@@ Computing user rating matrix");
        userRateMovie = new double[user_num][movie_num + 1];        // Instantiate the variable, userRateMovie
        similarity = new double[user_num][user_num];                // Instantiate the variable, similarity
        // get the average rating of EVERY MOVIE for EACH USER..
        // put those in a 2D array 'userRateMovie'
        for(int i=0; i<user_num;i++) {
            for(int j=0; j<movie_num; j++) {
                // Check if the rating of the movie j exists in the usesr i
                if (users.get(uIndex.get(i)).ratings.get(mIndex.get(j)) != null) {
                    userRateMovie[i][j] = users.get(uIndex.get(i)).ratings.get(mIndex.get(j)).rating;
                } else userRateMovie[i][j] = 0;
            }
            userRateMovie[i][movie_num] = users.get(uIndex.get(i)).getMeanRating();
        }

        System.out.println("@@@ Computing user sim matrix");
        // similarity is a method created to find similarity, so the code can be read easily
        similarity();
        System.out.println("@@@ Writing out model file");
        ///////////////////// Writing File /////////////////////////
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(modelFilename);
            pw.println("@NUM_USERS " + user_num + "\n" +
                    "@USER_MAP " + uIndex + "\n" +
                    "@NUM_MOVIES " + movie_num + "\n" +
                    "@MOVIE_MAP " + mIndex + "\n" +
                    "@RATING_MATRIX");
            for(int i=0; i<user_num; i++) {
                for(int j=0; j<movie_num; j++) {
                    pw.print(userRateMovie[i][j] + " ");
                }
                pw.println(userRateMovie[i][movie_num]);
            }
            pw.println("@USERSIM_MATRIX");
            for(int i=0; i<user_num; i++) {
                for(int j=0; j<user_num; j++) {
                    pw.print(similarity[i][j] + " ");
                }
                pw.println();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (pw != null) {
                pw.close();
            }
        }
        ////////////////////////////////////////////////////////////
    }

    ////////////////////  created method ///////////////////////
    /**
     * this method is th find similarity between 2 users
     * (user u, user v)
     * the similarity is stored in the 2D array called 'similarity' created above
     * (at the top)
     */
    public void similarity() {
        double numer, deno1, deno2, simCal, deno;
        int ms = movies.size();

        for(int u=0; u<users.size(); u++) {
            for(int v=0; v<=u; v++) {
                numer = 0;          // Numerator
                deno1 = 0;          // Denominator #1
                deno2 = 0;          // Denominator #2
                for(int r=0; r<ms; r++) {
                    if (userRateMovie[u][r] != 0 && userRateMovie[v][r] != 0) {
                        double du = userRateMovie[u][r] - userRateMovie[u][ms];     // du = difference between every movie user U rated and the average rating of user U
                        double dv = userRateMovie[v][r] - userRateMovie[v][ms];     // dv = difference between every movie user V rated and the average rating of user V
                        numer += du * dv;
                        deno1 += Math.pow(du, 2);
                        deno2 += Math.pow(dv, 2);
                    }
                }
                deno = Math.sqrt(deno1) * Math.sqrt(deno2);             // The denominator from calculating deno1 and deno2
                if (deno == 0) {
                    simCal = 0.0;
                    if (u == v) {
                        simCal = 1.0;
                    }
                } else {
                    simCal = numer / deno;
                    if (u == v && Math.round(simCal) >= -1.0 && Math.round(simCal) <= 1.0) {
                        simCal = 1.0;
                    }
                }
                similarity[u][v] = simCal;
                similarity[v][u] = simCal;
            }
        }
    }
    ////////////////////////////////////////////////////////////

    @Override
    public void loadModel(String modelFilename) {
        /////////////////// Create Index BiMap /////////////////////
        int user_num = 0, movie_num = 0;            // Variable : user index (u), and movie index (m)

        uIndex = HashBiMap.create();                // Create the existing user index map
        mIndex = HashBiMap.create();                // Create the existing movie index map

        for (Integer i : users.keySet()) {          // looping each user id to the assigned index
            uIndex.put(user_num++, i);
        }

        for (Integer i : movies.keySet()) {         // looping each movie id to the assigned index
            mIndex.put(movie_num++, i);
        }
        ////////////////////////////////////////////////////////////
        LineIterator itr = null;
        userRateMovie = new double[users.size()][movies.size()+1];        // Instantiate the variable, userRateMovie
        similarity = new double[users.size()][users.size()];            // Instantiate the variable, similarity
        try {
            itr = new FileUtils().lineIterator(new File(modelFilename));
            // Reading modelFile
            // 5 unwanted line yeah
            itr.nextLine();
            itr.nextLine();
            itr.nextLine();
            itr.nextLine();
            itr.nextLine();

            String[] temp;      // Variable : array of String to keep elements in a line
            for(int i=0; i<user_num; i++) {
                if (itr.hasNext()) {
                    temp = itr.nextLine().split(" ");                              // Split elements in a line, and keep them in temp
                    for(int j=0; j<movie_num; j++) {
                        userRateMovie[i][j] = Double.parseDouble(temp[j]);                // get the elements in temp and put them in userRateMovie
                    }
                    userRateMovie[i][movie_num] = Double.parseDouble(temp[movie_num]);    // put the average rating (the last element) from temp to userRateMovie
                }
            }
            itr.nextLine();         // unwanted line
            for(int i=0; i<user_num; i++) {
                if (itr.hasNext()) {
                    temp = itr.nextLine().split(" ");                              // same process as ^^^^
                    for(int j=0; j<user_num; j++) {
                        similarity[i][j] = Double.parseDouble(temp[j]);                   // put the similarity in temp in similarity array
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (itr != null) {
                LineIterator.closeQuietly(itr);
            }
        }
    }

    @Override
    public double predict(Movie m, User u) {
        // Check if the given user (u) is in our trainModel system
        // If not return the mean rating of the given user (u)
        // if yes, proceed..
        if(!uIndex.containsValue(u.uid)) return u.getMeanRating();

        double numer = 0, deno = 0;                                     // Variable : The numerator/denominator of prediction equation
        int movieIndex = mIndex.inverse().get(m.mid);                   // Variable : the index of the given movie
        int userIndex = uIndex.inverse().get(u.uid);                    // Variable : the index of the given user
        double returnThis = userRateMovie[userIndex][movies.size()];    // Variable : return this value; default value = the mean rating of the given user

        for (int i = 0; i < uIndex.size(); i++) {
            // Check if the i user has rated Movie m
            // AND Check if the looped user (i) is not the same as the given user (u) --> check uId
            if (userRateMovie[i][movieIndex] != 0.0 && u.uid != uIndex.get(i)) {
                numer += similarity[userIndex][i] * (userRateMovie[i][movieIndex] - userRateMovie[i][movies.size()]);
                deno += Math.abs(similarity[userIndex][i]);
            }
        }
        // Check if the denominator of the equation is zero,
        // if it is then the prediction is average rating of the given user u (returnThis)
        // if not, then proceed to the next line
        if (deno == 0) {
            return returnThis;
        }
        returnThis += (numer / deno);
        returnThis = returnThis > 5.0 ? 5.0 : returnThis < 0.0 ? 0.0 : returnThis;  // If returnThis is more than 5, it is 5. If it is less than 0, it is 0. Else it remains the same.
        return returnThis;
    }

    @Override
    public List<MovieItem> recommend(User u, int fromYear, int toYear, int K) {
        List<MovieItem> recommend = new ArrayList<>();          // List : list of all Object MovieItem of the movies during the given yearly range/period
        if (u == null) {
            return recommend;
        }
        for (int i : getAllMovies().keySet()) {
            if (getAllMovies().get(i).year >= fromYear && getAllMovies().get(i).year <= toYear) {
                recommend.add(new MovieItem(getAllMovies().get(i), predict(getAllMovies().get(i), u)));
            }
        }
        Collections.sort(recommend);

        // return only the top K element, if the list size exceed K
        // if not return the whole list
        return recommend.size() > K ? recommend.subList(0, K) : recommend;
    }
}
