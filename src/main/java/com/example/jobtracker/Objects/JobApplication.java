package com.example.jobtracker.Objects;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class JobApplication {
    // Properties for each field
    private final StringProperty companyName;
    private long id;
    private final StringProperty jobTitle;
    private final StringProperty status;
    private final StringProperty applicationDate;
    private final StringProperty notes;

    // Constructor
    public JobApplication(String companyName, String jobTitle, String status, String applicationDate, String notes) {
        this.companyName = new SimpleStringProperty(companyName);
        this.jobTitle = new SimpleStringProperty(jobTitle);
        this.status = new SimpleStringProperty(status);
        this.applicationDate = new SimpleStringProperty(applicationDate);
        this.notes = new SimpleStringProperty(notes);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
    // Getters for properties
    public StringProperty companyNameProperty() {
        return companyName;
    }

    public StringProperty jobTitleProperty() {
        return jobTitle;
    }

    public StringProperty statusProperty() {
        return status;
    }

    public StringProperty applicationDateProperty() {
        return applicationDate;
    }

    public StringProperty notesProperty() {
        return notes;
    }

    // Regular getters and setters
    public String getCompanyName() {
        return companyName.get();
    }

    public void setCompanyName(String companyName) {
        this.companyName.set(companyName);
    }

    public String getJobTitle() {
        return jobTitle.get();
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle.set(jobTitle);
    }

    public String getStatus() {
        return status.get();
    }

    public void setStatus(String status) {
        this.status.set(status);
    }

    public String getApplicationDate() {
        return applicationDate.get();
    }

    public void setApplicationDate(String applicationDate) {
        this.applicationDate.set(applicationDate);
    }

    public String getNotes() {
        return notes.get();
    }

    public void setNotes(String notes) {
        this.notes.set(notes);
    }

    public LocalDate getApplicationDateAsLocalDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd"); // Adjust to your date format
        return LocalDate.parse(getApplicationDate(), formatter);
    }
}