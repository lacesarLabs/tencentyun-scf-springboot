package scf;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.lacesar.Application;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletConfig;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 路由方法
 *
 * @author Freeeeeedom
 * @date 2020/12/2 15:23
 */
public class ScfRouter {
    private static DispatcherServlet dispatcherServlet;
    private static ServletContext servletContext;
    private static int gcControl = 0;
    private static int totalCount = 0;

    static {
        System.out.println("容器启动！！！");
        Thread.currentThread().setContextClassLoader(Application.class.getClassLoader());
        AnnotationConfigServletWebServerApplicationContext context = (AnnotationConfigServletWebServerApplicationContext) Application.start();
        servletContext = context.getServletContext();
        assert servletContext != null;
        servletContext.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, context);
        dispatcherServlet = context.getBean(DispatcherServlet.class);
        try {
            dispatcherServlet.init(new MockServletConfig(servletContext));
        } catch (ServletException e) {
            System.out.println("服务启动失败！" + e.toString());
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws Exception {
        String paramString = "{\"headers\":{\"accept\":\"*/*\",\"accept-encoding\":\"gzip, deflate, br\",\"accept-language\":\"zh-CN,zh;q=0.9,en;q=0.8\",\"cache-control\":\"no-cache\",\"connection\":\"keep-alive\",\"content-type\":\"application/json\",\"endpoint-timeout\":\"300\",\"host\":\"lacesar.com\",\"postman-token\":\"6e670681-165e-ef8d-78cb-65d7c75c76de\",\"requestsource\":\"APIGW\",\"sec-ch-ua\":\"\\\"Chromium\\\";v=\\\"88\\\", \\\"Google Chrome\\\";v=\\\"88\\\", \\\";Not A Brand\\\";v=\\\"99\\\"\",\"sec-ch-ua-mobile\":\"?0\",\"sec-fetch-dest\":\"empty\",\"sec-fetch-mode\":\"cors\",\"sec-fetch-site\":\"none\",\"user-agent\":\"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/88.0.4324.182 Safari/537.36\",\"x-api-requestid\":\"7ffc2236b2830f328d69893cb1fb7d6e\",\"x-api-scheme\":\"https\",\"x-b3-traceid\":\"7ffc2236b2830f328d69893cb1fb7d6e\",\"x-qualifier\":\"$LATEST\"},\"httpMethod\":\"GET\",\"isBase64Encoded\":false,\"path\":\"/hello\",\"pathParameters\":{\"path\":\"hello\"},\"queryString\":{\"name\":\"world1\"},\"queryStringParameters\":{},\"requestContext\":{\"httpMethod\":\"ANY\",\"identity\":{},\"path\":\"/{path}\",\"serviceId\":\"service-9sm0m73i\",\"sourceIp\":\"114.114.1hello world114.114\",\"stage\":\"release\"}}";
        Gson gson = new Gson();
        Map<String, Object> param = gson.fromJson(paramString, new TypeToken<Map<String, Object>>() {
        }.getType());
        long start;
        start = System.currentTimeMillis();
        System.out.println(new ScfRouter().run(param));
        System.out.println("耗时:" + (System.currentTimeMillis() - start));
    }

    public Object run(Map<String, Object> param) throws Exception {
        Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        System.out.println("当前函数第" + ++totalCount + "次执行");
        System.out.println("入参:" + gson.toJson(param));
        if (null == param.get("pathParameters")
                || StringUtils.isEmpty((String) ((Map) param.get("pathParameters")).get("path"))) {
            System.out.println("异常！" + param.get("pathParameters"));
            return buildResponse(param, 400);
        }
        //参数兼容cmq入参
        if (null != param.get("Records")
                && ((List) param.get("Records")).get(0) != null) {
            param = gson.fromJson(String.valueOf(((Map) (((Map) (((List) param.get("Records")).get(0))).get("CMQ"))).get("msgBody")), Map.class);
        }
        StringBuilder uriBuilder = new StringBuilder("/");
        uriBuilder.append(((Map) param.get("pathParameters")).get("path"));
        String uri = uriBuilder.toString();
        if (null != param.get("queryString") && ((Map) param.get("queryString")).size() != 0) {
            uriBuilder.append("?");
            ((Map) param.get("queryString")).forEach((k, v) -> uriBuilder.append(k).append("=").append(v).append("&"));
            uri = uriBuilder.substring(0, uriBuilder.length() - 1);
        }
        MockHttpServletRequestBuilder rb;
        switch (String.valueOf(param.get("httpMethod")).toUpperCase()) {
            case "UPDATE":
            case "POST": {
                rb = MockMvcRequestBuilders.post(uri)
                        .content(String.valueOf(param.get("body")));
                break;
            }
            case "GET": {
                rb = MockMvcRequestBuilders.get(uri);
                break;
            }
            case "PUT": {
                rb = MockMvcRequestBuilders.put(uri)
                        .content(String.valueOf(param.get("body")));
                break;
            }
            case "DELETE": {
                rb = MockMvcRequestBuilders.delete(uri)
                        .content(String.valueOf(param.get("body")));
                break;
            }
            default: {
                Map<String, String> err = new HashMap<>();
                err.put("msg", "不支持的请求类型!");
                System.out.println("不支持的请求类型!");
                return buildResponse(err, 400);
            }
        }
        if (rb == null) {
            Map<String, String> err = new HashMap<>();
            err.put("msg", "不支持的请求类型!");
            System.out.println("不支持的请求类型!");
            return buildResponse(err, 400);
        }
        ((Map) param.get("headers")).forEach((k, v) -> rb.header((String) k, v));
        MockHttpServletResponse response = new MockHttpServletResponse();
        dispatcherServlet.service(rb.buildRequest(servletContext), response);
        response.setCharacterEncoding("UTF-8");
        Object body = buildResponse(response);
        if (++gcControl >= 100) {
            System.out.println("开始GC");
            System.gc();
            Runtime.getRuntime().runFinalization();
            gcControl = 0;
        }
        return body;
    }

    private Object buildResponse(Map body, Integer statusCode) {
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
        Map<String, String> headers = new HashMap<>(1);
        Map<String, Object> response = new HashMap<>();
        headers.put("content-type", "application/json");
        response.put("statusCode", statusCode);
        response.put("headers", headers);
        response.put("body", gson.toJson(body));
        return gson.toJson(response);
    }

    private Object buildResponse(MockHttpServletResponse httpResponse) throws UnsupportedEncodingException {
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
        Map<String, String> headers = new HashMap<>(1);
        Map<String, Object> response = new HashMap<>();
        headers.put("content-type", httpResponse.getContentType());
        response.put("statusCode", httpResponse.getStatus());
        response.put("headers", headers);
        if (null == httpResponse.getContentType()) {
            response.put("body", httpResponse.getContentAsString());
        } else {
            String contentType = httpResponse.getContentType().toLowerCase();
            if (contentType.contains("image")) {
                String base64image = Base64.getEncoder().encodeToString(httpResponse.getContentAsByteArray());
                headers.put("content-type", "text/html");
                response.put("body", "data:" + contentType + ";base64," + base64image.replaceAll("\\u003d", ""));
            } else {
                response.put("body", httpResponse.getContentAsString());
            }
        }
        return gson.toJson(response);
    }
}