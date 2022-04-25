package com.ecanteen.ecanteen.utils;

import com.ecanteen.ecanteen.entities.Sale;
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
//        Task<Void> task = new Task<>() {
//            @Override
//            protected Void call() {
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

//                    JasperViewer viewer = new JasperViewer(print, false);
//                    viewer.setVisible(true);
//                    viewer.setFitPageZoomRatio();
//                    viewer.setTitle("e-Canteen System: Printing service");
                } catch (JRException e) {
                    e.printStackTrace();
                }

//                return null;
//            }
//        };
//
//        ExecutorService service = Executors.newCachedThreadPool();
//        service.execute(task);
//        service.shutdown();
    }
}
