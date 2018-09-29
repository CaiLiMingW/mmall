package org.clm.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.Serializable;

/**
 * @author Ccc
 * @date 2018/9/27 0027 上午 8:38
 *
 * @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
 * @JsonInclude(JsonInclude.Include.NON_NULL)
 * 序列化json时,Null的对象，Key也会消失
 */

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class ServiceResponse<T> implements Serializable {

    private int status;
    private String msg;
    private  T data;

    /**
     * 私有化构造器
     * @param status
     */
    private ServiceResponse(int status){
        this.status = status;
    }

    private ServiceResponse(int status, T data) {
        this.status = status;
        this.data = data;
    }

    private ServiceResponse(int status, String msg, T data) {
        this.status = status;
        this.msg = msg;
        this.data = data;
    }

    private ServiceResponse(int status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    /**不在Json序列化结果当中 @JsonIgnore*/
    @JsonIgnore
    public boolean isSuccess(){
        return this.status==ResponseCode.SUCCESS.getCode();
    }

    public int getStatus() {
        return status;
    }

    public String getMsg() {
        return msg;
    }

    public T getData() {
        return data;
    }

    /**创建ServiceResponse*/
    public static <T> ServiceResponse<T> createBySuccess(){
        return new ServiceResponse<T>(ResponseCode.SUCCESS.getCode());
    }
    /**创建ServiceResponse,设置msg*/
    public static <T> ServiceResponse<T> createBySuccessMessage(String msg){
        return new ServiceResponse<T>(ResponseCode.SUCCESS.getCode(),msg);
    }
    /**创建ServiceResponse，设置data信息*/
    public static <T> ServiceResponse<T> createBySucces(T data){
        return new ServiceResponse<T>(ResponseCode.SUCCESS.getCode(),data);
    }
    /**创建ServiceResponse设置data信息和msg*/
    public static <T> ServiceResponse<T> createBySuccess(String msg,T data){
        return new ServiceResponse<T>(ResponseCode.SUCCESS.getCode(),msg,data);
    }

    public static <T> ServiceResponse<T> createByError(){
        return new ServiceResponse<T>(ResponseCode.ERROR.getCode());
    }

    public static <T> ServiceResponse<T> createByErrorMessage(String errorMessage){
        return new ServiceResponse<T>(ResponseCode.ERROR.getCode(),errorMessage);
    }
    /**创建ServiceResponse设置data信息和msg*/
    public static <T> ServiceResponse<T> createByCodeError(int status,String errorMessage){
        return new ServiceResponse<T>(status,errorMessage);
    }
}
