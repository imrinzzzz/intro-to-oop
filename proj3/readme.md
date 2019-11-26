# Project 3 (Netflict)

## Introduction

Have	you	ever	wondered	how	Netflix	is	able	to	guess	what	exactly you	want	to	watch,	or	how	Facebook	
manages	to	pull	up	your	friends’	posts	that	you	have	been	eager	to	read?	Oftentimes,	these	mind	
guessing	tasks	are	translated	into	a	recommendation	problem	in	which	items	are	ranked	based	on	their	
relevance	scores	and	the	top	items	are	suggested	to	the	user.	In	this	project,	you	will	be	implementing	
one	of	the	most	widely	used	social	recommendation	paradigms,	called	**user-user	collaborative	filtering**
(well,	a	simple	version	of	it). The	intuition	behind	the	algorithm	is	that users	who	have	the	same	“taste”	
are	likely	to	like	to	same	set	of	things.	With	such	an	assumption,	a	recommendation	system	(a.k.a.	
recommender)	could	make	a	recommendation to	a	user	by	returning	the	list	of	items	that	other	
“similar”	users	have	expressed	their	liking	on.	For	example,	you	would	see	that	Facebook	often	displays
the	posts	that	your	friends	“like” on	your	feed. In	this	project,	you	will	be	implementing a	version	of	
user-user	collaborative	filtering	algorithm	for	recommending	movies	to	users.		

## Technical Definitions

**Movie**:	A	movie	is	a	tuple	of	(`mid,	title,	year,	tags`).	`mid` is	the	ID	of	the	movie,	always	a	positive	
integer.	`title` is	the	String	title	of	the	movie.	`year` is	a	positive	integer	representing	the	year	in	which	
this	movie	was	released.	`tags` is	a	set	of	String	relevant	tags	that	describe	this	movie.

**User**:	A	user	is	a	tuple	of	(`uid,	ratings`).	`uid` is	the	ID	of	the	user,	always	a	positive	integer.
`ratings` is	the	set	of	ratings	that	this	user	has	given.	Ratings	are	stored	in	a	`Map<mid, Rating>`
data	structure	for	fast lookup.	

**Rating**: A	rating	is	a	tuple	of	(`uid,	mid,	score,	timestamp`).	`uid` and	`mid` are	the	IDs	of	the	rating	
user	and	the	rated	movie, respectively. `score` is	a	double	numeric	value.	If	given,	a	rating	score	can	
range	from	[0.5,5]	inclusive.	A	rating	score	of	0	or	negative	values	implies that	no	rating	information	of	
the	user	uid and	the	movie	mid is	available	(the	user	may	never	have	rated	this	movie	or	the	rating	is	
made	unavailable).	`timestamp` is	a	long	value	indicating the time	at	which	this	rating	was	given.

## User-User Collaborative Filtering for Movie Recommentdation

User-user	collaborative	filtering	is	a	straightforward	algorithmic	interpretation	of	the	core	principles	of	
collaborative	filtering.	Intuitively,	it	finds	other	users	whose	past	rating	behavior	is	similar	to	a	given	
user,	and	uses	their	past	ratings	to	predict	the	rating of	a	movie that	the	current user	has not	rated.	If	a	
movie	is	predicted	to	have	high	rating	from	the user	(i.e.	4-5	stars) then	it	is	likely	that	this	user	will	like	
this	movie.

### Computing the rating prediction *p*<sub>u,i</sub>

*N* : the set of all users
*u ∈ *N* : the target user
*N*<sub>i</sub> ⊆ *N* − *u* :	the	set	of	all	users who	have	rated	the	movie	i
*I* :	the	set	of	all	movies
*i* ∈ *I* :	target	movie	(whose	rating	is	to	be	predicted)

In	order	to	predict	the	rating	score	for	*i* given	by	the	user *u*,	use	the	following	formula:

![](http://www.sciweavers.org/tex2img.php?eq=p_%7Bu%2Ci%7D%20%3D%20%5Coverline%7Br%7D_u%20%2B%20%5Cfrac%7B%5Csum_%7Bu%27%20%E2%88%88%20N_i%7D%5E%7B%7D%20%5C%7Bs%28u%2C%20u%27%29%20%5Ccdot%20%28r_u%27%2Ci%20-%20%5Coverline%7Br%7D_u%27%29%20%5C%7D%20%7D%7B%5Csum_%7Bu%27%20%E2%88%88%20N_i%7D%5E%7B%7D%20%5Clvert%20s%28u%2Cu%27%29%20%5Clvert%7D&bc=White&fc=Black&im=jpg&fs=12&ff=arev&edit=)

If the	denominator	is	0 or	*N*<sub>i</sub> is	empty, then ![](http://www.sciweavers.org/tex2img.php?eq=p_%7Bu%2Ci%7D%20%3D%20%5Coverline%7Br%7D_u&bc=White&fc=Black&im=jpg&fs=12&ff=arev&edit=)

*p*<sub>u,i</sub> ∈ [0,5] is the	predicted	rating	of	the	movie	*i* given	by	the	user *u*. 

![](http://www.sciweavers.org/tex2img.php?eq=![](http://www.sciweavers.org/tex2img.php?eq=HERE&bc=White&fc=Black&im=jpg&fs=12&ff=arev&edit=)&bc=White&fc=Black&im=jpg&fs=12&ff=arev&edit=) and	![](http://www.sciweavers.org/tex2img.php?eq=![](http://www.sciweavers.org/tex2img.phpeq=HERE&bc=White&fc=Black&im=jpg&fs=12&ff=arev&edit=)%27&bc=White&fc=Black&im=jpg&fs=12&ff=arev&edit=) are	the	average	rating	score	that	the	users	*u*	and	*u'*	have	given,	respectively. For	example,	if	
the	user *u* rated	three	movies	in	the	past	with	rating	scores	of	1.5,	0.5,	and	4	stars,	then	� = 2.0.

*r<sub>u',i</sub>* is	the	rating	score	that	the	user	u’ gave	to	the	movie	i.

*s(u,u')* is	the	similarity	score	between	the	users	*u* and	*u'*.

Note	that	|x|	denotes	the absolute	value	of	x.

### Computing the user similarity *s(u,v)*

![](http://www.sciweavers.org/tex2img.php?eq=p_%7B%28u%2Cv%29%7D%20%3D%20%5Cfrac%7B%5Csum_%7Bi%20%E2%88%88%20I_u%7D%5E%7B%7D%20%5B%28r_%7Bu%2Ci%7D%20-%20%5Coverline%7Br%7D_u%29%20%5Ccdot%20%28r_%7Bv%2Ci%7D%20-%20%5Coverline%7Br%7D_v%29%5D%20%7D%7B%5Csqrt%20%7B%5Csum_%7Bi%20%E2%88%88%20I_u%20%5Ccap%20I_v%7D%5E%7B%7D%20%28r_%7Bu%2Ci%7D%20-%20%5Coverline%7Br%7D_u%29%5E2%7D%20%5Csqrt%7B%5Csum_%7Bi%20%E2%88%88%20I_u%20%5Ccap%20I_v%7D%5E%7B%7D%20%28r_%7Bv%2Ci%7D%20-%20%5Coverline%7Br%7D_v%29%5E2%7D%20%7D&bc=White&fc=Black&im=jpg&fs=12&ff=arev&edit=)

*s(u,u)* ∈ [−1,1].	*s(u,u)* = 1.0. If	the	denominator	is	0,	then *s(u,v)* = 0.

## Movie file format

A	movie	file	(e.g.	movies.csv)	stores	the	meta-information	about	all	the	movies.	Except for the	first	line	
which	is	the	table	header,	each	line	in	the	movie	file	has	one	of	the	following	formats:

```
<mid>,<title> (<year>),<tag_1>|<tag_2>|<tag_3>|...|<tag_n>
//title does not contain ‘,’

<mid>,"<title> (<year>)",<tag_1>|<tag_2>|<tag_3>|...|<tag_n>
//title contains ‘,’
```

## User file format

A	user	file	(e.g.	users.train.csv	or	users.test.csv)	lists individual	ratings.	Rating	generated	by	the	same	
user	can	be	grouped	together,	and	stored	in	a	Map	structure of	the same	User	object where	they	can	be	
looked	up	using	the	corresponding	mid’s. Except	for	the	first	line	which	contains	the	table	header,	each	
line	in	the	user	file	has	the	following	format:

```
<uid>,<mid>,<rating>,<timestamp>
```
