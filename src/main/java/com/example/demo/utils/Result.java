package com.example.demo.utils;

import com.example.demo.enums.ResponseCode;

import java.io.Serializable;

public class Result<T> implements Serializable {

    private int status;
    private String msg;
    private T data;

    private Result(int status) {
        this.status = status;
    }

    private Result(int status, T data) {
        this.status = status;
        this.data = data;
    }

    private Result(int status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    private Result(int status, String msg, T data) {
        this.status = status;
        this.msg = msg;
        this.data = data;
    }

    public boolean isSuccess() {
        return this.status == ResponseCode.SUCCESS.getCode();
    }

    public int getStatus() {
        return status;
    }

    public T getData() {
        return data;
    }

    public String getMsg() {
        return msg;
    }

    public static <T> Result<T> createBySuccess() {
        return new
                Result<T>(ResponseCode.SUCCESS.getCode());
    }

    public static <T> Result<T> createBySuccess(T data) {
        return new
                Result<T>(ResponseCode.SUCCESS.getCode(), data);
    }

    public static <T> Result<T>
    createBySuccessMessage(String msg) {
        return new
                Result<T>(ResponseCode.SUCCESS.getCode(), msg);
    }

    public static <T> Result<T>
    createBySuccessCodeMessage(String msg, T data) {
        return new Result<T>
                (ResponseCode.SUCCESS.getCode(), msg, data)
                ;
    }

    public static <T> Result<T> createByError() {
        return new Result<T>
                (ResponseCode.ERROR.getCode(), ResponseCode.ERROR.getDesc())
                ;
    }

    public static <T> Result<T> createByErrorMessage
            (String errorMessage) {
        return new
                Result<T>
                (ResponseCode.ERROR.getCode(), errorMessage);
    }

    public static <T> Result<T> createByErrorCodeMessage
            (int erroCode, String errorMessage) {
        return new Result<T>(erroCode, errorMessage);
    }
}