package com.example.moneymanager.service;

import com.example.moneymanager.dto.ExpenseDTO;
import com.example.moneymanager.entity.ProfileEntity;
import com.example.moneymanager.repositry.ProfileRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final ProfileRepo profileRepo;
    private final EmailService emailService;
    private final ExpenseSerive expenseSerive;


    @Value("${money.manager.frontend.url}")
    private  String frontendUrl;

    @Scheduled(cron = "0 0 22 * * *",zone = "IST")
    public void sendDailyIncomeExpenseRemainder(){
        log.info("Job started : sendDailyIncomeExpenseRemainder");
        List<ProfileEntity> profiles = profileRepo.findAll();
        for(ProfileEntity profile: profiles){
            String body = "Hi " + profile.getFullname() + ", I hope you are fine,<br><br>"
                    + "This is a friendly reminder to add your income and expenses for today in Money Manager.<br><br>"
                    + "<a href='" + frontendUrl + "' style='display:inline-block; padding:10px 20px; background-color:#4CAF50; color:#fff; text-decoration:none; border-radius:5px; font-weight:bold;'>"
                    + "Money Manager is waiting for you...</a>"
                    + "<br><br>Best regards,<br>Money Manager Team";
            emailService.sendEmail(profile.getEmail(),"Daily Remainder Add Your Income And Expense",body);

        }
        log.info("Job completed : sendDailyIncomeExpenseRemainder() ");

    }

    @Scheduled(cron = "0 0 23 * * * ", zone = "IST")
    public void sendDailyExpenseSummary(){
        log.info("Job started : sendDailyexpenseSummary() ");

        List<ProfileEntity> profiles=profileRepo.findAll();
        for ( ProfileEntity profile : profiles){
            List<ExpenseDTO> todayExpenses=expenseSerive.getExpensesforuserondate(profile.getId(), LocalDate.now(ZoneId.of("Asia/Kolkata")));
            if (!todayExpenses.isEmpty()) {
                StringBuilder table = new StringBuilder();

                // Table start
                table.append("<table style='border-collapse:collapse; width:100%; font-family:Arial, sans-serif;'>")

                        // Header row
                        .append("<tr style='background-color:#4CAF50; color:white;'>")
                        .append("<th style='padding:10px; border:1px solid #ddd;'>S.No</th>")
                        .append("<th style='padding:10px; border:1px solid #ddd;'>Name</th>")
                        .append("<th style='padding:10px; border:1px solid #ddd;'>Amount</th>")
                        .append("<th style='padding:10px; border:1px solid #ddd;'>Category</th>")
                        .append("<th style='padding:10px; border:1px solid #ddd;'>Date</th>")
                        .append("</tr>");

                int i = 1;
                for (ExpenseDTO expense : todayExpenses) {
                    String rowColor = (i % 2 == 0) ? "#f2f2f2" : "#ffffff";
                    table.append("<tr style='background-color:").append(rowColor).append(";'>")
                            .append("<td style='padding:10px; border:1px solid #ddd; text-align:center;'>").append(i).append("</td>")
                            .append("<td style='padding:10px; border:1px solid #ddd;'>").append(expense.getName()).append("</td>")
                            .append("<td style='padding:10px; border:1px solid #ddd;'>₹").append(expense.getAmount()).append("</td>")
                            .append("<td style='padding:10px; border:1px solid #ddd;'>").append(expense.getCategoryId() !=null ? expense.getCategoryName(): "N/A").append("</td>")
                            .append("<td style='padding:10px; border:1px solid #ddd;'>").append(expense.getDate()).append("</td>")
                            .append("</tr>");
                    i++;
                }
                table.append("</table>");

                String body= "Hi "+profile.getFullname()+", <br><br> Here is a summary of your expenses for today:<br><br>"+table+"<br><br> Best Regards, <br><br> Money Manager Team";
                emailService.sendEmail(profile.getEmail(),"Your Daily Expense Summary",body);
            }
        }
    }
}
