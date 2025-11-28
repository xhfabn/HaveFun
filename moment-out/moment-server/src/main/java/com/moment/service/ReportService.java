package com.moment.service;

import com.moment.vo.OrderReportVO;
import com.moment.vo.SalesTop10ReportVO;
import com.moment.vo.TurnoverReportVO;
import com.moment.vo.UserReportVO;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;

public interface ReportService {

    TurnoverReportVO reportDayBus(LocalDate begin, LocalDate end);

    UserReportVO getUserStatistics(LocalDate begin, LocalDate end);

    OrderReportVO getOrderStatistics(LocalDate begin, LocalDate end);

    SalesTop10ReportVO getTop10Statistics(LocalDate begin, LocalDate end);

    void exportBusnessData(HttpServletResponse response);
}
