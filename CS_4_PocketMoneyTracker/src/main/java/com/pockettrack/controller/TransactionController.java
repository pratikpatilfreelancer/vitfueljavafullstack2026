package com.pockettrack.controller;

import com.pockettrack.model.Transaction;
import com.pockettrack.service.BillScanService;
import com.pockettrack.service.CategoryService;
import com.pockettrack.service.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("/transactions")
public class TransactionController {
    private static final int CURRENT_USER_ID = 1;
    private static final Path BILL_UPLOAD_DIR = Paths.get("src/main/resources/static/bills");

    private final TransactionService transactionService;
    private final CategoryService categoryService;
    private final BillScanService billScanService;

    public TransactionController(
            TransactionService transactionService,
            CategoryService categoryService,
            BillScanService billScanService) {
        this.transactionService = transactionService;
        this.categoryService = categoryService;
        this.billScanService = billScanService;
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("transaction", new Transaction());
        model.addAttribute("categories", categoryService.getAllCategories());
        return "addTransaction";
    }

    @PostMapping("/add")
    public String saveTransaction(
            @ModelAttribute Transaction txn,
            @RequestParam(value = "billImage", required = false) MultipartFile billImage) throws Exception {

        txn.setUserId(CURRENT_USER_ID);

        if (txn.getBillImagePath() == null || txn.getBillImagePath().isBlank()) {
            if (billImage != null && !billImage.isEmpty()) {
                String fileName = UUID.randomUUID() + "_" + safeFileName(billImage.getOriginalFilename());
                Files.createDirectories(BILL_UPLOAD_DIR);
                Path target = BILL_UPLOAD_DIR.resolve(fileName).normalize();
                Files.copy(billImage.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
                txn.setHasBill(true);
                txn.setBillImagePath("/bills/" + fileName);
            }
        } else {
            txn.setHasBill(true);
        }

        transactionService.addTransaction(txn);
        return "redirect:/dashboard";
    }

    @PostMapping("/scan-bill")
    @ResponseBody
    public ResponseEntity<Map<String, String>> scanBill(
            @RequestParam("image") MultipartFile image) throws Exception {

        BigDecimal amount = billScanService.scanTotal(image);

        String fileName = UUID.randomUUID() + "_" + safeFileName(image.getOriginalFilename());
        Files.createDirectories(BILL_UPLOAD_DIR);
        Path target = BILL_UPLOAD_DIR.resolve(fileName).normalize();
        Files.copy(image.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

        return ResponseEntity.ok(Map.of(
                "amount", amount.toPlainString(),
                "billImagePath", "/bills/" + fileName
        ));
    }

    private String safeFileName(String originalFileName) {
        String fileName = originalFileName == null ? "bill.jpg" : Path.of(originalFileName).getFileName().toString();
        return fileName.replaceAll("[^a-zA-Z0-9._-]", "_");
    }
}
