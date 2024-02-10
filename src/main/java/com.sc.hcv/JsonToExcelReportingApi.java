package com.sc.hcv;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootApplication
public class JsonToExcelReportingApi {

    public static void main(String[] args) {
        SpringApplication.run(JsonToExcelReportingApi.class, args);
    }
}

@RestController
@RequestMapping("/api/hcv")
@Validated
class JsonToExcelReportingController {

    @PostMapping(value = "/generate-dbaccounts-excel")
    public void generateDBAccountsExcelReport( HttpServletResponse response) throws IOException {
        List<HashMap<String, Object>> result = null;
        try {

            // Call to the actual API which should return the data as a collection of Map
            result = parseJSONFromFile("input/hcv-onboarded-data.json");
             result.forEach(map -> System.out.println(map));

            XSSFWorkbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Data");

            // Style for header
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            Font font = workbook.createFont();
            font.setBold(true);
            headerStyle.setFont(font);

            // Writing the header
            String[] header = {
                    "EntityId", "Entity Name", "S-BIA Rating", "Entity InstanceId", "LOB", "Platform", "DB Type", "Host Name",
                    "Port", "DB Account Name", "Oracle Service Name", "MSSQL Instance Name"
            };

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < header.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(header[i]);
                cell.setCellStyle(headerStyle);
            }


            int rowCount = 1;
            for (Map<String, Object> datum : result) {
                Row dataRow = sheet.createRow(rowCount++);
                int cellCount = 0;
                dataRow.createCell(0).setCellValue(String.valueOf(datum.get("entityId")));
                dataRow.createCell(1).setCellValue(String.valueOf(datum.get("entityName")));
                dataRow.createCell(2).setCellValue(String.valueOf(datum.get("sBIARating")));
                dataRow.createCell(3).setCellValue(String.valueOf(datum.get("entityInstance")));
                dataRow.createCell(4).setCellValue(String.valueOf(datum.get("lob")));
                dataRow.createCell(5).setCellValue(String.valueOf(datum.get("platform")));
                dataRow.createCell(6).setCellValue(String.valueOf(datum.get("dbType")));
                dataRow.createCell(7).setCellValue(String.valueOf(datum.get("hostName")));
                dataRow.createCell(8).setCellValue(String.valueOf(datum.get("port")));
                dataRow.createCell(9).setCellValue(String.valueOf(datum.get("dbAcctName")));
                dataRow.createCell(10).setCellValue(String.valueOf(datum.get("oracleServiceName")));
                dataRow.createCell(11).setCellValue(String.valueOf(datum.get("mssqlInstanceName")));
            }

            // Auto size the columns
            for (int i = 0; i < header.length; i++) {
                sheet.autoSizeColumn(i);
            }

            addMetadataToWorkbook(workbook);

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            workbook.write(bos);

            byte[] bytes = bos.toByteArray();
            ByteArrayInputStream bis = new ByteArrayInputStream(bytes);

            String filename = "data.xlsx";
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-disposition", "attachment; filename=" + filename);

            org.apache.commons.io.IOUtils.copy(bis, response.getOutputStream());
            response.flushBuffer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static void addMetadataToWorkbook(XSSFWorkbook workbook) {
        // Create a new sheet for metadata
        XSSFSheet metadataSheet = workbook.createSheet("Metadata");

        // Create a cell style for headers
        XSSFCellStyle headerStyle = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 12);
        headerStyle.setFont(font);
        headerStyle.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        // Add rows for each metadata item
        int rowIndex = 0;

        // Report Type
        Row reportTypeRow = metadataSheet.createRow(rowIndex++);
        Cell reportTypeLabelCell = reportTypeRow.createCell(0);
        reportTypeLabelCell.setCellValue("Report Type:");
        reportTypeLabelCell.setCellStyle(headerStyle);
        Cell reportTypeValueCell = reportTypeRow.createCell(1);
        reportTypeValueCell.setCellValue("Your report type here"); // Replace with your value

        // Description
        // Create a cell style for wrapped text
        XSSFCellStyle wrappedTextStyle = workbook.createCellStyle();
        wrappedTextStyle.setWrapText(true);
        Row descriptionRow = metadataSheet.createRow(rowIndex++);
        Cell descriptionLabelCell = descriptionRow.createCell(0);
        descriptionLabelCell.setCellValue("Description:");
        descriptionLabelCell.setCellStyle(headerStyle);
        Cell descriptionValueCell = descriptionRow.createCell(1);
        descriptionValueCell.setCellValue("A very long description that should be wrapped over multiple lines in the cell."); // Replace with your actual description
        descriptionValueCell.setCellStyle(wrappedTextStyle);
        // Set fixed width for Description column and enable wrapping
        metadataSheet.setColumnWidth(1, 256 * 40);  // 40 characters wide

        // Date Generated
        Row dateGeneratedRow = metadataSheet.createRow(rowIndex++);
        Cell dateGeneratedLabelCell = dateGeneratedRow.createCell(0);
        dateGeneratedLabelCell.setCellValue("Date Generated:");
        dateGeneratedLabelCell.setCellStyle(headerStyle);
        Cell dateGeneratedValueCell = dateGeneratedRow.createCell(1);
        dateGeneratedValueCell.setCellValue(new Date().toString()); // Current date and time

        // Source System
        Row sourceSystemRow = metadataSheet.createRow(rowIndex++);
        Cell sourceSystemLabelCell = sourceSystemRow.createCell(0);
        sourceSystemLabelCell.setCellValue("Source System:");
        sourceSystemLabelCell.setCellStyle(headerStyle);
        Cell sourceSystemValueCell = sourceSystemRow.createCell(1);
        sourceSystemValueCell.setCellValue("Your source system here"); // Replace with your value

        // Auto size columns for better presentation
        for (int i = 0; i <= 1; i++) {
            metadataSheet.autoSizeColumn(i);
        }

        // Move the metadata sheet to the front of the workbook
        int metadataSheetIndex = workbook.getSheetIndex("Metadata");
        workbook.setSheetOrder("Metadata", 0);

        // Ensure the metadata sheet is the active sheet when the workbook is opened
        workbook.setActiveSheet(metadataSheetIndex);
    }


    public static List<HashMap<String, Object>> parseJSONFromFile(String filePath) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        List<HashMap<String, Object>> data = objectMapper.readValue(new File(filePath),
                new TypeReference<List<HashMap<String, Object>>>() {});
        return data;
    }

}

