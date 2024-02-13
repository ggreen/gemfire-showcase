package io.spring.gemfire.perftest.components.query.domain;

import java.util.Calendar;
import java.util.Map;

public class WibCanonicalTradeDataModel {
    // Meta-data
    private Double modelVersion;
    private Calendar creationTime;
    private String tradeSource;

    // Trade data
    private String tradeReference;
    private Calendar tradeDate;
    private String tradeStatus;
    private Map<String,Object> tradeDictionary;
    private String tradeAuditAction ;
    private Double tradeAuditVersion;

    public Double getModelVersion() {
        return modelVersion;
    }

    public void setModelVersion(Double modelVersion) {
        this.modelVersion = modelVersion;
    }

    public Calendar getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Calendar creationTime) {
        this.creationTime = creationTime;
    }

    public String getTradeSource() {
        return tradeSource;
    }

    public void setTradeSource(String tradeSource) {
        this.tradeSource = tradeSource;
    }

    public String getTradeReference() {
        return tradeReference;
    }

    public void setTradeReference(String tradeReference) {
        this.tradeReference = tradeReference;
    }

    public Calendar getTradeDate() {
        return tradeDate;
    }

    public void setTradeDate(Calendar tradeDate) {
        this.tradeDate = tradeDate;
    }

    public String getTradeStatus() {
        return tradeStatus;
    }

    public void setTradeStatus(String tradeStatus) {
        this.tradeStatus = tradeStatus;
    }

    public Map<String, Object> getTradeDictionary() {
        return tradeDictionary;
    }

    public void setTradeDictionary(Map<String, Object> tradeDictionary) {
        this.tradeDictionary = tradeDictionary;
    }

    public String getTradeAuditAction() {
        return tradeAuditAction;
    }

    public void setTradeAuditAction(String tradeAuditAction) {
        this.tradeAuditAction = tradeAuditAction;
    }

    public Double getTradeAuditVersion() {
        return tradeAuditVersion;
    }

    public void setTradeAuditVersion(Double tradeAuditVersion) {
        this.tradeAuditVersion = tradeAuditVersion;
    }
}
