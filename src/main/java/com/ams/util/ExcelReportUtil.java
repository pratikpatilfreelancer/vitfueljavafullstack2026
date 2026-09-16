package com.ams.util;

import com.ams.dto.StudentAttendanceStats;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class ExcelReportUtil {

    private ExcelReportUtil() {} // utility class

    public static void generateAttendanceReport(
            List<StudentAttendanceStats> statsList,
            String reportTitle,
            OutputStream outputStream) throws IOException {

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Attendance Report");

            // Create styles
            CellStyle titleStyle = workbook.createCellStyle();
            Font titleFont = workbook.createFont();
            titleFont.setBold(true);
            titleFont.setFontHeightInPoints((short) 16);
            titleStyle.setFont(titleFont);
            titleStyle.setAlignment(HorizontalAlignment.CENTER);

            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);

            CellStyle dataStyle = workbook.createCellStyle();
            dataStyle.setBorderBottom(BorderStyle.THIN);
            dataStyle.setBorderTop(BorderStyle.THIN);
            dataStyle.setBorderLeft(BorderStyle.THIN);
            dataStyle.setBorderRight(BorderStyle.THIN);

            CellStyle belowThresholdStyle = workbook.createCellStyle();
            belowThresholdStyle.cloneStyleFrom(dataStyle);
            Font redFont = workbook.createFont();
            redFont.setColor(IndexedColors.RED.getIndex());
            redFont.setBold(true);
            belowThresholdStyle.setFont(redFont);

            // Title Row
            Row titleRow = sheet.createRow(0);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue(reportTitle);
            titleCell.setCellStyle(titleStyle);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 7));

            // Header Row
            String[] headers = {"Enrollment No", "Student Name", "Batch", "Total Classes",
                                "Present", "Absent", "Percentage", "Status"};
            Row headerRow = sheet.createRow(2);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Data Rows
            int rowNum = 3;
            for (StudentAttendanceStats stats : statsList) {
                Row row = sheet.createRow(rowNum++);

                Cell cell0 = row.createCell(0);
                cell0.setCellValue(stats.getEnrollmentNo());
                cell0.setCellStyle(dataStyle);

                Cell cell1 = row.createCell(1);
                cell1.setCellValue(stats.getStudentName());
                cell1.setCellStyle(dataStyle);

                Cell cell2 = row.createCell(2);
                cell2.setCellValue(stats.getBatchCode());
                cell2.setCellStyle(dataStyle);

                Cell cell3 = row.createCell(3);
                cell3.setCellValue(stats.getTotalClasses());
                cell3.setCellStyle(dataStyle);

                Cell cell4 = row.createCell(4);
                cell4.setCellValue(stats.getPresentCount());
                cell4.setCellStyle(dataStyle);

                Cell cell5 = row.createCell(5);
                cell5.setCellValue(stats.getAbsentCount());
                cell5.setCellStyle(dataStyle);

                Cell cell6 = row.createCell(6);
                cell6.setCellValue(String.format("%.2f%%", stats.getPercentage()));
                cell6.setCellStyle(dataStyle);

                Cell cell7 = row.createCell(7);
                if (stats.isBelowThreshold()) {
                    cell7.setCellValue("BELOW 75%");
                    cell7.setCellStyle(belowThresholdStyle);
                } else {
                    cell7.setCellValue("OK");
                    cell7.setCellStyle(dataStyle);
                }
            }

            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(outputStream);
        }
    }
}
