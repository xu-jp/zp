package com.xu.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xu.dto.FavoriteDTO;
import com.xu.entity.Favorite;
import com.xu.vo.FavoriteVO;

public interface FavoriteService {

    void addFavorite(FavoriteDTO dto, Long userId);

    void removeFavorite(Long jobId, Long userId);

    void updateRemark(Long id, String remark, Long userId);

    Page<FavoriteVO> getUserFavorites(Long userId, Integer pageNum, Integer pageSize);

    boolean isFavorited(Long jobId, Long userId);

    long count(LambdaQueryWrapper<Favorite> wrapper);
}
