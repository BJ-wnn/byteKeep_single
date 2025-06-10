/*
 * Copyright 2013-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wnn.bytekeep.demos.web;

import lombok.Data;
import org.wnn.bytekeep.core.validation.CreateGroup;
import org.wnn.bytekeep.core.validation.DeleteGroup;
import org.wnn.bytekeep.core.validation.UpdateGroup;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author <a href="mailto:chenxilzx1@gmail.com">theonefx</a>
 */
@Data
public class User {

    @NotNull(message = "用户名不能为空",groups = {UpdateGroup.class, DeleteGroup.class})
    private Integer id;

    @NotBlank(message = "用户名不能为空",groups = {CreateGroup.class})
    private String name;

    @NotNull(message = "年龄不能为空",groups = {CreateGroup.class})
    @Min(value = 0, message = "年龄不能小于0",groups = {CreateGroup.class})
    private Integer age;

    @Valid
    @NotNull(message = "手机信息不能为空",groups = {CreateGroup.class})
    private Phone phone;

}
