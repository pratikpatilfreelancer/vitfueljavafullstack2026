package com.bakeryvault.utils;

import com.bakeryvault.model.BakedGood;
import com.bakeryvault.model.Customer;
import com.bakeryvault.model.Order;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles all File I/O for the project:
 *  - Binary object serialization of the inventory and customer list
 *  - Plain-text, human-readable order log (BufferedWriter/PrintWriter)
 *  - Reading that log back with BufferedReader
 *
 * Every stream-based method uses try-with-resources so streams are
 * always closed, even if an exception is thrown mid-operation.
 */
public class FileManager {

    private final String dataDirectory;

    public FileManager(String dataDirectory) {
        this.dataDirectory = dataDirectory;
        File dir = new File(dataDirectory);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    // ---------- Object Serialization ----------

    public void saveItems(List<BakedGood> items) throws IOException {
        File file = new File(dataDirectory, "inventory.ser");
        try (FileOutputStream fos = new FileOutputStream(file);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(new ArrayList<>(items));
        }
    }

    @SuppressWarnings("unchecked")
    public List<BakedGood> loadItems() throws IOException, ClassNotFoundException {
        File file = new File(dataDirectory, "inventory.ser");
        if (!file.exists()) {
            return new ArrayList<>();
        }
        try (FileInputStream fis = new FileInputStream(file);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            return (List<BakedGood>) ois.readObject();
        }
    }

    public void saveCustomers(List<Customer> customers) throws IOException {
        File file = new File(dataDirectory, "customers.ser");
        try (FileOutputStream fos = new FileOutputStream(file);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(new ArrayList<>(customers));
        }
    }

    @SuppressWarnings("unchecked")
    public List<Customer> loadCustomers() throws IOException, ClassNotFoundException {
        File file = new File(dataDirectory, "customers.ser");
        if (!file.exists()) {
            return new ArrayList<>();
        }
        try (FileInputStream fis = new FileInputStream(file);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            return (List<Customer>) ois.readObject();
        }
    }

    // ---------- Plain-text order log ----------

    public void appendOrderLog(Order order) throws IOException {
        File file = new File(dataDirectory, "orders.log");
        // 'true' -> append mode, so history accumulates across runs.
        try (FileWriter fw = new FileWriter(file, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter pw = new PrintWriter(bw)) {
            pw.println(order.toLogLine());
        }
    }

    public List<String> readOrderLog() throws IOException {
        File file = new File(dataDirectory, "orders.log");
        List<String> lines = new ArrayList<>();
        if (!file.exists()) {
            return lines;
        }
        try (FileReader fr = new FileReader(file);
             BufferedReader br = new BufferedReader(fr)) {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
        }
        return lines;
    }
}
