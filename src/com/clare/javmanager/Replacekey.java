package com.clare.javmanager;

/**
 * Created by chenz on 8/17/2016.
 */
public class Replacekey {
    public String originalKey;
    public String newKey;

    public Replacekey(String originalKey, String newKey) {
        this.originalKey = originalKey;
        this.newKey = newKey;

    }

    public Replacekey() {

    }

    public String getOriginalKey() {
        return originalKey;
    }

    public void setOriginalKey(String originalKey) {
        this.originalKey = originalKey;
    }

    public String getNewKey() {
        return newKey;
    }

    public void setNewKey(String newKey) {
        this.newKey = newKey;
    }
}
