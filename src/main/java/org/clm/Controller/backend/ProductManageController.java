package org.clm.Controller.backend;

import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.clm.Pojo.Product;
import org.clm.Service.IFileService;
import org.clm.Service.IProductManageService;
import org.clm.Service.IUserService;
import org.clm.VO.ProductDetailVo;
import org.clm.common.ServiceResponse;
import org.clm.util.FTPUtil;
import org.clm.util.PropertiesUtil;
import org.clm.util.RedisTemplateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
    private IProductManageService iProductManageService;
    @Autowired
    private IUserService iUserService;



    @RequestMapping(value = "/list.do",method = RequestMethod.GET)
    public ServiceResponse<PageInfo> manageGetProductList(HttpServletRequest request,
                                                    @RequestParam(value = "pageNum",defaultValue = "1")Integer pageNum,
                                                    @RequestParam(value = "pageSize",defaultValue = "10")Integer pageSize){
            return iProductManageService.selectAllProduct(pageNum,pageSize);
    }

    @RequestMapping(value = "/search.do",method = RequestMethod.GET)
    public ServiceResponse<PageInfo> manageSearchProductByIdandName(HttpServletRequest request,String productName, Integer productId,
                                                    @RequestParam(value = "pageNum",defaultValue = "1")Integer pageNum,
                                                    @RequestParam(value = "pageSize",defaultValue = "10")Integer pageSize){
            return iProductManageService.searchProductByIdAndName(productName,productId,pageNum,pageSize);
    }
    @RequestMapping(value = "/save.do",method = RequestMethod.GET)
    public ServiceResponse<Product> manageSaveProduct(HttpServletRequest request, Product product){
        if (product==null){
            return ServiceResponse.createByErrorMessage("参数错误");
        }
            return iProductManageService.saveOrUpdateProduct(product);
    }


    @RequestMapping("/set_sale_status.do")
    public ServiceResponse managesetSaleStatus(HttpServletRequest request, Integer productId,Integer status){
            return iProductManageService.setSaleStatus(productId,status);
    }

    @RequestMapping("/detail.do")
    public ServiceResponse<ProductDetailVo> managegetDetail(HttpServletRequest request, Integer productId){
            return iProductManageService.geDetail(productId);

    }


    @RequestMapping(value = "/upload.do",method = RequestMethod.POST)
    public ServiceResponse manageupload(@RequestParam(value = "upload_file",required = false) MultipartFile file, HttpServletRequest request){
            //获取应用的绝对路径
            String path = request.getSession().getServletContext().getRealPath("upload");
            String targetFileName = iFileService.upload(file,path);
            String url = PropertiesUtil.getProperty("ftp.server.http.prefix")+targetFileName;

            Map<String,String> filemap = new HashMap<>();
            filemap.put("uri",targetFileName);
            filemap.put("url",url);
            return ServiceResponse.createBySucces(filemap);
    }

    @RequestMapping("/richtext_img_upload.do")
    public Map managerichtextImgUpload(HttpServletRequest request, @RequestParam(value = "upload_file",required = false) MultipartFile file,  HttpServletResponse response){
        Map map = new HashMap();
        ServiceResponse serviceResponse = iUserService.checkAdminRole(request);

        if (serviceResponse.isSuccess()){
            String path = request.getSession().getServletContext().getRealPath("upload");
            String targetFileName = iFileService.upload(file,path);
            if (StringUtils.isBlank(targetFileName)){
                map.put("msg","上传失败");
                map.put("success",false);
            }
            map.put("file_path",PropertiesUtil.getProperty("ftp.server.http.prefix")+targetFileName);
            map.put("msg","上传成功");
            map.put("success",true);
            //
            response.addHeader("Access-Control-Allow-Headers","X-File-Name");
            return map;
        }
        map.put("msg","无权限");
        map.put("success",false);
        return map;
    }
}
