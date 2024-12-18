package com.lartimes.tiktok.util;

import lombok.Data;

import java.text.MessageFormat;

/**
 * @author wüsch
 * @version 1.0
 * @description:
 * @since 2024/12/2 11:30
 */
@Data
public class R<T>{

    private static final long serialVersionUID = 22L;

    private T type;
    private int code;

    private Boolean state;

    private String message;

    private Object data;

    private long count;

    public R(){}

    // 成功
    public static R ok(){
        R responseUtils = new R();
        responseUtils.setCode(0);
        responseUtils.setState(true);
        responseUtils.setMessage("成功");
        return responseUtils;
    }

    // 失败
    public static R error(){
        R r = new R();
        r.setCode(201);
        r.setState(false);
        r.setMessage("失败");
        return r;
    }

    public R count(long count){
        this.setCount(count);
        return this;
    }
    public R code(int code){
        this.setCode(code);
        return this;
    }

    public R state(Boolean state){
        this.setState(state);
        return this;
    }

    public R message(String message){
        this.setMessage(message);
        return this;
    }
    public R message(String message, Object... objects){
        this.setMessage(MessageFormat.format(message, objects));
        return this;
    }

    public R data(Object result){
        this.setData(result);
        return this;
    }
}
