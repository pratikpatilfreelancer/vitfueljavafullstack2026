package model;

/**
 * Recruiter profile linked one-to-one with a User record (role = RECRUITER).
 */
public class Recruiter {
    private int recruiterId;
    private int userId;
    private String companyName;
    private String department;

    public Recruiter() {}

    public Recruiter(int recruiterId, int userId, String companyName, String department) {
        this.recruiterId = recruiterId;
        this.userId = userId;
        this.companyName = companyName;
        this.department = department;
    }

    public int getRecruiterId() { return recruiterId; }
    public void setRecruiterId(int recruiterId) { this.recruiterId = recruiterId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
}
