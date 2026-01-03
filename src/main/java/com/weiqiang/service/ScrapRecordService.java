package com.weiqiang.service;

import com.weiqiang.pojo.ScrapRecord;

import java.util.List;

/**
 * @author 袁志刚
 * @version 1.0
 */


public interface ScrapRecordService {
    List<ScrapRecord> getScrapRecords();

    boolean scrapEquip(String equipId, ScrapRecord scrapRecord);

    boolean deleteScrapRecord(String equipId, String scrapNo);

    int putScrapRecord(String scrapNo,ScrapRecord scrapRecord);
}
