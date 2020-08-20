package com.reimu.util;

import org.apache.commons.text.StringEscapeUtils;

import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class StringUtils extends org.apache.commons.lang3.StringUtils {

    private static final char SEPARATOR = '_';
    private static final String CHARSET_NAME = "UTF-8";

    /**
     * 转换为字节数组
     *
     * @param str
     * @return
     */
    public static byte[] getBytes(String str) {
        if (str != null) {
            try {
                return str.getBytes(CHARSET_NAME);
            } catch (UnsupportedEncodingException e) {
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * 转换为字节数组
     *
     * @return
     */
    public static String toString(byte[] bytes) {
        try {
            return new String(bytes, CHARSET_NAME);
        } catch (UnsupportedEncodingException e) {
            return EMPTY;
        }
    }

    /**
     * 是否包含字符串
     *
     * @param str  验证字符串
     * @param strs 字符串组
     * @return 包含返回true
     */
    public static boolean inString(String str, String... strs) {
        if (str != null) {
            for (String s : strs) {
                if (str.equals(trim(s))) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 替换掉HTML标签方法
     */
    public static String replaceHtml(String html) {
        if (isBlank(html)) {
            return "";
        }
        String regEx = "<.+?>";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(html);
        String s = m.replaceAll("");
        return s;
    }


    /**
     * 缩略字符串（不区分中英文字符）
     *
     * @param str    目标字符串
     * @param length 截取长度
     * @return
     */
    public static String abbr(String str, int length) {
        if (str == null) {
            return "";
        }
        try {
            StringBuilder sb = new StringBuilder();
            int currentLength = 0;
            for (char c : replaceHtml(StringEscapeUtils.unescapeHtml4(str)).toCharArray()) {
                currentLength += String.valueOf(c).getBytes("GBK").length;
                if (currentLength <= length - 3) {
                    sb.append(c);
                } else {
                    sb.append("...");
                    break;
                }
            }
            return sb.toString();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }


    /**
     * 转换为Double类型
     */
    public static Double toDouble(Object val) {
        if (val == null) {
            return 0D;
        }
        try {
            return Double.valueOf(trim(val.toString()));
        } catch (Exception e) {
            return 0D;
        }
    }

    /**
     * 转换为Float类型
     */
    public static Float toFloat(Object val) {
        return toDouble(val).floatValue();
    }

    /**
     * 转换为Long类型
     */
    public static Long toLong(Object val) {
        return toDouble(val).longValue();
    }

    /**
     * 转换为Integer类型
     */
    public static Integer toInteger(Object val) {
        return toLong(val).intValue();
    }


    /**
     * 驼峰命名法工具
     *
     * @return toCamelCase("hello_world") == "helloWorld"
     * toCapitalizeCamelCase("hello_world") == "HelloWorld"
     * toUnderScoreCase("helloWorld") = "hello_world"
     */
    public static String toCamelCase(String s) {
        if (s == null) {
            return null;
        }

        s = s.toLowerCase();

        StringBuilder sb = new StringBuilder(s.length());
        boolean upperCase = false;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);

            if (c == SEPARATOR) {
                upperCase = true;
            } else if (upperCase) {
                sb.append(Character.toUpperCase(c));
                upperCase = false;
            } else {
                sb.append(c);
            }
        }

        return sb.toString();
    }

    /**
     * 驼峰命名法工具
     *
     * @return toCamelCase("hello_world") == "helloWorld"
     * toCapitalizeCamelCase("hello_world") == "HelloWorld"
     * toUnderScoreCase("helloWorld") = "hello_world"
     */
    public static String toCapitalizeCamelCase(String s) {
        if (s == null) {
            return null;
        }
        s = toCamelCase(s);
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

    /**
     * 驼峰命名法工具
     *
     * @return toCamelCase("hello_world") == "helloWorld"
     * toCapitalizeCamelCase("hello_world") == "HelloWorld"
     * toUnderScoreCase("helloWorld") = "hello_world"
     */
    public static String toUnderScoreCase(String s) {
        if (s == null) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        boolean upperCase = false;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);

            boolean nextUpperCase = true;

            if (i < (s.length() - 1)) {
                nextUpperCase = Character.isUpperCase(s.charAt(i + 1));
            }

            if ((i > 0) && Character.isUpperCase(c)) {
                if (!upperCase || !nextUpperCase) {
                    sb.append(SEPARATOR);
                }
                upperCase = true;
            } else {
                upperCase = false;
            }

            sb.append(Character.toLowerCase(c));
        }

        return sb.toString();
    }



    /**
     * 以某个字符串作为分隔符 ,分割指定字符串得到字符串数组
     *
     * @param splitStr   分隔字符串
     * @param splitedStr 被分割的字符串
     * @return
     */
    public static String[] splitString(String splitStr, String splitedStr) {
        String[] str;
        str = splitedStr.split(splitStr);
        return str;
    }

    /**
     * 以某个字符串作为分隔符 ,分割指定字符串得到字符串数组,并在字符数组中根据字符串名取到它的值（tn=asdfgh）
     *
     * @param splitStr   分隔字符串
     * @param splitedStr 被分割的字符串
     * @param strName    字符串名(如tn)
     * @return
     */
    public static String getValueBySplitString(String splitStr,
                                               String splitedStr, String strName) {
        String value = "";
        String[] str;

        str = splitedStr.split(splitStr);
        for (String string : str) {
            if (string.startsWith(strName)) {
                value = string;
            }
        }

        value = value.substring(strName.length() + 1);

        return value;
    }


    /**
     * 对字符串去头或去尾处理
     *
     * @param str         字符串
     * @param truncateStr 开头或结尾的字符
     * @return
     */
    public static String truncateString(String str, String truncateStr) {
        if (str.startsWith(truncateStr)) {
            str = str.substring(1);
        }

        if (str.endsWith(truncateStr)) {
            int len = str.length();
            str = str.substring(0, len - 1);
        }

        return str;
    }


    /**
     * 过滤XSS关键字符
     *
     * @param str 待过滤字符
     */
    public static String xssEncode(String str) {
        str = StringEscapeUtils.escapeHtml4(str);
        str = StringEscapeUtils.escapeEcmaScript(str);
        str = decodeChinese(str);
        return str;
    }

    private static final Pattern reUnicode = Pattern.compile("\\\\u([0-9a-zA-Z]{4})");

    /**
     * 将字符中的中文Unicode码转义为UTF-8
     *
     * @param s
     * @return
     */
    public static String decodeChinese(String s) {
        Matcher m = reUnicode.matcher(s);
        StringBuffer sb = new StringBuffer(s.length());
        while (m.find()) {
            m.appendReplacement(sb,
                    Character.toString((char) Integer.parseInt(m.group(1), 16)));
        }
        m.appendTail(sb);
        return sb.toString();
    }


    /**
     * 适合解析结尾多一个字符的String
     *
     * @return String[]
     */
    static public String[] spiltStringforLong(String str, String truncateStr) {
        String[] authority = null;
        boolean onlyDeleteAll = false;
        if (StringUtils.isBlank(str)) // 如果为空则视为无需权限
            onlyDeleteAll = true;
        else if (StringUtils.isBlank(str.substring(0,
                str.length() - 1)))
            return null;
        if (!onlyDeleteAll) {
            try {
                authority = str.substring(0,
                        str.length() - 1).split(",");
            } catch (Exception e) {
                return null;
            }
        }
        return authority;
    }
}
