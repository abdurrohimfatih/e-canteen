package com.ecanteen.ecanteen.utils;

import com.ecanteen.ecanteen.entities.Sale;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;
import org.apache.log4j.BasicConfigurator;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ReportGenerator {
    public void generateInvoice(ObservableList<Sale> saleData) throws JRException {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                BasicConfigurator.configure();
                HashMap<String, Object> param = new HashMap<>();
                List<Sale> saleList = new ArrayList<>();

                for (Sale saleDatum : saleData) {
                    Sale sale = new Sale();
                    sale.setName(saleDatum.getName());
                    sale.setQuantity(saleDatum.getQuantity());
                    sale.setSellingPrice(saleDatum.getSellingPrice());
                    sale.setSubtotal(saleDatum.getSubtotal());
                    saleList.add(sale);
                }

                JRBeanCollectionDataSource itemsJRBean = new JRBeanCollectionDataSource(saleList);

                param.put("DS", itemsJRBean);
                param.put("cashier_name", Common.user.getName());
                param.put("sale_id", Common.saleId);
                param.put("date", Common.date);
                param.put("time", Common.time);
                param.put("total", Common.totalAmount);
                param.put("pay", Common.payAmount);
                param.put("change", Common.change);

//                JasperReport report = JasperCompileManager.compileReport("src/main/java/com/ecanteen/ecanteen/template/receipt-report.jrxml");

//                JasperReport jasperReport = (JasperReport) JRLoader.loadObjectFromFile(new File("").getAbsolutePath() + "/src/main/java/com/ecanteen/ecanteen/template/receipt-report.jasper");

                JasperPrint print = JasperFillManager.fillReport("src/main/java/com/ecanteen/ecanteen/template/receipt-report.jasper", param, itemsJRBean);

                JasperViewer viewer = new JasperViewer(print, false);
                viewer.setVisible(true);
                viewer.setFitPageZoomRatio();
                viewer.setTitle("e-Canteen System: Printing service");

                return null;
            }
        };

        ExecutorService service = Executors.newCachedThreadPool();
        service.execute(task);
        service.shutdown();
    }
}
