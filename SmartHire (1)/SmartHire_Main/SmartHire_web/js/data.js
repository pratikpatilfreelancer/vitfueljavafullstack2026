/*Cashe Database using seed constant*/
const DB_KEY = "smarthire_db_v1";

const SEED = {
  nextIds: { user: 7, job: 5, application: 5, interview: 2 },
  users: [
    { userId: 1, username: "recruiter1", password: "password123", role: "RECRUITER", email: "recruiter1@techcorp.com", fullName: "Anita Sharma" },
    { userId: 2, username: "recruiter2", password: "password123", role: "RECRUITER", email: "recruiter2@innovate.com", fullName: "Rahul Mehta" },
    { userId: 3, username: "candidate1", password: "password123", role: "CANDIDATE", email: "candidate1@mail.com", fullName: "Sujit Bhau Kakade" },
    { userId: 4, username: "candidate2", password: "password123", role: "CANDIDATE", email: "candidate2@mail.com", fullName: "Aditya Rao" },
    { userId: 5, username: "candidate3", password: "password123", role: "CANDIDATE", email: "candidate3@mail.com", fullName: "Sneha Kulkarni" },
    { userId: 6, username: "candidate4", password: "password123", role: "CANDIDATE", email: "candidate4@mail.com", fullName: "Vikram Singh" }
  ],
  recruiters: [
    { recruiterId: 1, userId: 1, companyName: "TechCorp Solutions", department: "Engineering" },
    { recruiterId: 2, userId: 2, companyName: "Innovate Labs", department: "Human Resources" }
  ],
  candidates: [
    { candidateId: 1, userId: 3, phone: "9876543210", skills: "Java, Spring Boot, MySQL, REST API, Git", education: "B.E. Computer Science, Pune University", experienceYears: 3, resumeText: "Experienced Java backend developer with 3 years building REST APIs using Spring Boot and MySQL. Familiar with Git workflows and unit testing." },
    { candidateId: 2, userId: 4, phone: "9876543211", skills: "Python, Django, PostgreSQL, Docker, AWS", education: "B.Tech Information Technology, VIT", experienceYears: 2, resumeText: "Python developer with 2 years experience in Django web applications, PostgreSQL, and deploying containerized apps with Docker on AWS." },
    { candidateId: 3, userId: 5, phone: "9876543212", skills: "Java, Swing, JDBC, MySQL, OOP", education: "B.E. Computer Engineering, Mumbai University", experienceYears: 1, resumeText: "Fresh graduate with strong OOP fundamentals in Java, hands-on academic projects using Swing and JDBC with MySQL." },
    { candidateId: 4, userId: 6, phone: "9876543213", skills: "JavaScript, React, Node.js, MongoDB, HTML, CSS", education: "B.Sc Computer Science, Delhi University", experienceYears: 4, resumeText: "Full-stack JavaScript developer with 4 years of experience building React front-ends and Node.js/MongoDB backends." }
  ],
  jobs: [
    { jobId: 1, recruiterId: 1, title: "Java Backend Developer", description: "Looking for a Java developer to build and maintain REST APIs using Spring Boot and MySQL.", requiredSkills: "Java, Spring Boot, MySQL, REST API", minExperience: 2, location: "Pune", status: "OPEN", postedDate: todayISO() },
    { jobId: 2, recruiterId: 1, title: "Junior Java Developer", description: "Entry-level role for a Java/Swing developer to work on desktop applications with JDBC and MySQL.", requiredSkills: "Java, Swing, JDBC, MySQL", minExperience: 0, location: "Pune", status: "OPEN", postedDate: todayISO() },
    { jobId: 3, recruiterId: 2, title: "Full-Stack JavaScript Developer", description: "Full-stack role building React front-ends and Node.js backends with MongoDB.", requiredSkills: "JavaScript, React, Node.js, MongoDB", minExperience: 3, location: "Bangalore", status: "OPEN", postedDate: todayISO() },
    { jobId: 4, recruiterId: 2, title: "Python/Django Developer", description: "Backend developer role using Python, Django and PostgreSQL, with AWS deployment experience.", requiredSkills: "Python, Django, PostgreSQL, AWS", minExperience: 1, location: "Remote", status: "OPEN", postedDate: todayISO() }
  ],
  applications: [
    { applicationId: 1, jobId: 1, candidateId: 1, appliedDate: todayISO(), status: "APPLIED", matchScore: 85.5 },
    { applicationId: 2, jobId: 2, candidateId: 3, appliedDate: todayISO(), status: "SHORTLISTED", matchScore: 92.0 },
    { applicationId: 3, jobId: 3, candidateId: 4, appliedDate: todayISO(), status: "APPLIED", matchScore: 88.75 },
    { applicationId: 4, jobId: 4, candidateId: 2, appliedDate: todayISO(), status: "APPLIED", matchScore: 79.25 }
  ],
  interviews: [
    { interviewId: 1, applicationId: 2, interviewDate: addDaysISO(3), interviewTime: "10:30", mode: "ONLINE", status: "SCHEDULED", feedback: "", rating: 0 }
  ]
};

function todayISO() { return new Date().toISOString().slice(0, 10); }
function addDaysISO(n) { const d = new Date(); d.setDate(d.getDate() + n); return d.toISOString().slice(0, 10); }

const DB = {
  load() {
    let raw = localStorage.getItem(DB_KEY);
    if (!raw) {
      localStorage.setItem(DB_KEY, JSON.stringify(SEED));
      raw = JSON.stringify(SEED);
    }
    return JSON.parse(raw);
  },
  save(db) { localStorage.setItem(DB_KEY, JSON.stringify(db)); },
  reset() { localStorage.setItem(DB_KEY, JSON.stringify(SEED)); },
};

/* Resume Screening split using 70-30 */
const ScreeningService = {
  SKILL_WEIGHT: 0.70,
  EXPERIENCE_WEIGHT: 0.30,

  tokenizeSkills(raw) {
    if (!raw) return [];
    return raw.split(/[,;/]/).map(s => s.trim().toLowerCase()).filter(Boolean);
  },

  calculateSkillMatch(candidate, job) {
    const required = [...new Set(this.tokenizeSkills(job.requiredSkills))];
    if (required.length === 0) return 100.0;
    const profile = ((candidate.skills || "") + " " + (candidate.resumeText || "")).toLowerCase();
    let matched = 0;
    required.forEach(skill => { if (profile.includes(skill)) matched++; });
    return (matched * 100.0) / required.length;
  },

  calculateExperienceMatch(candidate, job) {
    const required = job.minExperience || 0;
    const actual = candidate.experienceYears || 0;
    if (required <= 0) return 100.0;
    if (actual >= required) return 100.0;
    return Math.max(0, (actual / required) * 100.0);
  },

  calculateMatchScore(candidate, job) {
    const skillScore = this.calculateSkillMatch(candidate, job);
    const expScore = this.calculateExperienceMatch(candidate, job);
    const total = skillScore * this.SKILL_WEIGHT + expScore * this.EXPERIENCE_WEIGHT;
    return Math.round(total * 100) / 100;
  },

  scoreLabel(score) {
    if (score >= 80) return "Strong Match";
    if (score >= 60) return "Good Match";
    if (score >= 40) return "Moderate Match";
    return "Weak Match";
  },

  scoreBand(score) {
    if (score >= 80) return "strong";
    if (score >= 60) return "good";
    if (score >= 40) return "moderate";
    return "weak";
  }
};

/* DB Repo to create and update*/
const Repo = {
  db() { return DB.load(); },

  findUserByUsername(username) {
    return this.db().users.find(u => u.username.toLowerCase() === username.toLowerCase());
  },
  findUserById(id) { return this.db().users.find(u => u.userId === id); },

  createUser({ username, password, role, email, fullName }) {
    const db = this.db();
    if (db.users.some(u => u.username.toLowerCase() === username.toLowerCase())) {
      throw new Error("Username already exists.");
    }
    if (db.users.some(u => u.email.toLowerCase() === email.toLowerCase())) {
      throw new Error("Email already registered.");
    }
    const userId = db.nextIds.user++;
    db.users.push({ userId, username, password, role, email, fullName });
    if (role === "CANDIDATE") {
      const candidateId = (db.candidates.at(-1)?.candidateId || 0) + 1;
      db.candidates.push({ candidateId, userId, phone: "", skills: "", education: "", experienceYears: 0, resumeText: "" });
    } else {
      const recruiterId = (db.recruiters.at(-1)?.recruiterId || 0) + 1;
      db.recruiters.push({ recruiterId, userId, companyName: "", department: "" });
    }
    DB.save(db);
    return this.findUserById(userId);
  },

  candidateByUserId(userId) { return this.db().candidates.find(c => c.userId === userId); },
  recruiterByUserId(userId) { return this.db().recruiters.find(r => r.userId === userId); },
  candidateById(id) { return this.db().candidates.find(c => c.candidateId === id); },
  recruiterById(id) { return this.db().recruiters.find(r => r.recruiterId === id); },

  updateCandidate(candidateId, patch) {
    const db = this.db();
    const c = db.candidates.find(x => x.candidateId === candidateId);
    Object.assign(c, patch);
    DB.save(db);
    // re-score every application this candidate has already made
    db.applications.filter(a => a.candidateId === candidateId).forEach(a => {
      const job = db.jobs.find(j => j.jobId === a.jobId);
      if (job) a.matchScore = ScreeningService.calculateMatchScore(c, job);
    });
    DB.save(db);
    return c;
  },

  updateRecruiter(recruiterId, patch) {
    const db = this.db();
    const r = db.recruiters.find(x => x.recruiterId === recruiterId);
    Object.assign(r, patch);
    DB.save(db);
    return r;
  },

  allJobs() { return this.db().jobs; },
  openJobs() { return this.db().jobs.filter(j => j.status === "OPEN"); },
  jobById(id) { return this.db().jobs.find(j => j.jobId === id); },
  jobsByRecruiter(recruiterId) { return this.db().jobs.filter(j => j.recruiterId === recruiterId); },

  createJob(job) {
    const db = this.db();
    const jobId = db.nextIds.job++;
    db.jobs.push({ jobId, status: "OPEN", postedDate: todayISO(), ...job });
    DB.save(db);
    return jobId;
  },
  updateJob(jobId, patch) {
    const db = this.db();
    const j = db.jobs.find(x => x.jobId === jobId);
    Object.assign(j, patch);
    DB.save(db);
  },
  deleteJob(jobId) {
    const db = this.db();
    db.jobs = db.jobs.filter(j => j.jobId !== jobId);
    const deadApps = db.applications.filter(a => a.jobId === jobId).map(a => a.applicationId);
    db.applications = db.applications.filter(a => a.jobId !== jobId);
    db.interviews = db.interviews.filter(i => !deadApps.includes(i.applicationId));
    DB.save(db);
  },

  applicationsByCandidate(candidateId) {
    const db = this.db();
    return db.applications.filter(a => a.candidateId === candidateId).map(a => this.hydrateApplication(a, db));
  },
  applicationsByJob(jobId) {
    const db = this.db();
    return db.applications.filter(a => a.jobId === jobId).map(a => this.hydrateApplication(a, db));
  },
  applicationsByRecruiter(recruiterId) {
    const db = this.db();
    const jobIds = db.jobs.filter(j => j.recruiterId === recruiterId).map(j => j.jobId);
    return db.applications.filter(a => jobIds.includes(a.jobId)).map(a => this.hydrateApplication(a, db));
  },
  hydrateApplication(a, db) {
    const job = db.jobs.find(j => j.jobId === a.jobId);
    const cand = db.candidates.find(c => c.candidateId === a.candidateId);
    const user = cand ? db.users.find(u => u.userId === cand.userId) : null;
    return { ...a, job, candidate: cand, candidateName: user ? user.fullName : "Unknown" };
  },
  applicationById(id) { return this.hydrateApplication(this.db().applications.find(a => a.applicationId === id), this.db()); },

  hasApplied(candidateId, jobId) {
    return this.db().applications.some(a => a.candidateId === candidateId && a.jobId === jobId);
  },
  applyToJob(candidateId, jobId) {
    const db = this.db();
    if (db.applications.some(a => a.candidateId === candidateId && a.jobId === jobId)) {
      throw new Error("You already applied to this job.");
    }
    const cand = db.candidates.find(c => c.candidateId === candidateId);
    const job = db.jobs.find(j => j.jobId === jobId);
    const score = ScreeningService.calculateMatchScore(cand, job);
    const applicationId = db.nextIds.application++;
    db.applications.push({ applicationId, jobId, candidateId, appliedDate: todayISO(), status: "APPLIED", matchScore: score });
    DB.save(db);
    return score;
  },
  updateApplicationStatus(applicationId, status) {
    const db = this.db();
    const a = db.applications.find(x => x.applicationId === applicationId);
    a.status = status;
    DB.save(db);
  },
  rescreenJob(jobId) {
    const db = this.db();
    const job = db.jobs.find(j => j.jobId === jobId);
    db.applications.filter(a => a.jobId === jobId).forEach(a => {
      const cand = db.candidates.find(c => c.candidateId === a.candidateId);
      a.matchScore = ScreeningService.calculateMatchScore(cand, job);
    });
    DB.save(db);
  },

  interviewsByRecruiter(recruiterId) {
    const db = this.db();
    const apps = this.applicationsByRecruiter(recruiterId).map(a => a.applicationId);
    return db.interviews.filter(i => apps.includes(i.applicationId)).map(i => this.hydrateInterview(i, db));
  },
  interviewsByCandidate(candidateId) {
    const db = this.db();
    const apps = db.applications.filter(a => a.candidateId === candidateId).map(a => a.applicationId);
    return db.interviews.filter(i => apps.includes(i.applicationId)).map(i => this.hydrateInterview(i, db));
  },
  hydrateInterview(i, db) {
    const app = this.hydrateApplication(db.applications.find(a => a.applicationId === i.applicationId), db);
    return { ...i, application: app };
  },
  scheduleInterview(applicationId, data) {
    const db = this.db();
    const interviewId = db.nextIds.interview++;
    db.interviews.push({ interviewId, applicationId, status: "SCHEDULED", feedback: "", rating: 0, ...data });
    const app = db.applications.find(a => a.applicationId === applicationId);
    app.status = "INTERVIEW_SCHEDULED";
    DB.save(db);
  },
  updateInterview(interviewId, patch) {
    const db = this.db();
    const i = db.interviews.find(x => x.interviewId === interviewId);
    Object.assign(i, patch);
    DB.save(db);
  },

  reportsForRecruiter(recruiterId) {
    const apps = this.applicationsByRecruiter(recruiterId);
    const jobs = this.jobsByRecruiter(recruiterId);
    const byStatus = {};
    apps.forEach(a => { byStatus[a.status] = (byStatus[a.status] || 0) + 1; });
    const avgByJob = jobs.map(j => {
      const jobApps = apps.filter(a => a.jobId === j.jobId);
      const avg = jobApps.length ? jobApps.reduce((s, a) => s + a.matchScore, 0) / jobApps.length : 0;
      return { job: j, count: jobApps.length, avgScore: Math.round(avg * 100) / 100 };
    });
    return { totalJobs: jobs.length, openJobs: jobs.filter(j => j.status === "OPEN").length, totalApplications: apps.length, byStatus, avgByJob };
  }
};
