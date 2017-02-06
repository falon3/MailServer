# MailServer

This gets notifications from SafeTracks watch and Posts the data to the Django server 
for the [MoodAlert project](https://github.com/falon3/Ashbourne)    


## To run:    
first clone this project locally and open in a Java framework such as intelliJ        

copy <i>config.properties.sample</i> file to <i>config.properties</i> and fill in 
the attributes for your local info where the watch notifications are being extracted from.    

Set the amount of notification emails you want to get and the program will get that many of the most recent ones... and then add them in order from oldest to newest of those updates.    

Run main    
