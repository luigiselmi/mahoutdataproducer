User Logs Parser
=======================

The application extracts the preferences (or signals) from the user navigation data to be used in the collaborative filtering algorithms implemented in Apache Mahout. The user navigation data is used to 
create a file with three columns: userID, itemID, value. Apache Mahout needs the first two fields to be 
provided as integers and the last as double in that same order. The IDs can be also provided as string
but the recommender must be set up to handle the mapping between the string identifier and an integer 
identifier. The integer identifier can be created from the string ID using an hash function.  
The parser can use different types of user feedbacks, also called signals or observations: views, downloads and 
comparisons. The views are events in which a user has seen the details of an item, usually
after a search. A download refers to an event in which a user has downloaded a document related to an item and 
a comparison is the event in which a user has selected two or more items to make a comparison.
A recommeder based on Apache Mahout reads signals from a base file, e.g. signals.csv, and from other files in the same folder 
whose name begins in the same way as the base file, say signals-20180424.csv. The signals data are kept in a Mahout FileDataModel 
and are reloaded after a certain interval, if there has been a change. 


## Prerequisites 
You need Java 8 and Maven to build the code.

## Install
The software can be downloaded from Github

    $ git clone https://github.com/luigiselmi/mahoutdataproducer.git

## Build
From the project's root folder execute the command 

    $ mvn install

## Run
The parser can be run as an executable jar files. At least the path of a folder containing the 
files of one type of signals must be passed as an argument with the path to the output file. 
 
    $ java -jar target/mahoutdataproducer-0.0.1-SNAPSHOT-jar-with-dependencies.jar -output signals.csv  -downloads <downloads folder> -views <views folder> -comparisons <comparisons folder>


