package org.clm.Controller.backend;

import org.apache.commons.lang3.StringUtils;
import org.clm.Pojo.Category;
import org.clm.Service.ICategoryManageService;
import org.clm.Service.IUserService;
import org.clm.common.ServiceResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * @author Ccc
 * @date 2018/9/27 0027 下午 10:37
 */
@RestController
@RequestMapping("/manage/category")
public class CategoryManagerController {

    @Autowired
    private IUserService iUserService;

    @Autowired
    private ICategoryManageService iCategoryManageService;

    @RequestMapping(value = "/get_category.do",method = RequestMethod.GET)
    public ServiceResponse<List<Category>> getCategoryById(@RequestParam(value = "categoryId",defaultValue = "0") Integer categoryId){
        if(categoryId==null){
            return ServiceResponse.createByErrorMessage("参数错误");
        }
        return iCategoryManageService.getCategory(categoryId);
    }

    @RequestMapping(value = "/add_category.do",method = RequestMethod.GET)
    public ServiceResponse addCategory(HttpServletRequest request, String categoryName, @RequestParam(value = "parentId",defaultValue = "0") Integer parentId){
            return iCategoryManageService.addCategory(categoryName,parentId);
    }

    @RequestMapping(value = "/set_category_name.do",method = RequestMethod.GET)
    public ServiceResponse setCategory(HttpServletRequest request,String categoryName,
                                       @RequestParam(value = "categoryId",defaultValue = "0")Integer categoryId){
            return iCategoryManageService.setCategoryName(categoryName,categoryId);
    }

    @RequestMapping(value = "/get_deep_category.do",method = RequestMethod.GET)
    public ServiceResponse getChilerCategoryId(HttpServletRequest request,
                                               @RequestParam(value = "categoryId",defaultValue = "0") Integer categoryId){
            return iCategoryManageService.getAllChildCategoryById(categoryId);
    }

}
