package com.crionline.activiti.model;

import java.io.Serializable;

/**
 * Created by fx on 2019/2/12.
 */
public class AuditInfo implements Serializable{
    private static final long serialVersionUID = 2L;
    private String auditUser;

    private String auditMessage;

    public String getAuditUser() {
        return auditUser;
    }

    public void setAuditUser(String auditUser) {
        this.auditUser = auditUser;
    }

    public String getAuditMessage() {
        return auditMessage;
    }

    public void setAuditMessage(String auditMessage) {
        this.auditMessage = auditMessage;
    }
}
