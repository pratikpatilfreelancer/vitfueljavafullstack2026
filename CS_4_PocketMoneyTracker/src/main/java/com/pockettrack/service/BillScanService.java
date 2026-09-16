package com.pockettrack.service;

import org.springframework.web.multipart.MultipartFile;
import java.math.BigDecimal;

public interface BillScanService {
    BigDecimal scanTotal(MultipartFile image);
}
