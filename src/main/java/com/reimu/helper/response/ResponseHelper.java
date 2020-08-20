package com.reimu.helper.response;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.reimu.base.jackson.JacksonObjectMapperFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class ResponseHelper {

    private static final Logger logger = LoggerFactory.getLogger(ResponseHelper.class);
    private Object customObject;
    private ReturnFormat returnFormat = ReturnFormat.JSON;
    private HttpServletResponse httpServletResponse;
    private HttpDataContainer dataContainer;

    public static ResponseHelper createBody(HttpServletResponse httpServletResponse) {
        if (httpServletResponse.isCommitted()) {
            httpServletResponse.reset();
            if (logger.isWarnEnabled()) {
                logger.warn("HttpServletResponse has been reset,please check this httpServletResponse has been used?");
            }
        }
        return new ResponseHelper(httpServletResponse);
    }

    public ResponseHelper(HttpServletResponse httpServletResponse) {
        this.httpServletResponse = httpServletResponse;
    }


    public ResponseHelper setHttpStatus(int httpStatus) {
        this.httpServletResponse.setStatus(httpStatus);
        return this;
    }


    public ResponseHelper setReturnObject(Object customObject) {
        this.customObject = customObject;
        returnFormat = ReturnFormat.CUSTOM;
        return this;
    }


    public ResponseHelper setReturnFormat(ReturnFormat returnFormat) {
        this.returnFormat = returnFormat;
        return this;
    }



    public ResponseHelper setContentType(String type) {
        this.httpServletResponse.setContentType(type);
        return this;
    }

    public ResponseHelper createDataContainer(HttpDataContainer dataContainer) {
        this.dataContainer = dataContainer;
        return this;
    }

    public void printOut() {
        Object outObject = returnFormat == ReturnFormat.CUSTOM ? this.customObject : this.dataContainer;
        try {
        if (returnFormat == ReturnFormat.JSON) {
            this.httpServletResponse.setContentType("application/json;charset=utf-8");
            outObject = JacksonObjectMapperFactory.getSingleton().writeValueAsString(outObject);
        }
            this.httpServletResponse.getWriter().print(outObject);
        } catch (IOException e) {
            logger.error("httpServletResponse has a error = {}", e);
        }
    }

}
