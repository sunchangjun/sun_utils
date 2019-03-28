package com.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;



public class PoiExcelUtils {

    /* 创建Excel */
    public static void createExcel(String[] title, String sheetName, LinkedList<Map<String, String>> data, String filePath) {
        // 第一步创建workbook
        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFCellStyle style = wb.createCellStyle();
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 居中
        // 第二步创建sheet
        HSSFSheet sheet = wb.createSheet(sheetName);
        // 设置单元格宽度
        for (int i = 0; i < title.length; i++) {
            if (i == 3) {
                sheet.setColumnWidth(3, 35 * 256); // 设置列宽，20个字符宽
            } else if(i == 4){
                sheet.setColumnWidth(4, 40 * 256); // 设置列宽，20个字符宽
            }else{
                sheet.setColumnWidth(i, 10 * 256); // 设置列宽，20个字符宽
            }
        }
        // 创建合并单元格
        CellRangeAddress callRangeAddress = new CellRangeAddress(0, 0, 0, 10);// 起始行,结束行,起始列,结束列
        sheet.addMergedRegion(callRangeAddress);
        // 创建第一行
        HSSFRow row0 = sheet.createRow(0);
        row0.setHeight(Short.valueOf("600"));
        HSSFCell cell0 = row0.createCell(0);
        cell0.setCellValue("湖南IPTV平台合作节目信息安全自查表");
        cell0.setCellStyle(style);
        // 创建第二行
        HSSFRow row = sheet.createRow(1);
        row.setHeight(Short.valueOf("400"));
        HSSFCell cell = null;
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 居中
        // 插入第一行数据的表头
        for (int i = 0; i < title.length; i++) {
            cell = row.createCell(i);
            cell.setCellValue(title[i]);
            cell.setCellStyle(style);
        }
        // 第五步插入数据
        if (null == data) {
            data = new LinkedList<Map<String, String>>();
        }
        for (int i = 0; i < data.size(); i++) {
            HSSFRow newrow = sheet.createRow(i + 2);
            newrow.setHeight(Short.valueOf("400"));
            Map<String, String> mapData = data.get(i);
            for (int j = 0; j < title.length; j++) {
                System.out.println(title[j]);
                cell = newrow.createCell(j);
                cell.setCellValue(mapData.get(title[j]));
                cell.setCellStyle(style);
            }
        }
        // 第六步将生成excel文件保存到指定路径下s
        try {
            FileOutputStream fout = new FileOutputStream(filePath);
            wb.write(fout);
            fout.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Excel文件生成成功..." + filePath);
    }
    public static void addExcel(String[] title, String sheetName, Map<String, String> map, String filePath) {
        try {

            FileInputStream fileInputStream = new FileInputStream(filePath); // 获取d://test.xls,建立数据的输入通道
            POIFSFileSystem poifsFileSystem = new POIFSFileSystem(fileInputStream); // 使用POI提供的方法得到excel的信息
            HSSFWorkbook Workbook = new HSSFWorkbook(poifsFileSystem);// 得到文档对象
            HSSFCellStyle style = Workbook.createCellStyle();
            style.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 居中
            HSSFSheet sheet = Workbook.getSheet(sheetName); // 根据name获取sheet表
            HSSFCell cellUpdate = sheet.getRow(sheet.getLastRowNum()).getCell(0);
            HSSFRow newRow = sheet.createRow(sheet.getLastRowNum() + 1);
            for (int j = 0; j < title.length; j++) {
                HSSFCell cell = newRow.createCell(j);
                if (j == 0) {
                    if (org.apache.commons.lang3.StringUtils.isNumeric(getStringCellValue(cellUpdate))) {
                        cell.setCellValue(Integer.valueOf(getStringCellValue(cellUpdate)) + 1);
                    } else {
                        cell.setCellValue(1);
                    }

                } else {
                    cell.setCellValue(map.get(title[j]));
                }

                cell.setCellStyle(style);
            }
            FileOutputStream out = new FileOutputStream(filePath); // 向d://test.xls中写数据
            out.flush();
            Workbook.write(out);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public static void addAllExcel(String[] title, int sheetNum, Map<String, String> map, String filePath) {
        try {

            FileInputStream fileInputStream = new FileInputStream(filePath); // 获取d://test.xls,建立数据的输入通道
            POIFSFileSystem poifsFileSystem = new POIFSFileSystem(fileInputStream); // 使用POI提供的方法得到excel的信息
            HSSFWorkbook Workbook = new HSSFWorkbook(poifsFileSystem);// 得到文档对象
            HSSFSheet sheet = null;
            HSSFCellStyle style = Workbook.getCellStyleAt(0);
            style.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 居中
            if( Workbook.getNumberOfSheets()  >  sheetNum){
                sheet = Workbook.getSheetAt(sheetNum);
            }else{
                sheet=Workbook.createSheet();
                // 创建第一行
                HSSFRow row0 = sheet.createRow(0);
                row0.setHeight(Short.valueOf("600"));
                HSSFCell cell0 = row0.createCell(0);
                // 插入第一行数据的表头
                HSSFCell cell = null;
                for (int i = 0; i < title.length; i++) {
                    cell = row0.createCell(i);
                    cell.setCellValue(title[i]);
                    cell.setCellStyle(style);
                }
            }
            HSSFCell cellUpdate = sheet.getRow(sheet.getLastRowNum()).getCell(0);
            HSSFRow  newRow = sheet.createRow(sheet.getLastRowNum() +1);
            for (int j = 0; j < title.length; j++) {
                HSSFCell cell = newRow.createCell(j);
                if (j == 0) {

                    if (org.apache.commons.lang3.StringUtils.isNumeric(getStringCellValue(cellUpdate))) {
                        cell.setCellValue(Integer.valueOf(getStringCellValue(cellUpdate)) + 1);
                    } else {
                        cell.setCellValue(1);
                    }
                } else {
                    cell.setCellValue(map.get(title[j]));
                }
                cell.setCellStyle(style);
            }
            FileOutputStream out = new FileOutputStream(filePath); // 向d://test.xls中写数据
            out.flush();
            Workbook.write(out);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static String getStringCellValue(Cell cell) {

        String strCell = "";
        if(null == cell) {
            return strCell;
        }
        switch (cell.getCellType()) {

            case HSSFCell.CELL_TYPE_STRING:
                strCell = cell.getStringCellValue();
                break;
            case HSSFCell.CELL_TYPE_NUMERIC:
                strCell = String.valueOf((int) cell.getNumericCellValue());
                break;
            case HSSFCell.CELL_TYPE_BOOLEAN:
                strCell = String.valueOf(cell.getBooleanCellValue());
                break;
            case HSSFCell.CELL_TYPE_BLANK:
                strCell = "";
                break;
            default:
                strCell = "";
                break;
        }
        if (strCell.equals("") || strCell == null) {
            return "";
        }
        if (cell == null) {
            return "";
        }
        return strCell;
    }

    /* 读取Excel数据 */
    public static List<Long> readExcelXlsx(String xlsPath, Integer sheetNumber, Integer index) {
        List<Long> idList = new LinkedList<Long>();
        try {
            PoiExcelUtils poiUtils = new PoiExcelUtils();
            ArrayList<ArrayList<String>> list = readXSSFExcel(xlsPath,sheetNumber, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11,12,13,14,15);
            for (int i = 0; i < list.size(); i++) {
                if (i == 0) {
                    continue;
                }
                List<String> arrayList = list.get(i);
                if (!org.apache.commons.lang3.StringUtils.isNumeric(arrayList.get(index))) {
                    System.out.println("不是数字:" + arrayList.get(index));
                    continue;
                }
                Long id = Long.valueOf(arrayList.get(index));
                idList.add(id);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return idList;

    }

    /* 读取Excel数据 */
    public static List<String> readExcelXlsxStringValue(String xlsPath,Integer sheetNumber, Integer index) {
        List<String> idList = new LinkedList<String>();
        try {
            PoiExcelUtils poiUtils = new PoiExcelUtils();
            ArrayList<ArrayList<String>> list = readXSSFExcel(xlsPath,sheetNumber, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11,12,13,14,15);
            for (int i = 0; i < list.size(); i++) {
//				if (i == 0) {
//					continue;
//				}
                List<String> arrayList = list.get(i);
                idList.add(arrayList.get(index));

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return idList;

    }
    /* 读取Excel数据 */
    public static List<Long> readExcelXls(String xlsPath,Integer sheetNumber, Integer index) {
        List<Long> idList = new LinkedList<Long>();
        try {
            PoiExcelUtils poiUtils = new PoiExcelUtils();
            ArrayList<ArrayList<String>> list = readHSSFExcel(xlsPath,sheetNumber, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11,12,13,14,15);
            for (int i = 0; i < list.size(); i++) {
                if (i == 0) {
                    continue;
                }
                List<String> arrayList = list.get(i);
                if (!StringUtils.isNumeric(arrayList.get(index))) {
                    System.out.println("不是数字:" + arrayList.get(index));
                    continue;
                }
                Long id = Long.valueOf(arrayList.get(index));
                idList.add(id);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return idList;

    }

    public static List<String> readExcelXlsStringValue(String xlsPath,Integer sheetNumber, Integer index){
        List<String> idList = new LinkedList<String>();
        try {
            PoiExcelUtils poiUtils = new PoiExcelUtils();
            ArrayList<ArrayList<String>> list = readHSSFExcel(xlsPath,sheetNumber, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11,12,13,14,15);
            for (int i = 0; i < list.size(); i++) {
                if (i == 0) {
                    continue;
                }
                List<String> arrayList = list.get(i);
                idList.add(arrayList.get(index));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return idList;
    }

    public static ArrayList<ArrayList<String>> readXSSFExcel(String excelUrl,int sheetNumber,int ... args) throws IOException {

        //读取xlsx文件
        XSSFWorkbook xssfWorkbook = null;
        //寻找目录读取文件
        File excelFile = new File(excelUrl);
        InputStream is = new FileInputStream(excelFile);
        xssfWorkbook = new XSSFWorkbook(is);

        if(xssfWorkbook==null){
            System.out.println("未读取到内容,请检查路径！");
            return null;
        }

        ArrayList<ArrayList<String>> ans=new ArrayList<ArrayList<String>>();
        //遍历xlsx中的sheet
        for (int numSheet = 0; numSheet < xssfWorkbook.getNumberOfSheets(); numSheet++) {
            if(numSheet != sheetNumber) {
                continue;
            }
            XSSFSheet xssfSheet = xssfWorkbook.getSheetAt(numSheet);
            if (xssfSheet == null) {
                continue;
            }
            // 对于每个sheet，读取其中的每一行
            for (int rowNum = 0; rowNum <= xssfSheet.getLastRowNum(); rowNum++) {
                XSSFRow xssfRow = xssfSheet.getRow(rowNum);
                if (xssfRow == null) continue;
                ArrayList<String> curarr=new ArrayList<String>();
                for(int columnNum = 0 ; columnNum<args.length ; columnNum++){
                    XSSFCell cell = xssfRow.getCell(args[columnNum]);

                    curarr.add(getStringCellValue(cell));
                }
                ans.add(curarr);
            }
        }
        return ans;
    }



    public static ArrayList<ArrayList<String>> readHSSFExcel(String excelUrl,int sheetNumber, int ... args) throws IOException {

        //读取xlsx文件
        HSSFWorkbook xssfWorkbook = null;
        //寻找目录读取文件
        File excelFile = new File(excelUrl);
        InputStream is = new FileInputStream(excelFile);
        xssfWorkbook = new HSSFWorkbook(is);

        if(xssfWorkbook==null){
            System.out.println("未读取到内容,请检查路径！");
            return null;
        }

        ArrayList<ArrayList<String>> ans=new ArrayList<ArrayList<String>>();
        //遍历xlsx中的sheet
        for (int numSheet = 0; numSheet < xssfWorkbook.getNumberOfSheets(); numSheet++) {
            if(numSheet != sheetNumber) {
                continue;
            }
            HSSFSheet xssfSheet = xssfWorkbook.getSheetAt(numSheet);
            if (xssfSheet == null) {
                continue;
            }
            // 对于每个sheet，读取其中的每一行
            for (int rowNum = 0; rowNum <= xssfSheet.getLastRowNum(); rowNum++) {
                HSSFRow xssfRow = xssfSheet.getRow(rowNum);
                if (xssfRow == null) continue;
                ArrayList<String> curarr=new ArrayList<String>();
                for(int columnNum = 0 ; columnNum<args.length ; columnNum++){
                    HSSFCell cell = xssfRow.getCell(args[columnNum]);

                    curarr.add(getStringCellValue(cell));
                }
                ans.add(curarr);
            }
        }
        return ans;
    }


}
