package org.clm.util;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.clm.Pojo.User;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.type.JavaType;
import org.codehaus.jackson.type.TypeReference;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**jackson-mapper-asl 1.9.12
 * @author Ccc
 * @date 2018/10/9 0009 下午 9:10
 */
public class JsonUtil {

    private static ObjectMapper objectMapper = new ObjectMapper();

    /**初始化objectMapper*/
    static {
        /**对象所有字段全部列入*/
        objectMapper.setSerializationInclusion(JsonSerialize.Inclusion.ALWAYS);
        /**取消默认转换timestamps形式*/
        objectMapper.configure(SerializationConfig.Feature.WRITE_DATES_AS_TIMESTAMPS,false);
        /**忽略空bean转json错误*/
        objectMapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS,false);
        /**日期统一为yyyy-MM-dd HH:mm:ss样式*/
        objectMapper.setDateFormat(new SimpleDateFormat(DateTimeUtil.STANDARD_FORMAT));
        /**忽略在json字符串中存在，但在java对象中不存在对应属性的情况，防止错误 */
        objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES,false);
    }

    /**
     * java对象转字符串
     * @param obj
     * @param <T>
     * @return
     */
    public static <T> String objToString(T obj){
        if (obj==null){
            return null;
        }
        try {
            return obj instanceof String ? (String)obj : objectMapper.writeValueAsString(obj);
        } catch (IOException e) {
            return null;
        }
    }

    public static <T> String objToStringPretty(T obj){
        if (obj==null){
            return null;
        }
        try {
            return obj instanceof String ? (String)obj : objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (IOException e) {
            return null;
        }
    }

    /**
     *
     * @param str
     * @param clazz <T> 限制约束Class
     * @param <T> 泛型方法
     * @return 返回值泛型
     */
    public static <T> T StringToObj(String str,Class<T> clazz){
        if(StringUtils.isEmpty(str)||clazz == null){
            return null;
        }
        try {
            return clazz.equals(String.class)? (T) str : objectMapper.readValue(str,clazz);
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * typeReference泛型传递
     * @param str
     * @param typeReference
     * @param <T>
     * @return
     */
    public static <T> T StringToObj(String str, TypeReference<T> typeReference){
        if (StringUtils.isEmpty(str)||typeReference==null){
            return null;
        }
        try {
            return (T) (typeReference.equals(String.class)?str:objectMapper.readValue(str,typeReference));
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * 组合泛型传递
     * @param str
     * @param collectionClass
     * @param elementClasses
     * @param <T>
     * @return
     */
    public static <T> T StringToObj(String str,Class<?> collectionClass,Class<?>... elementClasses ){
        JavaType javaType = objectMapper.getTypeFactory().constructParametricType(collectionClass,elementClasses);
        try {
            return objectMapper.readValue(str,javaType);
        } catch (IOException e) {
            return null;
        }
    }


    public static void main(String[] args) {
        User u1 = new User();
        u1.setId(1);
        u1.setUsername("ccc");
        u1.setCreateTime(new Date());
        String s = JsonUtil.objToStringPretty(u1);
        System.out.println(s);
        /*User u2 = new User();
        u2.setId(2);
        u2.setUsername("ccc2");

        String u1Json = JsonUtil.objToString(u1);
        List<User> userList = new ArrayList<>();
        userList.add(u1);
        userList.add(u2);
        String ulist = JsonUtil.objToStringPretty(userList);
        System.out.println(ulist);*/

      /*  List<User> userList1 = JsonUtil.StringToObj(ulist, new TypeReference<List<User>>() {
        });
        User user1 = JsonUtil.StringToObj(u1Json, new TypeReference<User>() {
        });
        List<User> o = Lists.newArrayList()

        User user = JsonUtil.StringToObj(u1Json, User.class);

        String u1JsonPretty = JsonUtil.objToStringPretty(u1);*/
    }
}
