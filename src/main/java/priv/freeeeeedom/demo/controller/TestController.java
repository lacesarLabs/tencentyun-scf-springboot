package priv.freeeeeedom.demo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.Map;

@RestController
public class TestController {
    /**
     * log Object
     */
    private static Logger log = LoggerFactory.getLogger(TestController.class);

    @GetMapping("/hello")
    public String getTest(String name, HttpServletRequest request) {
        String hello = MessageFormat.format("hello {0}", name);
        System.out.println(hello);
        printHeaders(request);
        System.out.println();
        return hello;
    }

    @PostMapping("/hello")
    public String postTest(@RequestBody Map<String, String> param, HttpServletRequest request) {
        String hello = MessageFormat.format("hello {0}", param.get("name"));
        System.out.println(hello);
        printHeaders(request);
        return hello;
    }

    private void printHeaders(HttpServletRequest request) {
        Enumeration headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = (String) headerNames.nextElement();
            System.out.println(key + " " + request.getHeader(key));
        }
    }
}
