package com.reimu.base.response;


public class HttpDataContainer {

    public static final int STATUS_SUCCESS = 0;

    private Integer status;
    private Object data;
    private String msg;

    public Integer getStatus() {
        return status;
    }

    public Object getData() {
        return data;
    }

    public String getMsg() {
        return msg;
    }



    public static HttpDataContainer create(Integer status, Object data, String msg){
        return new HttpDataContainer(status,msg,data);
    }

    public static HttpDataContainer create(Integer status,String msg){
        return new HttpDataContainer(status,msg);
    }

    public static HttpDataContainer create(Integer status,Object data){
        return new HttpDataContainer(status,data);
    }

    public static HttpDataContainer create(Integer status){
        return new HttpDataContainer(status);
    }

    protected HttpDataContainer(Integer status) {
        this.status = status;
    }

    protected HttpDataContainer(Integer status, String msg,Object data) {
        this.status = status;
        this.data = data;
        this.msg = msg;
    }

    protected HttpDataContainer(Integer status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    protected HttpDataContainer(Integer status,Object data) {
        this.data = data;
        this.status = status;
    }



}

