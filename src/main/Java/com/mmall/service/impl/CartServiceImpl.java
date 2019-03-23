package com.mmall.service.impl;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CartMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Cart;
import com.mmall.pojo.Product;
import com.mmall.service.ICartService;
import com.mmall.util.BigDecimalUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.CartProductVo;
import com.mmall.vo.CartVo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Classname CartServiceImpl
 * @Description
 * @Date 2019/3/21 10:59
 * @Created by godFather
 */
@Service("iCartService")
public class CartServiceImpl implements ICartService {

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private ProductMapper productMapper;

    public ServerResponse<CartVo> list(Integer userId, String msg) {
        if (StringUtils.isBlank(msg)) {
            return ServerResponse.createBySuccess(this.getCartVoLimit(userId));
        }
        return ServerResponse.createBySuccess(msg, this.getCartVoLimit(userId));
    }

    public ServerResponse<CartVo> add(Integer userId, Integer productId, Integer count) {
        if (productId == null || count == null)
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        Cart cart = cartMapper.selectCartByUserIdProductId(userId, productId);

        int rowCount;
        if (cart == null) {
            //这个产品不再购物车里
            //需要新增这个产品的记录
            Cart cartItem = new Cart();
            cartItem.setQuantity(count);
            cartItem.setChecked(Const.Cart.CHECKED);
            cartItem.setProductId(productId);
            cartItem.setUserId(userId);
            rowCount = cartMapper.insert(cartItem);
            if (rowCount == 0) {
                return this.list(userId, "添加产品失败");
            } else {
                return this.list(userId, "添加产品成功");
            }
        } else {
            //这个产品已经在购物车
            //如果产品已存在，数量相加
            count = cart.getQuantity() + count;
            cart.setQuantity(count);
            rowCount = cartMapper.updateByPrimaryKeySelective(cart);
            if (rowCount == 0) {
                return this.list(userId, "更新产品失败");
            } else {
                return this.list(userId, "更新产品成功");
            }
        }
    }

    public ServerResponse<CartVo> update(Integer userId, Integer productId, Integer count) {
        if (productId == null || count == null)
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());

        Cart cart = cartMapper.selectCartByUserIdProductId(userId, productId);
        if (cart != null) {
            cart.setQuantity(count);
        }
        int rowCount = cartMapper.updateByPrimaryKeySelective(cart);
        if (rowCount == 0) {
            return this.list(userId, "更新产品失败");
        }
        return this.list(userId, "更新产品成功");
    }

    public ServerResponse<CartVo> deleteProduct(Integer userId, String productIds) {
        List<String> productList = Splitter.on(",").splitToList(productIds);
        if (CollectionUtils.isEmpty(productList)) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        int rowCount = cartMapper.deleteByUserIdProductIds(userId, productList);
        //都给前端返回一个CartVo
        //CartVo cartVo = this.getCartVoLimit(userId);
        //return ServerResponse.createBySuccess(cartVo);
        if (rowCount == 0) {
            return this.list(userId, "删除产品失败");
        }
        return this.list(userId, "删除产品成功");
    }

    //Controller共用该Service
    public ServerResponse<CartVo> selectOrUnSelect(Integer userId, Integer checked,
                                                   Integer productId) {
        cartMapper.checkedOrUncheckedProduct(userId, checked, productId);
        return this.list(userId, null);
    }

    public ServerResponse<Integer> getCartProductCount(Integer userId) {
        return ServerResponse.createBySuccess(cartMapper.selectCartProductCount(userId));
    }

    //非常关键的一个方法
    private CartVo getCartVoLimit(Integer userId) {
        CartVo cartVo = new CartVo();
        List<Cart> cartList = cartMapper.selectCartByUserId(userId);

        List<CartProductVo> cartProductVoList = Lists.newArrayList();
        BigDecimal cartTotalPrice = new BigDecimal("0");

        if (CollectionUtils.isNotEmpty(cartList)) {
            for (Cart cartItem : cartList) {
                CartProductVo cartProductVo = new CartProductVo();
                cartProductVo.setId(cartItem.getId());
                cartProductVo.setUserId(cartItem.getUserId());
                cartProductVo.setProductId(cartItem.getProductId());

                Product product = productMapper.selectByPrimaryKey(cartItem.getProductId());
                if (product != null) {
                    cartProductVo.setProductMainImage(product.getMainImage());
                    cartProductVo.setProductName(product.getName());
                    cartProductVo.setProductSubTitle(product.getSubtitle());
                    cartProductVo.setProductStatus(product.getStatus());
                    cartProductVo.setProductPrice(product.getPrice());
                    cartProductVo.setProductStock(product.getStock());

                    //判断库存
                    int buyLimitCount;
                    if (product.getStock() >= cartItem.getQuantity()) {
                        //库存充足的时候
                        buyLimitCount = cartItem.getQuantity();
                        cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_SUCCESS);
                    } else {
                        buyLimitCount = product.getStock();
                        cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_FAIL);
                        //购物车中更新有效库存
                        Cart cartQuantity = new Cart();
                        cartQuantity.setId(cartItem.getId());
                        cartQuantity.setQuantity(buyLimitCount);
                        cartMapper.updateByPrimaryKeySelective(cartQuantity);
                    }
                    cartProductVo.setQuantity(buyLimitCount);
                    //计算总价
                    cartProductVo.setProductTotalPrice(BigDecimalUtil.mul(product.getPrice().doubleValue(),
                            buyLimitCount));
                    cartProductVo.setProductChecked(cartItem.getChecked());
                }

                if (cartItem.getChecked() == Const.Cart.CHECKED) {
                    //如果已经勾选，增加到整个的购物车总价中
                    cartTotalPrice = BigDecimalUtil.add(cartTotalPrice.doubleValue(),
                            cartProductVo.getProductTotalPrice().doubleValue());
                }
                cartProductVoList.add(cartProductVo);
            }
        }
        cartVo.setCartTotalPrice(cartTotalPrice);
        cartVo.setCartProductVoList(cartProductVoList);
        cartVo.setAllChecked(this.getAllCheckedStatus(userId));
        cartVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));
        return cartVo;
    }

    private boolean getAllCheckedStatus(Integer userId) {
        if (userId == null) {
            return false;
        }
        // 等于0，说明没有未被勾选的，也就是全选状态
        return cartMapper.selectCartProductCheckedStatusByUserId(userId) == 0;
    }
}
