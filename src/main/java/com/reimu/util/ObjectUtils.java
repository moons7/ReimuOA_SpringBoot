package com.reimu.util;


import org.nustaq.serialization.FSTConfiguration;

@SuppressWarnings("unchecked")
public class ObjectUtils extends org.apache.commons.lang3.ObjectUtils {

    private final static FSTConfiguration fstConfiguration = FSTConfiguration.createDefaultConfiguration();

    private final static FSTConfiguration getFSTInstance() {
        return fstConfiguration;
    }

    /**
     * 序列化对象
     *
     * @param object
     * @return
     */
    public static byte[] serializeToByte(Object object) {
        return getFSTInstance().asByteArray(object);
    }


    /**
     * 反序列化对象(由调用者自行转换
     *
     * @param bytes
     * @return
     */
    public static Object unSerialize(byte[] bytes) {
        if (EmptyUtils.isEmpty(bytes)) return null;
        return getFSTInstance().asObject(bytes);
    }

}
