package org.clm.Controller.backend;

import com.github.pagehelper.PageInfo;
import org.clm.Pojo.Product;
import org.clm.Pojo.User;
import org.clm.Service.IFileService;
import org.clm.Service.IProductService;
import org.clm.Service.IUserService;
import org.clm.VO.ProductDetailVo;
import org.clm.common.Const;
import org.clm.common.ResponseCode;
import org.clm.common.ServiceResponse;
import org.clm.util.PropertiesUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.ws.Service;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Ccc
 * @date 2018/9/29 0029 下午 6:49
 */
@RestController
@RequestMapping("/manage/product")
public class ProductManageController {

    @Autowired
    private IFileService iFileService;
    @Autowired
    private IProductService iProductService;
    @Autowired
    private IUserService iUserService;

    @RequestMapping(value = "/list.do",method = RequestMethod.GET)
    public ServiceResponse<PageInfo> getProductList(HttpSession session,
                                                    @RequestParam(value = "pageNum",defaultValue = "1")Integer pageNum,
                                                    @RequestParam(value = "pageSize",defaultValue = "10")Integer pageSize){
        ServiceResponse response = iUserService.checkAdminRole(session);
        if (response.isSuccess()){
            return iProductService.selectAllProduct(pageNum,pageSize);
        }
        return response;
    }

    @RequestMapping(value = "/search.do",method = RequestMethod.GET)
    public ServiceResponse<PageInfo> searchProductByIdandName(HttpSession session,String productName, Integer productId,
                                                    @RequestParam(value = "pageNum",defaultValue = "1")Integer pageNum,
                                                    @RequestParam(value = "pageSize",defaultValue = "10")Integer pageSize){
        ServiceResponse response = iUserService.checkAdminRole(session);
        if (response.isSuccess()){
            return iProductService.searchProductByIdAndName(productName,productId,pageNum,pageSize);
        }
        return response;
    }
    @RequestMapping(value = "/save.do",method = RequestMethod.POST)
    public ServiceResponse<Product> saveProduct(HttpSession session, Product product){
        if (product==null){
            return ServiceResponse.createByErrorMessage("参数错误");
        }
        ServiceResponse response = iUserService.checkAdminRole(session);
        if(response.isSuccess()){
            iProductService.saveOrUpdateProduct(product);
        }
        return response;
    }


    @RequestMapping("/set_sale_status.do")
    public ServiceResponse setSaleStatus(HttpSession session, Integer productId,Integer status){
        ServiceResponse response = iUserService.checkAdminRole(session);
        if (response.isSuccess()){
            iProductService.setSaleStatus(productId,status);
        }
        return response;
    }

    @RequestMapping("/detail.do")
    public ServiceResponse<ProductDetailVo> getDetail(HttpSession session, Integer productId){
        ServiceResponse response = iUserService.checkAdminRole(session);
        if(response.isSuccess()){
            return iProductService.geDetail(productId);
        }
        return response;

    }


    @RequestMapping(value = "/upload.do",method = RequestMethod.POST)
    public ServiceResponse upload(@RequestParam(value = "upload_file",required = false) MultipartFile file, HttpServletRequest requset,HttpSession session){
        ServiceResponse response = iUserService.checkAdminRole(session);
        if (response.isSuccess()){
            String path = requset.getSession().getServletContext().getRealPath("upload");
            String targetFileName = iFileService.upload(file,path);
            String url = PropertiesUtil.getProperty("ftp.server.http.prefix")+targetFileName;

            Map<String,String> filemap = new HashMap<>();
            filemap.put("uri",targetFileName);
            filemap.put("url",url);
            return ServiceResponse.createBySucces(filemap);
        }
       return response;
    }

   /* @RequestMapping("richtext_img_upload.do")
    public Map richtextImgUpload(HttpSession session, @RequestParam(value = "upload_file",required = false) MultipartFile file, HttpServletRequest request, HttpServletResponse response){
        ServiceResponse response1 = iUserService.checkAdminRole(session);
        if (response1.isSuccess()){

        }
        return
    }*/
}
