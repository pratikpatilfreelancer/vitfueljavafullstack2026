package com.internships.dao;

import com.internships.model.Student;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class StudentDaoImpl implements StudentDao {

    private final JdbcTemplate jdbcTemplate;

    public StudentDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final RowMapper<Student> STUDENT_ROW_MAPPER = new RowMapper<Student>() {
        @Override
        public Student mapRow(ResultSet rs, int rowNum) throws SQLException {
            Student s = new Student();
            s.setStudentId(rs.getString("student_id"));
            s.setName(rs.getString("name"));
            s.setEmail(rs.getString("email"));
            s.setPhone(rs.getString("phone"));
            s.setCollege(rs.getString("college"));
            s.setCourse(rs.getString("course"));
            s.setYear(rs.getInt("year"));
            s.setResumeUrl(rs.getString("resume_url"));
            s.setCreatedAt(rs.getTimestamp("created_at").toString());
            return s;
        }
    };

    @Override
    public List<Student> findAll() {
        String sql = "SELECT * FROM students ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, STUDENT_ROW_MAPPER);
    }

    @Override
    public Student findById(String studentId) {
        String sql = "SELECT * FROM students WHERE student_id = ?";
        List<Student> list = jdbcTemplate.query(sql, STUDENT_ROW_MAPPER, studentId);
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public Student findByEmail(String email) {
        String sql = "SELECT * FROM students WHERE email = ?";
        List<Student> list = jdbcTemplate.query(sql, STUDENT_ROW_MAPPER, email);
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public int save(Student student) {
        String sql = "INSERT INTO students (student_id, name, email, phone, college, course, year, resume_url) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        return jdbcTemplate.update(sql,
                student.getStudentId(),
                student.getName(),
                student.getEmail(),
                student.getPhone(),
                student.getCollege(),
                student.getCourse(),
                student.getYear(),
                student.getResumeUrl()
        );
    }

    @Override
    public int update(Student student) {
        String sql = "UPDATE students SET name = ?, email = ?, phone = ?, college = ?, course = ?, year = ?, resume_url = ? WHERE student_id = ?";
        return jdbcTemplate.update(sql,
                student.getName(),
                student.getEmail(),
                student.getPhone(),
                student.getCollege(),
                student.getCourse(),
                student.getYear(),
                student.getResumeUrl(),
                student.getStudentId()
        );
    }

    @Override
    public int deleteById(String studentId) {
        String sql = "DELETE FROM students WHERE student_id = ?";
        return jdbcTemplate.update(sql, studentId);
    }
}
