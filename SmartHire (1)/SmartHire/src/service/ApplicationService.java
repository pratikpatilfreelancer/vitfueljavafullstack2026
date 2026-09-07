package service;

import dao.ApplicationDAO;
import dao.CandidateDAO;
import dao.JobDAO;
import model.Application;
import model.Candidate;
import model.Job;

import java.time.LocalDate;
import java.util.List;

/**
 * Business logic that sits between the UI and the Application/Job/Candidate
 * DAOs. Keeping this logic out of the UI classes means the screening rules
 * and validation live in one place.
 */
public class ApplicationService {

    private final ApplicationDAO applicationDAO = new ApplicationDAO();
    private final JobDAO jobDAO = new JobDAO();
    private final CandidateDAO candidateDAO = new CandidateDAO();
    private final ResumeScreeningService screeningService = new ResumeScreeningService();

    /**
     * Submits a new application for a candidate, automatically running the
     * resume screening service to compute an initial match score.
     *
     * @return the new application_id, or -1 if the candidate already applied
     *         to this job or the insert failed.
     */
    public int applyToJob(int candidateId, int jobId) {
        if (applicationDAO.hasApplied(jobId, candidateId)) {
            return -1;
        }

        Job job = jobDAO.getJobById(jobId);
        Candidate candidate = candidateDAO.getCandidateById(candidateId);
        if (job == null || candidate == null) {
            return -1;
        }

        double score = screeningService.calculateMatchScore(candidate, job);

        Application app = new Application();
        app.setJobId(jobId);
        app.setCandidateId(candidateId);
        app.setAppliedDate(LocalDate.now());
        app.setStatus("APPLIED");
        app.setMatchScore(score);

        return applicationDAO.createApplication(app);
    }

    /** Re-runs screening for every applicant of a job and persists the updated scores. */
    public void rescreenJob(int jobId) {
        Job job = jobDAO.getJobById(jobId);
        if (job == null) return;

        List<Application> applications = applicationDAO.getApplicationsByJob(jobId);
        for (Application app : applications) {
            Candidate candidate = candidateDAO.getCandidateById(app.getCandidateId());
            if (candidate != null) {
                double score = screeningService.calculateMatchScore(candidate, job);
                applicationDAO.updateMatchScore(app.getApplicationId(), score);
            }
        }
    }

    public boolean shortlist(int applicationId) {
        return applicationDAO.updateStatus(applicationId, "SHORTLISTED");
    }

    public boolean reject(int applicationId) {
        return applicationDAO.updateStatus(applicationId, "REJECTED");
    }

    public boolean markHired(int applicationId) {
        return applicationDAO.updateStatus(applicationId, "HIRED");
    }

    public List<Application> getApplicationsForJob(int jobId) {
        return applicationDAO.getApplicationsByJob(jobId);
    }

    public List<Application> getApplicationsForCandidate(int candidateId) {
        return applicationDAO.getApplicationsByCandidate(candidateId);
    }

    public List<Application> getApplicationsForRecruiter(int recruiterId) {
        return applicationDAO.getApplicationsByRecruiter(recruiterId);
    }
}
