package com.milai.ecoop.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/3/10 0010.
 */
public class Ordertrack implements Serializable{
    private String id;
    private String fkorderid;
    private String fkstatusid;
    private String stuser;
    private String stdate;
    private String stremark;
    private String statusinfo;
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFkorderid() {
        return fkorderid;
    }

    public void setFkorderid(String fkorderid) {
        this.fkorderid = fkorderid;
    }

    public String getFkstatusid() {
        return fkstatusid;
    }

    public void setFkstatusid(String fkstatusid) {
        this.fkstatusid = fkstatusid;
    }

    public String getStuser() {
        return stuser;
    }

    public void setStuser(String stuser) {
        this.stuser = stuser;
    }

    public String getStdate() {
        return stdate;
    }

    public void setStdate(String stdate) {
        this.stdate = stdate;
    }

    public String getStremark() {
        return stremark;
    }

    public void setStremark(String stremark) {
        this.stremark = stremark;
    }

    public String getStatusinfo() {
        return statusinfo;
    }

    public void setStatusinfo(String statusinfo) {
        this.statusinfo = statusinfo;
    }



}
