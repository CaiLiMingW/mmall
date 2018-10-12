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
 * class ServiceResponse<T> :泛型类，参数类型由传入参数确定
 */

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class ServiceResponse<T> implements Serializable {

    /** status:0:成功 1:失败 10：强制登录 */
    private int status;

    /**  msg:返回消息 */
    private String msg;

    /** T：对象类型，由传入参数指定 */
    private T data;

    /** 私有化构造器 ,只能通过内部方法创建该对象，不是单例，线程安全*/
    private ServiceResponse(int status){
        this.status = status;
    }
    private ServiceResponse(){}

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
        //如果当前类status==0 返回true
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
    //static静态方法:对象可以直接引用,可以使用类名直接访问,无需new对象后再使用该方法
    //静态泛型方法:参数类型T 由传入参数T data 指定
    //通过静态方法方法创建ServiceResponse对象 并由传入的实际参数确定泛型类型，提高类的可复用性

    /**创建ServiceResponse status=0*/
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
    public static <T> ServiceResponse<T> createByError(T data){
        return new ServiceResponse<T>(ResponseCode.ERROR.getCode(),data);
    }

    public static <T> ServiceResponse<T> createByErrorMessage(String errorMessage){
        return new ServiceResponse<T>(ResponseCode.ERROR.getCode(),errorMessage);
    }
    /**创建ServiceResponse设置data信息和msg*/
    public static <T> ServiceResponse<T> createByCodeError(int status,String errorMessage){
        return new ServiceResponse<T>(status,errorMessage);
    }
}
