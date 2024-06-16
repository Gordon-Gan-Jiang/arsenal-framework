package com.arsenal.framework.model;

import org.apache.hc.core5.http.ContentType;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @author Gordon.Gan
 */
public class ArsenalConstant {
    public static final String DES_KEY_PROPERTY = "com.arsenal.config.des-key";

    public static final int DEFAULT_PORT = 8080;

    public static final String UTF8_ENCODING = "UTF-8";
    public static final String GBK_ENCODING = "GBK";

    public static final Charset UTF8_CHARSET = StandardCharsets.UTF_8;
    public static final Charset GBK_CHARSET = Charset.forName(GBK_ENCODING);

    public static final ContentType APPLICATION_JSON_UTF8 = ContentType.APPLICATION_JSON;
    public static final ContentType TEXT_PLAIN_UTF8 = ContentType.TEXT_PLAIN.withCharset(UTF8_CHARSET);

    public static final String APPLICATION_JSON_UTF8_TEXT = APPLICATION_JSON_UTF8.toString();
    public static final String TEXT_PLAIN_UTF8_TEXT = TEXT_PLAIN_UTF8.toString();

    public static final String HTTP_PROTOCOL = "http://";
    public static final String HTTPS_PROTOCOL = "https://";
    public static final String HTTP_SLASH = "/";

    // Http headers.
    public static final String PROXY_FORWARDED_HEADER = "X-Alo7-Forwarded-For";
    public static final String X_ALO7_REQUEST_ID_HEADER = "X-Alo7-Request-Id";
    public static final String X_REQUEST_ID_HEADER = "X-Request-Id";
    public static final String X_ALO7_FRAMEWORK_HEADER = "X-Alo7-Framework";

    // Md5 file configuration.
    public static final String SPLIT_OF_MD5_FILE = "  ";
    public static final String MD5_SUFFIX = ".md5";
    public static final String BACKUP_SUFFIX = ".bak";
    public static final String TMP_SUFFIX = ".tmp";

    public static final String ZIP_POSTFIX = ".zip";
    public static final String CSV_POSTFIX = ".csv";
    public static final String SQL_POSTFIX = ".sql";

    public static final int HIGHEST_ORDER = Integer.MAX_VALUE;
    public static final int LOWEST_ORDER = Integer.MIN_VALUE;

    // application configuration properties.
    public static final String PROFILE_PROPERTY_NAME = "spring.profiles.active";


}
