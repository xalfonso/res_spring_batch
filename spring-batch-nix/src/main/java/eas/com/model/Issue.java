package eas.com.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * Issue model.
 *
 * @author Eduardo Alfonso Sanchez
 * @since 1.0.0
 */
@ToString
@Accessors(chain = true)
@Setter
@Getter
public class Issue {
    private Integer id;
    private String kee;
    private Integer ruleId;
    private String severity;
}
