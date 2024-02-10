package com.sc.hcv;

import com.sc.hcv.utils.JsonToExcelConstants;
import org.json.JSONArray;
import org.json.JSONObject;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;

public class JSONToExcelUtility {

    public static void main(String[] args) {
        // Check if we have the required arguments
        if (args.length < 2) {
            System.out.println("Usage: java -jar YourProjectName.jar <input_json_path> <output_excel_path>");
            return;
        }
        String inputJsonPath = args[0];
        String outputExcelPath = args[1];

        convertJSONToExcel(inputJsonPath, outputExcelPath);
    }

    public static void convertJSONToExcel(String inputJsonPath, String outputExcelPath) {
        // Read JSON from resources
        String jsonInput = readHCVOnboardingDataFromJson();
        JSONArray jsonArray = new JSONArray(jsonInput);

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Onboarded-Apps");

            // Formatting
            CellStyle headerStyle = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setBold(true);
            headerStyle.setFont(font);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            // Writing the header
            String[] header = {
                    "EntityId", "Entity InstanceId", "LOB", "Platform",
                    "AuthN - AppRoles", "AuthN - TLS", "DB Assets", "DB Accts Onboarded", "Static Secrets"
            };

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < header.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(header[i]);
                cell.setCellStyle(headerStyle);
            }

            // Writing the data rows
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Row dataRow = sheet.createRow(i + 1);

                dataRow.createCell(0).setCellValue(jsonObject.getString("entityId"));
                dataRow.createCell(1).setCellValue(jsonObject.getString("entityInstanceId"));
                dataRow.createCell(2).setCellValue(jsonObject.getString("LOB"));
                dataRow.createCell(3).setCellValue(jsonObject.getString("platform"));
                dataRow.createCell(4).setCellValue(jsonObject.getInt("appRoleCount"));
                dataRow.createCell(5).setCellValue(jsonObject.getInt("tlsCount"));
                dataRow.createCell(6).setCellValue(jsonObject.getInt("dbAssetCount"));
                dataRow.createCell(7).setCellValue(jsonObject.getInt("dbAccountsOnboardedCount"));
                dataRow.createCell(8).setCellValue(jsonObject.getInt("staticSecretsOnboardedCount"));
            }

            // Auto size the columns
            for (int i = 0; i < header.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Ensure the output directory exists, if not create it
            File outputFile = new File(outputExcelPath);
            File outputDir = outputFile.getParentFile();
            if (!outputDir.exists() && outputDir != null) {
                outputDir.mkdirs();
            }

            // Write the Excel file to the specified output path
            try (FileOutputStream fileOut = new FileOutputStream(outputFile)) {
                workbook.write(fileOut);
            }

            System.out.println("Excel file created successfully!");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String readHCVOnboardingDataFromJson () {
        String filePath = JsonToExcelConstants.HCV_ONBOARDED_DATA_JSON_INPUT;
        StringBuilder result = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line).append("\n");
            }
        } catch (FileNotFoundException e) {
            System.err.println("File not found at: " + filePath);
            return null;  // or you can return an empty string or a default value
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result.toString();
    }


}

