package com.file.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;
import com.alibaba.excel.metadata.Table;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.fastjson.JSONObject;
import com.utils.EasyExcelUtil;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.junit.Test;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.List;

/**
 * @author ：suncj
 * @date ：2019/9/12 14:53
 */
@Log4j2
public class EasyExcelTest {




    @Test
    public void readOneExcel() {
        try {
            List<EasyExcelTest.CaiHongExcelEntityDemo> list = EasyExcelUtil.readExcelData("G:\\sync\\彩虹音乐类总曲库.xlsx", EasyExcelTest.CaiHongExcelEntityDemo.class,1);
            for (EasyExcelTest.CaiHongExcelEntityDemo data : list) {
                log.info(JSONObject.toJSONString(data));
            }
            OutputStream outputStream=new FileOutputStream("G:\\sync\\复制彩虹音乐类总曲库.xlsx");
            EasyExcelUtil.writeExcelWithModel(outputStream,list,EasyExcelTest.CaiHongExcelEntityDemo.class, ExcelTypeEnum.XLSX);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Data
    public static class CaiHongExcelEntityDemo extends BaseRowModel implements Serializable {
        @ExcelProperty(value = "cpid" ,index = 0)
        private String cpid;
        @ExcelProperty(value = "批次",index = 1)
        private String batch;
        @ExcelProperty(value = "资源编码",index = 2)
        private String resource_code;
        @ExcelProperty(value = "资源名称",index = 3)
        private String resource_name;
        @ExcelProperty(value = "歌手名称",index = 4)
        private String singer_name;
        @ExcelProperty(value = "歌手类型",index = 5)
        private String singer_type;
        @ExcelProperty(value = "歌手地区",index = 6)
        private String singer_area;
        @ExcelProperty(value = "搜索关键字",index = 7)
        private String seach_key;
        @ExcelProperty(value = "版本",index = 8)
        private String version;
        @ExcelProperty(value = "主题类型",index = 9)
        private String theme;
        @ExcelProperty(value = "语种",index = 10)
        private String language;
        @ExcelProperty(value = "流派",index = 11)
        private String genre;
        @ExcelProperty(value = "场景",index = 12)
        private String scenes;
        @ExcelProperty(value = "情感",index = 13)
        private String moods;
        @ExcelProperty(value = "年代",index = 14)
        private String era;
        @ExcelProperty(value = "视频类别",index = 15)
        private String resource_type;
        @ExcelProperty(value = "影视描述",index = 16)
        private String resource_desc;
        @ExcelProperty(value = "清晰度",index = 17)
        private String sharpness;
        @ExcelProperty(value = "高标清",index = 18)
        private String highOrStandard;
        @ExcelProperty(value = "版权信息",index = 19)
        private String copyright_message;
        @ExcelProperty(value = "版权开始时间",index = 20)
        private String copyright_begintime;
        @ExcelProperty(value = "版权结束时间",index = 21)
        private String copyright_endTime;
        @ExcelProperty(value = "上线时间",index = 22)
        private String Online_time;
        @ExcelProperty(value = "下线时间",index = 23)
        private String offLine_time;
        @ExcelProperty(value = "下线原因",index = 24)
        private String offline_cause;
    }

}
