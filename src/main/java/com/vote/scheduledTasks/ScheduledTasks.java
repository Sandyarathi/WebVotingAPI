package com.vote.scheduledTasks;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.ISODateTimeFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.vote.service.VoteService;

@Component
public class ScheduledTasks {

    //private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
    @Autowired
	VoteService service ;
    @Scheduled(fixedRate = 300000)
    public void reportCurrentTime() {
    	System.out.println("Scheduled RUN");
       /* System.out.println("The time is now " + dateFormat.format(new Date()));
        SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //org.joda.time.format.DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy/MM/dd HH:mm:ss");
        //DateTime currDate=new org.joda.time.DateTime();
        Date currDate=new Date();
        System.out.println(currDate);
        System.out.println("Look here"+formatter.format(currDate));*/
    	DateTime dt = new DateTime(DateTimeZone.UTC);
        service.findExpiredPolls(dt.toString(ISODateTimeFormat.dateTime().withZoneUTC()));
    }
}