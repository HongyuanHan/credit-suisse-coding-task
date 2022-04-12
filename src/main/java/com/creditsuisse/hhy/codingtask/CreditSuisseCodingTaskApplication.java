package com.creditsuisse.hhy.codingtask;

import com.creditsuisse.hhy.codingtask.dto.EventDTO;
import com.creditsuisse.hhy.codingtask.entities.Event;
import com.creditsuisse.hhy.codingtask.services.LogEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.util.Collection;
import java.util.List;

@SpringBootApplication
public class CreditSuisseCodingTaskApplication implements CommandLineRunner {

    @Autowired
    LogEventService logEventService;

    public static void main(String[] args) {
        SpringApplication.run(CreditSuisseCodingTaskApplication.class, args);
    }


    @Override
    public void run(String... args) throws Exception {
        File logfile = logEventService.loadLogFile();
        List<EventDTO> eventDTOS = logEventService.convert(logfile);
        Collection<Event> eventEntities = logEventService.flagLongEvents(eventDTOS);
        logEventService.saveAllEvents(eventEntities);

        List<Event> loaded = logEventService.loadAllStoredEvents();
        System.out.println("*****************************************************************");
        System.out.println("****************************Results******************************");
        if (loaded.size() <= 100) {
            loaded.stream().forEach(System.out::println);
        }
        System.out.println(String.format("There are %d events processed successfully.", loaded.size()));

        System.out.println("*****************************************************************");
    }
}
