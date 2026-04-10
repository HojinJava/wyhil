package com.macro.mall.dao;

import com.macro.mall.model.UmsMember;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 自定义会员管理Dao
 * Created by macro on 2024/04/10.
 */
public interface UmsMemberDao {
    /**
     * 根据产品ID查询购买过该产品的会员
     */
    List<UmsMember> getMemberListByProductId(@Param("productId") Long productId);
}
