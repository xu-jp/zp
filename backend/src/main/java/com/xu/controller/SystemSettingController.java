package com.xu.controller;

import com.xu.annotation.RequireRole;
import com.xu.common.Result;
import com.xu.dto.SystemSettingDTO;
import com.xu.service.SystemSettingService;
import com.xu.vo.SystemSettingVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/settings")
@RequiredArgsConstructor
public class SystemSettingController {

    private final SystemSettingService systemSettingService;

    @GetMapping
    @RequireRole(3)
    public Result<List<SystemSettingVO>> getAllSettings() {
        List<SystemSettingVO> list = systemSettingService.getAllSettings();
        return Result.success(list);
    }

    @GetMapping("/{key}")
    @RequireRole(3)
    public Result<SystemSettingVO> getSettingByKey(@PathVariable String key) {
        SystemSettingVO vo = systemSettingService.getSettingByKey(key);
        return Result.success(vo);
    }

    @PutMapping
    @RequireRole(3)
    public Result<Void> updateSetting(@RequestBody SystemSettingDTO dto) {
        systemSettingService.updateSetting(dto);
        return Result.success();
    }

    @PutMapping("/batch")
    @RequireRole(3)
    public Result<Void> updateSettings(@RequestBody List<SystemSettingDTO> dtoList) {
        systemSettingService.updateSettings(dtoList);
        return Result.success();
    }
}
