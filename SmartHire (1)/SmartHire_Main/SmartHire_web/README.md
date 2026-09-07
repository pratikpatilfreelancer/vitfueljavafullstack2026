# SmartHire — Web Edition (Bootstrap 5)

This is a full front-end conversion of the original **Core Java + Swing +
MySQL** SmartHire desktop app into a static website built with
**Bootstrap 5** (+ Bootstrap Icons) and plain JavaScript. No server,
build step, or database installation is required — it runs entirely in
the browser.

## What changed

| Original (desktop) | This version (web) |
|---|---|
| Java Swing `JFrame`/`JPanel` screens | Bootstrap 5 pages (`.html`) with a sidebar app shell |
| MySQL database via JDBC | Browser `localStorage`, seeded with the same demo data from `sample_data.sql` |
| `ResumeScreeningService.java` | `js/data.js` → `ScreeningService` (same 70% skills / 30% experience formula, ported line-for-line) |
| DAO classes (`UserDAO`, `JobDAO`, …) | `js/data.js` → `Repo` object (same methods, same responsibilities) |
| `SessionManager` | `js/app.js` → `Session` (uses `sessionStorage`) |

All business logic — match scoring, application status flow, interview
scheduling — behaves the same as the Java version. Data persists in your
browser between visits (per-browser, not shared across devices) until you
clear site data.

## How to run it

You don't need Node, Python, or any package manager — it's plain HTML/CSS/JS.
You do need to **serve** the files over `http://` rather than opening them
with `file://`, because the browser blocks `localStorage` access for some
setups on the `file://` protocol.

Pick any one of these:

**Option A — VS Code "Live Server" extension**
1. Open the `SmartHire_web` folder in VS Code.
2. Install the "Live Server" extension.
3. Right-click `index.html` → "Open with Live Server".

**Option B — Python (already on most machines)**
```bash
cd SmartHire_web
python3 -m http.server 8000
```
Then open **http://localhost:8000** in your browser.

**Option C — Node**
```bash
cd SmartHire_web
npx serve .
```

**Option D — just double-click `index.html`**
Works in most modern browsers, but if login/demo data doesn't load, use
Option A/B/C instead.

## Demo accounts

Same as the original project — all passwords are `password123`:

| Username | Role |
|---|---|
| recruiter1 | Recruiter (TechCorp Solutions) |
| recruiter2 | Recruiter (Innovate Labs) |
| candidate1 | Candidate |
| candidate2 | Candidate |
| candidate3 | Candidate |
| candidate4 | Candidate |

Or click **Create Account** on the login screen to register a new
candidate or recruiter account. Click the two **demo** buttons on the
login screen to sign in instantly without typing.

## Pages

- `index.html` — Sign in
- `register.html` — Create account
- `candidate-dashboard.html`, `job-search.html`, `application-status.html`, `candidate-profile.html` — Candidate side
- `recruiter-dashboard.html`, `job-management.html`, `applicant-management.html`, `interview-management.html`, `reports.html` — Recruiter side

## Resetting demo data

Open the browser console on any page and run:
```js
localStorage.removeItem("smarthire_db_v1");
location.reload();
```
This restores the original seeded demo data (same records as
`database/sample_data.sql`).

## Design notes

- **Signature element** — the circular "match score ring" used throughout
  the app visualizes the same explainable score the Java version computes:
  70% skill-keyword overlap + 30% experience fit, shown as a colored
  progress ring (teal = strong, blue = good, amber = moderate, coral = weak).
- **Typography** — Space Grotesk for headings, Inter for body text, IBM
  Plex Mono for scores/data, loaded from Google Fonts.
- **Framework** — Bootstrap 5.3 (CDN) + Bootstrap Icons only; all other
  styling is in `css/style.css` as CSS custom properties (design tokens)
  layered on top of Bootstrap's grid, forms, modals, and tables.
