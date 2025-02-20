package com.example.jobtracker;

import com.example.jobtracker.Objects.JobApplication;
import com.example.jobtracker.email.EmailFetcher;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class MainController {
    @FXML private TableView<JobApplication> jobTable;
    @FXML private TableColumn<JobApplication, String> companyNameColumn;
    @FXML private TableColumn<JobApplication, String> jobTitleColumn;
    @FXML private TableColumn<JobApplication, String> statusColumn;
    @FXML private TableColumn<JobApplication, String> dateColumn;
    @FXML private TableColumn<JobApplication, String> notesColumn;

    private ObservableList<JobApplication> jobApplications = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Bind columns to model properties
        companyNameColumn.setCellValueFactory(cellData -> cellData.getValue().companyNameProperty());
        jobTitleColumn.setCellValueFactory(cellData -> cellData.getValue().jobTitleProperty());
        statusColumn.setCellValueFactory(cellData -> cellData.getValue().statusProperty());
        dateColumn.setCellValueFactory(cellData -> cellData.getValue().applicationDateProperty());
        notesColumn.setCellValueFactory(cellData -> cellData.getValue().notesProperty());

        // Add sample data
        jobApplications.add(new JobApplication("Company A", "Software Engineer", "Applied", "2023-10-01", "Waiting for response"));
        jobTable.setItems(jobApplications);
    }

    @FXML
    private void handleAddButton() {
        openJobDialog(null);
    }

    @FXML
    private void fetchJobApplicationsAndDisplay() throws Exception
    {
        EmailFetcher emailFetcher = new EmailFetcher();
        // Fetch job applications from the email account
        List<JobApplication> fetchedJobs = emailFetcher.fetchJobApplications();

        // Add the fetched jobs to the ObservableList
        jobApplications.addAll(fetchedJobs);

        // Set the ObservableList to the TableView
        jobTable.setItems(jobApplications);
    }

    @FXML
    private void handleEditButton() {
        JobApplication selectedJob = jobTable.getSelectionModel().getSelectedItem();
        if (selectedJob == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Please select a job to edit!", ButtonType.OK);
            alert.showAndWait();
            return;
        }
        openJobDialog(selectedJob);  // Pass selected job for editing
    }


    private void openJobDialog(JobApplication job) {
        try {
            System.out.println(getClass().getResource("/com/example/jobtracker/add_edit_job.fxml"));
            FXMLLoader loader = new FXMLLoader(getClass().getResource("add_edit_job.fxml"));
            Parent root = loader.load();

            AddEditJobController controller = loader.getController();
            controller.setMainController(this);
            controller.setJobApplication(job);

            Stage stage = new Stage();
            stage.setTitle(job == null ? "Add Job Application" : "Edit Job Application");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            System.out.println("Failed to open" + e.getMessage());
            e.printStackTrace();
        }
    }

    public void addJobApplication(JobApplication job) {
        jobApplications.add(job);
    }


    @FXML
    private void handleDeleteButton() {
        // Get the selected job application
        JobApplication selectedJob = jobTable.getSelectionModel().getSelectedItem();

        if (selectedJob != null) {
            // Show confirmation dialog
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete Job Application");
            alert.setHeaderText("Are you sure you want to delete this job application?");
            alert.setContentText("This action cannot be undone.");

            if (alert.showAndWait().get() == ButtonType.OK) {
                // Remove the selected job application from the ObservableList
                jobApplications.remove(selectedJob);
            }
        } else {
            // Show alert if no job is selected
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Selection");
            alert.setHeaderText("No Job Selected");
            alert.setContentText("Please select a job application to delete.");
            alert.showAndWait();
        }
    }
}