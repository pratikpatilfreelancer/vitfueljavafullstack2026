package model;

/**
 * Candidate profile linked one-to-one with a User record (role = CANDIDATE).
 */
public class Candidate {
    private int candidateId;
    private int userId;
    private String phone;
    private String skills;          // comma separated skill list
    private String education;
    private int experienceYears;
    private String resumeText;      // plain text extract of the resume, used for screening

    public Candidate() {}

    public Candidate(int candidateId, int userId, String phone, String skills,
                      String education, int experienceYears, String resumeText) {
        this.candidateId = candidateId;
        this.userId = userId;
        this.phone = phone;
        this.skills = skills;
        this.education = education;
        this.experienceYears = experienceYears;
        this.resumeText = resumeText;
    }

    public int getCandidateId() { return candidateId; }
    public void setCandidateId(int candidateId) { this.candidateId = candidateId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getSkills() { return skills; }
    public void setSkills(String skills) { this.skills = skills; }

    public String getEducation() { return education; }
    public void setEducation(String education) { this.education = education; }

    public int getExperienceYears() { return experienceYears; }
    public void setExperienceYears(int experienceYears) { this.experienceYears = experienceYears; }

    public String getResumeText() { return resumeText; }
    public void setResumeText(String resumeText) { this.resumeText = resumeText; }
}
