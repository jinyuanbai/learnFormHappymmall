package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.vo.CartVo;

/**
 * @Classname ICartService
 * @Description
 * @Date 2019/3/21 10:59
 * @Created by godFather
 */
public interface ICartService {
    ServerResponse<CartVo> list(Integer userId,String msg);

    ServerResponse<CartVo> add(Integer userId, Integer productId, Integer count);

    ServerResponse<CartVo> update(Integer userId, Integer productId, Integer count);

    ServerResponse<CartVo> deleteProduct(Integer userId, String productIds);

    ServerResponse<CartVo> selectOrUnSelect(Integer userId, Integer checked,
                                            Integer productId);

    ServerResponse<Integer> getCartProductCount(Integer userId);
}
