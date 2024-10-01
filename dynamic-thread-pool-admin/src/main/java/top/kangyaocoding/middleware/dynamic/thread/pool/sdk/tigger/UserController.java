package top.kangyaocoding.middleware.dynamic.thread.pool.sdk.tigger;

import org.springframework.web.bind.annotation.*;
import top.kangyaocoding.middleware.dynamic.thread.pool.sdk.model.dto.LoginFormDTO;
import top.kangyaocoding.middleware.dynamic.thread.pool.sdk.types.enums.ResponseEnum;
import top.kangyaocoding.middleware.dynamic.thread.pool.sdk.service.IUserService;
import top.kangyaocoding.middleware.dynamic.thread.pool.sdk.types.model.Response;

/**
 * 描述: 用户控制器
 *
 * @author K·Herbert
 * @since 2024-09-21 13:07
 */

@RestController()
@CrossOrigin(origins = "${app.config.cross-origin}")
@RequestMapping("/api/${app.config.api-version}/auth/")
public class UserController {

    private final IUserService userService;

    public UserController(IUserService userService) {
        this.userService = userService;
    }

    @RequestMapping(value = "login", method = RequestMethod.POST)
    public Response<String> login(@RequestBody LoginFormDTO loginFormDTO) {

        if (loginFormDTO == null) {
            return Response.<String>builder()
                    .code(ResponseEnum.ILLEGAL_PARAMETER.getCode())
                    .info(ResponseEnum.ILLEGAL_PARAMETER.getInfo())
                    .build();
        }

        String username = loginFormDTO.getUsername();
        String password = loginFormDTO.getPassword();

        return userService.login(username, password);
    }

    // 测试
    @RequestMapping(value = "test", method = RequestMethod.GET)
    public Response<String> test() {
        return Response.<String>builder()
                .code(ResponseEnum.SUCCESS.getCode())
                .info(ResponseEnum.SUCCESS.getInfo())
                .data("test")
                .build();
    }
}
