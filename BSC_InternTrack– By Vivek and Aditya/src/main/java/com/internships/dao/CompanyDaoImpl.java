package com.internships.dao;

import com.internships.model.Company;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class CompanyDaoImpl implements CompanyDao {

    private final JdbcTemplate jdbcTemplate;

    public CompanyDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final RowMapper<Company> COMPANY_ROW_MAPPER = new RowMapper<Company>() {
        @Override
        public Company mapRow(ResultSet rs, int rowNum) throws SQLException {
            Company c = new Company();
            c.setCompanyId(rs.getString("company_id"));
            c.setCompanyName(rs.getString("company_name"));
            c.setLocation(rs.getString("location"));
            c.setIndustry(rs.getString("industry"));
            c.setWebsite(rs.getString("website"));
            c.setContactEmail(rs.getString("contact_email"));
            c.setCreatedAt(rs.getTimestamp("created_at").toString());
            return c;
        }
    };

    @Override
    public List<Company> findAll() {
        String sql = "SELECT * FROM companies ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, COMPANY_ROW_MAPPER);
    }

    @Override
    public Company findById(String companyId) {
        String sql = "SELECT * FROM companies WHERE company_id = ?";
        List<Company> list = jdbcTemplate.query(sql, COMPANY_ROW_MAPPER, companyId);
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public int save(Company company) {
        String sql = "INSERT INTO companies (company_id, company_name, location, industry, website, contact_email) VALUES (?, ?, ?, ?, ?, ?)";
        return jdbcTemplate.update(sql,
                company.getCompanyId(),
                company.getCompanyName(),
                company.getLocation(),
                company.getIndustry(),
                company.getWebsite(),
                company.getContactEmail()
        );
    }

    @Override
    public int update(Company company) {
        String sql = "UPDATE companies SET company_name = ?, location = ?, industry = ?, website = ?, contact_email = ? WHERE company_id = ?";
        return jdbcTemplate.update(sql,
                company.getCompanyName(),
                company.getLocation(),
                company.getIndustry(),
                company.getWebsite(),
                company.getContactEmail(),
                company.getCompanyId()
        );
    }

    @Override
    public int deleteById(String companyId) {
        String sql = "DELETE FROM companies WHERE company_id = ?";
        return jdbcTemplate.update(sql, companyId);
    }
}
