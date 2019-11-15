package com.file.excel;

import com.utils.CsvUtils;
import org.junit.Test;
import com.csvreader.CsvWriter;
import java.io.IOException;
import java.util.*;

/**
 * @author ：suncj
 * @date ：2019/8/21 14:32
 */
public class CsvExcelTest {
    @Test
    public void test1(){
        Set<String> user_set=new HashSet<String>();
        List<String[]> list= CsvUtils.readeCsv("G:\\test.csv");
        for (String[] sp:list) {
            if(sp[3].startsWith("1")){
                user_set.add(sp[3]);
            }

        }
        System.out.println(user_set.size());

    }
    @Test
    public void test2(){
        List<Object> head= new ArrayList<>(Arrays.asList("时间","新增","续订"));
        List<Object> row1= new ArrayList<>(Arrays.asList("8.15","1","2"));
        List<Object> row2= new ArrayList<>(Arrays.asList("8.16","3","4"));
        List<List<Object>> listData= new ArrayList<List<Object>>(Arrays.asList(row1,row2));
        CsvUtils.createCSVFile(head,listData,"G:\\","test");
    }

    @Test
    public void test3(){
        List<Object> row2= new ArrayList<>(Arrays.asList("8.17","7","8"));

        try {
            CsvUtils.appendWriteRow(row2,"G:\\test.csv");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test4() throws IOException {
        CsvWriter csvWriter=new CsvWriter("G:\\test.csv");

        csvWriter.write("11");
    }


}


