

const Session = {
  KEY: "smarthire_session",
  set(user) { sessionStorage.setItem(this.KEY, JSON.stringify(user)); },
  get() { const r = sessionStorage.getItem(this.KEY); return r ? JSON.parse(r) : null; },
  clear() { sessionStorage.removeItem(this.KEY); },
};

function requireRole(role) {
  const u = Session.get();
  if (!u || u.role !== role) {
    window.location.href = "index.html";
    return null;
  }
  return u;
}

function logout() { Session.clear(); window.location.href = "index.html"; }

function escapeHtml(str) {
  const div = document.createElement("div");
  div.textContent = str ?? "";
  return div.innerHTML;
}

function formatDate(iso) {
  if (!iso) return "—";
  const d = new Date(iso + "T00:00:00");
  return d.toLocaleDateString(undefined, { day: "2-digit", month: "short", year: "numeric" });
}

function initials(name) {
  return (name || "?").split(" ").filter(Boolean).slice(0, 2).map(s => s[0].toUpperCase()).join("");
}


   /*70% skill overlap + 30% experience fit, rendered as an SVG*/
   pro
function scoreRing(score, size = 64) {
  const band = ScreeningService.scoreBand(score);
  const r = (size / 2) - 6;
  const c = 2 * Math.PI * r;
  const offset = c - (Math.min(score, 100) / 100) * c;
  return `
  <div class="score-ring score-ring--${band}" style="width:${size}px;height:${size}px;">
    <svg viewBox="0 0 ${size} ${size}">
      <circle class="score-ring__track" cx="${size/2}" cy="${size/2}" r="${r}"></circle>
      <circle class="score-ring__bar" cx="${size/2}" cy="${size/2}" r="${r}"
        stroke-dasharray="${c}" stroke-dashoffset="${offset}"
        transform="rotate(-90 ${size/2} ${size/2})"></circle>
    </svg>
    <div class="score-ring__value">${Math.round(score)}</div>
  </div>`;
}

const STATUS_META = {
  APPLIED:              { label: "Applied",              cls: "badge-applied" },
  SHORTLISTED:          { label: "Shortlisted",           cls: "badge-shortlisted" },
  INTERVIEW_SCHEDULED:  { label: "Interview Scheduled",   cls: "badge-interview" },
  REJECTED:             { label: "Rejected",              cls: "badge-rejected" },
  HIRED:                { label: "Hired",                 cls: "badge-hired" },
  OPEN:                 { label: "Open",                  cls: "badge-shortlisted" },
  CLOSED:               { label: "Closed",                cls: "badge-rejected" },
  SCHEDULED:            { label: "Scheduled",              cls: "badge-interview" },
  COMPLETED:            { label: "Completed",              cls: "badge-hired" },
  CANCELLED:            { label: "Cancelled",              cls: "badge-rejected" },
};

function statusBadge(status) {
  const meta = STATUS_META[status] || { label: status, cls: "badge-applied" };
  return `<span class="status-badge ${meta.cls}">${meta.label}</span>`;
}

function toast(message, variant = "success") {
  let holder = document.getElementById("toastHolder");
  if (!holder) {
    holder = document.createElement("div");
    holder.id = "toastHolder";
    holder.className = "toast-container position-fixed bottom-0 end-0 p-3";
    holder.style.zIndex = 1080;
    document.body.appendChild(holder);
  }
  const el = document.createElement("div");
  el.className = `toast align-items-center text-bg-${variant} border-0`;
  el.setAttribute("role", "alert");
  el.innerHTML = `<div class="d-flex">
      <div class="toast-body">${escapeHtml(message)}</div>
      <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast"></button>
    </div>`;
  holder.appendChild(el);
  const t = new bootstrap.Toast(el, { delay: 3200 });
  t.show();
  el.addEventListener("hidden.bs.toast", () => el.remove());
}

/* Renders the shared sidebar shell for logged-in pages */
function renderSidebar(activeKey, user) {
  const isRecruiter = user.role === "RECRUITER";
  const items = isRecruiter ? [
    { key: "dashboard", href: "recruiter-dashboard.html", icon: "bi-speedometer2", label: "Dashboard" },
    { key: "jobs", href: "job-management.html", icon: "bi-briefcase", label: "Job Postings" },
    { key: "applicants", href: "applicant-management.html", icon: "bi-people", label: "Applicants & Screening" },
    { key: "interviews", href: "interview-management.html", icon: "bi-calendar-check", label: "Interviews" },
    { key: "reports", href: "reports.html", icon: "bi-graph-up", label: "Reports" },
  ] : [
    { key: "dashboard", href: "candidate-dashboard.html", icon: "bi-speedometer2", label: "Dashboard" },
    { key: "jobs", href: "job-search.html", icon: "bi-search", label: "Find Jobs" },
    { key: "applications", href: "application-status.html", icon: "bi-clipboard-data", label: "My Applications" },
    { key: "profile", href: "candidate-profile.html", icon: "bi-person-badge", label: "My Profile" },
  ];

  return `
  <aside class="shr-sidebar">
    <div class="shr-brand">
      <span class="shr-brand__mark"><i class="bi bi-diagram-3-fill"></i></span>
      <span class="shr-brand__text">Smart<b>Hire</b></span>
    </div>
    <nav class="shr-nav">
      ${items.map(i => `
        <a href="${i.href}" class="shr-nav__link ${activeKey === i.key ? "active" : ""}">
          <i class="bi ${i.icon}"></i><span>${i.label}</span>
        </a>`).join("")}
    </nav>
    <div class="shr-sidebar__footer">
      <div class="shr-user">
        <div class="shr-user__avatar">${initials(user.fullName)}</div>
        <div>
          <div class="shr-user__name">${escapeHtml(user.fullName)}</div>
          <div class="shr-user__role">${isRecruiter ? "Recruiter" : "Candidate"}</div>
        </div>
      </div>
      <button class="btn btn-sm btn-outline-logout w-100 mt-3" onclick="logout()">
        <i class="bi bi-box-arrow-right me-1"></i> Sign out
      </button>
    </div>
  </aside>`;
}

function mountShell(activeKey, requiredRole) {
  const user = requireRole(requiredRole);
  if (!user) return null;
  document.getElementById("shrSidebarMount").innerHTML = renderSidebar(activeKey, user);
  return user;
}
async function registerUserInSQL(data) {

    const response = await fetch(
        "http://localhost:8080/api/users/register",
        {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(data)
        }
    );

    const result = await response.json();

    if (!response.ok) {
        throw new Error(result.message || "Registration failed");
    }

    return result;
}