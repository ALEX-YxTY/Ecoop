package com.milai.ecoop.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/5/25 0025.
 */
public class VersionInfo implements Serializable {
    private String app_name;
    private String app_file;
    private String app_version;
    private String app_version_name;
    private String app_update_desc;

    public VersionInfo() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VersionInfo that = (VersionInfo) o;

        if (app_name != null ? !app_name.equals(that.app_name) : that.app_name != null)
            return false;
        if (app_file != null ? !app_file.equals(that.app_file) : that.app_file != null)
            return false;
        if (app_version != null ? !app_version.equals(that.app_version) : that.app_version != null)
            return false;
        if (app_version_name != null ? !app_version_name.equals(that.app_version_name) : that.app_version_name != null)
            return false;
        return app_update_desc != null ? app_update_desc.equals(that.app_update_desc) : that.app_update_desc == null;

    }

    @Override
    public int hashCode() {
        int result = app_name != null ? app_name.hashCode() : 0;
        result = 31 * result + (app_file != null ? app_file.hashCode() : 0);
        result = 31 * result + (app_version != null ? app_version.hashCode() : 0);
        result = 31 * result + (app_version_name != null ? app_version_name.hashCode() : 0);
        result = 31 * result + (app_update_desc != null ? app_update_desc.hashCode() : 0);
        return result;
    }

    public VersionInfo(String app_name, String app_file, String app_version, String app_version_name, String app_update_desc) {
        this.app_name = app_name;
        this.app_file = app_file;
        this.app_version = app_version;
        this.app_version_name = app_version_name;
        this.app_update_desc = app_update_desc;
    }

    public String getApp_name() {
        return app_name;
    }

    public void setApp_name(String app_name) {
        this.app_name = app_name;
    }

    public String getApp_file() {
        return app_file;
    }

    public void setApp_file(String app_file) {
        this.app_file = app_file;
    }

    public String getApp_version() {
        return app_version;
    }

    public void setApp_version(String app_version) {
        this.app_version = app_version;
    }

    public String getApp_version_name() {
        return app_version_name;
    }

    public void setApp_version_name(String app_version_name) {
        this.app_version_name = app_version_name;
    }

    public String getApp_update_desc() {
        return app_update_desc;
    }

    public void setApp_update_desc(String app_update_desc) {
        this.app_update_desc = app_update_desc;
    }

    @Override
    public String toString() {
        return "VersionInfo{" +
                "app_name='" + app_name + '\'' +
                ", app_file='" + app_file + '\'' +
                ", app_version='" + app_version + '\'' +
                ", app_version_name='" + app_version_name + '\'' +
                ", app_update_desc='" + app_update_desc + '\'' +
                '}';
    }
}