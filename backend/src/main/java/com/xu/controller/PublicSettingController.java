package com.xu.controller;

import com.xu.common.Result;
import com.xu.service.SystemSettingService;
import com.xu.vo.SystemSettingVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/public/settings")
@RequiredArgsConstructor
public class PublicSettingController {

    private final SystemSettingService systemSettingService;

    @GetMapping
    public Result<List<SystemSettingVO>> getPublicSettings() {
        List<SystemSettingVO> list = systemSettingService.getPublicSettings();
        return Result.success(list);
    }

    @GetMapping("/{key}")
    public Result<SystemSettingVO> getPublicSettingByKey(@PathVariable String key) {
        SystemSettingVO vo = systemSettingService.getSettingByKey(key);
        if (vo != null && vo.getIsPublic() != null && vo.getIsPublic() == 1) {
            return Result.success(vo);
        }
        return Result.error("设置项不存在或不可访问");
    }
}
