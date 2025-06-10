package org.wnn.bytekeep.demos.web;

import lombok.Data;
import org.wnn.bytekeep.core.validation.CreateGroup;

import javax.validation.constraints.NotBlank;

/**
 * @author NanNan Wang
 */
@Data
public class Phone {

    @NotBlank(message = "手机号码不为空",groups = {CreateGroup.class})
    private String phoneNumber;
}
