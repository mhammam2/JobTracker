package com.example.jobtracker;

import com.example.jobtracker.MainController;
import com.example.jobtracker.Objects.JobApplication;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.time.LocalDate;

public class AddEditJobController {

    @FXML private TextField companyField;
    @FXML private TextField jobTitleField;
    // Replace statusField with a ComboBox for status selection.
    @FXML private ComboBox<String> statusComboBox;
    // Replace dateField with a DatePicker.
    @FXML private DatePicker datePicker;
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
            // Set the status in the ComboBox.
            statusComboBox.setValue(job.getStatus());
            // Assuming the job application date is in ISO format (yyyy-MM-dd)
            try {
                datePicker.setValue(LocalDate.parse(job.getApplicationDate()));
            } catch (Exception e) {
                datePicker.setValue(null);
            }
            notesField.setText(job.getNotes());
        }
    }

    @FXML
    private void initialize() {
        // If not set in FXML, add items to the ComboBox in the controller.

        saveButton.setOnAction(e -> handleSave());
    }

    private void handleSave() {
        String company = companyField.getText();
        String jobTitle = jobTitleField.getText();
        String status = statusComboBox.getValue(); // Get selected status
        LocalDate date = datePicker.getValue();      // Get selected date
        String notes = notesField.getText();

        // Validate required fields.
        if (company.isEmpty() || jobTitle.isEmpty() || status == null || date == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING,
                    "Please fill in all required fields!",
                    javafx.scene.control.ButtonType.OK);
            alert.showAndWait();
            return;
        }

        // Format the date as a string if needed (ISO format is default).
        String dateString = date.toString();

        if (isEditing) {
            jobApplication.setCompanyName(company);
            jobApplication.setJobTitle(jobTitle);
            jobApplication.setStatus(status);
            jobApplication.setApplicationDate(dateString);
            jobApplication.setNotes(notes);
        } else {
            JobApplication newJob = new JobApplication(company, jobTitle, status, dateString, notes);
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
