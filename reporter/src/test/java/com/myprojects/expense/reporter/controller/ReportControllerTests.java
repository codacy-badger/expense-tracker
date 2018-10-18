package com.myprojects.expense.reporter.controller;

import com.myprojects.expense.reporter.config.ReporterControllerConfig;
import com.myprojects.expense.reporter.model.DayReport;
import com.myprojects.expense.reporter.model.ReportData;
import com.myprojects.expense.reporter.model.ReportTransaction;
import com.myprojects.expense.reporter.service.ReportService;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockitoTestExecutionListener;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.web.servlet.MockMvc;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.UUID;

import static java.util.Collections.emptyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@WebMvcTest(ReportController.class)
@ContextConfiguration(classes = ReporterControllerConfig.class)
@TestExecutionListeners(MockitoTestExecutionListener.class)
public class ReportControllerTests extends AbstractTestNGSpringContextTests {

    private static final UUID TEST_ID = UUID.randomUUID();

    @MockBean
    private ReportService mockReportService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testGetReportSerializesToJsonCorrectly() throws Exception {
        Mockito.when(mockReportService.getDayReport(eq(2018), eq(12), eq(1)))
                .thenReturn(createReport());

        mockMvc.perform(get(ReportController.PATH + "/2018/12/1"))
                .andDo(print())
                .andExpect(content()
                        .json("{\n" +
                                "  \"id\": \"" + TEST_ID + "\",\n" +
                                "  \"date\": \"2018-12-01\",\n" +
                                "  \"data\": {\n" +
                                "    \"total\": 0,\n" +
                                "    \"totalIncomes\": 10,\n" +
                                "    \"totalExpenses\": 10,\n" +
                                "    \"incomes\": [\n" +
                                "      {\n" +
                                "        \"amount\": 10,\n" +
                                "        \"category\": \"test\",\n" +
                                "        \"id\": \"tid\"\n" +
                                "      }\n" +
                                "    ],\n" +
                                "    \"expenses\": []\n" +
                                "  }\n" +
                                "}"));
    }

    private static DayReport createReport() {
        ReportData data = new ReportData();
        data.setTotal(BigDecimal.ZERO);
        data.setTotalExpenses(BigDecimal.TEN);
        data.setTotalIncomes(BigDecimal.TEN);
        data.setIncomes(Arrays.asList(new ReportTransaction("tid", BigDecimal.TEN, "test")));
        data.setExpenses(emptyList());

        DayReport report = new DayReport();
        report.setId(TEST_ID);
        report.setDate(LocalDate.of(2018, 12, 1));
        report.setData(data);

        return report;
    }
}