package org.wnn.bytekeep.demo.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.wnn.bytekeep.core.validation.CreateGroup;

import javax.validation.constraints.NotBlank;

/**
 * @author NanNan Wang
 */
@Data
@ApiModel(value = "手机信息")
public class Phone {

    @ApiModelProperty(value = "手机号码")
    @NotBlank(message = "手机号码不为空",groups = {CreateGroup.class})
    private String phoneNumber;
}
