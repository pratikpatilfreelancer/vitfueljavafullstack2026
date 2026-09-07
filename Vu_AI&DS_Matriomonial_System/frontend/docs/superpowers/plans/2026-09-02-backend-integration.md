# Heartmate Backend Integration Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Integrate the Spring Boot matrimonial backend (port 8080) with the Next.js heartmate frontend, building all pages (Register, Profiles, Profile Details, Edit Profile, Interests) with the pixel-art theme.

**Architecture:** Next.js App Router frontend connects to Spring Boot REST API via axios. All API calls go through a centralized service layer. Pages match the existing pixel-art design system (Press Start 2P font, pink/cream palette, pixel borders/shadows).

**Tech Stack:** Next.js 16 (App Router), React 19, TypeScript, axios, Tailwind CSS v4, Framer Motion, custom pixel-art CSS

**Spec:** Backend API at `/home/sriyaan/Documents/Capgemini project/matrimonial-jpa-backend/` — controllers define endpoints, entities define data shapes.

## Backend API Reference

| Entity | Endpoint | Method | Body/Params |
|--------|----------|--------|-------------|
| User | `/api/users` | POST | `{ name, email }` |
| User | `/api/users` | GET | — |
| User | `/api/users/{id}` | GET | — |
| User | `/api/users/{id}` | PUT | `{ name, email }` |
| User | `/api/users/{id}` | DELETE | — |
| Profile | `/api/profiles/user/{userId}` | POST | `{ age, gender, city, education, occupation, about }` |
| Profile | `/api/profiles` | GET | — |
| Profile | `/api/profiles/{id}` | GET | — |
| Profile | `/api/profiles/user/{userId}` | GET | — |
| Profile | `/api/profiles/search` | GET | `?gender=&city=&age=` |
| Profile | `/api/profiles/{id}` | PUT | `{ age, gender, city, education, occupation, about }` |
| Profile | `/api/profiles/{id}` | DELETE | — |
| Interest | `/api/interests/send` | POST | `{ senderId, receiverId }` |
| Interest | `/api/interests/sent/{userId}` | GET | — |
| Interest | `/api/interests/received/{userId}` | GET | — |
| Interest | `/api/interests/{id}/accept` | PUT | — |
| Interest | `/api/interests/{id}/reject` | PUT | — |
| Interest | `/api/interests/{id}` | DELETE | — |

**CORS:** Backend allows `http://localhost:3000` (Next.js default port).

## Global Constraints

- Backend runs on `localhost:8080` (Spring Boot, MySQL)
- Frontend runs on `localhost:3000` (Next.js dev server)
- All UI must use pixel-art theme: Press Start 2P font, `--pixel-pink`, `--pixel-cream`, `--pixel-ink`, `--pixel-shadow`, `--pixel-pink-deep`, `--pixel-pink-soft`
- All buttons use `pixel-button` class, all cards use `pixel-border` class
- Dropdowns use custom `PixelDropdown` component (not native `<select>`)
- No external UI libraries (shadcn, MUI, etc.) — all custom pixel-art components

---

### Task 1: API Service Layer

**Files:**
- Create: `src/lib/api.ts`

**Interfaces:**
- Produces: typed axios instance + all API functions matching backend endpoints

- [ ] **Step 1: Create API service with typed functions**

```typescript
// src/lib/api.ts
import axios from "axios";

const api = axios.create({
  baseURL: "http://localhost:8080/api",
});

// ===== Users =====
export interface User {
  userId?: number;
  name: string;
  email: string;
}

export const userApi = {
  create: (data: User) => api.post<User>("/users", data),
  getAll: () => api.get<User[]>("/users"),
  getById: (id: number) => api.get<User>(`/users/${id}`),
  update: (id: number, data: User) => api.put<User>(`/users/${id}`, data),
  delete: (id: number) => api.delete(`/users/${id}`),
};

// ===== Profiles =====
export interface Profile {
  profileId?: number;
  user?: User;
  age: number;
  gender: string;
  city: string;
  education?: string;
  occupation?: string;
  about?: string;
}

export const profileApi = {
  create: (userId: number, data: Omit<Profile, "profileId" | "user">) =>
    api.post<Profile>(`/profiles/user/${userId}`, data),
  getAll: () => api.get<Profile[]>("/profiles"),
  getById: (id: number) => api.get<Profile>(`/profiles/${id}`),
  getByUserId: (userId: number) => api.get<Profile>(`/profiles/user/${userId}`),
  search: (params: { gender?: string; city?: string; age?: number }) =>
    api.get<Profile[]>("/profiles/search", { params }),
  update: (id: number, data: Partial<Profile>) =>
    api.put<Profile>(`/profiles/${id}`, data),
  delete: (id: number) => api.delete(`/profiles/${id}`),
};

// ===== Interests =====
export interface Interest {
  interestId?: number;
  sender?: User;
  receiver?: User;
  status: "PENDING" | "ACCEPTED" | "REJECTED";
}

export const interestApi = {
  send: (senderId: number, receiverId: number) =>
    api.post<Interest>("/interests/send", { senderId, receiverId }),
  getSent: (userId: number) => api.get<Interest[]>(`/interests/sent/${userId}`),
  getReceived: (userId: number) =>
    api.get<Interest[]>(`/interests/received/${userId}`),
  accept: (id: number) => api.put<Interest>(`/interests/${id}/accept`),
  reject: (id: number) => api.put<Interest>(`/interests/${id}/reject`),
  delete: (id: number) => api.delete(`/interests/${id}`),
};

export default api;
```

- [ ] **Step 2: Verify build passes**

Run: `npm run build` in heartmate directory. Expect success.

---

### Task 2: Shared Pixel UI Components

**Files:**
- Create: `src/components/PixelCard.tsx`
- Create: `src/components/PixelInput.tsx`
- Create: `src/components/PixelTextarea.tsx`
- Create: `src/components/PixelButton.tsx`
- Modify: `src/app/globals.css` (add new utility classes)

**Interfaces:**
- Produces: reusable pixel-art form/display components used by all pages

- [ ] **Step 1: Create PixelCard component**

```tsx
// src/components/PixelCard.tsx
"use client";

export function PixelCard({
  children,
  className = "",
}: {
  children: React.ReactNode;
  className?: string;
}) {
  return (
    <div className={`pixel-border pixel-card ${className}`}>{children}</div>
  );
}
```

- [ ] **Step 2: Create PixelInput component**

```tsx
// src/components/PixelInput.tsx
"use client";

interface PixelInputProps extends React.InputHTMLAttributes<HTMLInputElement> {
  label?: string;
}

export function PixelInput({ label, className = "", ...props }: PixelInputProps) {
  return (
    <div className="pixel-field">
      {label && <label className="pixel-field-label">{label}</label>}
      <input className={`pixel-input ${className}`} {...props} />
    </div>
  );
}
```

- [ ] **Step 3: Create PixelTextarea component**

```tsx
// src/components/PixelTextarea.tsx
"use client";

interface PixelTextareaProps extends React.TextareaHTMLAttributes<HTMLTextAreaElement> {
  label?: string;
}

export function PixelTextarea({ label, className = "", ...props }: PixelTextareaProps) {
  return (
    <div className="pixel-field">
      {label && <label className="pixel-field-label">{label}</label>}
      <textarea className={`pixel-textarea ${className}`} {...props} />
    </div>
  );
}
```

- [ ] **Step 4: Create PixelButton component**

```tsx
// src/components/PixelButton.tsx
"use client";

interface PixelButtonProps extends React.ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: "primary" | "secondary" | "danger" | "teal";
  children: React.ReactNode;
}

export function PixelButton({
  variant = "primary",
  className = "",
  children,
  ...props
}: PixelButtonProps) {
  const variantClass = {
    primary: "pixel-button",
    secondary: "pixel-button-secondary",
    danger: "pixel-button-danger",
    teal: "pixel-lets-begin",
  }[variant];

  return (
    <button className={`${variantClass} ${className}`} {...props}>
      {children}
    </button>
  );
}
```

- [ ] **Step 5: Add form field styles to globals.css**

Append to `globals.css`:

```css
/* ===== PIXEL FORM FIELDS ===== */
.pixel-card {
  padding: 1.5rem;
  background: rgba(255, 255, 255, 0.4);
  border-radius: 1.25rem;
}

.pixel-field {
  display: flex;
  flex-direction: column;
  gap: 0.4rem;
  margin-bottom: 1rem;
}

.pixel-field-label {
  font-family: "Press Start 2P", monospace;
  font-size: 0.55rem;
  color: var(--pixel-ink);
}

.pixel-input,
.pixel-textarea {
  width: 100%;
  min-height: 46px;
  border: 3px solid var(--pixel-pink);
  border-radius: 0.2rem;
  background: rgba(255, 255, 255, 0.5);
  box-shadow: 0 4px 0 var(--pixel-shadow);
  color: var(--pixel-ink);
  font-family: "Press Start 2P", monospace;
  font-size: 0.6rem;
  padding: 0 0.75rem;
  outline: none;
  box-sizing: border-box;
}

.pixel-input:focus,
.pixel-textarea:focus {
  outline: 3px solid var(--pixel-cream);
  outline-offset: 2px;
}

.pixel-textarea {
  min-height: 100px;
  padding: 0.6rem 0.75rem;
  resize: vertical;
  line-height: 1.8;
}

.pixel-button-secondary {
  min-height: 46px;
  border: 3px solid var(--pixel-pink);
  border-radius: 0.2rem;
  background: var(--pixel-pink-soft);
  box-shadow: 0 4px 0 var(--pixel-shadow);
  color: var(--pixel-ink);
  font-family: "Press Start 2P", monospace;
  font-size: 0.6rem;
  cursor: pointer;
  transition: transform 120ms ease, box-shadow 120ms ease;
}

.pixel-button-secondary:hover {
  transform: translate(2px, 2px);
  box-shadow: 3px 3px 0 var(--pixel-shadow);
}

.pixel-button-secondary:active {
  transform: translate(5px, 5px);
  box-shadow: 0 0 0 var(--pixel-shadow);
}

.pixel-button-danger {
  min-height: 46px;
  border: 3px solid #c0392b;
  border-radius: 0.2rem;
  background: #e74c3c;
  box-shadow: 0 4px 0 #962d22;
  color: white;
  font-family: "Press Start 2P", monospace;
  font-size: 0.6rem;
  cursor: pointer;
  transition: transform 120ms ease, box-shadow 120ms ease;
}

.pixel-button-danger:hover {
  transform: translate(2px, 2px);
  box-shadow: 3px 3px 0 #962d22;
}

.pixel-button-danger:active {
  transform: translate(5px, 5px);
  box-shadow: 0 0 0 #962d22;
}

.pixel-alert-success {
  padding: 0.8rem 1rem;
  margin-bottom: 1rem;
  border: 3px solid #27ae60;
  background: rgba(39, 174, 96, 0.15);
  color: #27ae60;
  font-family: "Press Start 2P", monospace;
  font-size: 0.55rem;
  line-height: 1.7;
}

.pixel-alert-error {
  padding: 0.8rem 1rem;
  margin-bottom: 1rem;
  border: 3px solid #e74c3c;
  background: rgba(231, 76, 60, 0.15);
  color: #e74c3c;
  font-family: "Press Start 2P", monospace;
  font-size: 0.55rem;
  line-height: 1.7;
}

.pixel-page-title {
  margin: 0 0 1.5rem;
  color: var(--pixel-ink);
  font-family: "Press Start 2P", monospace;
  font-size: clamp(1rem, 2.5vw, 1.6rem);
  text-shadow: 3px 3px 0 var(--pixel-cream);
}
```

- [ ] **Step 6: Verify build passes**

---

### Task 3: Update CORS Config (Backend)

**Files:**
- Modify: `/home/sriyaan/Documents/Capgemini project/matrimonial-jpa-backend/src/main/java/com/matrimonial/config/CorsConfig.java`

**Interfaces:**
- Backend must also allow `http://localhost:3001` in case Next.js runs on 3001

- [ ] **Step 1: Update CORS to allow both ports**

Change `allowedOrigins` to:
```java
.allowedOrigins("http://localhost:3000", "http://localhost:3001")
```

- [ ] **Step 2: Rebuild backend**

Run: `mvn clean compile` in backend directory.

---

### Task 4: Register Page (User CRUD)

**Files:**
- Create: `src/app/register/page.tsx`
- Create: `src/components/UserTable.tsx`

**Interfaces:**
- Consumes: `userApi` from `src/lib/api.ts`
- Produces: user list, user creation, user edit, user deletion

- [ ] **Step 1: Create Register page with user form + table**

```tsx
// src/app/register/page.tsx
"use client";

import { useState, useEffect } from "react";
import { userApi, User } from "@/lib/api";
import { PixelInput } from "@/components/PixelInput";
import { PixelButton } from "@/components/PixelButton";

export default function RegisterPage() {
  const [users, setUsers] = useState<User[]>([]);
  const [name, setName] = useState("");
  const [email, setEmail] = useState("");
  const [editingId, setEditingId] = useState<number | null>(null);
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");

  const loadUsers = async () => {
    try {
      const res = await userApi.getAll();
      setUsers(res.data);
    } catch {
      setError("Backend not running. Start Spring Boot on localhost:8080");
    }
  };

  useEffect(() => { loadUsers(); }, []);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setMessage("");
    setError("");
    try {
      if (editingId) {
        await userApi.update(editingId, { name, email });
        setMessage("User updated");
      } else {
        await userApi.create({ name, email });
        setMessage("User created");
      }
      setName("");
      setEmail("");
      setEditingId(null);
      await loadUsers();
    } catch (err: any) {
      setError(err.response?.data?.message || "Operation failed");
    }
  };

  const handleDelete = async (id: number) => {
    if (!confirm("Delete this user?")) return;
    try {
      await userApi.delete(id);
      await loadUsers();
    } catch {
      setError("Delete failed");
    }
  };

  return (
    <div className="match-page">
      <div className="match-stage" style={{ display: "block", padding: "2rem clamp(1rem, 4vw, 4rem)" }}>
        <h1 className="pixel-page-title">User Management</h1>

        <div className="pixel-border pixel-card" style={{ maxWidth: 560, marginBottom: "1.5rem" }}>
          <h2 className="form-heading">{editingId ? "Update User" : "Add User"}</h2>
          {message && <div className="pixel-alert-success">{message}</div>}
          {error && <div className="pixel-alert-error">{error}</div>}
          <form onSubmit={handleSubmit}>
            <PixelInput label="Name" value={name} onChange={e => setName(e.target.value)} required />
            <PixelInput label="Email" type="email" value={email} onChange={e => setEmail(e.target.value)} required />
            <div style={{ display: "flex", gap: "0.75rem" }}>
              <PixelButton type="submit">{editingId ? "Update" : "Create"}</PixelButton>
              {editingId && (
                <PixelButton variant="secondary" type="button" onClick={() => { setEditingId(null); setName(""); setEmail(""); }}>
                  Cancel
                </PixelButton>
              )}
            </div>
          </form>
        </div>

        <div className="pixel-border pixel-card">
          <h2 className="form-heading">All Users</h2>
          {users.map(u => (
            <div key={u.userId} style={{ display: "flex", justifyContent: "space-between", alignItems: "center", padding: "0.75rem 0", borderBottom: "2px dashed var(--pixel-pink-soft)" }}>
              <span style={{ fontFamily: "Press Start 2P", fontSize: "0.55rem", color: "var(--pixel-ink)" }}>
                #{u.userId} {u.name} — {u.email}
              </span>
              <span style={{ display: "flex", gap: "0.5rem" }}>
                <PixelButton variant="secondary" onClick={() => { setEditingId(u.userId!); setName(u.name); setEmail(u.email); }}>
                  Edit
                </PixelButton>
                <PixelButton variant="danger" onClick={() => handleDelete(u.userId!)}>
                  Delete
                </PixelButton>
              </span>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}
```

- [ ] **Step 2: Verify build passes**

---

### Task 5: Profiles Page (Browse + Search)

**Files:**
- Create: `src/app/profiles/page.tsx`
- Create: `src/components/ProfileCard.tsx`

**Interfaces:**
- Consumes: `profileApi` from `src/lib/api.ts`
- Produces: profile grid, search filters, profile cards

- [ ] **Step 1: Create ProfileCard component**

```tsx
// src/components/ProfileCard.tsx
"use client";

import { Profile } from "@/lib/api";
import { PixelButton } from "@/components/PixelButton";
import { useRouter } from "next/navigation";

export function ProfileCard({ profile }: { profile: Profile }) {
  const router = useRouter();
  return (
    <div className="pixel-border pixel-card" style={{ textAlign: "center" }}>
      <div style={{ fontSize: "2.5rem", marginBottom: "0.5rem" }}>
        {profile.gender === "Female" ? "👩" : profile.gender === "Male" ? "👨" : "🧑"}
      </div>
      <h3 style={{ fontFamily: "Press Start 2P", fontSize: "0.65rem", color: "var(--pixel-pink)", marginBottom: "0.5rem" }}>
        {profile.user?.name || "Unknown"}
      </h3>
      <div style={{ fontFamily: "Press Start 2P", fontSize: "0.5rem", color: "var(--pixel-ink)", lineHeight: 2 }}>
        <p>Age: {profile.age}</p>
        <p>City: {profile.city}</p>
        <p>{profile.occupation || "N/A"}</p>
      </div>
      <div style={{ marginTop: "0.75rem" }}>
        <PixelButton onClick={() => router.push(`/profiles/${profile.profileId}`)}>
          View Details
        </PixelButton>
      </div>
    </div>
  );
}
```

- [ ] **Step 2: Create Profiles page with search + grid**

```tsx
// src/app/profiles/page.tsx
"use client";

import { useState, useEffect } from "react";
import { profileApi, Profile } from "@/lib/api";
import { ProfileCard } from "@/components/ProfileCard";
import { PixelDropdown } from "@/components/PixelDropdown";
import { PixelButton } from "@/components/PixelButton";

export default function ProfilesPage() {
  const [profiles, setProfiles] = useState<Profile[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [gender, setGender] = useState("");
  const [city, setCity] = useState("");
  const [age, setAge] = useState("");

  const loadProfiles = async () => {
    setLoading(true);
    try {
      const res = await profileApi.getAll();
      setProfiles(res.data);
    } catch {
      setError("Failed to load profiles");
    }
    setLoading(false);
  };

  useEffect(() => { loadProfiles(); }, []);

  const handleSearch = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError("");
    try {
      const params: any = {};
      if (gender) params.gender = gender;
      if (city) params.city = city;
      if (age) params.age = parseInt(age);
      const res = await profileApi.search(params);
      setProfiles(res.data);
    } catch {
      setError("Search failed");
    }
    setLoading(false);
  };

  return (
    <div className="match-page">
      <div className="match-stage" style={{ display: "block", padding: "2rem clamp(1rem, 4vw, 4rem)" }}>
        <h1 className="pixel-page-title">Browse Profiles</h1>

        <div className="pixel-border pixel-card" style={{ marginBottom: "1.5rem" }}>
          <form onSubmit={handleSearch} style={{ display: "flex", flexWrap: "wrap", gap: "0.75rem", alignItems: "flex-end" }}>
            <PixelDropdown value={gender} onChange={setGender} placeholder="All Genders" options={[
              { value: "Male", label: "Male" },
              { value: "Female", label: "Female" },
            ]} />
            <input className="pixel-input" style={{ width: 150 }} placeholder="City" value={city} onChange={e => setCity(e.target.value)} />
            <input className="pixel-input" style={{ width: 100 }} type="number" placeholder="Age" value={age} onChange={e => setAge(e.target.value)} />
            <PixelButton type="submit">Search</PixelButton>
            <PixelButton variant="secondary" type="button" onClick={() => { setGender(""); setCity(""); setAge(""); loadProfiles(); }}>
              Reset
            </PixelButton>
          </form>
        </div>

        {loading && <p style={{ fontFamily: "Press Start 2P", fontSize: "0.6rem", color: "var(--pixel-ink)" }}>Loading...</p>}
        {error && <div className="pixel-alert-error">{error}</div>}
        {!loading && profiles.length === 0 && <p style={{ fontFamily: "Press Start 2P", fontSize: "0.6rem", color: "var(--pixel-ink)" }}>No profiles found.</p>}

        <div style={{ display: "grid", gridTemplateColumns: "repeat(auto-fill, minmax(260px, 1fr))", gap: "1.25rem" }}>
          {profiles.map(p => <ProfileCard key={p.profileId} profile={p} />)}
        </div>
      </div>
    </div>
  );
}
```

- [ ] **Step 3: Verify build passes**

---

### Task 6: Profile Details Page

**Files:**
- Create: `src/app/profiles/[id]/page.tsx`

**Interfaces:**
- Consumes: `profileApi`, `interestApi` from `src/lib/api.ts`
- Produces: full profile view + send interest action

- [ ] **Step 1: Create Profile Details page**

```tsx
// src/app/profiles/[id]/page.tsx
"use client";

import { useState, useEffect } from "react";
import { useParams, useRouter } from "next/navigation";
import { profileApi, Profile } from "@/lib/api";
import { interestApi } from "@/lib/api";
import { PixelButton } from "@/components/PixelButton";

export default function ProfileDetailsPage() {
  const { id } = useParams();
  const router = useRouter();
  const [profile, setProfile] = useState<Profile | null>(null);
  const [error, setError] = useState("");
  const [message, setMessage] = useState("");

  useEffect(() => {
    const load = async () => {
      try {
        const res = await profileApi.getById(Number(id));
        setProfile(res.data);
      } catch {
        setError("Profile not found");
      }
    };
    load();
  }, [id]);

  const handleSendInterest = async () => {
    const senderId = prompt("Enter your user ID:");
    if (!senderId) return;
    if (parseInt(senderId) === profile?.user?.userId) {
      alert("Cannot send interest to yourself");
      return;
    }
    try {
      await interestApi.send(parseInt(senderId), profile!.user!.userId!);
      setMessage("Interest sent!");
    } catch {
      setError("Could not send interest. Check backend.");
    }
  };

  if (error) return <div className="match-page"><div className="match-stage"><div className="pixel-alert-error">{error}</div></div></div>;
  if (!profile) return <div className="match-page"><div className="match-stage"><p style={{ fontFamily: "Press Start 2P", fontSize: "0.6rem" }}>Loading...</p></div></div>;

  return (
    <div className="match-page">
      <div className="match-stage" style={{ display: "block", padding: "2rem clamp(1rem, 4vw, 4rem)" }}>
        <PixelButton variant="secondary" onClick={() => router.back()} style={{ marginBottom: "1.5rem" }}>
          ← Back
        </PixelButton>
        {message && <div className="pixel-alert-success">{message}</div>}
        {error && <div className="pixel-alert-error">{error}</div>}

        <div className="pixel-border pixel-card" style={{ maxWidth: 600 }}>
          <div style={{ textAlign: "center", fontSize: "3rem", marginBottom: "0.75rem" }}>
            {profile.gender === "Female" ? "👩" : profile.gender === "Male" ? "👨" : "🧑"}
          </div>
          <h1 style={{ fontFamily: "Press Start 2P", fontSize: "0.9rem", color: "var(--pixel-pink)", textAlign: "center", marginBottom: "0.5rem" }}>
            {profile.user?.name}
          </h1>
          <p style={{ fontFamily: "Press Start 2P", fontSize: "0.5rem", color: "var(--pixel-ink)", textAlign: "center", marginBottom: "1.5rem" }}>
            {profile.gender} · {profile.age} years · {profile.city}
          </p>

          <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: "1rem", marginBottom: "1.5rem" }}>
            <div>
              <p style={{ fontFamily: "Press Start 2P", fontSize: "0.5rem", color: "var(--pixel-pink)" }}>Email</p>
              <p style={{ fontFamily: "Press Start 2P", fontSize: "0.5rem", color: "var(--pixel-ink)" }}>{profile.user?.email}</p>
            </div>
            <div>
              <p style={{ fontFamily: "Press Start 2P", fontSize: "0.5rem", color: "var(--pixel-pink)" }}>Education</p>
              <p style={{ fontFamily: "Press Start 2P", fontSize: "0.5rem", color: "var(--pixel-ink)" }}>{profile.education || "N/A"}</p>
            </div>
            <div>
              <p style={{ fontFamily: "Press Start 2P", fontSize: "0.5rem", color: "var(--pixel-pink)" }}>Occupation</p>
              <p style={{ fontFamily: "Press Start 2P", fontSize: "0.5rem", color: "var(--pixel-ink)" }}>{profile.occupation || "N/A"}</p>
            </div>
            <div>
              <p style={{ fontFamily: "Press Start 2P", fontSize: "0.5rem", color: "var(--pixel-pink)" }}>City</p>
              <p style={{ fontFamily: "Press Start 2P", fontSize: "0.5rem", color: "var(--pixel-ink)" }}>{profile.city}</p>
            </div>
          </div>

          {profile.about && (
            <div style={{ marginBottom: "1.5rem" }}>
              <p style={{ fontFamily: "Press Start 2P", fontSize: "0.5rem", color: "var(--pixel-pink)", marginBottom: "0.3rem" }}>About</p>
              <p style={{ fontFamily: "Press Start 2P", fontSize: "0.5rem", color: "var(--pixel-ink)", lineHeight: 1.8 }}>{profile.about}</p>
            </div>
          )}

          <PixelButton onClick={handleSendInterest}>Send Interest</PixelButton>
        </div>
      </div>
    </div>
  );
}
```

- [ ] **Step 2: Verify build passes**

---

### Task 7: Edit Profile Page

**Files:**
- Create: `src/app/edit-profile/page.tsx`

**Interfaces:**
- Consumes: `profileApi` from `src/lib/api.ts`
- Produces: create/edit/delete profile

- [ ] **Step 1: Create Edit Profile page**

```tsx
// src/app/edit-profile/page.tsx
"use client";

import { useState } from "react";
import { profileApi, Profile } from "@/lib/api";
import { PixelInput } from "@/components/PixelInput";
import { PixelTextarea } from "@/components/PixelTextarea";
import { PixelDropdown } from "@/components/PixelDropdown";
import { PixelButton } from "@/components/PixelButton";

export default function EditProfilePage() {
  const [userId, setUserId] = useState("");
  const [existing, setExisting] = useState<Profile | null>(null);
  const [age, setAge] = useState("");
  const [gender, setGender] = useState("Male");
  const [city, setCity] = useState("");
  const [education, setEducation] = useState("");
  const [occupation, setOccupation] = useState("");
  const [about, setAbout] = useState("");
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");

  const loadProfile = async () => {
    setMessage("");
    setError("");
    try {
      const res = await profileApi.getByUserId(parseInt(userId));
      const p = res.data;
      setExisting(p);
      setAge(String(p.age));
      setGender(p.gender);
      setCity(p.city);
      setEducation(p.education || "");
      setOccupation(p.occupation || "");
      setAbout(p.about || "");
      setMessage("Profile loaded");
    } catch {
      setExisting(null);
      setMessage("No profile found. You can create one.");
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError("");
    try {
      const data = { age: parseInt(age), gender, city, education, occupation, about };
      if (existing) {
        await profileApi.update(existing.profileId!, data);
        setMessage("Profile updated");
      } else {
        await profileApi.create(parseInt(userId), data);
        setMessage("Profile created");
      }
      await loadProfile();
    } catch (err: any) {
      setError(err.response?.data?.message || "Save failed");
    }
  };

  const handleDelete = async () => {
    if (!existing || !confirm("Delete profile?")) return;
    try {
      await profileApi.delete(existing.profileId!);
      setExisting(null);
      setMessage("Profile deleted");
    } catch {
      setError("Delete failed");
    }
  };

  return (
    <div className="match-page">
      <div className="match-stage" style={{ display: "block", padding: "2rem clamp(1rem, 4vw, 4rem)" }}>
        <h1 className="pixel-page-title">Create / Edit Profile</h1>

        <div className="pixel-border pixel-card" style={{ maxWidth: 560 }}>
          {message && <div className="pixel-alert-success">{message}</div>}
          {error && <div className="pixel-alert-error">{error}</div>}

          <PixelInput label="User ID" value={userId} onChange={e => setUserId(e.target.value)} placeholder="Enter user ID" />
          <PixelButton variant="secondary" onClick={loadProfile} style={{ marginBottom: "1.5rem" }}>Load Profile</PixelButton>

          {userId && (
            <form onSubmit={handleSubmit}>
              <PixelInput label="Age" type="number" min="18" value={age} onChange={e => setAge(e.target.value)} required />
              <PixelDropdown value={gender} onChange={setGender} placeholder="Gender" options={[
                { value: "Male", label: "Male" },
                { value: "Female", label: "Female" },
              ]} />
              <div style={{ height: "1rem" }} />
              <PixelInput label="City" value={city} onChange={e => setCity(e.target.value)} required />
              <PixelInput label="Education" value={education} onChange={e => setEducation(e.target.value)} />
              <PixelInput label="Occupation" value={occupation} onChange={e => setOccupation(e.target.value)} />
              <PixelTextarea label="About" value={about} onChange={e => setAbout(e.target.value)} rows={4} />
              <PixelButton type="submit">{existing ? "Update Profile" : "Create Profile"}</PixelButton>
            </form>
          )}

          {existing && (
            <PixelButton variant="danger" onClick={handleDelete} style={{ marginTop: "1rem" }}>
              Delete Profile
            </PixelButton>
          )}
        </div>
      </div>
    </div>
  );
}
```

- [ ] **Step 2: Verify build passes**

---

### Task 8: Interests Page

**Files:**
- Create: `src/app/interests/page.tsx`

**Interfaces:**
- Consumes: `interestApi` from `src/lib/api.ts`
- Produces: sent/received interests, accept/reject/delete actions

- [ ] **Step 1: Create Interests page**

```tsx
// src/app/interests/page.tsx
"use client";

import { useState } from "react";
import { interestApi, Interest } from "@/lib/api";
import { PixelInput } from "@/components/PixelInput";
import { PixelButton } from "@/components/PixelButton";

export default function InterestsPage() {
  const [userId, setUserId] = useState("");
  const [sent, setSent] = useState<Interest[]>([]);
  const [received, setReceived] = useState<Interest[]>([]);
  const [activeTab, setActiveTab] = useState<"received" | "sent">("received");
  const [error, setError] = useState("");

  const loadInterests = async () => {
    setError("");
    try {
      const s = await interestApi.getSent(parseInt(userId));
      const r = await interestApi.getReceived(parseInt(userId));
      setSent(s.data);
      setReceived(r.data);
    } catch {
      setError("Failed to load interests");
    }
  };

  const handleAccept = async (id: number) => {
    try { await interestApi.accept(id); await loadInterests(); } catch { setError("Accept failed"); }
  };

  const handleReject = async (id: number) => {
    try { await interestApi.reject(id); await loadInterests(); } catch { setError("Reject failed"); }
  };

  const handleDelete = async (id: number) => {
    if (!confirm("Delete?")) return;
    try { await interestApi.delete(id); await loadInterests(); } catch { setError("Delete failed"); }
  };

  const displayed = activeTab === "received" ? received : sent;

  return (
    <div className="match-page">
      <div className="match-stage" style={{ display: "block", padding: "2rem clamp(1rem, 4vw, 4rem)" }}>
        <h1 className="pixel-page-title">Interest Management</h1>
        {error && <div className="pixel-alert-error">{error}</div>}

        <div className="pixel-border pixel-card" style={{ marginBottom: "1.5rem" }}>
          <div style={{ display: "flex", gap: "0.75rem", alignItems: "flex-end" }}>
            <div style={{ flex: 1 }}>
              <PixelInput label="User ID" value={userId} onChange={e => setUserId(e.target.value)} placeholder="Enter user ID" />
            </div>
            <PixelButton onClick={loadInterests}>Load</PixelButton>
          </div>
        </div>

        <div style={{ display: "flex", gap: "0.5rem", marginBottom: "1rem" }}>
          <PixelButton variant={activeTab === "received" ? "primary" : "secondary"} onClick={() => setActiveTab("received")}>
            Received ({received.length})
          </PixelButton>
          <PixelButton variant={activeTab === "sent" ? "primary" : "secondary"} onClick={() => setActiveTab("sent")}>
            Sent ({sent.length})
          </PixelButton>
        </div>

        {displayed.map(i => (
          <div key={i.interestId} className="pixel-border pixel-card" style={{ marginBottom: "0.75rem" }}>
            <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", flexWrap: "wrap", gap: "0.5rem" }}>
              <div>
                <p style={{ fontFamily: "Press Start 2P", fontSize: "0.55rem", color: "var(--pixel-ink)" }}>
                  <strong>{i.sender?.name}</strong> → <strong>{i.receiver?.name}</strong>
                </p>
                <span style={{
                  fontFamily: "Press Start 2P", fontSize: "0.45rem",
                  color: i.status === "ACCEPTED" ? "#27ae60" : i.status === "REJECTED" ? "#e74c3c" : "var(--pixel-pink)",
                  marginTop: "0.25rem", display: "inline-block"
                }}>
                  {i.status}
                </span>
              </div>
              <div style={{ display: "flex", gap: "0.5rem" }}>
                {activeTab === "received" && i.status === "PENDING" && (
                  <>
                    <PixelButton onClick={() => handleAccept(i.interestId!)}>Accept</PixelButton>
                    <PixelButton variant="danger" onClick={() => handleReject(i.interestId!)}>Reject</PixelButton>
                  </>
                )}
                <PixelButton variant="danger" onClick={() => handleDelete(i.interestId!)}>Delete</PixelButton>
              </div>
            </div>
          </div>
        ))}

        {userId && displayed.length === 0 && (
          <p style={{ fontFamily: "Press Start 2P", fontSize: "0.6rem", color: "var(--pixel-ink)" }}>No interests found.</p>
        )}
      </div>
    </div>
  );
}
```

- [ ] **Step 2: Verify build passes**

---

### Task 9: Navigation Layout

**Files:**
- Create: `src/components/Nav.tsx` (replace existing)
- Modify: `src/app/layout.tsx`

**Interfaces:**
- Produces: pixel-art nav bar with links to all pages

- [ ] **Step 1: Create pixel-art Navbar**

```tsx
// src/components/Nav.tsx
"use client";

import Link from "next/link";
import { usePathname } from "next/navigation";

const links = [
  { href: "/", label: "Home" },
  { href: "/register", label: "Users" },
  { href: "/profiles", label: "Profiles" },
  { href: "/edit-profile", label: "Edit Profile" },
  { href: "/interests", label: "Interests" },
];

export function Nav() {
  const pathname = usePathname();

  return (
    <nav style={{
      position: "fixed", top: 0, left: 0, right: 0, zIndex: 100,
      background: "var(--pixel-ink)", borderBottom: "4px solid var(--pixel-pink)",
      padding: "0 clamp(1rem, 4vw, 4rem)",
      display: "flex", alignItems: "center", justifyContent: "space-between",
      height: 60,
    }}>
      <Link href="/" style={{
        fontFamily: "Press Start 2P", fontSize: "0.7rem",
        color: "var(--pixel-pink)", textDecoration: "none",
      }}>
        ♥ Heartmate
      </Link>
      <div style={{ display: "flex", gap: "0.25rem", flexWrap: "wrap" }}>
        {links.map(l => (
          <Link key={l.href} href={l.href} style={{
            fontFamily: "Press Start 2P", fontSize: "0.45rem",
            color: pathname === l.href ? "var(--pixel-pink)" : "var(--pixel-cream)",
            textDecoration: "none", padding: "0.4rem 0.6rem",
            borderRadius: "0.2rem",
            background: pathname === l.href ? "rgba(232,101,138,0.15)" : "transparent",
            transition: "background 150ms ease",
          }}>
            {l.label}
          </Link>
        ))}
      </div>
    </nav>
  );
}
```

- [ ] **Step 2: Update layout.tsx to include Nav + padding**

Replace `src/app/layout.tsx` content:

```tsx
import type { Metadata } from "next";
import { Press_Start_2P, Inter } from "next/font/google";
import "./globals.css";
import { Nav } from "@/components/Nav";

const pressStart2P = Press_Start_2P({
  weight: "400",
  subsets: ["latin"],
  variable: "--font-press-start-2p",
});

const inter = Inter({
  subsets: ["latin"],
  variable: "--font-inter",
});

export const metadata: Metadata = {
  title: "Heartmate",
  description: "Find someone who feels like home.",
};

export default function RootLayout({ children }: { children: React.ReactNode }) {
  return (
    <html lang="en" className={`${pressStart2P.variable} ${inter.variable}`}>
      <body style={{ margin: 0, paddingTop: 60 }}>
        <Nav />
        {children}
      </body>
    </html>
  );
}
```

- [ ] **Step 3: Update landing page to remove its own nav references**

Ensure `page.tsx` has no padding-top conflict (the layout adds `paddingTop: 60`).

- [ ] **Step 4: Verify build passes**

---

### Task 10: Update Search Form to Use Backend

**Files:**
- Modify: `src/app/page.tsx`

**Interfaces:**
- Consumes: `profileApi` from `src/lib/api.ts`
- Produces: search form navigates to `/profiles` with query params

- [ ] **Step 1: Update landing page form to navigate to /profiles with filters**

Replace the form `onSubmit` in `page.tsx`:

```tsx
import { useRouter } from "next/navigation";

// Inside the component:
const router = useRouter();

// In the form onSubmit:
onSubmit={(e) => {
  e.preventDefault();
  const params = new URLSearchParams();
  if (lookingFor) params.set("gender", lookingFor);
  if (ageFrom) params.set("ageFrom", ageFrom);
  if (ageTo) params.set("ageTo", ageTo);
  if (religion) params.set("religion", religion);
  if (motherTongue) params.set("motherTongue", motherTongue);
  router.push(`/profiles?${params.toString()}`);
}}
```

- [ ] **Step 2: Verify build passes**

---

### Task 11: Final Build + Integration Test

- [ ] **Step 1: Start backend**

```bash
cd "/home/sriyaan/Documents/Capgemini project/matrimonial-jpa-backend"
mvn spring-boot:run
```

- [ ] **Step 2: Start frontend**

```bash
cd "/home/sriyaan/Documents/opencode sessions/heartmate"
npm run dev
```

- [ ] **Step 3: Verify all pages render**

- `http://localhost:3000` — Landing page with search form
- `http://localhost:3000/register` — User CRUD
- `http://localhost:3000/profiles` — Browse/search profiles
- `http://localhost:3000/profiles/1` — Profile details
- `http://localhost:3000/edit-profile` — Create/edit profile
- `http://localhost:3000/interests` — Interest management

- [ ] **Step 4: Test CRUD flows**

- Create a user → verify in list
- Create a profile → verify in profiles page
- Search profiles → verify results
- Send interest → verify in interests page
- Accept/reject interest → verify status change

- [ ] **Step 5: Final build**

```bash
npm run build
```
