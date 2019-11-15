package com.utils;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.metadata.BaseRowModel;
import com.alibaba.excel.metadata.Sheet;
import com.alibaba.excel.metadata.Table;
import com.alibaba.excel.support.ExcelTypeEnum;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;

import lombok.Data;
import org.apache.poi.ss.formula.functions.T;

/**
 * @author ：suncj
 * @date ：2019/9/12 14:44
 */
public class EasyExcelUtil {
    /**
     * 模型解析监听器 -- 每解析一行会回调invoke()方法，整个excel解析结束会执行doAfterAllAnalysed()方法
     */
    private static class ModelExcelListener<E> extends AnalysisEventListener<E> {
        private List<E> dataList = new ArrayList<E>();
        @Override
        public void invoke(E object, AnalysisContext context) {
            dataList.add(object);
        }
        @Override
        public void doAfterAllAnalysed(AnalysisContext context) { }
        public List<E> getDataList() {
            return dataList;
        }
        @SuppressWarnings("unused")
        public void setDataList(List<E> dataList) {
            this.dataList = dataList;
        }
    }
    /**
     * 读取文件夹下所有表格
     * @param dirPath
     * @param clazz
     * @param <E>
     * @return
     */
    public static <E> List<E> readDirAllExcel(String dirPath,Class<? extends BaseRowModel> clazz){
        List<E> list= new ArrayList<E>();
        try{
        File dirFile=new File(dirPath);
        if(dirFile.exists() && dirFile.isDirectory()){
            for (File file:dirFile.listFiles()){
                if(file.getName().endsWith(".xls") || file.getName().endsWith(".xlsx")  ){
                    List<E> subList= readExcelData(file.getAbsolutePath(),clazz);
                    list.addAll(subList); } }
        }}catch(Exception e){ e.printStackTrace(); }
      return list;
    }

    /**
     * 读取单个表格
     * @param excelPathpath
     * @param clazz
     * @param <E>
     * @return
     * @throws IOException
     */
    public static <E> List<E> readExcelData(String excelPathpath,Class<? extends BaseRowModel> clazz,int  sheetNo) throws IOException {
        ModelExcelListener<E> listener = new ModelExcelListener<E>();
        Path path=Paths.get(excelPathpath);
        InputStream inputStream   = new BufferedInputStream(Files.newInputStream(path));
        ExcelReader excelReader = new ExcelReader(inputStream, null, listener);
        //默认只有一列表头
        excelReader.read(new Sheet(sheetNo, 1, clazz));

        return listener.getDataList();
    }
    public static <E> List<E> readExcelData(String excelPathpath,Class<? extends BaseRowModel> clazz) throws IOException {
        ModelExcelListener<E> listener = new ModelExcelListener<E>();
        Path path=Paths.get(excelPathpath);
        InputStream inputStream   = new BufferedInputStream(Files.newInputStream(path));
        ExcelReader excelReader = new ExcelReader(inputStream, null, listener);
        //默认只有一列表头
        excelReader.read(new Sheet(1, 1, clazz));

        return listener.getDataList();
    }
    /**
     * 使用 模型 来写入Excel
     * @param outputStream Excel的输出流
     * @param data 要写入的以 模型 为单位的数据
     * @param clazz 模型的类
     * @param excelTypeEnum Excel的格式(XLS或XLSX)
     */
    public static void writeExcelWithModel(OutputStream outputStream, List<? extends BaseRowModel> data,
                                           Class<? extends BaseRowModel> clazz, ExcelTypeEnum excelTypeEnum)  {
        //这里指定需要表头，因为model通常包含表头信息
        ExcelWriter writer = new ExcelWriter(outputStream, excelTypeEnum,true);
        //写第一个sheet, sheet1  数据全是List<String> 无模型映射关系
        Sheet sheet1 = new Sheet(1, 0, clazz);
        writer.write(data, sheet1);
        writer.finish();
    }
    public static void writeExcelWithModel(String  outPutPath, List<? extends BaseRowModel> data,
                                           Class<? extends BaseRowModel> clazz, ExcelTypeEnum excelTypeEnum) throws FileNotFoundException {
        //这里指定需要表头，因为model通常包含表头信息
        OutputStream outputStream=new FileOutputStream(outPutPath);
        ExcelWriter writer = new ExcelWriter(outputStream, excelTypeEnum,true);
        //写第一个sheet, sheet1  数据全是List<String> 无模型映射关系
        Sheet sheet1 = new Sheet(1, 0, clazz);
        writer.write(data, sheet1);
        writer.finish();
    }
    public static void writeExcelWithModel(String  outPutPath,String sheetName, List<? extends BaseRowModel> data,
                                           Class<? extends BaseRowModel> clazz, ExcelTypeEnum excelTypeEnum) throws FileNotFoundException {
        //这里指定需要表头，因为model通常包含表头信息
        OutputStream outputStream=new FileOutputStream(outPutPath);
        ExcelWriter writer = new ExcelWriter(outputStream, excelTypeEnum,true);
        //写第一个sheet, sheet1  数据全是List<String> 无模型映射关系
        Sheet sheet1 = new Sheet(1, 0, clazz);
        sheet1.setSheetName(sheetName);
        writer.write(data, sheet1);
        writer.finish();
    }


    @Data
     public static class ExcelEntityDemo extends BaseRowModel implements Serializable{
        @ExcelProperty(value = "姓名" ,index = 0)
        private String name;
        @ExcelProperty(value = "处理结果",index = 2)
        private String music_id;

//        @ExcelProperty(value = "年龄",index = 1)
//        private String age;
//
//        @ExcelProperty(value = "邮箱",index = 2)
//        private String email;
//
//        @ExcelProperty(value = "地址",index = 3)
//        private String address;
//


    }
}
