package com.pockettrack.dao;

import com.pockettrack.model.SavingsLog;
import java.util.List;

public interface SavingsLogDao {
    SavingsLog save(SavingsLog log);
    List<SavingsLog> findBySavingsId(int savingsId);
}
