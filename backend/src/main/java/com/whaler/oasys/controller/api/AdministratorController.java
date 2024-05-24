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

    /**
     * 系统管理员登录接口。
     * 该接口用于接收管理员的登录请求，并验证登录信息的有效性，进而返回登录成功的管理员信息。
     *
     * @param loginParam 包含管理员登录所需信息的参数对象，例如用户名和密码等。
     *                   该参数必须通过验证，确保数据的完整性和正确性。
     * @return 返回一个包含管理员详细信息的Vo对象，若登录失败则不会返回任何管理员信息。
     */
    @ApiOperation("系统管理员登录")
    @PostMapping("/login")
    public AdministratorVo login(@RequestBody @Validated LoginParam loginParam) {
        // 调用服务层方法，处理管理员登录逻辑
        return administratorService.login(loginParam);
    }

    /**
     * 系统管理员注册接口。
     * 该接口用于系统管理员的注册操作。
     * 
     * @param AdministratorParam 包含管理员注册信息的对象，需进行验证。
     *                          该参数通过请求体传入。
     * @return 无返回值。
     */
    @ApiOperation("系统管理员注册")
    @PostMapping("/register")
    public void register(@RequestBody @Validated AdministratorParam AdministratorParam) {
        // 调用管理员服务完成注册操作
        administratorService.register(AdministratorParam);
    }

    /**
     * 系统管理员查询所有的流程定义
     * 
     * @return 返回一个流程定义的列表，每个流程定义都以ProcessDefinitionVo对象的形式呈现。
     */
    @ApiOperation("系统管理员查询所有的流程定义")
    @GetMapping("/listProcessDefinitions")
    public List<ProcessDefinitionVo> listProcessDefinitions() {
        // 调用administratorService来查询并返回所有的流程定义
        return administratorService.listProcessDefinitions();
    }

    /**
     * 系统管理员部署流程定义。
     * 该接口允许系统管理员上传并部署流程定义文件。支持多个文件上传。
     * 
     * @param multipartFiles 多个文件的数组，这些文件是流程定义文件。
     *                       通过这个接口上传的文件将被解析并部署到流程引擎中。
     * @return 该方法没有返回值。
     * @throws ApiException 如果处理文件过程中发生异常，则抛出ApiException。
     */
    @ApiOperation("系统管理员部署流程定义")
    @PostMapping("/deployProcessDefinition")
    public void deployProcessDefinition(
        MultipartFile[] multipartFiles
    ) {
        // 初始化文件输入流和文件名数组
        InputStream[] files = new InputStream[multipartFiles.length];
        String[] filenames = new String[multipartFiles.length];
        try{
            // 遍历multipartFiles，获取每个文件的输入流和原始文件名
            for(int i=0;i<multipartFiles.length;i++){
                files[i] = multipartFiles[i].getInputStream();
                filenames[i] = multipartFiles[i].getOriginalFilename();
                // 关闭文件输入流
                files[i].close();
            }
        }catch(Exception e){
            // 如果在处理文件过程中发生异常，抛出ApiException
            throw new ApiException(e.getMessage());
        }
        // 调用管理员服务，部署流程定义
        administratorService.deployProcessDefinition(files, filenames);
    }

    /**
     * 验证管理员令牌
     * 
     * 本接口用于验证通过HTTP请求头传入的管理员令牌的有效性。
     * 它首先从请求头中提取Authorization信息，然后尝试解析该令牌。
     * 如果令牌不存在或无效，则抛出未授权异常；如果令牌有效，则返回简单的验证通过信息。
     * 
     * @param request HttpServletRequest对象，用于获取HTTP请求头中的Authorization信息。
     * @return 返回一个字符串 "验证通过"，表示令牌验证成功。
     * @throws ApiException 如果令牌不存在或无效，抛出此异常。
     */
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

    /**
     * 部署流程接口。
     * 该接口使用POST请求，通过多部分表单数据上传流程文件，然后进行流程部署。
     * 
     * @param multipartFile 一个包含多个流程文件的列表，每个流程文件以MultipartFile形式传递。
     *                      此参数是必需的，用于接收上传的文件。
     * @return 返回一个字符串表示部署的结果，成功则返回"部署成功"。
     */
    @ApiOperation("部署流程")
    @PostMapping(value = "/deployProcess", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String deployProcess(
        @RequestParam(value = "files", required = true) 
        List<MultipartFile> multipartFile 
        ) {
        // 初始化文件输入流数组和文件名数组
        InputStream [] files = new InputStream[multipartFile.size()];
        String [] fileNames = new String[multipartFile.size()]; 
        for(int i=0;i<multipartFile.size();i++){
            try{
                // 获取每个文件的输入流和原始文件名
                files[i] = multipartFile.get(i).getInputStream();
                fileNames[i] = multipartFile.get(i).getOriginalFilename();
            }catch(Exception e){
                // 文件处理异常，抛出自定义的API异常
                throw new ApiException("文件上传失败");
            }
        }
        // 调用服务层方法，进行流程部署
        administratorService.deployProcessDefinition(files, fileNames);
        return "部署成功";
    }

    /**
     * 管理员查看流程图
     * 
     * @param processDefinitionKey 流程定义的键，用于指定要获取流程图的流程定义
     * @return 返回流程图的字节数组，以供前端展示
     * @throws ApiException 如果获取流程图失败，则抛出此异常
     */
    @ApiOperation("管理员查看流程图")
    @GetMapping(value = "/getProcessDiagram/{processDefinitionKey}",
        produces = MediaType.IMAGE_PNG_VALUE)
    public byte[] getProcessDiagram(
        @PathVariable(value = "processDefinitionKey") String processDefinitionKey
    ) {
        // 通过流程定义键获取原始流程图的输入流
        InputStream inputStream = applicantService.getOriginalProcessDiagram(processDefinitionKey);
        byte[] bytes;
        try {
            // 将输入流中的数据读取到字节数组中
            bytes=new byte[inputStream.available()];
            inputStream.read(bytes, 0, inputStream.available());
            inputStream.close();
        } catch (Exception e) {
            // 如果在读取流程图时发生异常，则抛出获取流程图失败的异常
            throw new ApiException("获取流程图失败");
        }
        return bytes;
    }

    /**
     * 查询工单流转情况
     * 
     * 本接口无需参数，调用后将返回工单的流转情况列表。
     * 
     * @return List<ReportEntity> 返回工单流转情况的列表。
     */
    @ApiOperation("查询工单流转情况")
    @GetMapping("/listReports")
    public List<ReportEntity> listReports() {
        // 从管理员服务中获取工单流转情况列表
        return administratorService.listReports();
    }

    /**
     * 管理员查询周报列表的接口。
     * <p>
     * 本接口无需参数，用于管理员查询所有的周报列表。返回值为周报的列表，每个周报的信息包含在ReportVo对象中。
     *
     * @return List<ReportVo> 周报列表，每个列表项为一个ReportVo对象，包含周报的详细信息。
     */
    @ApiOperation("管理员查询周报列表")
    @GetMapping("/listWeeklyReports")
    public List<ReportVo> listWeeklyReports() {
        // 通过administratorService查询周报列表
        return administratorService.listWeeklyReports();
    }

    /**
     * 管理员查询指定周报的详细信息。
     * 
     * @param reportId 周报的唯一标识符，通过URL路径变量传递。
     * @return 返回对应周报的详细信息实体（ReportEntity）。
     */
    @ApiOperation("管理员查询周报详情")
    @GetMapping("/getWeeklyReport/{reportId}")
    public ReportEntity getWeeklyReport(
        @PathVariable(value = "reportId") String reportId
    ) {
        // 通过报告ID从管理员服务中获取指定的周报详情
        return administratorService.getWeeklyReport(reportId);
    }

    /**
     * 管理员查询指定日期的日报详情。
     * 
     * @param date 以字符串形式指定的日期，需符合日期格式要求。
     * @return 返回对应日期的日报实体（ReportEntity）。
     * @throws ApiException 如果传入的日期格式不正确，则抛出异常。
     */
    @ApiOperation("管理员查询日报详情")
    @GetMapping("/getDailyReport/{date}")
    public ReportEntity getDailyReport(
        @PathVariable(value = "date") String date
    ) {
        LocalDate localDate = LocalDate.parse(date); // 尝试将传入的字符串日期解析为LocalDate对象
        if (localDate==null) {
            throw new ApiException("日期格式错误"); // 如果解析失败，抛出日期格式错误异常
        }
        return administratorService.getDailyReport(localDate); // 调用服务层方法，查询并返回指定日期的日报详情
    }

    /**
     * 管理员实况查询接口
     * 该接口用于管理员查询当前系统的实况信息。
     * 
     * @return 返回查询到的实况信息字符串。如果无数据，则抛出ApiException异常。
     */
    @ApiOperation("管理员实况查询")
    @GetMapping("/getInfo")
    public String listProcessInstances() {
        String info = "";
        // 加锁以确保对共享数据的安全访问
        synchronized(Main.sharedData){
            info=Main.sharedData.get("msg");
        }
        // 若查询到的信息为空，则抛出异常
        if (info==null) {
            throw new ApiException("暂无数据");
        }
        return info;
    }

    /**
     * 测试生成日报的接口。
     * <p>
     * 该接口不接受任何参数，也不返回任何结果，它主要用于触发系统生成日报的任务。
     * 调用该接口后，系统将执行预定的每日任务，例如汇总数据、生成报告等。
     *
     * @author [作者姓名] 
     * @since [版本号] 
     */
    @ApiOperation("测试生成日报")
    @GetMapping("/generateDailyReport")
    public void insertDailyReport() {
        // 触发每日计划任务的执行
        scheduleService.dailyScheduledTask();
    }

    /**
     * 生成周报的接口。此接口不接受任何参数，也不返回任何结果，它主要用于触发系统中生成周报的任务。
     * 调用此接口后，系统将执行预定的周报生成任务。
     *
     * @apiNote 此接口适用于需要周期性生成周报的场景，例如在每周一早上自动生成上周的周报。
     */
    @ApiOperation("测试生成周报")
    @GetMapping("/generateWeeklyReport")
    public void insertWeeklyReport() {
        // 触发周报生成任务
        scheduleService.weeklyScheduledTask();
    }
}
