
package com.arsenal.framework.model.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Response bean for errors, refer and simplify from JSON:API specification.
 *
 * @author Gordon.Gan
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArsenalErrorResponse {
    // In fact we have only one error always.
    private List<ArsenalErrorBean> errors;
}
