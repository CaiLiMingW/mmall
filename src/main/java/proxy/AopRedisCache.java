//package test;
//
//import com.alipay.api.internal.util.StringUtils;
//import org.aspectj.lang.ProceedingJoinPoint;
//import org.aspectj.lang.annotation.Around;
//import org.aspectj.lang.annotation.Aspect;
//import org.aspectj.lang.annotation.Pointcut;
//import org.aspectj.lang.reflect.MethodSignature;
//import org.clm.util.RedisTemplateUtil;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
///**
// * @author Ccc
// * @date 2018/10/24 0024 下午 9:09
// */
//@Aspect
//@Component
//
//public class AopRedisCache {
//    @Autowired
//    private RedisTemplateUtil redisTemplateUtil;
//
//    @Pointcut("execution(public * com.itmayiedu.service..*.*(..))")
//    public void rlAop() {
//
//    }
//
//    @Around("rlAop()")
//    public Object doBefore(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
//        // 1.获取目标对象方法
//        MethodSignature signature = (MethodSignature) proceedingJoinPoint.getSignature();
//        // 使用Java反射技术获取方法上是否有@ExtRateLimiter注解类
//        ExtRedisCache extRedisCache = signature.getMethod().getDeclaredAnnotation(ExtRedisCache.class);
//        if (extRedisCache != null) {
//            // 1.获取cacheKey
//            String cacheKey = extRedisCache.cacheKey();
//            // 2.自定义缓存key
//            String methodCacheKey = methodCacheKey(proceedingJoinPoint, signature, cacheKey);
//            // 3.如果缓存中存在数据,直接缓存数据给客户端
//            String resultJson = redisUtils.getString(methodCacheKey);
//            if (!StringUtils.isEmpty(resultJson)) {
//                return JSONObject.parseObject(resultJson, Object.class);
//            }
//            // 3.如果缓存中没有该数据,调用数据库查询并且缓存数据
//            Object proceed = proceedingJoinPoint.proceed();
//            if (proceed != null) {
//                // 将数据缓存到redis中
//                String jsonString = JSONObject.toJSONString(proceed);
//                redisUtils.setString(methodCacheKey, jsonString);
//            }
//            return proceed;
//        }
//        ExtRemoveRedisCache extRemoveRedisCache = signature.getMethod()
//                .getDeclaredAnnotation(ExtRemoveRedisCache.class);
//        if (extRemoveRedisCache != null) {
//            // 1.获取cacheKey
//            String cacheKey = extRedisCache.cacheKey();
//            // 2.自定义缓存key
//            String methodCacheKey = methodCacheKey(proceedingJoinPoint, signature, cacheKey);
//            // 3.如果缓存中存在数据,直接缓存数据给客户端
//            String resultJson = redisUtils.getString(methodCacheKey);
//            if (!StringUtils.isEmpty(resultJson)) {
//                boolean deleteResult = redisUtils.delete(methodCacheKey);
//                System.out.println("该key:" + methodCacheKey + ",删除结果为:" + deleteResult);
//            }
//        }
//
//        // 执行业务逻辑方法
//        Object proceed = proceedingJoinPoint.proceed();
//        return proceed;
//
//    }
//
//    public String methodCacheKey(ProceedingJoinPoint proceedingJoinPoint, MethodSignature signature, String cacheKey) {
//        // 定义缓存名称 类的完整路径地址+自定义key+参数类型+参数名称+参数值+参数value
//        Class<?> methodClass = signature.getMethod().getDeclaringClass();
//        // 3.获取类的完整路径地址
//        String classAddres = methodClass.getName() + "." + methodClass.getSimpleName() + "." + signature.getName();
//
//        // 5.获取参数类型和值 转换为字符串
//        String methodNames = StringUtils.join(signature.getParameterNames());
//        String methodTypes = StringUtils.join(signature.getParameterTypes()).replace("class ", "");
//        String values = StringUtils.join(proceedingJoinPoint.getArgs());
//        String methodCacheKey = classAddres + "," + cacheKey + "," + methodNames + "," + methodTypes + "," + values;
//        System.out.println("methodCacheKey:" + methodCacheKey);
//        return methodCacheKey;
//    }
//
//
//
//}
