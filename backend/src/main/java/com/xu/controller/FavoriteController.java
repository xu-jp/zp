package com.xu.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xu.annotation.RequireRole;
import com.xu.dto.FavoriteDTO;
import com.xu.common.Result;
import com.xu.service.FavoriteService;
import com.xu.vo.FavoriteVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/favorite")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    @PostMapping("/add")
    @RequireRole(1)
    public Result<Void> addFavorite(@RequestBody FavoriteDTO dto, @RequestAttribute Long userId) {
        favoriteService.addFavorite(dto, userId);
        return Result.success();
    }

    @DeleteMapping("/remove/{jobId}")
    @RequireRole(1)
    public Result<Void> removeFavorite(@PathVariable Long jobId, @RequestAttribute Long userId) {
        favoriteService.removeFavorite(jobId, userId);
        return Result.success();
    }

    @PutMapping("/remark/{id}")
    @RequireRole(1)
    public Result<Void> updateRemark(
            @PathVariable Long id,
            @RequestParam String remark,
            @RequestAttribute Long userId) {
        favoriteService.updateRemark(id, remark, userId);
        return Result.success();
    }

    @GetMapping("/list")
    @RequireRole(1)
    public Result<Page<FavoriteVO>> getUserFavorites(
            @RequestAttribute Long userId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<FavoriteVO> page = favoriteService.getUserFavorites(userId, pageNum, pageSize);
        return Result.success(page);
    }

    @GetMapping("/check/{jobId}")
    @RequireRole(1)
    public Result<Boolean> isFavorited(@PathVariable Long jobId, @RequestAttribute Long userId) {
        boolean favorited = favoriteService.isFavorited(jobId, userId);
        return Result.success(favorited);
    }
}
