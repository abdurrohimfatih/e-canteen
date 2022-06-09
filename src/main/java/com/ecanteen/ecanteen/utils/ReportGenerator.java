package com.ecanteen.ecanteen.utils;

import com.ecanteen.ecanteen.controllers.TransactionCashierController;
import com.ecanteen.ecanteen.dao.TransactionDaoImpl;
import com.ecanteen.ecanteen.entities.*;
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

    public void printSupplierReport(ObservableList<Supply> supplies, String supplier, String date, int totalAdd, int totalSold, int totalReturn, String total) {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                BasicConfigurator.configure();
                HashMap<String, Object> param = new HashMap<>();
                List<Supply> supplyList = new ArrayList<>();

                for (Supply item : supplies) {
                    Supply supply = new Supply();
                    supply.setBarcode(item.getBarcode());
                    supply.setName(item.getName());
                    supply.setAdded(item.getAdded());
                    supply.setSold(item.getSold());
                    supply.setReturned(item.getReturned());
                    supply.setSubtotal(item.getSubtotal());

                    supplyList.add(supply);
                }

                JRBeanCollectionDataSource itemsJRBean = new JRBeanCollectionDataSource(supplyList);

                InputStream logoStream = this.getClass().getResourceAsStream("/com/ecanteen/ecanteen/image/bts-mart.png");

                param.put("DS", itemsJRBean);
                param.put("supplier", supplier);
                param.put("date", date);
                param.put("total-add", totalAdd);
                param.put("total-sold", totalSold);
                param.put("total-return", totalReturn);
                param.put("total", total);
                param.put("employee", Common.user.getName());
                param.put("bts-mart-dir", logoStream);

                try {
                    InputStream inputStream = this.getClass().getResourceAsStream("/com/ecanteen/ecanteen/template/supplier-report.jasper");
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

    public void printAddReturnStock(ObservableList<Stock> stocks, String date, String employee, String addReturn) {
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

                InputStream logoStream = this.getClass().getResourceAsStream("/com/ecanteen/ecanteen/image/bts-mart.png");

                param.put("DS", itemsJRBean);
                param.put("date", date);
                param.put("employee", employee);
                param.put("bts-mart-dir", logoStream);
                param.put("add-return", addReturn);

                try {
                    InputStream inputStream = this.getClass().getResourceAsStream("/com/ecanteen/ecanteen/template/add-return-stock.jasper");
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

    public void printIncomeReport(ObservableList<Income> incomes, String totalIncome, String totalProfit, String date, String employee) {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                BasicConfigurator.configure();
                HashMap<String, Object> param = new HashMap<>();
                List<Income> incomeList = new ArrayList<>();

                for (Income item : incomes) {
                    Income income = new Income();
                    income.setCashier(item.getCashier());
                    income.setIncome(item.getIncome());
                    income.setProfit(item.getProfit());

                    incomeList.add(income);
                }

                JRBeanCollectionDataSource itemsJRBean = new JRBeanCollectionDataSource(incomeList);

                InputStream logoStream = this.getClass().getResourceAsStream("/com/ecanteen/ecanteen/image/bts-mart.png");

                param.put("DS", itemsJRBean);
                param.put("date", date);
                param.put("employee", employee);
                param.put("bts-mart-dir", logoStream);
                param.put("total-income", totalIncome);
                param.put("total-profit", totalProfit);

                try {
                    InputStream inputStream = this.getClass().getResourceAsStream("/com/ecanteen/ecanteen/template/income-report.jasper");
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
