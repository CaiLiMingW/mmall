package org.clm.Service;

import org.clm.Pojo.Category;
import org.clm.common.ServiceResponse;

import java.util.List;

/**
 * @author Ccc
 * @date 2018/9/28 0028 上午 8:12
 */
public interface ICategoryManageService {
    ServiceResponse<List<Category>> getCategory(Integer categoryId);

    ServiceResponse addCategory(String categoryName,Integer parentId);

    ServiceResponse setCategoryName(String categoryName, Integer categoryId);

    ServiceResponse getAllChildCategoryById(Integer categoryId);
}
