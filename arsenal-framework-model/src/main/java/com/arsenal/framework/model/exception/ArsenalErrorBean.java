
package com.arsenal.framework.model.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Error bean used in json rpc.
 *
 * @author Gordon.Gan
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArsenalErrorBean {
    public static final String REQUEST_ID_FIELD = "requestId";
    public static final String SOURCE_FIELD = "source";
    public static final String SEVERITY_FIELD = "severity";

    public static final String LEVEL_ERROR = "ERROR";
    public static final String WARN_ERROR = "WARN";

    private String code;
    private String detail;
    private Map<String, Object> meta;
}
