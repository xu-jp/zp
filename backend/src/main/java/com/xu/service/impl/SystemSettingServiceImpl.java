package com.xu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.xu.dto.SystemSettingDTO;
import com.xu.entity.SystemSetting;
import com.xu.mapper.SystemSettingMapper;
import com.xu.service.SystemSettingService;
import com.xu.vo.SystemSettingVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SystemSettingServiceImpl implements SystemSettingService {

    private final SystemSettingMapper systemSettingMapper;

    @Override
    public List<SystemSettingVO> getAllSettings() {
        LambdaQueryWrapper<SystemSetting> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SystemSetting::getDeleted, 0);
        List<SystemSetting> settings = systemSettingMapper.selectList(wrapper);
        return settings.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    @Override
    public List<SystemSettingVO> getPublicSettings() {
        LambdaQueryWrapper<SystemSetting> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SystemSetting::getDeleted, 0)
                .eq(SystemSetting::getIsPublic, 1);
        List<SystemSetting> settings = systemSettingMapper.selectList(wrapper);
        return settings.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    @Override
    public SystemSettingVO getSettingByKey(String key) {
        LambdaQueryWrapper<SystemSetting> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SystemSetting::getSettingKey, key)
                .eq(SystemSetting::getDeleted, 0);
        SystemSetting setting = systemSettingMapper.selectOne(wrapper);
        return setting != null ? convertToVO(setting) : null;
    }

    @Override
    @Transactional
    public void updateSetting(SystemSettingDTO dto) {
        LambdaUpdateWrapper<SystemSetting> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(SystemSetting::getId, dto.getId())
                .set(SystemSetting::getSettingValue, dto.getSettingValue())
                .set(SystemSetting::getDescription, dto.getDescription())
                .set(dto.getIsPublic() != null, SystemSetting::getIsPublic, dto.getIsPublic());
        systemSettingMapper.update(null, updateWrapper);
    }

    @Override
    @Transactional
    public void updateSettings(List<SystemSettingDTO> dtoList) {
        for (SystemSettingDTO dto : dtoList) {
            updateSetting(dto);
        }
    }

    private SystemSettingVO convertToVO(SystemSetting setting) {
        SystemSettingVO vo = new SystemSettingVO();
        vo.setId(setting.getId());
        vo.setSettingKey(setting.getSettingKey());
        vo.setSettingValue(setting.getSettingValue());
        vo.setDescription(setting.getDescription());
        vo.setIsPublic(setting.getIsPublic());
        vo.setCreateTime(setting.getCreateTime());
        vo.setUpdateTime(setting.getUpdateTime());
        return vo;
    }
}
