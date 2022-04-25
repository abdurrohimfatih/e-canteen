package com.ecanteen.ecanteen.utils;

import com.ecanteen.ecanteen.entities.Sale;
import com.ecanteen.ecanteen.entities.Supply;
import com.ecanteen.ecanteen.entities.Transaction;
import javafx.collections.ObservableList;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.apache.log4j.BasicConfigurator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ReportGenerator {
    public void generateInvoice(ObservableList<Sale> saleData, Transaction transaction) {
        BasicConfigurator.configure();
        HashMap<String, Object> param = new HashMap<>();
        List<Sale> saleList = new ArrayList<>();

        for (Sale item : saleData) {
            Sale sale = new Sale();
            sale.setName(item.getName());
            sale.setQuantity(item.getQuantity());
            sale.setSellingPrice(item.getSellingPrice());
            sale.setSubtotal(item.getSubtotal());

            saleList.add(sale);
        }

        JRBeanCollectionDataSource itemsJRBean = new JRBeanCollectionDataSource(saleList);

        param.put("DS", itemsJRBean);
        param.put("cashier_name", transaction.getUsername());
        param.put("sale_id", transaction.getId());
        param.put("date", transaction.getDate());
        param.put("time", transaction.getTime());
//                param.put("totalAll", transaction.getTotalAll());
//                param.put("discount", transaction.getTotalDiscount());
        param.put("totalAmount", transaction.getTotalAmount());
        param.put("pay", transaction.getPayAmount());
        param.put("change", transaction.getChange());

        try {
            JasperReport report = JasperCompileManager.compileReport("src/main/java/com/ecanteen/ecanteen/template/receipt-report.jrxml");
            JasperPrint print = JasperFillManager.fillReport(report, param, new JREmptyDataSource());
            JasperPrintManager.printReport(print, false);
        } catch (JRException e) {
            e.printStackTrace();
        }
    }

    public void printAdminSupply(ObservableList<Supply> supplies, String supplier, String date, String total) {
        BasicConfigurator.configure();
        HashMap<String, Object> param = new HashMap<>();
        List<Supply> supplyList = new ArrayList<>();

        for (Supply item : supplies) {
            Supply supply = new Supply();
            supply.setProduct(item.getProduct());
            supply.setQuantity(item.getQuantity());
            supply.setSubtotal(item.getSubtotal());

            supplyList.add(supply);
        }

        JRBeanCollectionDataSource itemsJRBean = new JRBeanCollectionDataSource(supplyList);

        param.put("DS", itemsJRBean);
        param.put("supplier", supplier);
        param.put("date", date);
        param.put("total", total);

        try {
            JasperReport report = JasperCompileManager.compileReport("src/main/java/com/ecanteen/ecanteen/template/admin-supply-history.jrxml");
            JasperPrint print = JasperFillManager.fillReport(report, param, new JREmptyDataSource());
            JasperPrintManager.printReport(print, false);
        } catch (JRException e) {
            e.printStackTrace();
        }
    }
}
