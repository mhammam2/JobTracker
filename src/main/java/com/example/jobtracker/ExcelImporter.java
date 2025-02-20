package com.example.jobtracker;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.poi.ss.usermodel.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import com.example.jobtracker.Objects.JobApplication;

public class ExcelImporter {

    public static ObservableList<JobApplication> importFromExcel()
    {
        ObservableList<JobApplication> jobList = FXCollections.observableArrayList();

        try (FileInputStream fis = new FileInputStream(new File("/com/example/jobtracker/jobs.xlsx"));
             Workbook workbook = WorkbookFactory.create(fis)) {

            Sheet sheet = workbook.getSheetAt(0); // Assuming first sheet
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // Skip header row

                String company = getCellValue(row, 0);
                String role = getCellValue(row, 1);
                String location = getCellValue(row, 2);
                String date = getCellValue(row, 3);
                String status = getCellValue(row, 4);

                String notes = "Location: " + location; // Adding location in notes

                jobList.add(new JobApplication(company, role, date, status, notes));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return jobList;
    }

    private static String getCellValue(Row row, int columnIndex) {
        Cell cell = row.getCell(columnIndex, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
        return cell.toString().trim();
    }
}
