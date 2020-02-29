package com.debug.steadyjack.mapper;


import com.debug.steadyjack.entity.CrmOrder;
import org.apache.ibatis.annotations.Param;

public interface CrmOrderMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(CrmOrder record);

    int insertSelective(CrmOrder record);

    CrmOrder selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(CrmOrder record);

    int updateByPrimaryKey(CrmOrder record);

    int countByMobile(@Param("mobile") String mobile);
}