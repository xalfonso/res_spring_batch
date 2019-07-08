package eas.com.spring.batch.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Person Entity.
 *
 * @author Eduardo Alfonso Sanchez
 * @since 1.0.0
 */
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Person {

    private String firstName;

    private String secondName;

    private Integer age;

}
