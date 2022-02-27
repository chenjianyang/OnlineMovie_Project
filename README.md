# cs122b-spring20-team-188


- # General
    - #### Team#: TEAM188
    
    - #### Names: Jianyang Chen
    
    - #### Project 5 Video Demo Link:  https://youtu.be/EfpnTEmziVY

    - #### Instruction of deployment:
			1. First clone whole project from github use command line or use git (local machine).
			2. Use Maven create war package (at where xml file locate).
			3. Copy war file to correct location (tomcat webapps). On local machine need to specify where it is by using IntelliJ.
			4. Show tomcat web apps by using https://<AWS public IP>:8443/manager/html or command line. On local machine just run it through IntelliJ.
			5. launch the local emulator and open the andriod java file
    - #### Collaborations and Work Distribution: just finsih it by myself


- # Connection Pooling
    - #### Include the filename/path of all code/configuration files in GitHub of using JDBC Connection Pooling.
		Master/src/addMovie.java 
		Master/src/addStar.java 
		Master/src/PlaceOrderServlet.java  
		Master/src/employee.java  
		Master/src/Forsearch.java  
		Master/src/LoginServlet.java  
		Master/src/LoginServletE.java  
		Master/src/MovieList.java  
		Master/src/MovieSuggestion.java  
		Master/src/SearchPage.java 
		Master/src/showmeta.java 
		Master/src/showPlacedOrder.java 
		Master/src/Showsearch.java 
		Master/src/MovieSuggestion.java 
		Master/src/SingleMovie.java 
		Master/src/SingleStarServlet.java 
    - #### Explain how Connection Pooling is utilized in the Fabflix code.
		1. create the resource of moviedb
		2. set up the cache prepared to be true and set up the connection pooling with the max connection and other properties 
		
    - #### Explain how Connection Pooling works with two backend SQL.
		1. set the cachePrepareStatment become true to use prepared statement in the connection pooling in the Resource
		2. create the resource on the master/WebContent/META-INF/context.xml name moviedbmaster and look up this datasource
		3. send the write request to the master instance backend server on the below and look up the datasource name moviedbmaster:
				Master/src/addMovie.java 
				Master/src/addStar.java 
				Master/src/PlaceOrderServlet.java  
		4. send the read request to either master instance or slave instance for all other severlvet and use them to look up the datasource name moviedb:
			Master/src/employee.java  
			Master/src/Forsearch.java  
			Master/src/LoginServlet.java  
			Master/src/LoginServletE.java  
			Master/src/MovieList.java  
			Master/src/MovieSuggestion.java  
			Master/src/SearchPage.java 
			Master/src/showmeta.java 
			Master/src/showPlacedOrder.java 
			Master/src/Showsearch.java 
			Master/src/MovieSuggestion.java 
			Master/src/SingleMovie.java 
			Master/src/SingleStarServlet.java 

- # Master/Slave
    - #### Include the filename/path of all code/configuration files in GitHub of routing queries to Master/Slave SQL.
			Master/src/addMovie.java 
			Master/src/addStar.java 
			Master/src/PlaceOrderServlet.java  
			Master/src/employee.java  
			Master/src/Forsearch.java  
			Master/src/LoginServlet.java  
			Master/src/LoginServletE.java  
			Master/src/MovieList.java  
			Master/src/MovieSuggestion.java  
			Master/src/SearchPage.java 
			Master/src/showmeta.java 
			Master/src/showPlacedOrder.java 
			Master/src/Showsearch.java 
			Master/src/MovieSuggestion.java 
			Master/src/SingleMovie.java 
			Master/src/SingleStarServlet.java 
    - #### How read/write requests were routed to Master/Slave SQL?
		send the write request to mysql sever on the master instance backend server on the below and look up the datasource name moviedbmaster:
			rewrite the url of mysql sever on the resource part on the context.xml file and change the IP address in this url to the master instance ip address and create a user on the master 
			instance to interact with the mysql master instance sever 
				Master/src/addMovie.java 
				Master/src/addStar.java 
				Master/src/PlaceOrderServlet.java  
		send the read request to either master instance or slave instance for all other severlvet and use them to look up the datasource name moviedb:
			Master/src/employee.java  
			Master/src/Forsearch.java  
			Master/src/LoginServlet.java  
			Master/src/LoginServletE.java  
			Master/src/MovieList.java  
			Master/src/MovieSuggestion.java  
			Master/src/SearchPage.java 
			Master/src/showmeta.java 
			Master/src/showPlacedOrder.java 
			Master/src/Showsearch.java 
			Master/src/MovieSuggestion.java 
			Master/src/SingleMovie.java 
			Master/src/SingleStarServlet.java 

- # JMeter TS/TJ Time Logs
    - #### Instructions of how to use the `log_processing.*` script to process the JMeter logs.
	  location: I use log_processing.py and the locationo is in the Master/log/log_processing.py 
		I create the file called timeForTS.txt and timeForTJ.txt and use them to log the time line by line and I use the log_processing.py to readlines to get 
		the content of each line which is each time for every query and then I calculate the total amount and devide it with the number of lines 
		to get the average time
		
	  ####
	  the location of all log files:
	  Master/case1ts.txt
	  Master/case2ts.txt
	  Master/case3ts.txt
	  Master/case4ts.txt
	  Master/case5ts.txt
	  Master/case6ts.txt
	  Master/case7ts.txt
	  Master/case1tj.txt
	  Master/case2tj.txt
	  Master/case3tj.txt
	  Master/case4tj.txt
	  Master/case5tj.txt
	  Master/case6tj.txt
	  Master/case7tj.txt

- # JMeter TS/TJ Time Measurement Report
*********************** Important: Since my apporach is using two severlvet one is to store the info and parameter the other is for check the query and show the result, therefore, the average
*********************** Important: in the graph should multiple 2 to get the real average since I use two severlvet so their time should be add together but not to be average together
*********************** Important:  and the case5 is the image used to explain for this detail in the master/image/case5


| **Single-instance Version Test Plan**          | **Graph Results Screenshot** | **Average Query Time(ms)** | **Average Search Servlet Time(ms)** | **Average JDBC Time(ms)** | **Analysis** |
|------------------------------------------------|------------------------------|----------------------------|-------------------------------------|---------------------------|--------------|
| Case 1: HTTP/1 thread                          | master/image/case1image      | 61                         | 37.914                              | 37.454                    | see below    |
| Case 2: HTTP/10 threads                        | master/image/case2image      | 396                        | 362.135                             | 357.837                   | see below    |
| Case 3: HTTPS/10 threads                       | master/image/case3image      | 235                        | 344.086                             | 339.997                   | see below    |
| Case 4: HTTP/10 threads/No connection pooling  | master/image/case4image      | 382                        | 338.855                             | 334.656                   | see below    |

  Case 1 Analysis: using one thread with the normal http would be slower than others 
  Case 2 Analysis: with the 10 threading, it speed up and cost less time but the average query time and the TS and TJ was larger than single thread 
  Case 3 Analysis: compared to the http, https is faster and we can see the reduce of time is about 10% and it is a large improvement if the amount of website is large 
  Case 4 Analysis: without the connection pooling, the effect is not very huge probably the reason is that the 10 threads is not considered as many threads so the connection connect and 
  close very time is not a huge improvement and also the connection time doesn't make a big difference to the speed






| **Scaled Version Test Plan**                   | **Graph Results Screenshot** | **Average Query Time(ms)** | **Average Search Servlet Time(ms)** | **Average JDBC Time(ms)** | **Analysis** |
|------------------------------------------------|------------------------------|----------------------------|-------------------------------------|---------------------------|--------------|
| Case 1: HTTP/1 thread                          | master/image/case5image      | 59                         | 37.019                              | 36.568                    | see below    |
| Case 2: HTTP/10 threads                        | master/image/case6image      | 195                        | 150.654                             | 148.879                   | see below    |
| Case 3: HTTP/10 threads/No connection pooling  | master/image/case7image      | 207                        | 163.114                             | 161.243                   | see below    |

  Case 1 Analysis: with the single thread, there is not big improvement compared to the single instance and probably that is what we expect since every time access once so it is the same as send 
	request to the same instance as allocate the request to one of two instance, the difference is not too big 
  Case 2 Analysis: compared to the single instance, the scaled version is more like the multiple threading and it is more efficient so we can see a huge improvement here and probably there are 
	2 instances so the effect of multiple threads didn't make a big difference and the number of time is not very large
  Case 3 Analysis: compared to the scaled version of case 2， we can see the difference of having the connection pooling or not especially for the multiple threads cases and there are some 
  difference but not very big. What is more, compared with the single instance, it still has a huge improvement with using scaled version because of the more instance used.



























