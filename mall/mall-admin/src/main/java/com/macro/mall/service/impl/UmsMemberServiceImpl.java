package com.macro.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.macro.mall.dao.UmsMemberDao;
import com.macro.mall.model.UmsMember;
import com.macro.mall.service.UmsMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 会员管理Service实现类
 * Created by macro on 2024/04/10.
 */
@Service
public class UmsMemberServiceImpl implements UmsMemberService {
    @Autowired
    private UmsMemberDao memberDao;

    @Override
    public List<UmsMember> listByProductId(Long productId, Integer pageSize, Integer pageNum) {
        PageHelper.startPage(pageNum, pageSize);
        return memberDao.getMemberListByProductId(productId);
    }
}
