package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.Category;

import java.util.List;

/**
 * @Interfacename ICategoryService
 * @Description
 * @Date 2019/3/19 10:14
 * @Created by godFather
 */
public interface ICategoryService {

    ServerResponse addCategory(String categoryName, Integer parenId);

    ServerResponse updateCategoryName(Integer categoryId,
                                      String categoryName);

    ServerResponse<List<Category>> getChildrenParallelCategory(Integer categoryId);

    ServerResponse<List<Integer>> getCategoryAndDeepChildrenCategory(Integer categoryId);
}
