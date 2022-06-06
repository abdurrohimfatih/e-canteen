package com.ecanteen.ecanteen.utils;

import com.ecanteen.ecanteen.controllers.TransactionCashierController;
import com.ecanteen.ecanteen.dao.TransactionDaoImpl;
import com.ecanteen.ecanteen.entities.Sale;
import com.ecanteen.ecanteen.entities.Stock;
import com.ecanteen.ecanteen.entities.Supply;
import com.ecanteen.ecanteen.entities.Transaction;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.view.JasperViewer;
import org.apache.log4j.BasicConfigurator;

import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ReportGenerator {
    public void generateInvoice(TransactionDaoImpl transactionDao, TransactionCashierController controller, ObservableList<Sale> saleData, Transaction transaction) {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
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
                param.put("transactionId", transaction.getId());
                param.put("username", transaction.getUsername());
                param.put("date", transaction.getDate());
                param.put("time", transaction.getTime());
                param.put("totalAmount", Common.totalAmountString);
                param.put("pay", transaction.getPayAmount());
                param.put("change", transaction.getChange());

                try {
                    InputStream inputStream = this.getClass().getResourceAsStream("/com/ecanteen/ecanteen/template/receipt-report.jasper");
                    JasperPrint print = JasperFillManager.fillReport(inputStream, param, new JREmptyDataSource());
                    JasperPrintManager.printReport(print, false);

//                    JasperViewer viewer = new JasperViewer(print, false);
//                    viewer.setVisible(true);
//                    viewer.setFitPageZoomRatio();

                    transactionDao.addSale(saleData, transaction.getId());
                    controller.resetSale();
                } catch (JRException | SQLException | ClassNotFoundException e) {
                    e.printStackTrace();
                }

                return null;
            }
        };

        ExecutorService service = Executors.newCachedThreadPool();
        service.execute(task);
        service.shutdown();
    }

    public void printSupplierHistory(ObservableList<Supply> supplies, String supplier, String date, String total) {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                BasicConfigurator.configure();
                HashMap<String, Object> param = new HashMap<>();
                List<Supply> supplyList = new ArrayList<>();

                for (Supply item : supplies) {
                    Supply supply = new Supply();
                    supply.setProduct(item.getProduct());
                    supply.setSold(item.getSold());
                    supply.setSubtotal(item.getSubtotal());

                    supplyList.add(supply);
                }

                JRBeanCollectionDataSource itemsJRBean = new JRBeanCollectionDataSource(supplyList);

                param.put("DS", itemsJRBean);
                param.put("supplier", supplier);
                param.put("date", date);
                param.put("total", total);

                try {
                    InputStream inputStream = this.getClass().getResourceAsStream("/com/ecanteen/ecanteen/template/supplier-history-report.jasper");
                    JasperPrint print = JasperFillManager.fillReport(inputStream, param, new JREmptyDataSource());
                    JasperPrintManager.printReport(print, false);

//                    JasperViewer viewer = new JasperViewer(print, false);
//                    viewer.setVisible(true);
//                    viewer.setFitPageZoomRatio();
                } catch (JRException e) {
                    e.printStackTrace();
                }

                return null;
            }
        };

        ExecutorService service = Executors.newCachedThreadPool();
        service.execute(task);
        service.shutdown();
    }

    public void printAddStock(ObservableList<Stock> stocks, String date, String employee) {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                BasicConfigurator.configure();
                HashMap<String, Object> param = new HashMap<>();
                List<Stock> stockList = new ArrayList<>();

                for (Stock item : stocks) {
                    Stock stock = new Stock();
                    stock.setId(item.getId());
                    stock.setBarcode(item.getProduct().getBarcode());
                    stock.setName(item.getProduct().getName());
                    stock.setQty(item.getQty());
                    stock.setSupplier(item.getProduct().getSupplier().getName());

                    stockList.add(stock);
                }

                JRBeanCollectionDataSource itemsJRBean = new JRBeanCollectionDataSource(stockList);

                param.put("DS", itemsJRBean);
                param.put("date", date);
                param.put("employee", employee);

                try {
                    InputStream inputStream = this.getClass().getResourceAsStream("/com/ecanteen/ecanteen/template/add-stock.jasper");
                    JasperPrint print = JasperFillManager.fillReport(inputStream, param, new JREmptyDataSource());
//                    JasperPrintManager.printReport(print, true);

                    JasperViewer viewer = new JasperViewer(print, false);
                    viewer.setVisible(true);
                    viewer.setFitPageZoomRatio();
                } catch (JRException e) {
                    e.printStackTrace();
                }

                return null;
            }
        };

        ExecutorService service = Executors.newCachedThreadPool();
        service.execute(task);
        service.shutdown();
    }
}
