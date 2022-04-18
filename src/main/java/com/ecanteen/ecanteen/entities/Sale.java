package com.ecanteen.ecanteen.entities;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

public class Sale {
    private String barcode, name, sellingPrice, subtotal;
    private int quantity, discount;

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getSellingPrice() {
        return sellingPrice;
    }

    public void setSellingPrice(String sellingPrice) {
        this.sellingPrice = sellingPrice;
    }

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public String getSubtotal() {
        String[] selling = getSellingPrice().split("\\.");
        StringBuilder price = new StringBuilder();
        for (String s : selling) {
            price.append(s);
        }
        int sellingInt = Integer.parseInt(String.valueOf(price));
        int subtotalStart = sellingInt * getQuantity();
        int subtotalInt = subtotalStart;
        if (getDiscount() != 0) {
            int discountAmount = subtotalStart * getDiscount() / 100;
            subtotalInt = subtotalStart - discountAmount;
        }

        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();
        symbols.setGroupingSeparator('.');
        formatter.setDecimalFormatSymbols(symbols);

        subtotal = formatter.format(subtotalInt);

        return subtotal;
    }

    public void setSubtotal(String subtotal) {
        this.subtotal = subtotal;
    }
}
