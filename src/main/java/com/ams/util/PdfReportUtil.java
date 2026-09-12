package com.ams.util;

import com.ams.dto.StudentAttendanceStats;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import java.awt.Color;
import java.io.OutputStream;
import java.util.List;

public class PdfReportUtil {

    private PdfReportUtil() {} // utility class

    public static void generateAttendanceReport(
            List<StudentAttendanceStats> statsList,
            String reportTitle,
            OutputStream outputStream) throws Exception {

        Document document = new Document(PageSize.A4.rotate());
        PdfWriter.getInstance(document, outputStream);

        document.open();

        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
        Paragraph title = new Paragraph(reportTitle, titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20);
        document.add(title);

        PdfPTable table = new PdfPTable(8);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);
        table.setSpacingAfter(10f);

        String[] headers = {"Enrollment No", "Student Name", "Batch", "Total Classes",
                            "Present", "Absent", "Percentage", "Status"};
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD);

        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
            cell.setBackgroundColor(Color.LIGHT_GRAY);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(5);
            table.addCell(cell);
        }

        Font dataFont = FontFactory.getFont(FontFactory.HELVETICA);
        Font redFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.RED);
        Font greenFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, new Color(0, 153, 0));

        for (StudentAttendanceStats stats : statsList) {
            table.addCell(new PdfPCell(new Phrase(stats.getEnrollmentNo(), dataFont)));
            table.addCell(new PdfPCell(new Phrase(stats.getStudentName(), dataFont)));
            table.addCell(new PdfPCell(new Phrase(stats.getBatchCode(), dataFont)));
            table.addCell(new PdfPCell(new Phrase(String.valueOf(stats.getTotalClasses()), dataFont)));
            table.addCell(new PdfPCell(new Phrase(String.valueOf(stats.getPresentCount()), dataFont)));
            table.addCell(new PdfPCell(new Phrase(String.valueOf(stats.getAbsentCount()), dataFont)));
            table.addCell(new PdfPCell(new Phrase(String.format("%.2f%%", stats.getPercentage()), dataFont)));

            PdfPCell statusCell = new PdfPCell();
            if (stats.isBelowThreshold()) {
                statusCell.setPhrase(new Phrase("BELOW 75%", redFont));
            } else {
                statusCell.setPhrase(new Phrase("OK", greenFont));
            }
            table.addCell(statusCell);
        }

        document.add(table);
        document.close();
    }
}
