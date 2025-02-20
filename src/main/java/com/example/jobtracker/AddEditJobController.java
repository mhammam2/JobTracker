package com.example.jobtracker;

import com.example.jobtracker.MainController;
import com.example.jobtracker.Objects.JobApplication;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AddEditJobController {
    @FXML private TextField companyField;
    @FXML private TextField jobTitleField;
    @FXML private TextField statusField;
    @FXML private TextField dateField;
    @FXML private TextField notesField;
    @FXML private Button saveButton;

    private JobApplication jobApplication;  // Existing job if editing
    private boolean isEditing = false;
    private MainController mainController;

    public void setMainController(MainController controller) {
        this.mainController = controller;
    }

    public void setJobApplication(JobApplication job) {
        if (job != null) {
            this.jobApplication = job;
            isEditing = true;
            companyField.setText(job.getCompanyName());
            jobTitleField.setText(job.getJobTitle());
            statusField.setText(job.getStatus());
            dateField.setText(job.getApplicationDate());
            notesField.setText(job.getNotes());
        }
    }

    @FXML
    private void initialize() {
        saveButton.setOnAction(e -> handleSave());
    }

    private void handleSave() {
        String company = companyField.getText();
        String jobTitle = jobTitleField.getText();
        String status = statusField.getText();
        String date = dateField.getText();
        String notes = notesField.getText();

        if (company.isEmpty() || jobTitle.isEmpty() || status.isEmpty() || date.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Please fill in all required fields!", javafx.scene.control.ButtonType.OK);
            alert.showAndWait();
            return;
        }

        if (isEditing) {
            jobApplication.setCompanyName(company);
            jobApplication.setJobTitle(jobTitle);
            jobApplication.setStatus(status);
            jobApplication.setApplicationDate(date);
            jobApplication.setNotes(notes);
        } else {
            JobApplication newJob = new JobApplication(company, jobTitle, status, date, notes);
            mainController.addJobApplication(newJob);
        }

        closeWindow();
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) saveButton.getScene().getWindow();
        stage.close();
    }
}

