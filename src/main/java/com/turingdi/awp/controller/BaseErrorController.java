package com.turingdi.awp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ErrorAttributes;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * 错误/异常统一处理Controller
 *
 * Created by huangz on 2017/7/10.
 */
@Controller
@RequestMapping(value = "error")
@EnableConfigurationProperties({ServerProperties.class})
public class BaseErrorController implements ErrorController {
    private ErrorAttributes errorAttributes;

    @Autowired
    private ServerProperties serverProperties;


    /**
     * 初始化ExceptionController
     * @param errorAttributes
     */
    @Autowired
    public BaseErrorController(ErrorAttributes errorAttributes) {
        Assert.notNull(errorAttributes, "ErrorAttributes must not be null");
        this.errorAttributes = errorAttributes;
    }


    /**
     * 定义404的ModelAndView
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(produces = "text/html",value = "404")
    public ModelAndView errorHtml404(HttpServletRequest request,
                                     HttpServletResponse response) {
        response.setStatus(getStatus(request).value());
        Map<String, Object> model = getErrorAttributes(request,
                isIncludeStackTrace(request, MediaType.TEXT_HTML));
        return new ModelAndView("error/404", model);
    }

    /**
     * 定义404的JSON数据
     * @param request
     * @return
     */
    @RequestMapping(value = "404")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> error404(HttpServletRequest request) {
        Map<String, Object> body = getErrorAttributes(request,
                isIncludeStackTrace(request, MediaType.TEXT_HTML));
        HttpStatus status = getStatus(request);
        return new ResponseEntity<Map<String, Object>>(body, status);
    }

    /**
     * 定义500的ModelAndView
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(produces = "text/html",value = "500")
    public ModelAndView errorHtml500(HttpServletRequest request,
                                     HttpServletResponse response) {
        response.setStatus(getStatus(request).value());
        Map<String, Object> model = getErrorAttributes(request,
                isIncludeStackTrace(request, MediaType.TEXT_HTML));
        return new ModelAndView("error/error", model);
    }


    /**
     * 定义400的ModelAndView
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(produces = "text/html",value = "400")
    public ModelAndView errorHtml400(HttpServletRequest request,
                                     HttpServletResponse response) {
        response.setStatus(getStatus(request).value());
        Map<String, Object> model = getErrorAttributes(request,
                isIncludeStackTrace(request, MediaType.TEXT_HTML));
        return new ModelAndView("error/error", model);
    }

    /**
     * 定义400的JSON数据
     * @param request
     * @return
     */
    @RequestMapping(value = "400")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> error400(HttpServletRequest request) {
        Map<String, Object> body = getErrorAttributes(request,
                isIncludeStackTrace(request, MediaType.TEXT_HTML));
        HttpStatus status = getStatus(request);
        return new ResponseEntity<Map<String, Object>>(body, status);
    }

    /**
     * 定义500的错误JSON信息
     * @param request
     * @return
     */
    @RequestMapping(value = "500")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> error500(HttpServletRequest request) {
        Map<String, Object> body = getErrorAttributes(request,
                isIncludeStackTrace(request, MediaType.TEXT_HTML));
        HttpStatus status = getStatus(request);
        return new ResponseEntity<Map<String, Object>>(body, status);
    }


    /**
     * Determine if the stacktrace attribute should be included.
     * @param request the source request
     * @param produces the media type produced (or {@code MediaType.ALL})
     * @return if the stacktrace attribute should be included
     */
    protected boolean isIncludeStackTrace(HttpServletRequest request,
                                          MediaType produces) {
        ErrorProperties.IncludeStacktrace include = this.serverProperties.getError().getIncludeStacktrace();
        if (include == ErrorProperties.IncludeStacktrace.ALWAYS) {
            return true;
        }
        if (include == ErrorProperties.IncludeStacktrace.ON_TRACE_PARAM) {
            return getTraceParameter(request);
        }
        return false;
    }


    /**
     * 获取错误的信息
     * @param request
     * @param includeStackTrace
     * @return
     */
    private Map<String, Object> getErrorAttributes(HttpServletRequest request,
                                                   boolean includeStackTrace) {
        RequestAttributes requestAttributes = new ServletRequestAttributes(request);
        return this.errorAttributes.getErrorAttributes(requestAttributes,
                includeStackTrace);
    }

    /**
     * 是否包含trace
     * @param request
     * @return
     */
    private boolean getTraceParameter(HttpServletRequest request) {
        String parameter = request.getParameter("trace");
        if (parameter == null) {
            return false;
        }
        return !"false".equals(parameter.toLowerCase());
    }

    /**
     * 获取错误编码
     * @param request
     * @return
     */
    private HttpStatus getStatus(HttpServletRequest request) {
        Integer statusCode = (Integer) request
                .getAttribute("javax.servlet.error.status_code");
        if (statusCode == null) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
        try {
            return HttpStatus.valueOf(statusCode);
        }
        catch (Exception ex) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }

    /**
     * 实现错误路径,暂时无用
     * @see
     * @return
     */
    @Override
    public String getErrorPath() {
        return "";
    }

}