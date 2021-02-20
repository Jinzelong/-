package sample;

import javafx.beans.property.SimpleStringProperty;

public class apk {
    public SimpleStringProperty nameProperty = new SimpleStringProperty();
    public SimpleStringProperty packageProperty = new SimpleStringProperty();
    public SimpleStringProperty targetVersionProperty = new SimpleStringProperty();
    public SimpleStringProperty MD5Property = new SimpleStringProperty();
    public SimpleStringProperty versionCodeProperty = new SimpleStringProperty();
    public SimpleStringProperty versionNameProperty = new SimpleStringProperty();
    public SimpleStringProperty installProperty = new SimpleStringProperty();

    public apk(String name,String packagename,String targetVersion,String MD5,String versionCode,String versionName,String install){
        nameProperty.set(name);
        packageProperty.set(packagename);
        targetVersionProperty.set(targetVersion);
        MD5Property.set(MD5);
        versionCodeProperty.set(versionCode);
        versionNameProperty.set(versionName);
        installProperty.set(install);

    }
    public void setName(String name){
        nameProperty.set(name);
    }
    public String getName(){
        return nameProperty.get();
    }
    public void setPackagename(String packagename){
        packageProperty.set(packagename);
    }
    public String getPackagename(){
        return packageProperty.get();
    }
    public void setTargetVersion(String targetVersion){
        targetVersionProperty.set(targetVersion);
    }
    public String getTargetVersion(){
        return targetVersionProperty.get();
    }
    public void setMD5(String MD5){
        MD5Property.set(MD5);
    }
    public String getMD5(){
        return MD5Property.get();
    }
    public void setVersionCode(String versionCode){
        versionCodeProperty.set(versionCode);
    }
    public String getVersionCode(){
        return versionCodeProperty.get();
    }
    public void setVersionNameProperty(String versionName){
        versionNameProperty.set(versionName);
    }
    public String getVersionName(){
        return versionNameProperty.get();
    }
    public void setInstall(String install){
        installProperty.set(install);
    }
    public String getInstall(){
        return installProperty.get();
    }


}
