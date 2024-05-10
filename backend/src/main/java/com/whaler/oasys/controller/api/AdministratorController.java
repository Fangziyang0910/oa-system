package com.whaler.oasys.controller.api;

import java.io.InputStream;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.whaler.oasys.model.entity.ReportEntity;
import com.whaler.oasys.model.enums.ResultCode;
import com.whaler.oasys.model.exception.ApiException;
import com.whaler.oasys.model.param.AdministratorParam;
import com.whaler.oasys.model.param.LoginParam;
import com.whaler.oasys.model.vo.AdministratorVo;
import com.whaler.oasys.model.vo.ProcessDefinitionVo;
import com.whaler.oasys.security.JwtManager;
import com.whaler.oasys.security.UserContext;
import com.whaler.oasys.service.AdministratorService;
import com.whaler.oasys.service.ApplicantService;

import io.jsonwebtoken.Claims;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/admin")
@Api(description = "系统管理员")
public class AdministratorController {
    @Autowired
    private AdministratorService administratorService;
    @Autowired
    private ApplicantService applicantService;
    @Autowired
    private JwtManager jwtManager;

    @ApiOperation("系统管理员登录")
    @PostMapping("/login")
    public AdministratorVo login(@RequestBody @Validated LoginParam loginParam) {
        return administratorService.login(loginParam);
    }

    @ApiOperation("系统管理员注册")
    @PostMapping("/register")
    public void register(@RequestBody @Validated AdministratorParam AdministratorParam) {
        administratorService.register(AdministratorParam);
    }

    @ApiOperation("系统管理员查询所有的流程定义")
    @GetMapping("/listProcessDefinitions")
    public List<ProcessDefinitionVo> listProcessDefinitions() {
        return administratorService.listProcessDefinitions();
    }

    @ApiOperation("系统管理员部署流程定义")
    @PostMapping("/deployProcessDefinition")
    public void deployProcessDefinition(
        MultipartFile[] multipartFiles
    ) {
        InputStream[] files = new InputStream[multipartFiles.length];
        String[] filenames = new String[multipartFiles.length];
        try{
            for(int i=0;i<multipartFiles.length;i++){
                files[i] = multipartFiles[i].getInputStream();
                filenames[i] = multipartFiles[i].getOriginalFilename();
                files[i].close();
            }
        }catch(Exception e){
            throw new ApiException(e.getMessage());
        }
        administratorService.deployProcessDefinition(files, filenames);
    }

    @ApiOperation("验证管理员令牌")
    @GetMapping("/validateToken")
    public String validateToken(HttpServletRequest request){
        // 从请求头中获取Authorization信息
        String token=request.getHeader("Authorization");
        // 如果token不存在，则抛出未授权异常
        if(token==null){
            throw new ApiException(ResultCode.UNAUTHORIZED);
        }
        // 解析token，并校验其有效性
        Claims claims=jwtManager.parse(token);
        // 如果解析失败或token无效，则抛出未授权异常
        if(claims==null){
            throw new ApiException(ResultCode.UNAUTHORIZED);
        }
        return "验证通过"; // 表示请求通过验证，可以继续处理
    }

    @ApiOperation("部署流程")
    @PostMapping(value = "/deployProcess", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String deployProcess(
        @RequestParam(value = "files", required = true) 
        List<MultipartFile> multipartFile 
        ) {
        InputStream [] files = new InputStream[multipartFile.size()];
        String [] fileNames = new String[multipartFile.size()];
        for(int i=0;i<multipartFile.size();i++){
            try{
                files[i] = multipartFile.get(i).getInputStream();
                fileNames[i] = multipartFile.get(i).getOriginalFilename();
            }catch(Exception e){
                throw new ApiException("文件上传失败");
            }
        }
        administratorService.deployProcessDefinition(files, fileNames);
        return "部署成功";
    }

    @ApiOperation("管理员查看流程图")
    @GetMapping(value = "/getProcessDiagram/{processDefinitionKey}",
        produces = MediaType.IMAGE_PNG_VALUE)
    public byte[] getProcessDiagram(
        @PathVariable(value = "processDefinitionKey") String processDefinitionKey
    ) {
        InputStream inputStream = applicantService.getOriginalProcessDiagram(processDefinitionKey);
        byte[] bytes;
        try {
            bytes=new byte[inputStream.available()];
            inputStream.read(bytes, 0, inputStream.available());
            inputStream.close();
        } catch (Exception e) {
            throw new ApiException("获取流程图失败");
        }
        return bytes;
    }

    @ApiOperation("查询工单流转情况")
    @GetMapping("/listReports")
    public List<ReportEntity> listReports() {
        return administratorService.listReports();
    }
}
