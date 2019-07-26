package hk.reco.music.iptv.hndx.utils;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;


import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.lang.reflect.Field;
import java.util.*;

/**
 * author : wjtang
 * date : 2017年6月16日
 * description : POI 导出 excel
 */
public class PoiExcelUtils {
    //模板map
    private Map<String,Workbook> tempWorkbook = new HashMap<String, Workbook>();
    //模板输入流map
    private Map<String,FileInputStream> tempStream = new HashMap<String, FileInputStream>();

    /**
     * 导出excel
     * @param datas 数据
     * @param template 模板路径
     * @return
     */
    @SuppressWarnings("rawtypes")
    public String exportExcel(List<?> datas,String template,String resultPath){
        Workbook book = null;
        try {
            book = getTempWorkbook(template);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("Excel模板不存在！"+template);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Excel模板读取异常！"+template);
        }
        if(book == null){
            return null;
        }

        //1.获取模板中第一个sheet的第二行所有cell的变量名
        //2.通过反射，匹配datas中的变量名，相同则取值写数据
        Sheet tempSheet = book.getSheetAt(0);
        Row secondRow = tempSheet.getRow(1);
        if(secondRow == null){
            System.out.println("Excel模板内变量为空，无法匹配！"+template);
        }
        Map<String,Integer> cellMap = new HashMap<String, Integer>();
        for (Cell cell : secondRow) {
            if(cell != null){
                String cellName = cell.getStringCellValue();
                Integer columnIndex = cell.getColumnIndex();
                if(StringUtils.isNotBlank(cellName)){
                    cellMap.put(cellName, columnIndex);
                }
            }
        }

		/*int type = (cell==null)?Cell.CELL_TYPE_BLANK:cell.getCellType();
		String value = null;
		switch(type){
			case Cell.CELL_TYPE_NUMERIC:
				value = new DecimalFormat("#").format(cell.getNumericCellValue());
				break;
			case Cell.CELL_TYPE_STRING:
				value = cell.getStringCellValue();
				break;
			case Cell.CELL_TYPE_BLANK:
				value = null;
				break;
			default:
				logger.error("格式异常!");*/

        if(CollectionUtils.isNotEmpty(datas)){
            Object obj = datas.get(0);
            Class clazz = (Class)obj.getClass();
            Field[] fields = clazz.getDeclaredFields();

            int j = 1;
            for(Object data : datas){
                Row row = tempSheet.createRow(j);
                for(int i = 0 ; i < fields.length; i++){
                    Field f = fields[i];
                    f.setAccessible(true); //设置属性是可以访问的
                    Integer cellIndex = cellMap.get(f.getName());
                    if(cellIndex != null){
                        try {
                            Cell cell = row.createCell(cellIndex);
                            cell.setCellValue(f.get(data)+"");
                        } catch (IllegalArgumentException e) {
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                }
                j++;
            }
        }
        String fileName = getFileName();
        File file = new File(resultPath+fileName);
        try {
            OutputStream os = new FileOutputStream(file);
            book.write(os);
            os.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resultPath+fileName;
    }

    /**
     * 追加导出excel
     * @param datas
     * @param template 模板.xlsx
     * @param targetFile 已存在文件.xlsx
     * @return
     */
    @SuppressWarnings("rawtypes")
    public String addExportExcel(List<?> datas,String template,String targetFile){
        Workbook book = null;
        try {
            book = getTempWorkbook(template);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("Excel模板不存在！"+template);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Excel模板读取异常！"+template);
        }
        if(book == null){
            return null;
        }

        //1.获取模板中第一个sheet的第二行所有cell的变量名
        //2.通过反射，匹配datas中的变量名，相同则取值写数据
        Sheet tempSheet = book.getSheetAt(0);
        Row secondRow = tempSheet.getRow(1);
        if(secondRow == null){
            System.out.println("Excel模板内变量为空，无法匹配！"+template);
        }
        Map<String,Integer> cellMap = new HashMap<String, Integer>();
        for (Cell cell : secondRow) {
            if(cell != null){
                String cellName = cell.getStringCellValue();
                Integer columnIndex = cell.getColumnIndex();
                if(StringUtils.isNotBlank(cellName)){
                    cellMap.put(cellName, columnIndex);
                }
            }
        }

        // 处理目标文档，追加数据
        Workbook targetBook = null;
        try {
            targetBook = getTempWorkbook(targetFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("目标Excel不存在！"+targetFile);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("目标Excel读取异常！"+targetFile);
        }
        if(targetBook != null && CollectionUtils.isNotEmpty(datas)){

            Object obj = datas.get(0);
            Class clazz = (Class)obj.getClass();
            Field[] fields = clazz.getDeclaredFields();

            Sheet targetSheet = targetBook.getSheetAt(0);
            int j = targetSheet.getLastRowNum()+1;//获取
            for(Object data : datas){
                Row row = targetSheet.createRow(j);
                for(int i = 0 ; i < fields.length; i++){
                    Field f = fields[i];
                    f.setAccessible(true); //设置属性是可以访问的
                    Integer cellIndex = cellMap.get(f.getName());
                    if(cellIndex != null){
                        try {
                            Cell cell = row.createCell(cellIndex);
                            cell.setCellValue(f.get(data)+"");
                        } catch (IllegalArgumentException e) {
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                }
                j++;
            }
        }
        File file = new File(targetFile);
        try {
            OutputStream os = new FileOutputStream(file);
            targetBook.write(os);
            os.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return targetFile;
    }

    /**
     * 根据路径获取模板
     * @param tempFilePath
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    private Workbook getTempWorkbook(String tempFilePath) throws FileNotFoundException, IOException {
        if(!tempWorkbook.containsKey(tempFilePath)){
            if(tempFilePath.endsWith(".xlsx")){
                tempWorkbook.put(tempFilePath, new XSSFWorkbook(getFileInputStream(tempFilePath)));
            }else if(tempFilePath.endsWith(".xls")){
                tempWorkbook.put(tempFilePath, new HSSFWorkbook(getFileInputStream(tempFilePath)));
            }
        }
        return tempWorkbook.get(tempFilePath);
    }

    /**
     * 读取模板流文件
     * @param tempFilePath
     * @return
     * @throws FileNotFoundException
     */
    private FileInputStream getFileInputStream(String tempFilePath) throws FileNotFoundException {
        if(!tempStream.containsKey(tempFilePath)){
            tempStream.put(tempFilePath, new FileInputStream(tempFilePath));
        }
        return tempStream.get(tempFilePath);
    }

    /**
     * 获取随机文件名
     * @return
     */
    private String getFileName(){
        String baseStr = "1234567890abcdefghijklmnopqrstuvwxyz";
        long timer = System.currentTimeMillis();
        Random random = new Random();
        StringBuffer sb = new StringBuffer(Long.toString(timer));
        for (int i = 0; i < 4; i++) {
            int number = random.nextInt(baseStr.length());
            sb.append(baseStr.charAt(number));
        }
        sb.append(".xlsx");
        return sb.toString();
    }

    /**
     * 第二个参数为要读取的列，从0开始
     * @param args
     * @return
     * @throws IOException
     */
    public ArrayList<ArrayList<String>> readExcel(String excelUrl,int ... args) throws IOException {

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
    /**
     * 第二个参数为要读取的列，从0开始
     * @param args
     * @return
     * @throws IOException
     */
    public ArrayList<ArrayList<String>> readExcelXls(String excelUrl,int ... args) throws IOException {

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




    public static String getStringCellValue(Cell cell) {
        String cellValue = "";
        if (cell == null) {
            return cellValue;
        }
        // 判断数据的类型
        switch (cell.getCellType()) {
            case NUMERIC: // 数字
                cell.setCellType(CellType.STRING);
                cellValue = cell.getStringCellValue();
                break;
            case STRING: // 字符串
                cell.setCellType(CellType.STRING);
                cellValue = cell.getStringCellValue();
                break;
            case BOOLEAN: // Boolean
                cell.setCellType(CellType.STRING);
                cellValue = cell.getStringCellValue();
                break;
            case FORMULA: // 公式
                cell.setCellType(CellType.STRING);
                cellValue = cell.getStringCellValue();
                break;
            case BLANK: // 空值
                cellValue = "";
                break;
            case ERROR: // 故障
                cellValue = "非法字符";
                break;
            default:
                cellValue = "未知类型";
                break;
        }
        return cellValue;
    }






    public static void removeRow(String excelPath, int rowIndex) throws IOException {
        FileInputStream is = new FileInputStream(excelPath);

        Workbook workbook = null;
        int lastRowNum = 0;
        int size = 0;
        if(excelPath.endsWith(".xlsx")) {
            workbook = new XSSFWorkbook(is);
            XSSFSheet sheet = (XSSFSheet) workbook.getSheetAt(0);
            lastRowNum = sheet.getLastRowNum();
            if(lastRowNum == rowIndex) {
                XSSFRow removingRow = sheet.getRow(rowIndex);
                if(removingRow != null) {
                    sheet.removeRow(removingRow);
                    System.out.println("删除" + excelPath + " 第  " + rowIndex + "行" );
                }
            } else {
                if(rowIndex < lastRowNum) {
                    sheet.shiftRows(rowIndex + 1, lastRowNum, -1);//将行号为rowIndex+1一直到行号为lastRowNum的单元格全部上移一行，以便删除rowIndex行
                    System.out.println("删除" + excelPath + " 第  " + rowIndex + "行");
                }
            }
            size = sheet.getLastRowNum() + 1;
            System.out.println("剩下" + size + "行");
        } else if(excelPath.endsWith(".xls")) {
            workbook = new HSSFWorkbook(is);
            HSSFSheet sheet = (HSSFSheet)workbook.getSheetAt(0);
            lastRowNum=sheet.getLastRowNum();

            if(lastRowNum == rowIndex) {
                HSSFRow removingRow = sheet.getRow(rowIndex);
                if(removingRow != null) {
                    sheet.removeRow(removingRow);
                    System.out.println("删除" + excelPath + " 第  " + rowIndex + "行");
                }
            } else {
                if(rowIndex < lastRowNum) {
                    sheet.shiftRows(rowIndex + 1, lastRowNum, -1);//将行号为rowIndex+1一直到行号为lastRowNum的单元格全部上移一行，以便删除rowIndex行
                }
            }
            size = sheet.getLastRowNum() + 1;
            System.out.println("剩下" + size + "行");
        }

        FileOutputStream os = new FileOutputStream(excelPath);
        workbook.write(os);

//		 is.close();

//		 is = new FileInputStream(excelPath);
//		 if(excelPath.endsWith(".xlsx")) {
//				workbook = new XSSFWorkbook(is);
//				XSSFSheet sheet = (XSSFSheet) workbook.getSheetAt(0);
//				lastRowNum = sheet.getLastRowNum();
//
//		 } else {
//			 workbook = new HSSFWorkbook(is);
//       	 HSSFSheet sheet = (HSSFSheet)workbook.getSheetAt(0);
//       	 lastRowNum = sheet.getLastRowNum();
//		 }

        is.close();
        os.close();
        workbook.close();
    }

    public static void main(String[] args){
		/*List<TestUser> userList = new ArrayList<TestUser>();
		TestUser user1 = new TestUser();
		user1.setName("user1");
		user1.setValue("value1");
		TestUser user2 = new TestUser();
		user2.setName("user2");
		user2.setValue("value2");
		userList.add(user1);
		userList.add(user2);

		PoiExcelUtils ut = new PoiExcelUtils();
		System.out.println(ut.addExportExcel(userList, "E://temp.xlsx","E://myuser.xlsx"));*/
//		PoiExcelUtils poiExcelUtils = new PoiExcelUtils();
//		try {
//			ArrayList<ArrayList<String>> list = poiExcelUtils.readExcel("F:/topList.xlsx", 0, 1, 2, 3);
//			for (ArrayList<String> arrayList : list) {
//				for (String string : arrayList) {
//					System.out.print(string + "  ");
//				}
//				System.out.println("");
//			}
//			System.out.println("总共" + list.size() + "行");
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
    }
}
