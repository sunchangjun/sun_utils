package com.utils;


import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;

/**
 * @author ：suncj
 * @date ：2019/8/21 14:25
 */
public class CsvUtils {


    /**
     * 读取CSV文件
     *
     * @param csvFilePath 文件路径
     */
    public static List<String[]> readeCsv(String csvFilePath) {
        ArrayList<String[]> csvList = new ArrayList<String[]>(); // 用来保存数据
        try {
            CsvReader reader = new CsvReader(csvFilePath, ',', Charset.forName("GBK")); // 一般用这编码读就可以了
            reader.readHeaders(); // 跳过表头 如果需要表头的话，不要写这句。
            while (reader.readRecord()) { // 逐行读入除表头的数据
                csvList.add(reader.getValues());
                System.out.println(JSONObject.toJSONString(reader.getValues()));
            }
            reader.close();
        } catch (Exception ex) {
            System.out.println(ex);
        }
        return csvList;
    }

    /**
     * 读取CSV文件
     *
     * @param csvFilePath 文件路径
     */
    public static List<String> readeCsvRow(String csvFilePath, int row) {
        ArrayList<String> csvList = new ArrayList<String>(); // 用来保存数据
        try {
            CsvReader reader = new CsvReader(csvFilePath, ',', Charset.forName("GBK")); // 一般用这编码读就可以了
            reader.readHeaders(); // 跳过表头 如果需要表头的话，不要写这句。
            while (reader.readRecord()) { // 逐行读入除表头的数据
                csvList.add(reader.getValues()[row]);
            }
            reader.close();
        } catch (Exception ex) {
            System.out.println(ex);
        }
        return csvList;
    }


    /**
     * CSV文件生成方法
     *
     * @param head
     * @param dataList
     * @param outPutPath
     * @param filename
     * @return
     */
    public static File createCSVFile(List<Object> head, List<List<Object>> dataList,
                                     String outPutPath, String filename) {

        File csvFile = null;
        BufferedWriter csvWtriter = null;
        try {
            csvFile = new File(outPutPath + File.separator + filename + ".csv");
            File parent = csvFile.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }
            csvFile.createNewFile();
            // GB2312使正确读取分隔符","
            csvWtriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
                    csvFile), "GB2312"), 1024);
            // 写入文件头部
            writeRow(head, csvWtriter);
            // 写入文件内容
            for (List<Object> row : dataList) {
                writeRow(row, csvWtriter);
            }
            csvWtriter.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                csvWtriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return csvFile;
    }

    /**
     * 写一行数据方法
     *
     * @param row
     * @param csvWriter
     * @throws IOException
     */
    private static void writeRow(List<Object> row, BufferedWriter csvWriter) throws IOException {
        // 写入文件头部
        for (Object data : row) {
            StringBuffer sb = new StringBuffer();
            String rowStr = sb.append("\"").append(data).append("\",").toString();
            csvWriter.write(rowStr);
        }
        csvWriter.newLine();
    }

    /**
     * 追加数据:不建议使用
     * @param row
     * @param csvPath
     * @throws IOException
     */
    public static File appendWriteRow(List<Object> row, String csvPath) {
        BufferedWriter csvWriter = null;
        File csvFile = null;
        try {
            //先读取旧数据
            CsvReader reader = new CsvReader(csvPath, ',', Charset.forName("GBK")); // 一般用这编码读就可以了
            List<List<Object>> dataList = new ArrayList<List<Object>>();
            while (reader.readRecord()) { // 逐行读入除表头的数据
                List<Object> line = new ArrayList<Object>();
                for (String str : reader.getValues()) {
                    line.add(str);
                }
                dataList.add(line);
            }
            dataList.add(row);

            //再重新写入
            csvFile = new File(csvPath);
            // GB2312使正确读取分隔符","
            csvWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(csvFile), "GBK"), 1024);
            // 写入数据
            for (List<Object> line : dataList) {
                for (Object data : line) {
                    StringBuffer sb = new StringBuffer();
                    String rowStr = sb.append("\"").append(data).append("\",").toString();
                    csvWriter.write(rowStr);
                }
                csvWriter.newLine();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                csvWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return csvFile;
    }

}
