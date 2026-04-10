package com.macro.mall.service;

import com.macro.mall.model.UmsMember;

import java.util.List;

/**
 * 会员管理Service
 * Created by macro on 2024/04/10.
 */
public interface UmsMemberService {
    /**
     * 根据产品ID分页查询购买过该产品的会员
     */
    List<UmsMember> listByProductId(Long productId, Integer pageSize, Integer pageNum);
}
