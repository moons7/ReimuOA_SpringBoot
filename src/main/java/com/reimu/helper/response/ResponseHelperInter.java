package com.reimu.helper.response;


import com.reimu.base.response.HttpDataContainer;

/**
 * 生成器接口，定义创建一个输出文件对象所需的各个部件的操作
 * @author cx
 *
 */
public interface ResponseHelperInter {

    ResponseHelperInter setHttpStatus(int httpStatus);

    ResponseHelperInter setReturnObject(Object customObject);

    ResponseHelperInter setReturnFormat(ReturnFormat returnFormat);

    ResponseHelperInter setJsonPCallBackName(String name);

    ResponseHelperInter setContentType(String name);

    ResponseHelperInter createDataContainer(HttpDataContainer dataContainer);

    void printOut() ;

}

