package cloud.igoldenbeta.hippo.controller;

import cloud.igoldenbeta.hippo.client.HippoProxy;
import cloud.igoldenbeta.hippo.framework.ResponseEntity;
import cloud.igoldenbeta.hippo.utils.HttpAnalysisUtils;
import cloud.igoldenbeta.hippo.utils.RouteRulesUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;

/**
 * Created by hanruofei on 16/8/4.
 */
@EnableAutoConfiguration
@Controller
@RequestMapping(value = "/service")
public class ServiceControllor {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceControllor.class);

    @Autowired
    private HippoProxy hippoProxy;

    @ResponseBody
    @RequestMapping(value = "/ping")
    ResponseEntity<?> ping() {
        return ResponseEntity.success("Hello World!");
    }

    @RequestMapping(value = "/{serviceName}/**/",
            method = {RequestMethod.GET,RequestMethod.DELETE},
            produces = {"application/json;charset=UTF-8"})
    public @ResponseBody ResponseEntity<?> http1(@PathVariable(value = "serviceName") String serviceName,
                                                HttpServletRequest request){
        try {
            String requestURI = request.getRequestURI();
            String methodName = StringUtils.substringAfter(requestURI, serviceName + "/");
            String host = RouteRulesUtils.getHost(serviceName, methodName);
            try {
                Object o = hippoProxy.apiRequest(host, methodName, HttpAnalysisUtils.resolveRequestToUrl(request));
                return ResponseEntity.success(o);
            } catch (Throwable e) {
                return ResponseEntity.error(e.getMessage(),e);
            }
        } catch (Exception e) {
            LOGGER.error("getHttp_exception", e);
            return ResponseEntity.error(e.getMessage());
        }
    }

    @RequestMapping(value = "/{serviceName}/**/",
            method = {RequestMethod.POST,RequestMethod.PUT,RequestMethod.PATCH},
            produces = {"application/json;charset=UTF-8"})
    public @ResponseBody ResponseEntity<?> http2(@PathVariable(value = "serviceName") String serviceName,
                                                HttpServletRequest request){
        try {
            String requestURI = request.getRequestURI();
            String methodName = StringUtils.substringAfter(requestURI, serviceName + "/");
            String host = RouteRulesUtils.getHost(serviceName, methodName);
            try {
                Object o = hippoProxy.apiRequest(host, methodName, HttpAnalysisUtils.resolveRequestToBody(request));
                return ResponseEntity.success(o);
            } catch (Throwable e) {
                return ResponseEntity.error(e.getMessage(),e);
            }
        } catch (Exception e) {
            LOGGER.error("getHttp_exception", e);
            return ResponseEntity.error(e.getMessage());
        }
    }


}
