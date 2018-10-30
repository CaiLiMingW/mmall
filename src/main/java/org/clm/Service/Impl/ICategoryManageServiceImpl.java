package org.clm.Service.Impl;

import com.google.common.collect.Sets;
import com.mchange.v1.db.sql.ConnectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Select;
import org.clm.Dao.CategoryMapper;
import org.clm.Pojo.Category;
import org.clm.Service.ICategoryManageService;
import org.clm.common.ServiceResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author Ccc
 * @date 2018/9/28 0028 上午 8:23
 */
@Service
public class ICategoryManageServiceImpl implements ICategoryManageService {

    @Autowired
    private CategoryMapper categoryMapper;


    @Override
    public ServiceResponse<List<Category>> getCategory(Integer parentId) {
        int resulltCount = categoryMapper.checkId(parentId);
        if(resulltCount == 0){
            return ServiceResponse.createByErrorMessage("未找到该品类");
        }
        List<Category> categories = categoryMapper.selectByPrimaryKey(parentId);
        return ServiceResponse.createBySucces(categories);
    }

    @Override
    public ServiceResponse addCategory(String categoryName,Integer parentId){
        if(parentId ==null || StringUtils.isBlank(categoryName)){
            return ServiceResponse.createByErrorMessage("参数错误");
        }
        Category category = new Category();
        category.setName(categoryName);
        category.setParentId(parentId);
        category.setStatus(true);

        int rowCount = categoryMapper.insertSelective(category);
        if(rowCount>0){
            return ServiceResponse.createBySuccessMessage("添加品类成功");
        }
        return ServiceResponse.createByErrorMessage("添加品类失败");


    }

    @Override
    public ServiceResponse setCategoryName(String categoryName, Integer categoryId) {
        if(categoryId==null||StringUtils.isBlank(categoryName)){
            return ServiceResponse.createByErrorMessage("参数错误");
        }
        Category category = new Category();
        category.setName(categoryName);
        category.setId(categoryId);
        int i = categoryMapper.updateByPrimaryKeySelective(category);
        if(i>0){
            return ServiceResponse.createBySuccessMessage("更新品类名字成功");
        }
        return ServiceResponse.createByErrorMessage("更新品类失败");

    }

    @Override
    public ServiceResponse getAllChildCategoryById(Integer categoryId) {
        Set<Category> categorySet = Sets.newHashSet();
        selectx(categoryId, categorySet);
        List<Integer> list = new ArrayList<>();
        for (Category category : categorySet) {
            list.add(category.getId());
        }
        return ServiceResponse.createBySucces(list);
    }

    /**遍历，算出所有子节点*/
    private Set<Category> selectx(Integer categoryId, Set<Category> set){
        List<Category> list = categoryMapper.selectByPrimaryKey(categoryId);
        for (Category category : list) {
            if (category!=null){
                set.add(category);
            }
            selectx(category.getId(),set);
        }
        return set;
    }
}
