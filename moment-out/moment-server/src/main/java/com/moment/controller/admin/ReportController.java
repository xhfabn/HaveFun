package com.moment.controller.admin;

import com.moment.result.Result;
import com.moment.service.ReportService;
import com.moment.vo.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;

@RestController
@RequestMapping("/admin/report")
@Tag(name = "数据统计相关接口")
@Slf4j
public class ReportController {

    @Autowired
    private ReportService reportService;

    //在进行开发的过程中遇到问题是很常见的情况，要多进行迭代
    @GetMapping("/turnoverStatistics")
    @Operation(summary = "营业额统计接口")
    public Result<TurnoverReportVO> reportDayBus(
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end){
        log.info("开始统计营业额相关数据：{} {}",begin,end);
        TurnoverReportVO turnoverReportVO=reportService.reportDayBus(begin,end);
        return Result.success(turnoverReportVO);
    }

    @GetMapping("/userStatistics")
    @Operation(summary = "用户统计接口")
    public Result<UserReportVO> getUserStatistics(
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end){
    log.info("开始统计用户相关数据{}{}",begin,end);
    UserReportVO userReportVO=reportService.getUserStatistics(begin,end);
    return Result.success(userReportVO);
    }

    @GetMapping("/ordersStatistics")
    @Operation(summary = "订单统计接口")
    public Result<OrderReportVO> getOrderStatistics(
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end){
        log.info("开始统计订单相关的数据：{}{}",begin,end);
        OrderReportVO orderReportVO=reportService.getOrderStatistics(begin,end);
        return Result.success(orderReportVO);
    }

    @GetMapping("/top10")
    @Operation(summary = "查询销量排名top10接口")
    public Result<SalesTop10ReportVO> getTop10Statistics(
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end){
        log.info("开始统计订单相关的数据：{}{}",begin,end);
        SalesTop10ReportVO salesTop10ReportVO=reportService.getTop10Statistics(begin,end);
        return Result.success(salesTop10ReportVO);
    }
    @GetMapping("/export")
    @Operation(summary = "导出Excel报表接口")
    public void exportBusnessData(HttpServletResponse response){
        reportService.exportBusnessData(response);
    }
}
