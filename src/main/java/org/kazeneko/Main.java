package org.kazeneko;

import com.opencsv.CSVWriter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        List<Product> products = new ArrayList<>();
        // first page of the category
        Document document = Jsoup.connect("https://rtw.vkusvill.ru/goods/khleb-i-vypechka/").get();
        int currentPageNumberValue = 1;
        while (true) {
            System.out.println("Processing page " + currentPageNumberValue + "...");
            // save all products from the current page
            products.addAll(getAllProductsFromThePage(document));
            // check if the next page exists
            if (!document.getElementsByAttributeValue("data-page",String.valueOf(currentPageNumberValue + 1)).isEmpty()) {
                document = Jsoup.connect("https://rtw.vkusvill.ru/goods/khleb-i-vypechka/" + "?PAGEN_1=" + ++currentPageNumberValue).get();
            } else break;
        }
        // write the product list to a .csv file
        File csvFile = new File("khleb-i-vypechka.csv");
        FileWriter fileWriter = new FileWriter(csvFile);
        CSVWriter csvWriter = new CSVWriter(fileWriter,';',
                CSVWriter.NO_QUOTE_CHARACTER,
                CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                CSVWriter.DEFAULT_LINE_END);
        csvWriter.writeNext(new String[]{"Title","Price"});
        for (Product product : products) {
            csvWriter.writeNext(new String[]{product.getTitle(),String.valueOf(product.getPrice())});
        }
        csvWriter.close();
        fileWriter.close();
    }
    public static List<Product> getAllProductsFromThePage(Document document) {
        Elements allProductTitles = document.getElementsByClass(
                "ProductCard__link rtext _desktop-md _mobile-sm gray900 js-datalayer-catalog-list-name");
        Elements allProductPrices = document.getElementsByClass("Price__value");
        List<Product> products = new ArrayList<>();
        for (int i = 0; i < allProductTitles.size(); i++) {
            products.add(new Product(allProductTitles.get(i).attr("title"),
                    Integer.parseInt(allProductPrices.get(i).text())));
        }
        return products;
    }
}