package com.milai.ecoop.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/4/22 0022.
 */
public class Category implements Serializable{
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public String getCzone() {
        return czone;
    }

    public void setCzone(String czone) {
        this.czone = czone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEname() {
        return ename;
    }

    public void setEname(String ename) {
        this.ename = ename;
    }

    public String getLetter() {
        return letter;
    }

    public void setLetter(String letter) {
        this.letter = letter;
    }

    public String getSort_order() {
        return sort_order;
    }

    public void setSort_order(String sort_order) {
        this.sort_order = sort_order;
    }

    public String getDisplay() {
        return display;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    public String getRelate_data() {
        return relate_data;
    }

    public void setRelate_data(String relate_data) {
        this.relate_data = relate_data;
    }

    public String getFid() {
        return fid;
    }

    public void setFid(String fid) {
        this.fid = fid;
    }

    private String zone;
    private String czone;
    private String name;
    private String ename;
    private String letter;
    private String sort_order;
    private String display;
    private String relate_data;
    private String fid;
}
