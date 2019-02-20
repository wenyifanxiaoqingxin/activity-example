package com.crionline.activiti.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by fx on 2019/2/12.
 */
public class SubmitInfo implements Serializable{

    private static final long serialVersionUID = 1L;
    private String name;
    private String info;
    private int day;
    private List<AuditInfo> auditInfos;
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public List<AuditInfo> getAuditInfos() {
        return auditInfos;
    }

    public void setAuditInfos(List<AuditInfo> auditInfos) {
        this.auditInfos = auditInfos;
    }
}
