package com.file.excel;

import com.alibaba.fastjson.JSONObject;
import com.utils.EasyExcelUtil;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

/**
 * @author ：suncj
 * @date ：2019/9/12 14:53
 */
public class EasyExcelTest {


    @Test
    public void test(){
        try {
//        List<EasyExcelUtil.ExcelEntityDemo> list= EasyExcelUtil.readExcelData("G:\\9.12统计上周资源访问排行.xls",EasyExcelUtil.ExcelEntityDemo.class);
            List<EasyExcelUtil.ExcelEntityDemo> list= EasyExcelUtil.readDirAllExcel("G:\\",EasyExcelUtil.ExcelEntityDemo.class);
            for (EasyExcelUtil.ExcelEntityDemo model:list) {
            }
        System.out.println(JSONObject.toJSONString(list));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
