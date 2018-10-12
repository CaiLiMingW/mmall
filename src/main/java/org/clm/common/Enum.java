package org.clm.common;

/**
 * @author Ccc
 * @date 2018/10/12 0012 下午 2:55
 */
public enum Enum {
    ONE(0,"1");

    private int code;
    private String value;

    Enum(int code, String value) {
        this.code = code;
        this.value = value;
    }

    public int getCode() {
        return code;
    }

    public String getValue() {
        return value;
    }

}
