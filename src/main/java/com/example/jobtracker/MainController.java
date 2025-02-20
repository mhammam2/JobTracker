package com.example.jobtracker;

import com.example.jobtracker.Objects.JobApplication;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

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

        statusColumn.setCellFactory(column -> {
            return new TableCell<JobApplication, String>() {
                @Override
                protected void updateItem(String status, boolean empty) {
                    super.updateItem(status, empty);
                    if (empty || status == null) {
                        setText(null);
                        setStyle("");  // Reset style
                    } else {
                        setText(status);
                        // Apply background color based on status
                        switch (status) {
                            case "Pending":
                                setStyle("-fx-background-color: yellow;");
                                break;
                            case "Accepted":
                                setStyle("-fx-background-color: green;");
                                break;
                            case "Rejected":
                                setStyle("-fx-background-color: red;");
                                break;
                            case "Expired":
                                setStyle("-fx-background-color: red;");
                                break;
                            default:
                                setStyle("");  // Default style if no match
                                break;
                        }
                    }
                }
            };
        });

        DatabaseManager.initializeDatabase();
        loadJobs();  // Load jobs from the database
        checkForExpiredJobs();
        jobTable.setItems(jobApplications);
    }

    @FXML
    private void handleImportExcel() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));
        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            importJobsFromExcel(file);
        }
    }

    private void importJobsFromExcel(File file) {
        try (FileInputStream fis = new FileInputStream(file);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0); // Assume data is in the first sheet


                for (Row row : sheet) {

                    String companyName = row.getCell(0).getStringCellValue();
                    String jobTitle = row.getCell(1).getStringCellValue();
                    String status = row.getCell(4).getStringCellValue();
                    String applicationDate = row.getCell(3).getStringCellValue();
                    String notes = row.getCell(2) != null ? row.getCell(4).getStringCellValue() : "";
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/dd/yyyy");
                    JobApplication job = new JobApplication(companyName, jobTitle, status, LocalDate.parse(applicationDate, formatter).toString(), notes);
                    addJobApplication(job);
                }
                jobTable.refresh(); // Refresh TableView
            } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to import Excel data.");
        }
    }


    // Fetch jobs from the database and add them to the ObservableList
    private void loadJobs() {
        jobApplications.clear();  // Clear existing jobs
        String query = "SELECT * FROM JobApplications ORDER BY applicationDate DESC";  // Adjust the query as needed
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                JobApplication job = new JobApplication(
                        rs.getString("companyName"),
                        rs.getString("jobTitle"),
                        rs.getString("status"),
                        rs.getString("applicationDate"),
                        rs.getString("notes")
                );
                job.setId(rs.getInt("id"));
                jobApplications.add(job);  // Add each job to the list
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Could not load job applications from the database.");
        }
    }

    // Show alert messages
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void checkForExpiredJobs() {
        // Get current date
        LocalDate currentDate = LocalDate.now();

        // Loop through all job applications
        for (JobApplication job : jobApplications) {
            LocalDate applicationDate = job.getApplicationDateAsLocalDate();

            // Calculate the difference in days between application date and current date
            long daysBetween = ChronoUnit.DAYS.between(applicationDate, currentDate);

            System.out.println("Checking job with application date: " + applicationDate);
            System.out.println("Date now: " + currentDate);
            System.out.println("Days between: " + daysBetween);

            // If the job application is older than 30 days, update the status to "Expired"
            if (daysBetween > 30) {
                if (!job.getStatus().equals("Expired")) {
                    job.setStatus("Expired");
                    System.out.println("Job " + job.getCompanyName() + " has expired and status updated.");
                }
            }
        }

        // Refresh the table after updates
        jobTable.refresh();
    }


    @FXML
    private void handleAddButton() {
        openJobDialog(null);  // Open dialog to add a new job
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
            e.printStackTrace();
            showAlert("Error", "Failed to open the job dialog.");
        }
    }


    public void updateJobInDatabase(JobApplication job, long id) {
        String updateQuery = "UPDATE JobApplications SET companyName = ?, jobTitle = ?, status = ?, applicationDate = ?, notes = ? WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(updateQuery)) {

            // Set the fields for the job
            stmt.setString(1, job.getCompanyName());
            stmt.setString(2, job.getJobTitle());
            stmt.setString(3, job.getStatus());
            stmt.setString(4, job.getApplicationDate());
            stmt.setString(5, job.getNotes());
            stmt.setLong(6, id);  // Use the ID for the update

            // Print the id to debug
            System.out.println("Updating job with ID: " + id);

            int rowsAffected = stmt.executeUpdate();
            System.out.println("Rows affected: " + rowsAffected);

            if (rowsAffected > 0) {
                // Successfully updated the job in the database
                System.out.println("Job successfully updated in the database.");

                // You can update your application model if needed
                // For example, you could find the job in your list and update it
            } else {
                showAlert("Error", "Failed to update the job application. No matching job found with this ID.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Error while updating the job application.");
        }
    }



    // Add a job to the database and ObservableList
    public void addJobApplication(JobApplication newJob) {
        String insertQuery = "INSERT INTO JobApplications (companyName, jobTitle, status, applicationDate, notes) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, newJob.getCompanyName());
            stmt.setString(2, newJob.getJobTitle());
            stmt.setString(3, newJob.getStatus());
            stmt.setString(4, newJob.getApplicationDate());
            stmt.setString(5, newJob.getNotes());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                // Get the generated ID
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        long generatedId = generatedKeys.getLong(1);  // The first column contains the auto-incremented ID
                        newJob.setId(generatedId); // Set the ID in your JobApplication object
                        System.out.println(generatedId);
                        jobApplications.add(newJob); // Add to your list
                    }
                }
                FXCollections.sort(jobApplications, (job1, job2) -> {
                    // Assuming the date format is 'yyyy-MM-dd', we can parse it and compare
                    LocalDate date1 = LocalDate.parse(job1.getApplicationDate());
                    LocalDate date2 = LocalDate.parse(job2.getApplicationDate());
                    return date2.compareTo(date1); // Most recent first
                });
            } else {
                showAlert("Error", "Failed to insert the job application.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Error while inserting the job application.");
        }
    }

    @FXML
    private void handleDeleteButton() {
        JobApplication selectedJob = jobTable.getSelectionModel().getSelectedItem();

        if (selectedJob != null) {
            // Show confirmation dialog
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete Job Application");
            alert.setHeaderText("Are you sure you want to delete this job application?");
            alert.setContentText("This action cannot be undone.");

            if (alert.showAndWait().get() == ButtonType.OK) {
                // Delete the job from the database
                String deleteQuery = "DELETE FROM JobApplications WHERE companyName = ? AND jobTitle = ? AND applicationDate = ?";
                try (Connection conn = DatabaseManager.getConnection();
                     PreparedStatement stmt = conn.prepareStatement(deleteQuery)) {

                    stmt.setString(1, selectedJob.getCompanyName());
                    stmt.setString(2, selectedJob.getJobTitle());
                    stmt.setString(3, selectedJob.getApplicationDate());

                    int rowsAffected = stmt.executeUpdate();
                    if (rowsAffected > 0) {
                        jobApplications.remove(selectedJob);  // Remove the job from the list
                    } else {
                        showAlert("Error", "Failed to delete the job application.");
                    }

                } catch (SQLException e) {
                    e.printStackTrace();
                    showAlert("Error", "Error while deleting the job from the database.");
                }
            }
        } else {
            showAlert("No Selection", "Please select a job application to delete.");
        }
    }
}
