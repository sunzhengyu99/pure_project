package com.sunzy.common;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
/*
@ApiModel: 用于请求的方法上，表示一组响应
    (这种一般用在post创建的时候,使用@RequestBody这样的场景)
    请求参数无法使用@ApiImplicitParam注解进行描述的时候
 */
@ApiModel("API通用数据")
public class CommonResult<T>{
    /*
    标识代码 0表示成功，非0表示出错
    @ApiModelProperty：用在属性上，描述响应类的属性
     */
    @ApiModelProperty("标识代码,0表示成功,非0表示出错")
    private Integer code;

    /*
    描述信息，通常错时使用
     */
    @ApiModelProperty("错误描述")
    private String msg;
    /*
    业务数据
     */
    @ApiModelProperty("业务数据")
    private T data;

    public CommonResult(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }
    /*
    成功
     */
    public static <T> CommonResult<T> success(T data){
        return new CommonResult<>(0,"成功",data);
    }

    public static <T> CommonResult<T> success(Integer code,String msg){
        return new CommonResult<>(code,msg,null);
    }
    /*
    错误
     */
    public static <T> CommonResult<T> error(Integer code,String msg){
        return new CommonResult<>(code,msg,null);
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}

