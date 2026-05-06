package com.xu.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xu.dto.SystemSettingDTO;
import com.xu.vo.SystemSettingVO;

import java.util.List;

public interface SystemSettingService {

    List<SystemSettingVO> getAllSettings();

    List<SystemSettingVO> getPublicSettings();

    SystemSettingVO getSettingByKey(String key);

    void updateSetting(SystemSettingDTO dto);

    void updateSettings(List<SystemSettingDTO> dtoList);
}
