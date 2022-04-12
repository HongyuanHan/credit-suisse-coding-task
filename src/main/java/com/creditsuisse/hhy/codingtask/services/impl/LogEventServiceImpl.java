package com.creditsuisse.hhy.codingtask.services.impl;

import com.creditsuisse.hhy.codingtask.dao.LogEventRepository;
import com.creditsuisse.hhy.codingtask.dto.EventDTO;
import com.creditsuisse.hhy.codingtask.entities.Event;
import com.creditsuisse.hhy.codingtask.services.LogEventService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.creditsuisse.hhy.codingtask.functions.FunctionWithException.wrapper;
import static com.pivovarit.function.ThrowingFunction.unchecked;

@Service
public class LogEventServiceImpl implements LogEventService {

    private static final Logger logger = LoggerFactory.getLogger(LogEventServiceImpl.class.getName());

    @Value("${app.resources.logfile.location}")
    private String resourcePath;

    private ObjectMapper objectMapper;

    private LogEventRepository eventRepository;

    @Autowired
    public LogEventServiceImpl(ObjectMapper objectMapper, LogEventRepository eventRepository) {
        this.objectMapper = objectMapper;
        this.eventRepository = eventRepository;
    }

    @Override
    public File loadLogFile() {
        try {
            return ResourceUtils.getFile(this.resourcePath);
        } catch (FileNotFoundException e) {
            logger.error(e.getMessage());
        }

        return null;
    }

    @Override
    public List<EventDTO> convert(File logfile) {
        List<EventDTO> result = new ArrayList<>();

        // read file line by line
        try (Stream<String> stream = Files.lines(logfile.toPath())) {
            result = stream.parallel()
                    .map(wrapper(str -> objectMapper.readValue(str, EventDTO.class)))
                    .collect(Collectors.toUnmodifiableList());
        } catch (IOException e) {
            logger.error(e.getMessage());
        }

        return result;
    }

    /**
     * Assumption: Every event always has 2 entries in the log file
     * @param eventDtos
     * @return
     */
    @Override
    public Collection<Event> flagLongEvents(List<EventDTO> eventDtos) {
        Map<String, Event> eventMap = new HashMap<>();

        eventDtos.stream().parallel().forEach(e -> {
            if (eventMap.containsKey(e.getId())) {
                Event event = eventMap.get(e.getId());
                event.setDuration((int) Math.abs(e.getTimestamp() - event.getTimestampTemp()));
                event.setTimestampTemp(-1);
                event.setAlert(event.getDuration() > 4);
            } else {
                Event event = new Event();
                event.setId(e.getId());
                event.setTimestampTemp(e.getTimestamp());
                event.setDuration(Integer.MAX_VALUE);
                event.setAlert(true);
                event.setHost(e.getHost());
                event.setType(e.getType());

                eventMap.put(e.getId(), event);
            }
        });

        return eventMap.values();
    }

    @Override
    public void saveAllEvents(Collection<Event> eventEntities) {
        eventRepository.saveAll(eventEntities);
    }

    @Override
    public List<Event> loadAllStoredEvents() {
        return eventRepository.findAll();
    }


}
