/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.j148.backend.contractor_performance.resource;

import com.j148.backend.attendance.model.Attendance;
import com.j148.backend.contractor_performance.model.ContractorPerformance;
import com.j148.backend.contractor_performance.repo.ContractorPerformanceRepo;
import com.j148.backend.hearing.model.Hearing;
import com.j148.backend.warning.model.Warning;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xddf.usermodel.chart.AxisCrosses;
import org.apache.poi.xddf.usermodel.chart.AxisPosition;
import org.apache.poi.xddf.usermodel.chart.BarDirection;
import org.apache.poi.xddf.usermodel.chart.ChartTypes;
import org.apache.poi.xddf.usermodel.chart.XDDFBarChartData;
import org.apache.poi.xddf.usermodel.chart.XDDFCategoryAxis;
import org.apache.poi.xddf.usermodel.chart.XDDFChartData;
import org.apache.poi.xddf.usermodel.chart.XDDFDataSource;
import org.apache.poi.xddf.usermodel.chart.XDDFDataSourcesFactory;
import org.apache.poi.xddf.usermodel.chart.XDDFNumericalDataSource;
import org.apache.poi.xddf.usermodel.chart.XDDFValueAxis;
import org.apache.poi.xssf.usermodel.XSSFChart;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author arshr
 */
@RequestScoped
public class GenerateExcelFile {

    @Inject
    private ContractorPerformanceRepo contractorPerformanceRepo;

    public byte [] downloadReportFile() throws IOException, SQLException {
        List<ContractorPerformance> contractorPerformanceList = contractorPerformanceRepo.getAllContractorPerformance();

        try (Workbook workbook = new XSSFWorkbook()) {
            CreationHelper creationHelper = workbook.getCreationHelper();

            // Define a cell style for date and time
            CellStyle dateTimeStyle = workbook.createCellStyle();
            dateTimeStyle.setDataFormat(creationHelper.createDataFormat().getFormat("dd/MM/yyyy HH:mm:ss"));

            // Define a cell style for date only
            CellStyle dateStyle = workbook.createCellStyle();
            dateStyle.setDataFormat(creationHelper.createDataFormat().getFormat("dd/MM/yyyy"));

            // Users Sheet
            Sheet userSheet = workbook.createSheet("User details");
            Row userHeaderRow = userSheet.createRow(0);
            userHeaderRow.createCell(0).setCellValue("Name");
            userHeaderRow.createCell(1).setCellValue("Surname");
            userHeaderRow.createCell(2).setCellValue("Email");
            userHeaderRow.createCell(3).setCellValue("Age");
            userHeaderRow.createCell(4).setCellValue("Gender");
            userHeaderRow.createCell(5).setCellValue("Race");
            userHeaderRow.createCell(6).setCellValue("Status");
            userHeaderRow.createCell(7).setCellValue("Contract Period");
            userHeaderRow.createCell(8).setCellValue("Start date");
            userHeaderRow.createCell(9).setCellValue("End date");
            int userRowNum = 1;

            for (ContractorPerformance cp : contractorPerformanceList) {
                Row row = userSheet.createRow(userRowNum++);
                row.createCell(0).setCellValue(cp.getUser().getName());
                row.createCell(1).setCellValue(cp.getUser().getSurname());
                row.createCell(2).setCellValue(cp.getUser().getEmail());
                row.createCell(3).setCellValue(cp.getUser().getAge());
                row.createCell(4).setCellValue(cp.getUser().getGender());
                row.createCell(5).setCellValue(cp.getUser().getRace());
                row.createCell(6).setCellValue(cp.getContractor().getStatus().toString());
                row.createCell(7).setCellValue(cp.getContractPeriod().getName());
                Cell startDateCell = row.createCell(8);
                startDateCell.setCellValue(cp.getContractPeriod().getStartDate());
                startDateCell.setCellStyle(dateStyle); // Format as date
                Cell endDateCell = row.createCell(9);
                endDateCell.setCellValue(cp.getContractPeriod().getEndDate());
                endDateCell.setCellStyle(dateStyle); // Format as date
            }

            // Attendance Sheet
            Sheet attendanceSheet = workbook.createSheet("Attendance list");
            Row attHeaderRow = attendanceSheet.createRow(0);
            attHeaderRow.createCell(0).setCellValue("User full name");
            attHeaderRow.createCell(1).setCellValue("Time-in");
            attHeaderRow.createCell(2).setCellValue("Time-out");
            attHeaderRow.createCell(3).setCellValue("Register");
            int attRowNum = 1;

            for (ContractorPerformance cp : contractorPerformanceList) {
                for (Attendance att : cp.getAttendanceList()) {
                    Row row = attendanceSheet.createRow(attRowNum++);
                    row.createCell(0).setCellValue(cp.getUser().getName() + " " + cp.getUser().getSurname());
                    Cell timeInCell = row.createCell(1);
                    timeInCell.setCellValue(att.getTimeIn());
                    timeInCell.setCellStyle(dateTimeStyle); // Format as date with time
                    Cell timeOutCell = row.createCell(2);
                    timeOutCell.setCellValue(att.getTimeOut());
                    timeOutCell.setCellStyle(dateTimeStyle); // Format as date with time
                    row.createCell(3).setCellValue(att.getRegister().toString());
                }
            }

            // Hearings Sheet
            Sheet hearingSheet = workbook.createSheet("Hearings");
            Row hearHeaderRow = hearingSheet.createRow(0);
            hearHeaderRow.createCell(0).setCellValue("User full name");
            hearHeaderRow.createCell(1).setCellValue("Schedule date");
            hearHeaderRow.createCell(2).setCellValue("Reason");
            hearHeaderRow.createCell(3).setCellValue("Outcome");
            int hearingRowNum = 1;

            for (ContractorPerformance cp : contractorPerformanceList) {
                for (Hearing h : cp.getHearingList()) {
                    Row row = hearingSheet.createRow(hearingRowNum++);
                    row.createCell(0).setCellValue(cp.getUser().getName() + " " + cp.getUser().getSurname());
                    Cell scheduleDateCell = row.createCell(1);
                    scheduleDateCell.setCellValue(h.getScheduleDate());
                    scheduleDateCell.setCellStyle(dateTimeStyle); // Format as date with time
                    row.createCell(2).setCellValue(h.getReason());
                    row.createCell(3).setCellValue(h.getOutcome().toString());
                }
            }

            // Warnings Sheet
            Sheet warningSheet = workbook.createSheet("Warnings");
            Row warningHeaderRow = warningSheet.createRow(0);
            warningHeaderRow.createCell(0).setCellValue("User full name");
            warningHeaderRow.createCell(1).setCellValue("Date issued");
            warningHeaderRow.createCell(2).setCellValue("Reason");
            warningHeaderRow.createCell(3).setCellValue("State");
            int warningRowNum = 1;

            for (ContractorPerformance cp : contractorPerformanceList) {
                for (Warning w : cp.getWarningList()) {
                    Row row = warningSheet.createRow(warningRowNum++);
                    row.createCell(0).setCellValue(cp.getUser().getName() + " " + cp.getUser().getSurname());
                    Cell dateIssueCell = row.createCell(1);
                    dateIssueCell.setCellValue(w.getDateIssue());
                    dateIssueCell.setCellStyle(dateStyle); // Format as date only
                    row.createCell(2).setCellValue(w.getReason().toString());
                    row.createCell(3).setCellValue(w.getState().toString());
                }
            }

            // Aptitude Tests Sheet
            Sheet aptSheet = workbook.createSheet("Aptitude tests");
            Row aptHeaderRow = aptSheet.createRow(0);
            aptHeaderRow.createCell(0).setCellValue("User full name");
            aptHeaderRow.createCell(1).setCellValue("Test mark");
            aptHeaderRow.createCell(2).setCellValue("Test date");
            int aptRowNum = 1;

            for (ContractorPerformance cp : contractorPerformanceList) {
                Row row = aptSheet.createRow(aptRowNum++);
                row.createCell(0).setCellValue(cp.getUser().getName() + " " + cp.getUser().getSurname());
                row.createCell(1).setCellValue(cp.getAptitudeTest().getTestMark());
                Cell testDateCell = row.createCell(2);
                testDateCell.setCellValue(cp.getAptitudeTest().getTestDate());
                testDateCell.setCellStyle(dateStyle); // Format as date only
            }

            // Contractors by age range
            Map<String, Integer> contractorsByAgeRange = new TreeMap<>();
// Contractors by gender
            Map<String, Integer> contractorsByGender = new TreeMap<>();
// Contractors by race
            Map<String, Integer> contractorsByRace = new TreeMap<>();
// Contractor by status
            Map<String, Integer> contractorsByStatus = new TreeMap<>();

            //Enrollments per year for line graph
            Map<String, Integer> contractorEnrollmentsPerYear = new TreeMap<>();

// *** WARNINGS BY GENDER, RACE, AND AGE AGGREGATION ***
            Map<String, Integer> warningsByGender = new TreeMap<>();
            Map<String, Integer> warningsByRace = new TreeMap<>();
            Map<String, Integer> warningsByAgeRange = new TreeMap<>();

// HEARINGS BY GENDER, RACE, AND AGE AGGREGATION
            Map<String, Integer> hearingsByGender = new TreeMap<>();
            Map<String, Integer> hearingsByRace = new TreeMap<>();
            Map<String, Integer> hearingsByAgeRange = new TreeMap<>();

// ATTENDANCE BY GENDER, RACE AND AGE AGGREGATION
            Map<String, Integer> attendanceByGender = new TreeMap<>();
            Map<String, Integer> attendanceByRace = new TreeMap<>();
            Map<String, Integer> attendanceByAgeRange = new TreeMap<>();

// Map to hold the count of attendance per month
            Map<String, Integer> monthlyAttendance = new TreeMap<>();

// Separate maps for males and females for warnings and hearings
            Map<String, Integer> maleWarnings = new TreeMap<>();
            Map<String, Integer> femaleWarnings = new TreeMap<>();
            Map<String, Integer> maleHearings = new TreeMap<>();
            Map<String, Integer> femaleHearings = new TreeMap<>();

// Aptitude tests
            Map<String, Integer> aptitudeTestScores = new TreeMap<>();
            aptitudeTestScores.put("65-80% (Good)", 0);
            aptitudeTestScores.put("80+% (Excellent)", 0);

// Initialize counts for "With" and "Without" categories
            maleWarnings.put("With Warnings", 0);
            maleWarnings.put("Without Warnings", 0);
            femaleWarnings.put("With Warnings", 0);
            femaleWarnings.put("Without Warnings", 0);

            maleHearings.put("With Hearings", 0);
            maleHearings.put("Without Hearings", 0);
            femaleHearings.put("With Hearings", 0);
            femaleHearings.put("Without Hearings", 0);

// Initialize totals for gender count (unchanged)
            Map<String, Integer> totalContractorsByGender = new TreeMap<>();
            totalContractorsByGender.put("Female", 0);
            totalContractorsByGender.put("Male", 0);

            for (ContractorPerformance cp : contractorPerformanceList) {
                String gender = cp.getUser().getGender();
                String race = cp.getUser().getRace();
                int age = cp.getUser().getAge();
                String status = cp.getContractor().getStatus().toString();

                String ageRange = age < 20 ? "Under 20"
                        : age < 25 ? "20-24"
                                : age < 30 ? "25-29"
                                        : age < 35 ? "30-34"
                                                : age < 40 ? "35-39" : "40+";

                //Contractors by age range
                contractorsByAgeRange.put(ageRange, contractorsByAgeRange.getOrDefault(ageRange, 0) + 1);
                //Contractors by gender
                contractorsByGender.put(gender, contractorsByGender.getOrDefault(gender, 0) + 1);
                //Contractors by race
                contractorsByRace.put(race, contractorsByRace.getOrDefault(race, 0) + 1);
                //Contractor by status
                contractorsByStatus.put(status, contractorsByStatus.getOrDefault(status, 0) + 1);

                //Contractor Enrollment per year
                //Extract the start date of the contractor period
                LocalDate startDate = cp.getContractPeriod().getStartDate(); // Assuming it's a LocalDate
                if (startDate != null) {
                    // Extract the year
                    String year = String.valueOf(startDate.getYear());

                    // Increment the enrollment count for that year
                    contractorEnrollmentsPerYear.put(year, contractorEnrollmentsPerYear.getOrDefault(year, 0) + 1);
                }

                int warningCount = cp.getWarningList().size();
                warningsByGender.put(gender, warningsByGender.getOrDefault(gender, 0) + warningCount);
                warningsByRace.put(race, warningsByRace.getOrDefault(race, 0) + warningCount);
                warningsByAgeRange.put(ageRange, warningsByAgeRange.getOrDefault(ageRange, 0) + warningCount);

                // Update warnings count
                if ("Male".equalsIgnoreCase(gender)) {
                    if (warningCount > 0) {
                        maleWarnings.put("With Warnings", maleWarnings.get("With Warnings") + 1);
                    } else {
                        maleWarnings.put("Without Warnings", maleWarnings.get("Without Warnings") + 1);
                    }
                } else if ("Female".equalsIgnoreCase(gender)) {
                    if (warningCount > 0) {
                        femaleWarnings.put("With Warnings", femaleWarnings.get("With Warnings") + 1);
                    } else {
                        femaleWarnings.put("Without Warnings", femaleWarnings.get("Without Warnings") + 1);
                    }
                }

                int hearingCount = cp.getHearingList().size();
                hearingsByGender.put(gender, hearingsByGender.getOrDefault(gender, 0) + hearingCount);
                hearingsByRace.put(race, hearingsByRace.getOrDefault(race, 0) + hearingCount);
                hearingsByAgeRange.put(ageRange, hearingsByAgeRange.getOrDefault(ageRange, 0) + hearingCount);

                // Update hearings count
                if ("Male".equalsIgnoreCase(gender)) {
                    if (hearingCount > 0) {
                        maleHearings.put("With Hearings", maleHearings.get("With Hearings") + 1);
                    } else {
                        maleHearings.put("Without Hearings", maleHearings.get("Without Hearings") + 1);
                    }
                } else if ("Female".equalsIgnoreCase(gender)) {
                    if (hearingCount > 0) {
                        femaleHearings.put("With Hearings", femaleHearings.get("With Hearings") + 1);
                    } else {
                        femaleHearings.put("Without Hearings", femaleHearings.get("Without Hearings") + 1);
                    }
                }

                int attendanceCount = cp.getAttendanceList().size();
                attendanceByGender.put(gender, attendanceByGender.getOrDefault(gender, 0) + attendanceCount);
                attendanceByRace.put(race, attendanceByRace.getOrDefault(race, 0) + attendanceCount);
                attendanceByAgeRange.put(ageRange, attendanceByAgeRange.getOrDefault(ageRange, 0) + attendanceCount);

                for (Attendance att : cp.getAttendanceList()) {
                    // Extracting the time-in date (assuming it's already in LocalDateTime format)
                    if (att.getTimeIn() != null) {
                        // Directly extract the year and month without any time zone manipulation
                        LocalDateTime timeIn = att.getTimeIn();  // Use the time as it is (no conversion needed)

                        // Format the month and year (e.g., "2024-01")
                        String monthYear = timeIn.getYear() + "-" + String.format("%02d", timeIn.getMonthValue());

                        // Update the monthly attendance count
                        monthlyAttendance.put(monthYear, monthlyAttendance.getOrDefault(monthYear, 0) + 1);
                    }
                }

                //Aptitude Tests
                double testScore = cp.getAptitudeTest().getTestMark();

                if (testScore >= 65 && testScore <= 80) {
                    aptitudeTestScores.put("65-80% (Good)", aptitudeTestScores.get("65-80% (Good)") + 1);
                } else if (testScore > 80) {
                    aptitudeTestScores.put("80+% (Excellent)", aptitudeTestScores.get("80+% (Excellent)") + 1);
                }
            }

// * SUMMARY SHEET  Warnings Summary*
            Sheet summarySheet = workbook.createSheet("Warnings Summary");
            Row summaryHeader = summarySheet.createRow(0);
            summaryHeader.createCell(0).setCellValue("Category");
            summaryHeader.createCell(1).setCellValue("Count");

            int summaryRowNum = 1;
//// Add Warnings by Gender
//            Row genderHeader = summarySheet.createRow(summaryRowNum++);
//            genderHeader.createCell(0).setCellValue("Warnings by Gender");
//            for (Map.Entry<String, Integer> entry : warningsByGender.entrySet()) {
//                Row row = summarySheet.createRow(summaryRowNum++);
//                row.createCell(0).setCellValue(entry.getKey());
//                row.createCell(1).setCellValue(entry.getValue());
//            }

//// Add Warnings by Race
//            Row raceHeader = summarySheet.createRow(summaryRowNum++);
//            raceHeader.createCell(0).setCellValue("Warnings by Race");
//            for (Map.Entry<String, Integer> entry : warningsByRace.entrySet()) {
//                Row row = summarySheet.createRow(summaryRowNum++);
//                row.createCell(0).setCellValue(entry.getKey());
//                row.createCell(1).setCellValue(entry.getValue());
//            }
// Add Warnings by Age Range
            Row ageHeader = summarySheet.createRow(summaryRowNum++);
            ageHeader.createCell(0).setCellValue("Warnings by Age Range");
            for (Map.Entry<String, Integer> entry : warningsByAgeRange.entrySet()) {
                Row row = summarySheet.createRow(summaryRowNum++);
                row.createCell(0).setCellValue(entry.getKey());
                row.createCell(1).setCellValue(entry.getValue());
            }

//Add warnings by female
            Row femaleHeader = summarySheet.createRow(summaryRowNum++);
            femaleHeader.createCell(0).setCellValue("Warnings by female");
            for (Map.Entry<String, Integer> entry : femaleWarnings.entrySet()) {
                Row row = summarySheet.createRow(summaryRowNum++);
                row.createCell(0).setCellValue(entry.getKey());
                row.createCell(1).setCellValue(entry.getValue());
            }

//Add warnings by male
            Row maleHeader = summarySheet.createRow(summaryRowNum++);
            maleHeader.createCell(0).setCellValue("Warnings by male");
            for (Map.Entry<String, Integer> entry : maleWarnings.entrySet()) {
                Row row = summarySheet.createRow(summaryRowNum++);
                row.createCell(0).setCellValue(entry.getKey());
                row.createCell(1).setCellValue(entry.getValue());
            }

// * CREATING GRAPHS *
            XSSFDrawing drawing = (XSSFDrawing) summarySheet.createDrawingPatriarch();

//// Gender Pie Chart
//            XSSFClientAnchor genderAnchor = drawing.createAnchor(0, 0, 0, 0, 5, 1, 15, 21);
//            XSSFChart genderChart = drawing.createChart(genderAnchor);
//            genderChart.setTitleText("Warnings by Gender");
//            genderChart.setTitleOverlay(false);
//            genderChart.getOrAddLegend();
//            XDDFDataSource<String> genderCategories = XDDFDataSourcesFactory.fromStringCellRange((XSSFSheet) summarySheet,
//                    new CellRangeAddress(2, 2 + warningsByGender.size() - 1, 0, 0));
//            XDDFNumericalDataSource<Double> genderValues = XDDFDataSourcesFactory.fromNumericCellRange((XSSFSheet) summarySheet,
//                    new CellRangeAddress(2, 2 + warningsByGender.size() - 1, 1, 1));
//            XDDFChartData genderData = genderChart.createData(ChartTypes.PIE, null, null);
//            XDDFChartData.Series genderSeries = genderData.addSeries(genderCategories, genderValues);
//            genderChart.plot(genderData);
//// Race Pie Chart
//            XSSFClientAnchor raceAnchor = drawing.createAnchor(0, 0, 0, 0, 5, 22, 15, 42);
//            XSSFChart raceChart = drawing.createChart(raceAnchor);
//            raceChart.setTitleText("Warnings by Race");
//            raceChart.setTitleOverlay(false);
//            raceChart.getOrAddLegend();
//            XDDFDataSource<String> raceCategories = XDDFDataSourcesFactory.fromStringCellRange((XSSFSheet) summarySheet,
//                    new CellRangeAddress(3 + warningsByGender.size(), 3 + warningsByGender.size() + warningsByRace.size() - 1, 0, 0));
//            XDDFNumericalDataSource<Double> raceValues = XDDFDataSourcesFactory.fromNumericCellRange((XSSFSheet) summarySheet,
//                    new CellRangeAddress(3 + warningsByGender.size(), 3 + warningsByGender.size() + warningsByRace.size() - 1, 1, 1));
//            XDDFChartData raceData = raceChart.createData(ChartTypes.PIE, null, null);
//            XDDFChartData.Series raceSeries = raceData.addSeries(raceCategories, raceValues);
//            raceChart.plot(raceData);
//
            // *** CREATING BAR CHART FOR CONTRACTORS BY AGE RANGE ***
            XSSFDrawing barDrawing = (XSSFDrawing) summarySheet.createDrawingPatriarch();

            // Age Range Bar Chart
            XSSFClientAnchor aptitudeTestAnchor = barDrawing.createAnchor(0, 0, 0, 0, 5, 1, 15, 21);
            XSSFChart ageChart = barDrawing.createChart(aptitudeTestAnchor);
            ageChart.setTitleText("Total Warnings by Age Range");
            ageChart.getOrAddLegend();

            // Create the category axis (X-axis)
            XDDFCategoryAxis categoryAxis = (XDDFCategoryAxis) ageChart.createCategoryAxis(AxisPosition.LEFT);
            categoryAxis.setTitle("Age Range");

            // Set the data for the category axis (age ranges)
            System.out.println(warningsByAgeRange.size());
            XDDFDataSource<String> ageCategories = XDDFDataSourcesFactory.fromStringCellRange((XSSFSheet) summarySheet,
                    new CellRangeAddress(2, warningsByAgeRange.size() + 1, 0, 0));

            // Create the value axis (Y-axis)
            XDDFValueAxis valueAxis = (XDDFValueAxis) ageChart.createValueAxis(AxisPosition.BOTTOM);
            valueAxis.setTitle("Contractor Count");

            // Set the data for the value axis (contractor counts)
            XDDFNumericalDataSource<Double> ageValues = XDDFDataSourcesFactory.fromNumericCellRange((XSSFSheet) summarySheet,
                    new CellRangeAddress(2, warningsByAgeRange.size() + 1, 1, 1));

            // Create chart data for the bar chart
            XDDFBarChartData ageData = (XDDFBarChartData) ageChart.createData(ChartTypes.BAR, categoryAxis, valueAxis);
            ageData.setBarDirection(BarDirection.COL);
            ageData.setGapWidth(150);
            ageData.setOverlap((byte) 0);
            // Add the data series to the chart
            XDDFChartData.Series ageSeries = ageData.addSeries(ageCategories, ageValues);

            // Customize the series (e.g., set bar color or width if needed)
            ageSeries.setTitle("Contractors by Age Range", null);

            // Plot the chart with both axes and data
            ageChart.plot(ageData);

            // Female Warnings Pie Chart
            XSSFClientAnchor femaleAnchor = drawing.createAnchor(0, 0, 0, 0, 5, 22, 15, 42); // Adjust position if needed
            XSSFChart femaleChart = drawing.createChart(femaleAnchor);
            femaleChart.setTitleText("Warnings for Females");
            femaleChart.setTitleOverlay(false);
            femaleChart.getOrAddLegend();
            XDDFDataSource<String> femaleCategories = XDDFDataSourcesFactory.fromStringCellRange((XSSFSheet) summarySheet,
                    new CellRangeAddress(2 + warningsByAgeRange.size() + 1, 2 + warningsByAgeRange.size() + 2, 0, 0));
            XDDFNumericalDataSource<Double> femaleValues = XDDFDataSourcesFactory.fromNumericCellRange((XSSFSheet) summarySheet,
                    new CellRangeAddress(2 + warningsByAgeRange.size() + 1, 2 + warningsByAgeRange.size() + 2, 1, 1)); // Fix range alignment
            XDDFChartData femaleData = femaleChart.createData(ChartTypes.PIE, null, null);
            XDDFChartData.Series femaleSeries = femaleData.addSeries(femaleCategories, femaleValues);
            femaleChart.plot(femaleData);

// Male Warnings Pie Chart
            XSSFClientAnchor maleAnchor = drawing.createAnchor(0, 0, 0, 0, 16, 22, 26, 42); // Adjust position to avoid overlap
            XSSFChart maleChart = drawing.createChart(maleAnchor);
            maleChart.setTitleText("Warnings for Males");
            maleChart.setTitleOverlay(false);
            maleChart.getOrAddLegend();
            XDDFDataSource<String> maleCategories = XDDFDataSourcesFactory.fromStringCellRange((XSSFSheet) summarySheet,
                    new CellRangeAddress(2 + warningsByAgeRange.size() + 1 + femaleWarnings.size() + 1, 2 + warningsByAgeRange.size() + 1 + femaleWarnings.size() + maleWarnings.size(), 0, 0));
            XDDFNumericalDataSource<Double> maleValues = XDDFDataSourcesFactory.fromNumericCellRange((XSSFSheet) summarySheet,
                    new CellRangeAddress(2 + warningsByAgeRange.size() + 1 + femaleWarnings.size() + 1, 2 + warningsByAgeRange.size() + 1 + femaleWarnings.size() + maleWarnings.size(), 1, 1)); // Ensure correct range for male data
            XDDFChartData maleData = maleChart.createData(ChartTypes.PIE, null, null);
            XDDFChartData.Series maleSeries = maleData.addSeries(maleCategories, maleValues);
            maleChart.plot(maleData);

            //-------------------------------------------------------------------------------------------------------------------------
            // * SUMMARY SHEET Hearings *
            summarySheet = workbook.createSheet("Hearing Summary");
            summaryHeader = summarySheet.createRow(0);
            summaryHeader.createCell(0).setCellValue("Category");
            summaryHeader.createCell(1).setCellValue("Count");

            summaryRowNum = 1;
//// Add Hearings by Gender
//            genderHeader = summarySheet.createRow(summaryRowNum++);
//            genderHeader.createCell(0).setCellValue("Hearings by Gender");
//            for (Map.Entry<String, Integer> entry : hearingsByGender.entrySet()) {
//                Row row = summarySheet.createRow(summaryRowNum++);
//                row.createCell(0).setCellValue(entry.getKey());
//                row.createCell(1).setCellValue(entry.getValue());
//            }

//// Add Hearings by Race
//            raceHeader = summarySheet.createRow(summaryRowNum++);
//            raceHeader.createCell(0).setCellValue("Hearings by Race");
//            for (Map.Entry<String, Integer> entry : hearingsByRace.entrySet()) {
//                Row row = summarySheet.createRow(summaryRowNum++);
//                row.createCell(0).setCellValue(entry.getKey());
//                row.createCell(1).setCellValue(entry.getValue());
//            }
// Add Hearings by Age Range
            ageHeader = summarySheet.createRow(summaryRowNum++);
            ageHeader.createCell(0).setCellValue("Hearings by Age Range");
            for (Map.Entry<String, Integer> entry : hearingsByAgeRange.entrySet()) {
                Row row = summarySheet.createRow(summaryRowNum++);
                row.createCell(0).setCellValue(entry.getKey());
                row.createCell(1).setCellValue(entry.getValue());
            }

            //Add hearings by female
            femaleHeader = summarySheet.createRow(summaryRowNum++);
            femaleHeader.createCell(0).setCellValue("Hearings by female");
            for (Map.Entry<String, Integer> entry : femaleHearings.entrySet()) {
                Row row = summarySheet.createRow(summaryRowNum++);
                row.createCell(0).setCellValue(entry.getKey());
                row.createCell(1).setCellValue(entry.getValue());
            }

//Add hearings by male
            maleHeader = summarySheet.createRow(summaryRowNum++);
            maleHeader.createCell(0).setCellValue("Hearings by male");
            for (Map.Entry<String, Integer> entry : maleHearings.entrySet()) {
                Row row = summarySheet.createRow(summaryRowNum++);
                row.createCell(0).setCellValue(entry.getKey());
                row.createCell(1).setCellValue(entry.getValue());
            }

// * CREATING GRAPHS *
            drawing = (XSSFDrawing) summarySheet.createDrawingPatriarch();

//// Gender Pie Chart
//            genderAnchor = drawing.createAnchor(0, 0, 0, 0, 5, 1, 15, 21);
//            genderChart = drawing.createChart(genderAnchor);
//            genderChart.setTitleText("Hearings by Gender");
//            genderChart.setTitleOverlay(false);
//            genderChart.getOrAddLegend();
//            genderCategories = XDDFDataSourcesFactory.fromStringCellRange((XSSFSheet) summarySheet,
//                    new CellRangeAddress(2, 2 + hearingsByGender.size() - 1, 0, 0));
//            genderValues = XDDFDataSourcesFactory.fromNumericCellRange((XSSFSheet) summarySheet,
//                    new CellRangeAddress(2, 2 + hearingsByGender.size() - 1, 1, 1));
//            genderData = genderChart.createData(ChartTypes.PIE, null, null);
//            genderSeries = genderData.addSeries(genderCategories, genderValues);
//            genderChart.plot(genderData);
//// Race Pie Chart
//            raceAnchor = drawing.createAnchor(0, 0, 0, 0, 5, 22, 15, 42);
//            raceChart = drawing.createChart(raceAnchor);
//            raceChart.setTitleText("Hearings by Race");
//            raceChart.setTitleOverlay(false);
//            raceChart.getOrAddLegend();
//            raceCategories = XDDFDataSourcesFactory.fromStringCellRange((XSSFSheet) summarySheet,
//                    new CellRangeAddress(3 + hearingsByGender.size(), 3 + hearingsByGender.size() + hearingsByRace.size() - 1, 0, 0));
//            raceValues = XDDFDataSourcesFactory.fromNumericCellRange((XSSFSheet) summarySheet,
//                    new CellRangeAddress(3 + hearingsByGender.size(), 3 + hearingsByGender.size() + hearingsByRace.size() - 1, 1, 1));
//            raceData = raceChart.createData(ChartTypes.PIE, null, null);
//            raceSeries = raceData.addSeries(raceCategories, raceValues);
//            raceChart.plot(raceData);
            // *** CREATING BAR CHART FOR CONTRACTORS BY AGE RANGE ***
            barDrawing = (XSSFDrawing) summarySheet.createDrawingPatriarch();

            // Age Range Bar Chart
            aptitudeTestAnchor = barDrawing.createAnchor(0, 0, 0, 0, 5, 1, 15, 21);
            ageChart = barDrawing.createChart(aptitudeTestAnchor);
            ageChart.setTitleText("Total Hearings by Age Range");
            ageChart.getOrAddLegend();

            // Create the category axis (X-axis)
            categoryAxis = (XDDFCategoryAxis) ageChart.createCategoryAxis(AxisPosition.LEFT);
            categoryAxis.setTitle("Age Range");

            // Set the data for the category axis (age ranges)
            ageCategories = XDDFDataSourcesFactory.fromStringCellRange((XSSFSheet) summarySheet,
                    new CellRangeAddress(2, hearingsByAgeRange.size() + 1, 0, 0));

            // Create the value axis (Y-axis)
            valueAxis = (XDDFValueAxis) ageChart.createValueAxis(AxisPosition.BOTTOM);
            valueAxis.setTitle("Contractor Count");

            // Set the data for the value axis (contractor counts)
            ageValues = XDDFDataSourcesFactory.fromNumericCellRange((XSSFSheet) summarySheet,
                    new CellRangeAddress(2, hearingsByAgeRange.size() + 1, 1,1));

            // Create chart data for the bar chart
            ageData = (XDDFBarChartData) ageChart.createData(ChartTypes.BAR, categoryAxis, valueAxis);
            ageData.setBarDirection(BarDirection.COL);
            ageData.setGapWidth(150);
            ageData.setOverlap((byte) 0);
            // Add the data series to the chart
            ageSeries = ageData.addSeries(ageCategories, ageValues);

            // Customize the series (e.g., set bar color or width if needed)
            ageSeries.setTitle("Total Hearings by Age Range", null);

            // Plot the chart with both axes and data
            ageChart.plot(ageData);

            // Female Warnings Pie Chart
            femaleAnchor = drawing.createAnchor(0, 0, 0, 0, 5, 22, 15, 42); // Adjust position if needed
            femaleChart = drawing.createChart(femaleAnchor);
            femaleChart.setTitleText("Hearings for Females");
            femaleChart.setTitleOverlay(false);
            femaleChart.getOrAddLegend();
            femaleCategories = XDDFDataSourcesFactory.fromStringCellRange((XSSFSheet) summarySheet,
                    new CellRangeAddress(2 + hearingsByAgeRange.size() + 1, 2 + hearingsByAgeRange.size() + femaleHearings.size() , 0, 0));
            femaleValues = XDDFDataSourcesFactory.fromNumericCellRange((XSSFSheet) summarySheet,
                    new CellRangeAddress(2 + hearingsByAgeRange.size() + 1, 2 + hearingsByAgeRange.size() + femaleHearings.size(), 1, 1)); // Fix range alignment
            femaleData = femaleChart.createData(ChartTypes.PIE, null, null);
            femaleSeries = femaleData.addSeries(femaleCategories, femaleValues);
            femaleChart.plot(femaleData);

// Male Warnings Pie Chart
            maleAnchor = drawing.createAnchor(0, 0, 0, 0, 16, 22, 26, 42); // Adjust position to avoid overlap
            maleChart = drawing.createChart(maleAnchor);
            maleChart.setTitleText("Hearings for Males");
            maleChart.setTitleOverlay(false);
            maleChart.getOrAddLegend();
            maleCategories = XDDFDataSourcesFactory.fromStringCellRange((XSSFSheet) summarySheet,
                    new CellRangeAddress(2 + hearingsByAgeRange.size() + 1 + femaleHearings.size() + 1, 2 + hearingsByAgeRange.size() + femaleHearings.size() + maleHearings.size() + 1, 0, 0));
            maleValues = XDDFDataSourcesFactory.fromNumericCellRange((XSSFSheet) summarySheet,
                    new CellRangeAddress(2 + hearingsByAgeRange.size() + 1 + femaleHearings.size() + 1, 2 + hearingsByAgeRange.size() + femaleHearings.size() + maleHearings.size() + 1, 1, 1)); // Ensure correct range for male data
            maleData = maleChart.createData(ChartTypes.PIE, null, null);
            maleSeries = maleData.addSeries(maleCategories, maleValues);
            maleChart.plot(maleData);

            //-------------------------------------------------------------------------------------------------------------------------
            // * SUMMARY SHEET *
//            summarySheet = workbook.createSheet("Attendance Summary");
//            summaryHeader = summarySheet.createRow(0);
//            summaryHeader.createCell(0).setCellValue("Category");
//            summaryHeader.createCell(1).setCellValue("Count");
//
//            summaryRowNum = 1;
//// Add Attendance by Gender
//            genderHeader = summarySheet.createRow(summaryRowNum++);
//            genderHeader.createCell(0).setCellValue("Attendance by Gender");
//            for (Map.Entry<String, Integer> entry : attendanceByGender.entrySet()) {
//                Row row = summarySheet.createRow(summaryRowNum++);
//                row.createCell(0).setCellValue(entry.getKey());
//                row.createCell(1).setCellValue(entry.getValue());
//            }
//// Add Attendance by Race
//            raceHeader = summarySheet.createRow(summaryRowNum++);
//            raceHeader.createCell(0).setCellValue("Attendance by Race");
//            for (Map.Entry<String, Integer> entry : attendanceByRace.entrySet()) {
//                Row row = summarySheet.createRow(summaryRowNum++);
//                row.createCell(0).setCellValue(entry.getKey());
//                row.createCell(1).setCellValue(entry.getValue());
//            }
//// Add Attendance by Age Range
//            ageHeader = summarySheet.createRow(summaryRowNum++);
//            ageHeader.createCell(0).setCellValue("attendance by Age Range");
//            for (Map.Entry<String, Integer> entry : attendanceByAgeRange.entrySet()) {
//                Row row = summarySheet.createRow(summaryRowNum++);
//                row.createCell(0).setCellValue(entry.getKey());
//                row.createCell(1).setCellValue(entry.getValue());
//            }
// * CREATING GRAPHS *
//            drawing = (XSSFDrawing) summarySheet.createDrawingPatriarch();
//// Gender Pie Chart
//            genderAnchor = drawing.createAnchor(0, 0, 0, 0, 5, 1, 15, 21);
//            genderChart = drawing.createChart(genderAnchor);
//            genderChart.setTitleText("Hearings by Gender");
//            genderChart.setTitleOverlay(false);
//            genderChart.getOrAddLegend();
//            genderCategories = XDDFDataSourcesFactory.fromStringCellRange((XSSFSheet) summarySheet,
//                    new CellRangeAddress(2, 2 + attendanceByGender.size() - 1, 0, 0));
//            genderValues = XDDFDataSourcesFactory.fromNumericCellRange((XSSFSheet) summarySheet,
//                    new CellRangeAddress(2, 2 + attendanceByGender.size() - 1, 1, 1));
//            genderData = genderChart.createData(ChartTypes.PIE, null, null);
//            genderSeries = genderData.addSeries(genderCategories, genderValues);
//            genderChart.plot(genderData);
//
//// Race Pie Chart
//            raceAnchor = drawing.createAnchor(0, 0, 0, 0, 5, 22, 15, 42);
//            raceChart = drawing.createChart(raceAnchor);
//            raceChart.setTitleText("Attendance by Race");
//            raceChart.setTitleOverlay(false);
//            raceChart.getOrAddLegend();
//            raceCategories = XDDFDataSourcesFactory.fromStringCellRange((XSSFSheet) summarySheet,
//                    new CellRangeAddress(3 + attendanceByGender.size(), 3 + attendanceByGender.size() + attendanceByRace.size() - 1, 0, 0));
//            raceValues = XDDFDataSourcesFactory.fromNumericCellRange((XSSFSheet) summarySheet,
//                    new CellRangeAddress(3 + attendanceByGender.size(), 3 + attendanceByGender.size() + attendanceByRace.size() - 1, 1, 1));
//            raceData = raceChart.createData(ChartTypes.PIE, null, null);
//            raceSeries = raceData.addSeries(raceCategories, raceValues);
//            raceChart.plot(raceData);
//            // *** CREATING BAR CHART FOR CONTRACTORS BY AGE RANGE ***
//            barDrawing = (XSSFDrawing) summarySheet.createDrawingPatriarch();
//
//            // Age Range Bar Chart
//            aptitudeTestAnchor = barDrawing.createAnchor(0, 0, 0, 0, 16, 1, 26, 21);
//            ageChart = barDrawing.createChart(aptitudeTestAnchor);
//            ageChart.setTitleText("Contractors by Age Range");
//            ageChart.getOrAddLegend();
//
//            // Create the category axis (X-axis)
//            categoryAxis = (XDDFCategoryAxis) ageChart.createCategoryAxis(AxisPosition.LEFT);
//            categoryAxis.setTitle("Age Range");
//
//            // Set the data for the category axis (age ranges)
//            ageCategories = XDDFDataSourcesFactory.fromStringCellRange((XSSFSheet) summarySheet,
//                    new CellRangeAddress(2 + attendanceByGender.size() + 2 + attendanceByRace.size(), 2 + attendanceByGender.size() + 2 + attendanceByRace.size() + attendanceByAgeRange.size() - 1, 0, 0));
//
//            // Create the value axis (Y-axis)
//            valueAxis = (XDDFValueAxis) ageChart.createValueAxis(AxisPosition.BOTTOM);
//            valueAxis.setTitle("Contractor Count");
//
//            // Set the data for the value axis (contractor counts)
//            ageValues = XDDFDataSourcesFactory.fromNumericCellRange((XSSFSheet) summarySheet,
//                    new CellRangeAddress(2 + attendanceByGender.size() + 2 + attendanceByRace.size(), 2 + attendanceByGender.size() + 2 + attendanceByRace.size() + attendanceByAgeRange.size() - 1, 1, 1));
//
//            // Create chart data for the bar chart
//            ageData = (XDDFBarChartData) ageChart.createData(ChartTypes.BAR, categoryAxis, valueAxis);
//            ageData.setBarDirection(BarDirection.COL);
//            ageData.setGapWidth(150);
//            ageData.setOverlap((byte) 0);
//            // Add the data series to the chart
//            ageSeries = ageData.addSeries(ageCategories, ageValues);
//
//            // Customize the series (e.g., set bar color or width if needed)
//            ageSeries.setTitle("Contractors by Age Range", null);
//
//            // Plot the chart with both axes and data
//            ageChart.plot(ageData);
            //-------------------------------------------------------------------------------------------------------------------------
            // * SUMMARY SHEET Contractors*
            summarySheet = workbook.createSheet("Contractors Summary");
            summaryHeader = summarySheet.createRow(0);
            summaryHeader.createCell(0).setCellValue("Category");
            summaryHeader.createCell(1).setCellValue("Count");

            summaryRowNum = 1;
// Contractors by Gender
            Row genderHeader = summarySheet.createRow(summaryRowNum++);
            genderHeader.createCell(0).setCellValue("Contractors Gender");
            for (Map.Entry<String, Integer> entry : contractorsByGender.entrySet()) {
                Row row = summarySheet.createRow(summaryRowNum++);
                row.createCell(0).setCellValue(entry.getKey());
                row.createCell(1).setCellValue(entry.getValue());
            }

// Contractors by Race
            Row raceHeader = summarySheet.createRow(summaryRowNum++);
            raceHeader.createCell(0).setCellValue("Contractors by Race");
            for (Map.Entry<String, Integer> entry : contractorsByRace.entrySet()) {
                Row row = summarySheet.createRow(summaryRowNum++);
                row.createCell(0).setCellValue(entry.getKey());
                row.createCell(1).setCellValue(entry.getValue());
            }

// Contractors by status
            raceHeader = summarySheet.createRow(summaryRowNum++);
            raceHeader.createCell(0).setCellValue("Contractors by Status");
            for (Map.Entry<String, Integer> entry : contractorsByStatus.entrySet()) {
                Row row = summarySheet.createRow(summaryRowNum++);
                row.createCell(0).setCellValue(entry.getKey());
                row.createCell(1).setCellValue(entry.getValue());
            }

// Contractors by Age Range
            ageHeader = summarySheet.createRow(summaryRowNum++);
            ageHeader.createCell(0).setCellValue("Contractos by Age Range");
            for (Map.Entry<String, Integer> entry : contractorsByAgeRange.entrySet()) {
                Row row = summarySheet.createRow(summaryRowNum++);
                row.createCell(0).setCellValue(entry.getKey());
                row.createCell(1).setCellValue(entry.getValue());
            }

// * CREATING GRAPHS *
            drawing = (XSSFDrawing) summarySheet.createDrawingPatriarch();

            //// Gender Pie Chart
//            XSSFClientAnchor genderAnchor = drawing.createAnchor(0, 0, 0, 0, 5, 1, 15, 21);
//            XSSFChart genderChart = drawing.createChart(genderAnchor);
//            genderChart.setTitleText("Warnings by Gender");
//            genderChart.setTitleOverlay(false);
//            genderChart.getOrAddLegend();
//            XDDFDataSource<String> genderCategories = XDDFDataSourcesFactory.fromStringCellRange((XSSFSheet) summarySheet,
//                    new CellRangeAddress(2, 2 + warningsByGender.size() - 1, 0, 0));
//            XDDFNumericalDataSource<Double> genderValues = XDDFDataSourcesFactory.fromNumericCellRange((XSSFSheet) summarySheet,
//                    new CellRangeAddress(2, 2 + warningsByGender.size() - 1, 1, 1));
//            XDDFChartData genderData = genderChart.createData(ChartTypes.PIE, null, null);
//            XDDFChartData.Series genderSeries = genderData.addSeries(genderCategories, genderValues);
//            genderChart.plot(genderData);
//// Race Pie Chart
//            XSSFClientAnchor raceAnchor = drawing.createAnchor(0, 0, 0, 0, 5, 22, 15, 42);
//            XSSFChart raceChart = drawing.createChart(raceAnchor);
//            raceChart.setTitleText("Warnings by Race");
//            raceChart.setTitleOverlay(false);
//            raceChart.getOrAddLegend();
//            XDDFDataSource<String> raceCategories = XDDFDataSourcesFactory.fromStringCellRange((XSSFSheet) summarySheet,
//                    new CellRangeAddress(3 + warningsByGender.size(), 3 + warningsByGender.size() + warningsByRace.size() - 1, 0, 0));
//            XDDFNumericalDataSource<Double> raceValues = XDDFDataSourcesFactory.fromNumericCellRange((XSSFSheet) summarySheet,
//                    new CellRangeAddress(3 + warningsByGender.size(), 3 + warningsByGender.size() + warningsByRace.size() - 1, 1, 1));
//            XDDFChartData raceData = raceChart.createData(ChartTypes.PIE, null, null);
//            XDDFChartData.Series raceSeries = raceData.addSeries(raceCategories, raceValues);
//            raceChart.plot(raceData);
// Gender Pie Chart
            XSSFClientAnchor genderAnchor = drawing.createAnchor(0, 0, 0, 0, 5, 1, 15, 21);
            XSSFChart genderChart = drawing.createChart(genderAnchor);
            genderChart.setTitleText("Gender Demographics for Contractors");
            genderChart.setTitleOverlay(false);
            genderChart.getOrAddLegend();
            XDDFDataSource<String> genderCategories = XDDFDataSourcesFactory.fromStringCellRange((XSSFSheet) summarySheet,
                    new CellRangeAddress(2, contractorsByGender.size() + 1, 0, 0));
            XDDFNumericalDataSource<Double> genderValues = XDDFDataSourcesFactory.fromNumericCellRange((XSSFSheet) summarySheet,
                    new CellRangeAddress(2, contractorsByGender.size() + 1, 1, 1));
            XDDFChartData genderData = genderChart.createData(ChartTypes.PIE, null, null);
            XDDFChartData.Series genderSeries = genderData.addSeries(genderCategories, genderValues);
            genderChart.plot(genderData);

// Race Pie Chart
            XSSFClientAnchor raceAnchor = drawing.createAnchor(0, 0, 0, 0, 5, 22, 15, 42);
            XSSFChart raceChart = drawing.createChart(raceAnchor);
            raceChart.setTitleText("Race Demographics for Contractors");
            raceChart.setTitleOverlay(false);
            raceChart.getOrAddLegend();
            XDDFDataSource<String> raceCategories = XDDFDataSourcesFactory.fromStringCellRange((XSSFSheet) summarySheet,
                    new CellRangeAddress(2 + contractorsByGender.size() + 1, 2 + contractorsByGender.size() + contractorsByRace.size(), 0, 0));
            XDDFNumericalDataSource<Double> raceValues = XDDFDataSourcesFactory.fromNumericCellRange((XSSFSheet) summarySheet,
                    new CellRangeAddress(2 + contractorsByGender.size() + 1, 2 + contractorsByGender.size() + contractorsByRace.size(), 1, 1));
            XDDFChartData raceData = raceChart.createData(ChartTypes.PIE, null, null);
            XDDFChartData.Series raceSeries = raceData.addSeries(raceCategories, raceValues);
            raceChart.plot(raceData);

// Status Bar Chart
            XSSFClientAnchor statusAnchor = drawing.createAnchor(0, 0, 0, 0, 16, 22, 26, 42);
            XSSFChart statusChart = drawing.createChart(statusAnchor);
            statusChart.setTitleText("Number of Contractors by Status");
            statusChart.setTitleOverlay(false);
            statusChart.getOrAddLegend();

// Define axes for the bar chart
            categoryAxis = statusChart.createCategoryAxis(AxisPosition.BOTTOM);
            categoryAxis.setTitle("Status");
            valueAxis = statusChart.createValueAxis(AxisPosition.LEFT);
            valueAxis.setTitle("Number of Contractors");

// Create data sources from the summary sheet
            XDDFDataSource<String> statusCategories = XDDFDataSourcesFactory.fromStringCellRange(
                    (XSSFSheet) summarySheet,
                    new CellRangeAddress(2 + contractorsByGender.size() + 1 + contractorsByRace.size() + 1, 2 + contractorsByGender.size() + 1 + contractorsByRace.size() + contractorsByStatus.size(), 0, 0) // Adjust row range for status
            );
            XDDFNumericalDataSource<Double> statusValues = XDDFDataSourcesFactory.fromNumericCellRange(
                    (XSSFSheet) summarySheet,
                    new CellRangeAddress(2 + contractorsByGender.size() + 1 + contractorsByRace.size() + 1, 2 + contractorsByGender.size() + 1 + contractorsByRace.size() + contractorsByStatus.size(), 1, 1) // Adjust column range for values
            );

// Create the bar chart data
            XDDFChartData barData = statusChart.createData(ChartTypes.BAR, categoryAxis, valueAxis);
            XDDFChartData.Series barSeries = barData.addSeries(statusCategories, statusValues);
            barSeries.setTitle("Contractors", null); // Add a title for the series

// Plot the chart with the data
            statusChart.plot(barData);

// Adjust bar chart direction (vertical instead of horizontal)
            ((XDDFBarChartData) barData).setBarDirection(BarDirection.COL);

            // *** CREATING BAR CHART FOR CONTRACTORS BY AGE RANGE ***
            barDrawing = (XSSFDrawing) summarySheet.createDrawingPatriarch();

            // Age Range Bar Chart
            aptitudeTestAnchor = barDrawing.createAnchor(0, 0, 0, 0, 16, 1, 26, 21);
            ageChart = barDrawing.createChart(aptitudeTestAnchor);
            ageChart.setTitleText("Number of Contractors by Age Range");
            ageChart.getOrAddLegend();

            // Create the category axis (X-axis)
            categoryAxis = (XDDFCategoryAxis) ageChart.createCategoryAxis(AxisPosition.LEFT);
            categoryAxis.setTitle("Age Range");

            // Set the data for the category axis (age ranges)
            ageCategories = XDDFDataSourcesFactory.fromStringCellRange((XSSFSheet) summarySheet,
                    new CellRangeAddress(2 + contractorsByGender.size() + 1 + contractorsByRace.size() + 1 + contractorsByStatus.size() + 1, 2 + contractorsByGender.size()  + contractorsByRace.size() + 1 + contractorsByStatus.size() + contractorsByAgeRange.size() + 1, 0, 0));

            // Create the value axis (Y-axis)
            valueAxis = (XDDFValueAxis) ageChart.createValueAxis(AxisPosition.BOTTOM);
            valueAxis.setTitle("Contractor Count");

            // Set the data for the value axis (contractor counts)
            ageValues = XDDFDataSourcesFactory.fromNumericCellRange((XSSFSheet) summarySheet,
                    new CellRangeAddress(2 + contractorsByGender.size() + 1 + contractorsByRace.size() + 1 + contractorsByStatus.size() + 1, 2 + contractorsByGender.size()  + contractorsByRace.size() + 1 + contractorsByStatus.size() + contractorsByAgeRange.size() + 1, 1, 1));

            // Create chart data for the bar chart
            ageData = (XDDFBarChartData) ageChart.createData(ChartTypes.BAR, categoryAxis, valueAxis);
            ageData.setBarDirection(BarDirection.COL);
            ageData.setGapWidth(150);
            ageData.setOverlap((byte) 0);
            // Add the data series to the chart
            ageSeries = ageData.addSeries(ageCategories, ageValues);

            // Customize the series (e.g., set bar color or width if needed)
            ageSeries.setTitle("Contractors by Age Range", null);

            // Plot the chart with both axes and data
            ageChart.plot(ageData);

            //------------------------------------------------------------------------------------------------------------------------------------------------------
            //Aptitude tests Summary Sheet
            summarySheet = workbook.createSheet("Aptitude Test Summary");
            summaryHeader = summarySheet.createRow(0);
            summaryHeader.createCell(0).setCellValue("Category");
            summaryHeader.createCell(1).setCellValue("Count");

            summaryRowNum = 1;
// Contractors by Gender
            Row aptitudeTestHeader = summarySheet.createRow(summaryRowNum++);
            aptitudeTestHeader.createCell(0).setCellValue("Aptitude Test Categories");
            for (Map.Entry<String, Integer> entry : aptitudeTestScores.entrySet()) {
                Row row = summarySheet.createRow(summaryRowNum++);
                row.createCell(0).setCellValue(entry.getKey());
                row.createCell(1).setCellValue(entry.getValue());
            }

            // *** CREATING BAR CHART FOR CONTRACTORS BY AGE RANGE ***
            barDrawing = (XSSFDrawing) summarySheet.createDrawingPatriarch();

            // Aptitude Mark Bar Chart
            aptitudeTestAnchor = barDrawing.createAnchor(0, 0, 0, 0, 5, 1, 15, 21);
            XSSFChart aptitudeTestChart = barDrawing.createChart(aptitudeTestAnchor);
            aptitudeTestChart.setTitleText("Aptitude Test Marks");
            aptitudeTestChart.getOrAddLegend();

            // Create the category axis (X-axis)
            categoryAxis = (XDDFCategoryAxis) aptitudeTestChart.createCategoryAxis(AxisPosition.LEFT);
            categoryAxis.setTitle("Percentage Range");

            // Set the data for the category axis (age ranges)
            XDDFDataSource<String> markCategories = XDDFDataSourcesFactory.fromStringCellRange((XSSFSheet) summarySheet,
                    new CellRangeAddress(2, 2 + aptitudeTestScores.size() - 1, 0, 0));

            // Create the value axis (Y-axis)
            valueAxis = (XDDFValueAxis) aptitudeTestChart.createValueAxis(AxisPosition.BOTTOM);
            valueAxis.setTitle("Contractor Count");

            // Set the data for the value axis (contractor counts)
            XDDFNumericalDataSource<Double> aptitudeTestValues = XDDFDataSourcesFactory.fromNumericCellRange((XSSFSheet) summarySheet,
                    new CellRangeAddress(2, 2 + aptitudeTestScores.size() - 1, 1, 1));

            // Create chart data for the bar chart
            XDDFBarChartData aptitudeTestData = (XDDFBarChartData) aptitudeTestChart.createData(ChartTypes.BAR, categoryAxis, valueAxis);
            aptitudeTestData.setBarDirection(BarDirection.COL);
            aptitudeTestData.setGapWidth(150);
            aptitudeTestData.setOverlap((byte) 0);
            aptitudeTestData.setGapWidth(30);
            // Add the data series to the chart
            XDDFChartData.Series aptitudeTestSeries = aptitudeTestData.addSeries(markCategories, aptitudeTestValues);

            // Customize the series (e.g., set bar color or width if needed)
            aptitudeTestSeries.setTitle("Mark Distribution", null);

            // Plot the chart with both axes and data
            aptitudeTestChart.plot(aptitudeTestData);

            //---------------------------------------------------------------------------------------------------------------------------
            // Create a sheet for the attendance summary
            Sheet attendanceSummarySheet2 = workbook.createSheet("Total Attendance per Month");
            summaryHeader = attendanceSummarySheet2.createRow(0);
            summaryHeader.createCell(0).setCellValue("Category");
            summaryHeader.createCell(1).setCellValue("Count");

// Fill in the data from monthlyAttendance map
            int rowIndex = 1;
            Row attendanceHeader = attendanceSummarySheet2.createRow(rowIndex++);
            attendanceHeader.createCell(0).setCellValue("Year-month");
            attendanceHeader.createCell(1).setCellValue("Attendance");
            for (Map.Entry<String, Integer> entry : monthlyAttendance.entrySet()) {
                Row row = attendanceSummarySheet2.createRow(rowIndex++);
                row.createCell(0).setCellValue(entry.getKey()); // Month-Year (e.g., "2024-01")
                row.createCell(1).setCellValue(entry.getValue()); // Attendance count
            }

// Create the drawing patriarch and anchor for the chart
            drawing = (XSSFDrawing) attendanceSummarySheet2.createDrawingPatriarch();
            XSSFClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, 5, 2, 20, 20);

// Create the chart and set its title
            XSSFChart chart = drawing.createChart(anchor);
            chart.setTitleText("Total Attendance per Month");
            chart.setTitleOverlay(false);

// Set the bottom (category) axis for months
            XDDFCategoryAxis bottomAxis = chart.createCategoryAxis(AxisPosition.BOTTOM);
            bottomAxis.setTitle("Month");
            bottomAxis.setCrosses(AxisCrosses.AUTO_ZERO);

// Set the left (value) axis for total attendance
            XDDFValueAxis leftAxis = chart.createValueAxis(AxisPosition.LEFT);
            leftAxis.setTitle("Total Attendance");
            leftAxis.setCrosses(AxisCrosses.AUTO_ZERO);

// Prepare data sources for the chart from the sheet data
            XDDFDataSource<String> months = XDDFDataSourcesFactory.fromStringCellRange(
                    (XSSFSheet) attendanceSummarySheet2,
                    new CellRangeAddress(2, rowIndex - 1, 0, 0)); // Month-Year column range

            XDDFNumericalDataSource<Double> attendanceCounts = XDDFDataSourcesFactory.fromNumericCellRange(
                    (XSSFSheet) attendanceSummarySheet2,
                    new CellRangeAddress(2, rowIndex - 1, 1, 1)); // Attendance count column range

// Create the chart data and add a series
            XDDFChartData data = chart.createData(ChartTypes.LINE, bottomAxis, leftAxis);
            XDDFChartData.Series series = data.addSeries(months, attendanceCounts);
            series.setTitle("Attendance", null);

            //--------------------------------------------------------------------------------------------------------------------------------
            // Create a sheet for the attendance summary
            Sheet ContractorEnrollmentSheet = workbook.createSheet("Total Enrollment Per Year");
            summaryHeader = ContractorEnrollmentSheet.createRow(0);
            summaryHeader.createCell(0).setCellValue("Category");
            summaryHeader.createCell(1).setCellValue("Count");

// Fill in the data from monthlyAttendance map
            // Reset row index for new sheet
            rowIndex = 1;

// Add header
            Row enrollmentHeader = ContractorEnrollmentSheet.createRow(rowIndex++);
            enrollmentHeader.createCell(0).setCellValue("Year");
            enrollmentHeader.createCell(1).setCellValue("Enrollment");

// Fill in the data
            for (Map.Entry<String, Integer> entry : contractorEnrollmentsPerYear.entrySet()) {
                Row row = ContractorEnrollmentSheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(entry.getKey());
                row.createCell(1).setCellValue(entry.getValue());
            }

// Create new drawing and chart
            XSSFDrawing enrollmentDrawing = (XSSFDrawing) ContractorEnrollmentSheet.createDrawingPatriarch();
            XSSFClientAnchor enrollmentAnchor = enrollmentDrawing.createAnchor(0, 0, 0, 0, 5, 2, 20, 20);
            XSSFChart enrollmentChart = enrollmentDrawing.createChart(enrollmentAnchor);
            enrollmentChart.setTitleText("Total Enrollment per Year");
            enrollmentChart.setTitleOverlay(false);

// Create axes
            XDDFCategoryAxis yearAxis = enrollmentChart.createCategoryAxis(AxisPosition.BOTTOM);
            yearAxis.setTitle("Year");
            XDDFValueAxis enrollmentAxis = enrollmentChart.createValueAxis(AxisPosition.LEFT);
            enrollmentAxis.setTitle("Total Enrollment");

// Prepare data sources
            XDDFDataSource<String> years = XDDFDataSourcesFactory.fromStringCellRange(
                    (XSSFSheet) ContractorEnrollmentSheet,
                    new CellRangeAddress(2, contractorEnrollmentsPerYear.size() + 1, 0, 0));

            XDDFNumericalDataSource<Double> enrollmentCounts = XDDFDataSourcesFactory.fromNumericCellRange(
                    (XSSFSheet) ContractorEnrollmentSheet,
                    new CellRangeAddress(2, contractorEnrollmentsPerYear.size() + 1, 1, 1));

// Create and plot data
            XDDFChartData enrollmentData = enrollmentChart.createData(ChartTypes.LINE, yearAxis, enrollmentAxis);
            XDDFChartData.Series enrollmentSeries = enrollmentData.addSeries(years, enrollmentCounts);
            enrollmentSeries.setTitle("Enrollment", null);
            enrollmentChart.plot(enrollmentData);

// Plot the chart
            chart.plot(data);

            try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
                workbook.write(byteArrayOutputStream);
                return byteArrayOutputStream.toByteArray();
            }

//            try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
//        workbook.write(byteArrayOutputStream);
//        
//        // Convert ByteArrayOutputStream to InputStream
//        InputStream inputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
//        return inputStream;
//    }
        }

    }

}
