package tn.esprit.services;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;
import tn.esprit.entities.Consultation;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;

public class GoogleCalendarService {

    private static final String APPLICATION_NAME = "";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String CREDENTIALS_FILE_PATH = "";
    private static final String TOKENS_DIRECTORY_PATH = "";
    private static final String CALENDAR_ID = "";

    private final Calendar service;

    public GoogleCalendarService() throws GeneralSecurityException, IOException {
        NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        Credential credential = getCredentials(httpTransport);
        this.service = new Calendar.Builder(httpTransport, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    private Credential getCredentials(NetHttpTransport httpTransport) throws IOException {
        InputStream in = GoogleCalendarService.class.getClassLoader().getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new IOException("Credentials file not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                httpTransport, JSON_FACTORY, clientSecrets, Collections.singleton("https://www.googleapis.com/auth/calendar"))
                .setDataStoreFactory(new FileDataStoreFactory(new File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();

        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    public String createEvent(Consultation consultation) throws IOException {
        Event event = new Event()
                .setSummary("Consultation - " + consultation.getNomPatient())
                .setDescription("Consultation ID: " + consultation.getId());

        // Convert java.sql.Date to LocalDateTime
        LocalDateTime startDateTime = consultation.getDateConsultation()
                .toLocalDate()
                .atTime(LocalTime.of(0, 0)); // Default to midnight; adjust time as needed
        DateTime start = new DateTime(startDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
        EventDateTime startEvent = new EventDateTime().setDateTime(start).setTimeZone("Europe/Paris");
        event.setStart(startEvent);

        LocalDateTime endDateTime = startDateTime.plusHours(1);
        DateTime end = new DateTime(endDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
        EventDateTime endEvent = new EventDateTime().setDateTime(end).setTimeZone("Europe/Paris");
        event.setEnd(endEvent);

        event = service.events().insert(CALENDAR_ID, event).execute();
        return event.getId();
    }

    public List<Event> getEvents(LocalDateTime start, LocalDateTime end) throws IOException {
        DateTime startTime = new DateTime(start.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
        DateTime endTime = new DateTime(end.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
        Events events = service.events().list(CALENDAR_ID)
                .setTimeMin(startTime)
                .setTimeMax(endTime)
                .setSingleEvents(true)
                .setOrderBy("startTime")
                .execute();
        return events.getItems();
    }

    public void updateEvent(String eventId, Consultation consultation) throws IOException {
        Event event = service.events().get(CALENDAR_ID, eventId).execute();
        event.setSummary("Consultation - " + consultation.getNomPatient());
        event.setDescription("Consultation ID: " + consultation.getId());

        // Convert java.sql.Date to LocalDateTime
        LocalDateTime startDateTime = consultation.getDateConsultation()
                .toLocalDate()
                .atTime(LocalTime.of(0, 0)); // Default to midnight; adjust time as needed
        DateTime start = new DateTime(startDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
        EventDateTime startEvent = new EventDateTime().setDateTime(start).setTimeZone("Europe/Paris");
        event.setStart(startEvent);

        LocalDateTime endDateTime = startDateTime.plusHours(1);
        DateTime end = new DateTime(endDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
        EventDateTime endEvent = new EventDateTime().setDateTime(end).setTimeZone("Europe/Paris");
        event.setEnd(endEvent);

        service.events().update(CALENDAR_ID, eventId, event).execute();
    }

    public void deleteEvent(String eventId) throws IOException {
        service.events().delete(CALENDAR_ID, eventId).execute();
    }
}