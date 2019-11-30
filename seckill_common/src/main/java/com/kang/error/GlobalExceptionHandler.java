package com.kang.error;

import com.alibaba.fastjson.JSONObject;
import com.kang.base.BaseApiService;
import com.kang.base.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.NoHandlerFoundException;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends BaseApiService<JSONObject> {
    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseBody
    public BaseResponse<JSONObject> noHandlerFoundException(Exception e) {
        log.debug("###全局捕获异常###,error:{}", e);
        return setResultError(404, e.getMessage());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseBody
    public BaseResponse<JSONObject> httpRequestMethodNotSupportedException(Exception e) {
        log.debug("###全局捕获异常###,error:{}", e);
        return setResultError(405, e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public BaseResponse<JSONObject> exceptionHandler(Exception e) {
        log.debug("###全局捕获异常###,error:{}", e);
        return setResultError(500, "服务器内部错误：" + e.getMessage());
    }
}
