// Name: Thanirin Trironnarith
// Student ID: 6088122
// Section: 1A

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class SimpleMovieSearchEngine implements BaseMovieSearchEngine {
	public Map<Integer, Movie> movies;
	
	@Override
	public Map<Integer, Movie> loadMovies(String movieFilename) {
		// YOUR CODE GOES HERE
		
		movies = new HashMap<Integer, Movie>();
		
		/////////Read file///////
		BufferedReader bfr = null;
		
		try {
			bfr = new BufferedReader(new FileReader(movieFilename));
			//////Pattern + Match/////
			String line;
			Pattern p = Pattern.compile("^([\\d]+),\"?([\\S ]+)\\s\\(([\\d]{4})\\)\"?,(.*)$");
			
			int mID, year;
			Movie thisMoviesNameIsNancy; ///Movie name matters
			
			while((line = bfr.readLine())!=null) {
				Matcher m = p.matcher(line);
				if(m.matches()) {
					mID = Integer.parseInt(m.group(1));
					year = Integer.parseInt(m.group(3));
					thisMoviesNameIsNancy = new Movie(mID, m.group(2), year);
					
					String[] fromSplitting = m.group(4).split("\\|");
					for(String t : fromSplitting) {
						thisMoviesNameIsNancy.addTag(t);
					}
					movies.put(mID, thisMoviesNameIsNancy);
				}
			}
		} catch(IOException eiei) {
			eiei.printStackTrace();
		} finally {
			try {
				bfr.close();
			} catch(IOException eh) {
				eh.printStackTrace();
			}
		}
		return movies;
	}

	@Override
	public void loadRating(String ratingFilename) {
		// YOUR CODE GOES HERE
		
		String line;
		BufferedReader bfr = null;
		
		try {
			bfr = new BufferedReader(new FileReader(ratingFilename));
			Pattern p = Pattern.compile("^(\\d+),(\\d+),(\\d+\\.\\d+),(\\d+)$");
			
			int uID, mID;
			double rating;
			long ts_AsInTimeStampNotAsTaylorSwift;
			Movie movieNameIsJohn;  ///Because movie deserves to have name too
			
			while((line = bfr.readLine()) != null) {
				Matcher m = p.matcher(line);
				if(m.matches()) {
					uID = Integer.parseInt(m.group(1));
					mID = Integer.parseInt(m.group(2));
					ts_AsInTimeStampNotAsTaylorSwift = Long.parseLong(m.group(4));
					rating = Double.parseDouble(m.group(3));
					
					movieNameIsJohn = movies.get(mID);
					/**
					 * If the movies doesn't exist, then user shouldn't be able to rate
					 * also if the rating isn't between 0.5 - 5.0 then it's also pointless
					 */
					if(movieNameIsJohn != null && rating>=0.5 && rating<=5) {
						User user = new User(uID);
						/**
						 * If the same user has already rated
						 * then we have to compare the timstamp
						 * and use the msot recent rating
						 */
						Rating ratingObj;
						if((ratingObj = movieNameIsJohn.getRating().get(uID)) != null) {
							if(ratingObj.timestamp < ts_AsInTimeStampNotAsTaylorSwift) {
								ratingObj.timestamp = ts_AsInTimeStampNotAsTaylorSwift;
								ratingObj.rating = rating;
								movieNameIsJohn.getRating().put(uID, ratingObj);
							}
						}
						/**
						 * If not then just add a new one
						 */
						else {
							movieNameIsJohn.addRating(user, movieNameIsJohn, rating, ts_AsInTimeStampNotAsTaylorSwift);
						}
					}
				}
			}
		} catch(IOException eh) {
			eh.printStackTrace();
		} finally {
			try {
				bfr.close();
			} catch(IOException erm) {
				erm.printStackTrace();
			}
		}
		
		/**
		 * calculate mean of each movie
		 * since the ratings have been added
		 */
		for(Integer i : movies.keySet()) {
			movies.get(i).calMeanRating();
		}
	}

	@Override
	public void loadData(String movieFilename, String ratingFilename) {
		// YOUR CODE GOES HERE
		loadMovies(movieFilename);
		loadRating(ratingFilename);
			
	}

	@Override
	public Map<Integer, Movie> getAllMovies() {
		// YOUR CODE GOES HERE
		
		return movies;
	}

	@Override
	public List<Movie> searchByTitle(String title, boolean exactMatch) {
		// YOUR CODE GOES HERE
		List<Movie> toReturn = new ArrayList<Movie>();
		if(exactMatch) {
			for(Integer i : movies.keySet()){
				if(movies.get(i).getTitle().equalsIgnoreCase(title)) {
					toReturn.add(movies.get(i));
				}
			}
		} else {
			for(Integer i : movies.keySet()) {
				if(movies.get(i).getTitle().toUpperCase().contains(title.toUpperCase())) {
					toReturn.add(movies.get(i));
				}
			}
		}
		return toReturn;
	}

	@Override
	public List<Movie> searchByTag(String tag) {
		// YOUR CODE GOES HERE
		
		List<Movie> toReturn = new ArrayList<Movie>();
		
		for(Integer i : movies.keySet()) {
			if(movies.get(i).getTags().contains(tag)) {
				toReturn.add(movies.get(i));
			}
		}
		return toReturn;
	}

	@Override
	public List<Movie>searchByYear(int year) {
		// YOUR CODE GOES HERE
		
		List<Movie> toReturn = new ArrayList<Movie>();
		
		for(Integer i : movies.keySet()) {
			if(movies.get(i).getYear() == year) {
				toReturn.add(movies.get(i));
			}
		}
		return toReturn;
	}

	@Override
	public List<Movie> advanceSearch(String title, String tag, int year) {
		// YOUR CODE GOES HERE
		/**
		 * LinkedList because >> faster than ArrayList
		 */
		List<Movie> toReturn = new LinkedList<Movie>();
		if(title == null && tag == null && year <= 0) {
			return toReturn;
		}
		
		/**
		 * Decided to copy the whole title in the list
		 * and remove the one that doesn't match out
		 * Instead of add to the list when found
		 * (use too many conditions)
		 */
		toReturn = new LinkedList<Movie>(movies.values());
		
		/**
		 * The fastest!!
		 */
		ListIterator<Movie> iter = toReturn.listIterator();
		while(iter.hasNext()) {
			Movie m = iter.next();
			if((title != null && !(m.getTitle().toUpperCase().contains(title.toUpperCase()))) || (tag != null && !(m.getTags().contains(tag))) || (year >= 0 && m.getYear() != year)) {
				iter.remove();
			}
		}
		
		 /**
         * me trying to try different iterator/ loop
         * to test which is the fastest
         * (obviously it's the one I decided to use)
         */
//      //ITERATOR ALL IN ONE CONDITION//
//      for(ListIterator<Movie> iter = toReturn.listIterator(); iter.hasNext();) {
//            Movie m = iter.next();
//            if ((title != null && !(m.getTitle().toUpperCase().contains(title.toUpperCase()))) || (tag != null && !(m.getTags().contains(tag))) || (year >= 0 && m.getYear() != year)) {
//                iter.remove();
//            }
//        }
//
//      //ITERATOR 3 LOOPS (whoops)//
//		for(ListIterator<Movie> iter = toReturn.listIterator(); iter.hasNext();) {
//		    Movie m = iter.next();
//
//		    if (title != null && !(m.getTitle().toUpperCase().contains(title.toUpperCase()))) {
//		        iter.remove();
//		    }
//        }
//
//        for(ListIterator<Movie> iter = toReturn.listIterator(); iter.hasNext();) {
//            Movie m = iter.next();
//
//            if (tag != null && !(m.getTags().contains(tag))) {
//                iter.remove();
//            }
//        }
//
//        for(ListIterator<Movie> iter = toReturn.listIterator(); iter.hasNext();) {
//            Movie m = iter.next();
//
//            if (year >= 0 && m.getYear() != year) {
//                iter.remove();
//            }
//        }
//
//      //NORMAL FOR EACH//
//		for (Integer i : movies.keySet()) {
//
//			Movie m = movies.get(i);
//			if (title != null && !(m.getTitle().toUpperCase().contains(title.toUpperCase()))) {
//				toReturn.remove(m);
//			}
//			if (tag != null && !(m.getTags().contains(tag))) {
//				toReturn.remove(m);
//			}
//			if (year >= 0 && m.getYear() != year) {
//				toReturn.remove(m);
//			}
//		}
		return toReturn;
	}

	@Override
	public List<Movie> sortByTitle(List<Movie> unsortedMovies, boolean asc) {
		// YOUR CODE GOES HERE
		List<Movie> toReturn = new ArrayList<Movie>(unsortedMovies);
		toReturn.sort(Comparator.comparing(Movie::getTitle));
		if(!asc) {
			Collections.reverse(toReturn);
		}
		return toReturn;
	}

	@Override
	public List<Movie> sortByRating(List<Movie> unsortedMovies, boolean asc) {
		// YOUR CODE GOES HERE
		List<Movie> toReturn = new ArrayList<Movie>(unsortedMovies);
		
		toReturn.sort(Comparator.comparing(Movie::getMeanRating));
		if(!asc) {
			Collections.reverse(toReturn);
		}
		return toReturn;
	}

}
