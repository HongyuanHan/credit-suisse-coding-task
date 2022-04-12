package com.creditsuisse.hhy.codingtask.services;


import com.creditsuisse.hhy.codingtask.dto.EventDTO;
import com.creditsuisse.hhy.codingtask.entities.Event;

import java.io.File;
import java.util.Collection;
import java.util.List;

public interface LogEventService {
    File loadLogFile();

    List<EventDTO> convert(File logfile);

    Collection<Event> flagLongEvents(List<EventDTO> eventDtos);


    void saveAllEvents(Collection<Event> eventEntities);

    List<Event> loadAllStoredEvents();
}
