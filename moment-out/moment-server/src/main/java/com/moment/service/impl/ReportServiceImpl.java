package com.moment.service.impl;

import com.moment.dto.GoodsSalesDTO;
import com.moment.entity.Orders;
import com.moment.mapper.OrderMapper;
import com.moment.mapper.UserMapper;
import com.moment.service.ReportService;
import com.moment.service.WorkspaceService;
import com.moment.vo.*;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.time.LocalTime.MAX;
import static java.time.LocalTime.MIN;

@Service
public class ReportServiceImpl implements ReportService {


    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private WorkspaceService workspaceService;

    public TurnoverReportVO reportDayBus(LocalDate begin, LocalDate end) {
        //通过起始和结束时间得到日期和订单量的对应
        //日期，以逗号分隔，例如：2022-10-01,2022-10-02,2022-10-03
        //字符串格式 json格式
        List<LocalDate> dateList=new ArrayList<>();
        dateList.add(begin);
        while (!begin.equals(end)) {
            begin=begin.plusDays(1);
            dateList.add(begin);
        }
        List<Double> turnoverList=new ArrayList<>();
        //sql查询语句 select sum(amount) from orders where
        for (LocalDate date : dateList) {
            //未传入的日期赋值
            LocalDateTime beginTime=LocalDateTime.of(date, MIN);
            LocalDateTime endTime=LocalDateTime.of(date,LocalTime.MAX);

            //传入map集合 封装
            Map map=new HashMap<>();
            map.put("begin",beginTime);
            map.put("end",endTime);
            map.put("status", Orders.COMPLETED);
            Double turnover=orderMapper.sumByMap(map);
            //若当天无营业额，则为null 不为0
            turnover= turnover ==null ? 0.0 : turnover;
            turnoverList.add(turnover);
        }
        return TurnoverReportVO
                .builder()
                .dateList(StringUtils.join(dateList,","))
                .turnoverList(StringUtils.join(turnoverList,","))
                .build();


    }

    @Override
    public UserReportVO getUserStatistics(LocalDate begin, LocalDate end) {
//        依旧是获取日期列表
        List<LocalDate> dateList=new ArrayList<>();
        dateList.add(begin);
        while (!begin.equals(end)) {
            begin=begin.plusDays(1);
            dateList.add(begin);
        }
        //获取总的用户量
        List<Integer> totalUserList=new ArrayList<>();
        List<Integer> newUserList=new ArrayList<>();
        //通过user表进行统计
        for (LocalDate localDate : dateList) {
            //首先必须先获得这一天具体的时间范围
            LocalDateTime beginTime=LocalDateTime.of(localDate,MIN);
            LocalDateTime endTime=LocalDateTime.of(localDate,MAX);
            //创建map集合一一对应
           Integer numAll=userMapper.getByOrderTime(beginTime);
           Integer numNew=userMapper.getNewByOrderTime(beginTime,endTime);
           totalUserList.add(numAll);
           newUserList.add(numNew);
        }
        return UserReportVO
                .builder()
                .dateList(StringUtils.join(dateList,","))
                .newUserList(StringUtils.join(newUserList,","))
                .totalUserList(StringUtils.join(totalUserList,","))
                .build();

    }

    @Override
    public OrderReportVO getOrderStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            dateList.add(begin);
        }
//        先统计每日订单数和有效订单数
        List<Integer> orderCountList = new ArrayList<>();
        List<Integer> vaildOrderCountList = new ArrayList<>();
        for (LocalDate localDate : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(localDate, MIN);
            LocalDateTime endTime = LocalDateTime.of(localDate, MAX);
            /*每日订单直接传入日期查询
             * 有效订单需要传入状态 */
            //获得每日订单数
            //todo 使用函数进行改善
            Integer orderNum = orderMapper.getByTime(beginTime, endTime);
            //获得每日有效订单数
            Integer orderNumVa = orderMapper.getByTimeAndStatus(beginTime, endTime, Orders.COMPLETED);
            orderCountList.add(orderNum);
            vaildOrderCountList.add(orderNumVa);
        }
        //获得总的订单数和有效订单数 使用stream流
        Integer totalOrderCount = orderCountList.stream().reduce(Integer::sum).get();
        Integer validOrderCount = vaildOrderCountList.stream().reduce(Integer::sum).get();
        //获得订单完成率
        Double orderCompletionRate = 0.0;
        if (totalOrderCount != null) {
            orderCompletionRate = validOrderCount.doubleValue() / totalOrderCount;
        }

        return OrderReportVO
                .builder()
                .dateList(StringUtils.join(dateList, ","))
                .orderCountList(StringUtils.join(orderCountList, ","))
                .validOrderCountList(StringUtils.join(vaildOrderCountList, ","))
                .totalOrderCount(totalOrderCount)
                .validOrderCount(validOrderCount)
                .orderCompletionRate(orderCompletionRate)
                .build();


       }

    @Override
    public SalesTop10ReportVO getTop10Statistics(LocalDate begin, LocalDate end) {
        //在begin和end这个时间段里 获得销量排名前十的菜品
        LocalDateTime beginTime=LocalDateTime.of(begin, MIN);
        LocalDateTime endTime=LocalDateTime.of(end,MAX);
        //可以使用map集合封装从数据库获得的菜品名称和数量
        //todo sql语句的学习
        //select od.name,sum(od.number) number from order_detail od,orders o where od.order_id=o.id
        //  and o.status=5 and o.order_time between '2025-04-01'and '2025-04-10'
        //    group by od.name order by number desc limit 0,10;
        //调用ordermap查表
        List<GoodsSalesDTO> topSalesList=orderMapper.getSalesTop(beginTime,endTime);
        //todo stream流回忆
        List<String> names= topSalesList.stream().map(GoodsSalesDTO::getName).collect(Collectors.toList());
        List<Integer> numbers = topSalesList.stream().map(GoodsSalesDTO::getNumber).collect(Collectors.toList());
        String nameList=StringUtils.join(names,",");
        String numberList=StringUtils.join(numbers,",");
        return SalesTop10ReportVO
                .builder()
                .nameList(nameList)
                .numberList(numberList)
                .build();
    }

    @Override
    public void exportBusnessData(HttpServletResponse response) {
        //1.查询数据库 获得需要的数据
        LocalDate dateBegin = LocalDate.now().minusDays(30);
        LocalDate dateEnd=LocalDate.now().minusDays(1);

        //查询概览数据
        BusinessDataVO businessData = workspaceService.getBusinessData(LocalDateTime.of(dateBegin, MIN), LocalDateTime.of(dateEnd, MAX));
        //2.通过POI将数据写入到excel表格中
        InputStream in=this.getClass().getClassLoader().getResourceAsStream("template/运营数据报表模板.xlsx");
        //基于模板文件创建一个新的excel文件
        try {
            XSSFWorkbook excel = new XSSFWorkbook(in);

            XSSFSheet sheet= excel.getSheet("sheet1");

            sheet.getRow(1).getCell(1).setCellValue("时间："+dateBegin+"至"+dateEnd);
            //获得第四行
            XSSFRow row=sheet.getRow(3);
            row.getCell(2).setCellValue(businessData.getTurnover());
            row.getCell(4).setCellValue(businessData.getOrderCompletionRate());

            row.getCell(6).setCellValue(businessData.getNewUsers());
            //获得第五行
            row = sheet.getRow(4);
            row.getCell(2).setCellValue(businessData.getValidOrderCount());
            row.getCell(4).setCellValue(businessData.getUnitPrice());
            //填充明细数据
            for (int i = 0; i < 30; i++) {
                LocalDate date=dateBegin.plusDays(i);
                BusinessDataVO businessData1 = workspaceService.getBusinessData(LocalDateTime.of(date, MIN), LocalDateTime.of(date, MAX));

                row = sheet.getRow(7 + i);
                row.getCell(1).setCellValue(date.toString());
                row.getCell(2).setCellValue(businessData1.getTurnover());
                row.getCell(3).setCellValue(businessData1.getValidOrderCount());
                row.getCell(4).setCellValue(businessData1.getOrderCompletionRate());
                row.getCell(5).setCellValue(businessData1.getUnitPrice());
                row.getCell(6).setCellValue(businessData1.getNewUsers());
            }
            //3.通过输出流将excel表格下载到客户端网页
            ServletOutputStream out = response.getOutputStream();
            excel.write(out);

            //关闭资源
            out.close();
            excel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
