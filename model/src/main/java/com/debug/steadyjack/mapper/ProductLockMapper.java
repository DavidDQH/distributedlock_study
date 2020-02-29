package com.debug.steadyjack.mapper;

import com.debug.steadyjack.entity.ProductLock;
import org.apache.ibatis.annotations.Param;

public interface ProductLockMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(ProductLock record);

    int insertSelective(ProductLock record);

    ProductLock selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ProductLock record);

    int updateByPrimaryKey(ProductLock record);

    int updateStock(ProductLock lock);

    int updateStockV1(ProductLock lock);

    int updateStockForNegative(ProductLock lock);

    ProductLock selectByPKForNegative(@Param("id") Integer id);
}