package com.whaler.oasys.controller.api;

import java.io.InputStream;
import java.time.LocalDate;
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

import com.whaler.oasys.Main;
import com.whaler.oasys.model.entity.ReportEntity;
import com.whaler.oasys.model.enums.ResultCode;
import com.whaler.oasys.model.exception.ApiException;
import com.whaler.oasys.model.param.AdministratorParam;
import com.whaler.oasys.model.param.LoginParam;
import com.whaler.oasys.model.vo.AdministratorVo;
import com.whaler.oasys.model.vo.ProcessDefinitionVo;
import com.whaler.oasys.model.vo.ReportVo;
import com.whaler.oasys.security.JwtManager;
import com.whaler.oasys.security.UserContext;
import com.whaler.oasys.service.AdministratorService;
import com.whaler.oasys.service.ApplicantService;
import com.whaler.oasys.service.ScheduleService;

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
    private ScheduleService scheduleService;
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

    @ApiOperation("管理员查询周报列表")
    @GetMapping("/listWeeklyReports")
    public List<ReportVo> listWeeklyReports() {
        return administratorService.listWeeklyReports();
    }

    @ApiOperation("管理员查询周报详情")
    @GetMapping("/getWeeklyReport/{reportId}")
    public ReportEntity getWeeklyReport(
        @PathVariable(value = "reportId") String reportId
    ) {
        return administratorService.getWeeklyReport(reportId);
    }

    @ApiOperation("管理员查询日报详情")
    @GetMapping("/getDailyReport/{date}")
    public ReportEntity getDailyReport(
        @PathVariable(value = "date") String date
    ) {
        LocalDate localDate = LocalDate.parse(date);
        if (localDate==null) {
            throw new ApiException("日期格式错误");
        }
        return administratorService.getDailyReport(localDate);
    }

    @ApiOperation("管理员实况查询")
    @GetMapping("/getInfo")
    public String listProcessInstances() {
        String info = "";
        synchronized(Main.sharedData){
            info=Main.sharedData.get("msg");
        }
        if (info==null) {
            throw new ApiException("暂无数据");
        }
        return info;
    }

    @ApiOperation("测试生成日报")
    @GetMapping("/generateDailyReport")
    public void insertDailyReport() {
        scheduleService.dailyScheduledTask();
    }

    @ApiOperation("测试生成周报")
    @GetMapping("/generateWeeklyReport")
    public void insertWeeklyReport() {
        scheduleService.weeklyScheduledTask();
    }
}
