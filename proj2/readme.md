# Project 2 (MOOGLE)

## Introduction

A search engine application is a tool that helps users to find a specific content that matches
with users’ information needs. In this project, our task is to implement a search engine
application for movies. Some part of the system is already provided in the project
package. 

## Technical Definitions

**Movie**: A movie is a tuple of (`mid, title, year, tags, ratings`). `mid` is the ID of the
movie, always a positive integer. title is the String `title` of the movie. `year` is a positive
integer representing the year in which this movie was released. tags is a set of String relevant
tags that describe this movie. Finally, `ratings` is the set of ratings that this movie has been
rated by users. Ratings are stored in a `Map<uid, Rating>` data structure for fast lookup.
Noted that `uid` is the ID of the user.

**Rating**: A rating is a tuple of (`uid, mid, score, timestamp`). `uid` and `mid` are the IDs of
the rating user and the rated movie, respectively. `score` is a double numeric value. If given, a
rating score can range from [0.5,5] inclusive. A rating score of 0 or negative values implies that
no rating information of the user uid and the movie mid is available (the user may never have
rated this movie or the rating is made unavailable). `timestamp` is a long value indicating the
time at which this rating was given. What is a timestamp?1

**User**: A user is a tuple of (`uid`). `uid` is the ID of the user, always a positive integer.

#### Movie file format

A movie file (e.g. movies.csv) stores the meta-information about all the movies. Except for the
first line which is the table header, each line in the movie file has one of the following formats:

```
<mid>,<title> (<year>),<tag_1>|<tag_2>|<tag_3>|...|<tag_n>
//title does not contain ‘,’

<mid>,"<title> (<year>)",<tag_1>|<tag_2>|<tag_3>|...|<tag_n>
//title contains ‘,’
```
  
 #### Rating file format
 
 A rating file (e.g., ratings.csv) lists individual ratings. Rating generated by the users of the same movie can be grouped together, and stored in a Map structure of the same Movie object.
Except for the first line which contains the table headers, each line in the user file has the
following format:

```
<uid>,<mid>,<rating>,<timestamp>
```
