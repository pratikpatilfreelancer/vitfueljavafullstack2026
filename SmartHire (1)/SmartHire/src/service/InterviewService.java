package service;

import dao.ApplicationDAO;
import dao.InterviewDAO;
import model.Interview;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class InterviewService {

    private final InterviewDAO interviewDAO = new InterviewDAO();
    private final ApplicationDAO applicationDAO = new ApplicationDAO();

    /**
     * Schedules an interview for an application and flips the application's
     * status to INTERVIEW_SCHEDULED.
     */
    public int scheduleInterview(int applicationId, LocalDate date, LocalTime time, String mode) {
        Interview iv = new Interview();
        iv.setApplicationId(applicationId);
        iv.setInterviewDate(date);
        iv.setInterviewTime(time);
        iv.setMode(mode);
        iv.setStatus("SCHEDULED");
        iv.setFeedback("");
        iv.setRating(0);

        int id = interviewDAO.scheduleInterview(iv);
        if (id != -1) {
            applicationDAO.updateStatus(applicationId, "INTERVIEW_SCHEDULED");
        }
        return id;
    }

    public boolean completeInterview(int interviewId, Interview updated) {
        updated.setInterviewId(interviewId);
        updated.setStatus("COMPLETED");
        return interviewDAO.updateInterview(updated);
    }

    public boolean cancelInterview(Interview iv) {
        iv.setStatus("CANCELLED");
        return interviewDAO.updateInterview(iv);
    }

    public List<Interview> getInterviewsForRecruiter(int recruiterId) {
        return interviewDAO.getInterviewsByRecruiter(recruiterId);
    }

    public List<Interview> getInterviewsForCandidate(int candidateId) {
        return interviewDAO.getInterviewsByCandidate(candidateId);
    }
}
