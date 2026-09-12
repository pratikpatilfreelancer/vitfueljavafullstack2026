# HeartMate Frontend Documentation

## Complete Technical Reference for the HeartMate Matrimonial Platform Frontend

---

## Table of Contents

1. [Project Overview](#1-project-overview)
2. [Technology Stack](#2-technology-stack)
3. [Project Structure](#3-project-structure)
4. [Configuration Files](#4-configuration-files)
5. [CSS Architecture & Design System](#5-css-architecture--design-system)
6. [Authentication System](#6-authentication-system)
7. [API Client & Backend Integration](#7-api-client--backend-integration)
8. [Layout & Root Components](#8-layout--root-components)
9. [Page-by-Page Navigation Map](#9-page-by-page-navigation-map)
10. [Core Pages](#10-core-pages)
11. [Pixel Art UI Components](#11-pixel-art-ui-components)
12. [Feature Components](#12-feature-components)
13. [Sarvam AI Chat Integration](#13-sarvam-ai-chat-integration)
14. [React 19 & Next.js 16 Patterns](#14-react-19--nextjs-16-patterns)
15. [TypeScript Usage Throughout](#15-typescript-usage-throughout)
16. [Error Handling Patterns](#16-error-handling-patterns)
17. [Responsive Design Strategy](#17-responsive-design-strategy)
18. [State Management Architecture](#18-state-management-architecture)
19. [Performance Considerations](#19-performance-considerations)
20. [Development Workflow](#20-development-workflow)

---

## 1. Project Overview

HeartMate is an 8-bit pixel-art themed matrimonial and matchmaking platform built as a full-stack web application. The frontend is a single-page application (SPA) built with Next.js 16, React 19, Tailwind CSS v4, and TypeScript, while the backend is a Spring Boot REST API running on `localhost:8080`.

The platform allows users to register accounts, create detailed profiles, browse other profiles using a 3-stage search system, send romantic interests to potential matches, and chat with an AI-powered matchmaker chatbot powered by Sarvam AI. The entire UI follows a distinctive pixel-art aesthetic inspired by retro video games, using the "Press Start 2P" Google Font throughout and custom CSS classes that simulate pixel borders, pixel cards, and retro button styles.

The core user flow follows this path: a visitor lands on the home page with a 3-stage search form, can navigate to login or register, after registration must create a profile, then can browse profiles, send interests, receive and accept/reject interests, and interact with the AI chatbot. Admin users (identified by userId 60 or email "admin@example.com") have access to a dedicated admin panel for managing users, profiles, and interests.

The frontend communicates with the backend exclusively through HTTP requests using Axios, with all authentication handled via session cookies (HttpOnly cookies set by the Spring Boot backend). The `withCredentials: true` flag on the Axios instance ensures cookies are sent with every request, enabling stateless session-based authentication without JWT tokens on the client side.

---

## 2. Technology Stack

### Core Framework
- **Next.js 16.3.4** — The latest version of the React framework using the App Router architecture. Next.js 16 provides server-side rendering (SSR), static site generation (SSG), file-system based routing, and API routes. The project uses only client-side rendering (all pages are "use client") since the app relies on session cookies and dynamic data fetching.
- **React 19.2.8** — The latest stable React version with improved concurrent rendering, automatic batching, and the new `use` hook. While the project primarily uses the traditional `useState`/`useEffect` pattern, React 19's performance improvements benefit the entire application through faster reconciliation and reduced re-renders.
- **React DOM 19.2.8** — The DOM renderer for React, paired with React 19.

### Styling
- **Tailwind CSS v4** — The utility-first CSS framework configured via `@tailwindcss/postcss`. Tailwind v4 uses the new CSS-first configuration approach. The project supplements Tailwind utilities with extensive custom CSS in `globals.css` for the pixel-art design system.
- **Press Start 2P** — A Google Font that provides the retro pixel-art typography used throughout the application for headings, labels, buttons, and decorative text.

### Language
- **TypeScript 5** — Provides static type checking across the entire codebase. All components, hooks, API functions, and utility files use TypeScript interfaces and type annotations.

### HTTP & Data
- **Axios 1.20.0** — Promise-based HTTP client used for all API communication with the Spring Boot backend. Configured with `withCredentials: true` for session cookie support.
- **canvas-confetti 1.9.4** — A lightweight library for creating confetti explosion animations on the canvas element. Used for celebration effects when users successfully send interests.

### Icons & UI
- **lucide-react 1.40.0** — A library of beautifully designed, customizable icons. Available for use though the current codebase primarily uses emoji characters for the pixel-art aesthetic.

### Development Tools
- **ESLint 9** with `eslint-config-next` — Code linting configured for Next.js best practices.
- **@types/node, @types/react, @types/react-dom** — TypeScript type definitions for Node.js, React, and React DOM.

---

## 3. Project Structure

```
frontend/
├── package.json              # Dependencies and scripts
├── next.config.ts            # Next.js configuration (minimal)
├── tsconfig.json             # TypeScript compiler options
├── .env.local                # Environment variables (Sarvam API key)
└── src/
    ├── app/                  # Next.js App Router pages
    │   ├── globals.css       # Global styles & pixel-art design system
    │   ├── layout.tsx        # Root layout wrapping all pages
    │   ├── page.tsx          # Home page with 3-stage search
    │   ├── login/page.tsx    # Login page
    │   ├── register/page.tsx # Registration page
    │   ├── create-profile/page.tsx   # Profile creation form
    │   ├── edit-profile/page.tsx     # Profile editing form
    │   ├── profiles/page.tsx         # Browse/search profiles
    │   ├── profiles/[id]/page.tsx    # Individual profile detail
    │   ├── interests/page.tsx        # Interest management
    │   ├── admin/page.tsx            # Admin dashboard
    │   └── api/chat/route.ts         # Sarvam AI proxy endpoint
    ├── components/           # Reusable UI components
    │   ├── AuthForm.tsx      # Login/register sliding panel
    │   ├── ProfileCard.tsx   # Profile display card
    │   ├── UserMenu.tsx      # Floating user menu dropdown
    │   ├── PixelButton.tsx   # Pixel-art styled button
    │   ├── PixelInput.tsx    # Pixel-art styled input
    │   ├── PixelTextarea.tsx # Pixel-art styled textarea
    │   ├── PixelDropdown.tsx # Pixel-art styled dropdown select
    │   ├── PixelAvatar.tsx   # Gender-based pixel avatar
    │   ├── PhysicsCupid.tsx  # Floating cupid decoration
    │   └── PhoneChatMockup.tsx # AI chat widget
    ├── contexts/
    │   └── AuthContext.tsx   # Authentication state management
    └── lib/
        └── api.ts            # Axios API client & type definitions
```

The directory structure follows Next.js 16 App Router conventions where each folder under `src/app/` represents a route. The `page.tsx` file in each folder defines the UI for that route. The `layout.tsx` at the root level wraps every page with the `AuthProvider` context and the `PhoneChatMockup` widget.

---

## 4. Configuration Files

### package.json

The project is named "heartmate" at version 0.1.0, marked as private (not published to npm). It defines four scripts:

- `npm run dev` — Starts the Next.js development server with hot module replacement (HMR). Uses Turbopack under the hood in Next.js 16 for fast rebuilds.
- `npm run build` — Creates an optimized production build of the application.
- `npm run start` — Serves the production build.
- `npm run lint` — Runs ESLint across the codebase.

The dependencies are carefully chosen to keep the bundle size small while providing all necessary functionality. The `@types/canvas-confetti` package provides TypeScript definitions for the canvas-confetti library.

### next.config.ts

The Next.js configuration is minimal — an empty object. This means all Next.js defaults are used:
- No custom `basePath` or `assetPrefix`
- No image optimization configuration
- No custom webpack configuration
- No redirects or rewrites
- Standard App Router behavior

### tsconfig.json

The TypeScript configuration targets ES2017 for broad browser compatibility while enabling modern features:

- `"target": "ES2017"` — Compiles to ES2017 JavaScript, supported by all modern browsers.
- `"lib": ["dom", "dom.iterable", "esnext"]` — Includes DOM APIs, iterable protocol support, and latest ECMAScript features.
- `"allowJs": true` — Permits JavaScript files alongside TypeScript files.
- `"skipLibCheck": true` — Skips type checking of declaration files (`.d.ts`) for faster compilation.
- `"strict": true` — Enables all strict type-checking options including `noImplicitAny`, `strictNullChecks`, and `strictFunctionTypes`.
- `"noEmit": true` — TypeScript is used only for type checking; Next.js handles the actual compilation.
- `"module": "esnext"` — Uses ES module syntax for tree-shaking compatibility.
- `"moduleResolution": "bundler"` — Uses the bundler resolution algorithm (Next.js 16 default) which resolves `index` files and `.ts` extensions automatically.
- `"jsx": "react-jsx"` — Uses the automatic JSX transform (no need to import React in every file).
- `"incremental": true` — Enables incremental compilation for faster rebuilds.
- `"paths": { "@/*": ["./src/*"] }` — Maps the `@/` import alias to the `src/` directory, allowing clean imports like `import { PixelButton } from "@/components/PixelButton"`.

### .env.local

Contains a single environment variable:
```
SARVAM_API_KEY=sk_xduhgfhx_oei8sGY3invMhVbjFnVaTAfE
```

This key is used by the `/api/chat/route.ts` API route to authenticate with the Sarvam AI service. The `.env.local` file is git-ignored and should never be committed to version control. In production, this would be set as an environment variable on the deployment platform.

---

## 5. CSS Architecture & Design System

### globals.css Structure

The `globals.css` file is the heart of the pixel-art design system. It defines all custom CSS properties, base element styles, utility classes, and animation keyframes. The file follows this structure:

### CSS Custom Properties (Design Tokens)

The design system uses CSS custom properties (CSS variables) defined on the `:root` selector for consistent theming:

- `--pixel-pink` — The primary accent color (a warm pink/red tone for hearts, buttons, and highlights)
- `--pixel-dark` — Dark background color for dark sections
- `--pixel-cream` — Light background color for light sections
- `--pixel-border` — The pixel-art border color (typically a darker shade)
- `--pixel-shadow` — Shadow color for depth effects
- `--pixel-card-bg` — Background color for card components
- `--pixel-text` — Primary text color
- `--pixel-text-muted` — Secondary/muted text color

These variables allow the entire color scheme to be changed from a single location, making theming and dark mode support straightforward.

### Pixel-Art Border System

The pixel-art aesthetic is achieved through a custom border system. Unlike standard CSS borders which are smooth, pixel-art borders use a stepped, aliased appearance. This is implemented using:

- `box-shadow` based borders that create multiple offset layers simulating pixel edges
- A combination of `border` and `box-shadow` properties to create the characteristic 8-bit look
- The `image-rendering: pixelated` CSS property on relevant elements to prevent anti-aliasing

### Font System

The application uses two font families:

1. **Press Start 2P** — Loaded from Google Fonts via a `<link>` tag in the root layout. This is a pixel-art typeface that renders all text in a retro 8-bit style. It is used for headings, labels, button text, and decorative elements. Due to its fixed-width pixel nature, it requires careful sizing — most text using this font is set at very small pixel sizes (0.4rem to 0.7rem) to remain readable.

2. **System font stack** — Used as the body font for longer text passages (about sections, descriptions) where Press Start 2P would be too hard to read. The `font-family` falls back through system fonts for maximum compatibility.

### Utility Classes

The design system defines several utility classes:

- `.pixel-border` — Applies the pixel-art border treatment to any element
- `.pixel-card` — A card container with pixel border, background, and shadow
- `.pixel-button` — Base pixel-art button style
- `.pixel-button-secondary` — Variant for secondary actions
- `.pixel-button-danger` — Variant for destructive actions
- `.pixel-input` — Styled text input field
- `.pixel-textarea` — Styled textarea field
- `.pixel-dropdown` — Custom dropdown select component
- `.pixel-avatar` — Avatar container with pixel-art styling
- `.pixel-field` — Form field wrapper with label and input
- `.pixel-field-label` — Label text for form fields

### Animation Keyframes

The CSS defines several keyframe animations:

- `cupid-hover` — A floating/bobbing animation for the PhysicsCupid decoration elements
- `pixel-pulse` — A subtle pulsing effect for attention-grabbing elements
- `slide-in` / `slide-out` — Panel transition animations for the AuthForm sliding panel
- `confetti` — Fallback keyframe for browsers without canvas support

### Responsive Breakpoints

The responsive design uses standard Tailwind breakpoints supplemented with custom media queries:

- Mobile-first approach with base styles for small screens
- `@media (min-width: 640px)` — Small tablet adjustments
- `@media (min-width: 768px)` — Tablet and small desktop
- `@media (min-width: 1024px)` — Desktop layouts
- `@media (min-width: 1280px)` — Large desktop layouts
- `clamp()` function used for fluid typography and spacing that scales smoothly between breakpoints

---

## 6. Authentication System

### Authentication Flow Overview

The authentication system follows a session-based approach using HTTP-only cookies. The complete flow is:

1. **Registration** (`/register`): User provides name, email, password, and password confirmation. The frontend sends a POST request to `/api/auth/register`. The backend creates the user account and sets a session cookie. The user is redirected to `/create-profile`.

2. **Profile Creation** (`/create-profile`): After registration, users must create a profile with age, gender, city, education, occupation, and about information. This is required before they can browse other profiles.

3. **Login** (`/login`): User provides email and password. The frontend sends a POST request to `/api/auth/login`. The backend validates credentials and sets a session cookie. If the user has a profile, they are redirected to `/`; if not, they are redirected to `/create-profile`.

4. **Session Persistence**: The `AuthContext` calls `authApi.me()` on mount to check if the user has an active session. If the session cookie is valid, the user data is loaded from the backend and the user remains logged in across page refreshes.

5. **Logout**: The frontend sends a POST request to `/api/auth/logout`. The backend invalidates the session and clears the cookie. The user is redirected to `/login`.

### AuthContext Implementation

The `AuthContext.tsx` file implements the authentication state management using React Context. Here is a detailed breakdown:

#### State Variables

```tsx
const [user, setUser] = useState<User | null>(null);
const [profile, setProfile] = useState<Profile | null>(null);
const [loading, setLoading] = useState(true);
```

- `user` — Stores the authenticated user's data (userId, name, email, hasProfile, profileId). Set to `null` when not authenticated.
- `profile` — Stores the user's profile data if they have one. Fetched separately from user data because the profile endpoint returns more detailed information.
- `loading` — Indicates whether the initial auth check is in progress. Used to prevent flash of unauthenticated content.

#### User Interface

```tsx
interface User {
  userId: number;
  name: string;
  email: string;
  hasProfile: boolean;
  profileId: number | null;
}
```

The `User` interface represents the backend's user entity as returned by the `/api/auth/me` endpoint. The `hasProfile` boolean and `profileId` field are critical for determining whether to redirect new users to profile creation.

#### Profile Interface

```tsx
interface Profile {
  profileId: number;
  age: number;
  gender: string;
  city: string;
  education: string;
  occupation: string;
  about: string;
}
```

The `Profile` interface represents the user's dating profile. It is fetched from a separate endpoint (`/api/profiles/{id}`) because profile data includes more fields than the basic user data.

#### checkAuth Function

The `checkAuth` function runs on component mount via `useEffect`. It:

1. Calls `authApi.me()` to check for an active session
2. If successful, sets the `user` state
3. If the user has a profile (`hasProfile` is true), fetches the profile data from `/api/profiles/{profileId}`
4. If the API call fails (no session), clears both user and profile states
5. Sets `loading` to false regardless of success or failure

This function uses the `fetch` API directly for the profile endpoint instead of the Axios instance, likely because the profile endpoint returns data in a slightly different format or to avoid circular dependencies.

#### login Function

The `login` function:
1. Calls `authApi.login()` with email and password
2. Sets the user state from the response
3. If the user has a profile, fetches profile data
4. Redirects to `/create-profile` if no profile exists, otherwise to `/`

#### register Function

The `register` function:
1. Calls `authApi.register()` with name, email, password, and confirmPassword
2. Sets the user state from the response
3. Redirects to `/create-profile` (all new users must create a profile)

#### logout Function

The `logout` function:
1. Calls `authApi.logout()` to invalidate the session on the backend
2. Clears both user and profile states
3. Redirects to `/login`

#### refreshUser Function

The `refreshUser` function re-fetches the current user and profile data. This is called after profile creation or updates to ensure the AuthContext has the latest data. It follows the same pattern as `checkAuth`.

### useAuth Hook

The `useAuth` hook provides a convenient way to access the auth context:

```tsx
export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) throw new Error("useAuth must be used within an AuthProvider");
  return context;
}
```

This hook includes a safety check that throws a descriptive error if used outside the AuthProvider, preventing common React Context mistakes.

### Admin Role Detection

The admin role is determined by a simple check in the `UserMenu` component:

```tsx
const isAdmin = user.userId === 60 || user.email === "admin@example.com";
```

This is a hardcoded check — userId 60 is the admin user in the database, and the email check provides a fallback. This pattern is used in both the UserMenu (to show the admin link) and the admin page (to redirect non-admin users).

---

## 7. API Client & Backend Integration

### Axios Instance Configuration

The `api.ts` file creates a configured Axios instance that serves as the foundation for all API communication:

```tsx
const api = axios.create({
  baseURL: "http://localhost:8080/api",
  withCredentials: true,
  headers: { "Content-Type": "application/json" },
});
```

- `baseURL` — All requests are prefixed with `http://localhost:8080/api`, so individual API functions only need to specify the relative path.
- `withCredentials: true` — Ensures session cookies are sent with every request. This is critical for the session-based authentication to work.
- `Content-Type: application/json` — All requests send JSON bodies.

### TypeScript Interfaces

The API module exports three main interfaces that mirror the backend data models:

#### User Interface
```tsx
export interface User {
  userId: number;
  name: string;
  email: string;
  hasProfile: boolean;
  profileId: number | null;
}
```

#### Profile Interface
```tsx
export interface Profile {
  profileId: number;
  user?: User;
  age: number;
  gender: string;
  city: string;
  education?: string;
  occupation?: string;
  about?: string;
}
```

Note that `user` is optional (marked with `?`) because not all profile endpoints return the associated user object. The `education`, `occupation`, and `about` fields are also optional since they are not required during profile creation.

#### Interest Interface
```tsx
export interface Interest {
  interestId: number | null;
  sender?: User;
  receiver?: User;
  status: string;
}
```

The `Interest` interface represents a romantic interest sent from one user to another. The `status` field can be "PENDING", "ACCEPTED", or "REJECTED".

### API Functions

#### authApi — Authentication Endpoints

- `login(data)` — POST `/api/auth/login` — Sends email and password, returns User object
- `register(data)` — POST `/api/auth/register` — Sends name, email, password, confirmPassword, returns User object
- `logout()` — POST `/api/auth/logout` — Invalidates session, no return data
- `me()` — GET `/api/auth/me` — Returns current authenticated User object

#### userApi — User CRUD Endpoints

- `getAll()` — GET `/api/users` — Returns array of all users
- `getById(id)` — GET `/api/users/{id}` — Returns single user by ID
- `create(data)` — POST `/api/users` — Creates a new user
- `update(id, data)` — PUT `/api/users/{id}` — Updates existing user
- `delete(id)` — DELETE `/api/users/{id}` — Deletes a user

#### profileApi — Profile CRUD & Search Endpoints

- `getAll()` — GET `/api/profiles` — Returns all profiles
- `getById(id)` — GET `/api/profiles/{id}` — Returns single profile by profile ID
- `getByUserId(userId)` — GET `/api/profiles/user/{userId}` — Returns profile by associated user ID
- `search(params)` — GET `/api/profiles/search` — Searches profiles by gender, city, and/or age
- `create(userId, data)` — POST `/api/profiles/user/{userId}` — Creates a profile for a user
- `update(profileId, data)` — PUT `/api/profiles/{profileId}` — Updates existing profile
- `delete(profileId)` — DELETE `/api/profiles/{profileId}` — Deletes a profile

#### interestApi — Interest Management Endpoints

- `send(senderId, receiverId)` — POST `/api/interests/send` — Sends an interest from sender to receiver
- `getSent(senderId)` — GET `/api/interests/sent/{senderId}` — Returns interests sent by a user
- `getReceived(receiverId)` — GET `/api/interests/received/{receiverId}` — Returns interests received by a user
- `accept(interestId)` — PUT `/api/interests/{interestId}/accept` — Accepts a pending interest
- `reject(interestId)` — PUT `/api/interests/{interestId}/reject` — Rejects a pending interest
- `delete(interestId)` — DELETE `/api/interests/{interestId}` — Deletes an interest

### Backend Communication Pattern

All API calls follow a consistent pattern:
1. The Axios instance sends the request with credentials
2. The response is accessed via `res.data` (Axios wraps the response)
3. Errors are caught in try/catch blocks
4. Error messages are extracted from `err.response?.data?.message` when available

The `withCredentials: true` configuration is essential because the Spring Boot backend uses session-based authentication with HttpOnly cookies. Without this flag, the session cookie would not be sent with requests, and every API call after login would fail authentication.

---

## 8. Layout & Root Components

### Root Layout (layout.tsx)

The root layout wraps every page in the application with two essential providers:

```tsx
<html lang="en">
  <head>
    <link href="https://fonts.googleapis.com/css2?family=Press+Start+2P&display=swap" rel="stylesheet" />
  </head>
  <body style={{ margin: 0 }}>
    <AuthProvider>
      {children}
      <PhoneChatMockup />
    </AuthProvider>
  </body>
</html>
```

Key aspects:

1. **Google Fonts Loading** — The Press Start 2P font is loaded via a `<link>` tag in the `<head>`. This ensures the font is available before any content renders, preventing flash of unstyled text (FOUT).

2. **AuthProvider Wrapping** — The `AuthProvider` context wraps all page content (`{children}`), making the authentication state available to every page and component in the application.

3. **PhoneChatMockup** — The AI chat widget is rendered as a sibling to `{children}` inside the AuthProvider, ensuring it has access to auth state while being positioned as a floating overlay that persists across all pages.

4. **Body Margin Reset** — `style={{ margin: 0 }}` removes the default browser margin on the body element, allowing the pixel-art design system to control all spacing.

5. **Metadata Export** — The `metadata` object sets the page title and description for SEO and browser tab display:
   - Title: "Heartmate - Find someone who feels like home"
   - Description: "8-bit Pixel Matrimonial & Matchmaking Platform"

### PhoneChatMockup (Floating Chat Widget)

The `PhoneChatMockup` component is a persistent floating chat widget that appears on every page. It consists of:

1. **Toggle Button** — A heart icon button positioned in the bottom-right corner of the screen. Clicking it opens/closes the chat panel.

2. **Phone-Shaped Container** — When open, displays a phone-shaped chat interface with a pixel-art border, mimicking a mobile phone screen.

3. **Message Display** — Shows conversation history with messages styled as pixel-art bubbles. User messages appear on the right, AI responses on the left.

4. **Input Field** — A text input at the bottom of the phone mockup for typing messages.

5. **AI Integration** — Sends messages to the `/api/chat` API route, which proxies requests to the Sarvam AI API. The AI is configured with a system prompt that makes it respond as a matched profile (e.g., "Priya Patel").

6. **Loading State** — Shows a loading indicator while waiting for AI responses.

7. **Error Handling** — Displays a friendly error message if the AI service is unavailable.

The chat widget is positioned using `position: fixed` with high `z-index` to ensure it floats above all other content.

---

## 9. Page-by-Page Navigation Map

### Navigation Flow

```
/home (Landing/Search)
├── /login (Login Page)
│   ├── → / (if user has profile)
│   └── → /create-profile (if user has no profile)
├── /register (Registration Page)
│   └── → /create-profile (always)
├── /create-profile (Profile Creation)
│   └── → / (after successful creation)
├── /profiles (Browse Profiles)
│   └── /profiles/[id] (Profile Detail)
│       └── → Send Interest (triggers confetti)
├── /edit-profile (Edit Profile)
├── /interests (Interest Management)
├── /admin (Admin Dashboard)
│   ├── → /register (User CRUD)
│   ├── → /edit-profile (Profile CRUD)
│   └── → /interests (Interest CRUD)
```

### Auth Guards

- **Protected Pages**: `/edit-profile`, `/interests`, `/admin`, `/profiles/[id]` — These pages check if the user is authenticated via `useAuth()`. If not logged in, they typically show a loading state or redirect.
- **Admin Guard**: `/admin` — Checks if `user.userId === 60` or `user.email === "admin@example.com"`. Non-admin users are redirected to `/`.
- **Guest-Only Pages**: `/login`, `/register` — If a user is already logged in, these pages could redirect to `/` (though this behavior depends on implementation).

### Inter-Page Linking

- **Home Page** → Links to `/login`, `/register` via the AuthForm
- **UserMenu** → Links to `/`, `/profiles`, `/edit-profile`, `/interests`, `/admin` (admin only)
- **ProfileCard** → Links to `/profiles/{profileId}` for detail view
- **Profile Detail** → "Send Interest" button (no page navigation, updates state)
- **Admin Page** → Links to `/register`, `/edit-profile`, `/interests` for CRUD operations

---

## 10. Core Pages

### Home Page (page.tsx)

The home page implements a 3-stage search system that serves as the main entry point for the application. It uses a `Stage` type to manage the transition between the landing view and results view:

```tsx
type Stage = "landing" | "results";
```

#### State Variables

- `stage` — Controls which view is displayed (landing form or search results)
- `lookingFor` — Gender preference for the search (woman, man, other)
- `ageFrom` — Minimum age filter
- `ageTo` — Maximum age filter
- `profiles` — Array of matching profiles returned from the search
- `error` — Error message for failed API calls
- `loading` — Indicates search in progress
- `message` — Informational message (e.g., "No matches found")

#### Search Implementation

The `handleSearch` function:

1. Prevents default form submission
2. Sets loading state and clears errors
3. Maps the `lookingFor` value to backend gender format ("woman" → "Female", "man" → "Male", "other" → "Other")
4. Builds search params object with only defined values
5. Calls `profileApi.search(params)` if params exist, otherwise `profileApi.getAll()`
6. Applies client-side age filtering on the results
7. If results exist, transitions to "results" stage
8. If no results, shows informational message

#### Landing Stage UI

The landing stage displays:
- The "HeartMate" logo/title with pixel-art styling
- The "Find someone who feels like home" tagline
- The 3-stage search form with:
  - "Looking for" dropdown (Woman/Man/Other)
  - "Age from" dropdown (18-67)
  - "Age to" dropdown (18-67)
  - Search button
- Pixel-art decorative elements (hearts, cupid)
- The PhysicsCupid animations on either side

#### Results Stage UI

The results stage displays:
- A back button to return to the landing stage
- A grid of `ProfileCard` components for each matching profile
- The `PhysicsCupid` decorations
- The search form still visible for refining results

### Login Page (login/page.tsx)

A simple page that renders the `AuthForm` component with `initialMode="login"`. The page serves as a wrapper that provides the authentication form in login mode.

### Registration Page (register/page.tsx)

Similar to the login page but renders the `AuthForm` component with `initialMode="signup"`. This displays the registration form with name, email, password, and confirm password fields.

### Create Profile Page (create-profile/page.tsx)

A form page that allows newly registered users to create their dating profile. The form includes:

- **Age** — Number input for the user's age
- **Gender** — Dropdown select (Male/Female)
- **City** — Text input for the user's city
- **Education** — Text input for educational background
- **Occupation** — Text input for job title/occupation
- **About** — Textarea for a personal description

On submission, the form calls `profileApi.create(userId, formData)` to create the profile on the backend, then calls `refreshUser()` from the AuthContext to update the user's state (specifically to set `hasProfile` to true).

### Edit Profile Page (edit-profile/page.tsx)

A form page that pre-fills data from the existing profile (accessed via `useAuth().profile`). The form uses the same fields as create-profile but with initial values populated. On submission, it calls either:

- `profileApi.update(profileId, formData)` if the profile exists
- `profileApi.create(userId, formData)` if somehow no profile exists (fallback)

The page also includes a delete button that calls `profileApi.delete(profileId)` with a confirmation prompt.

### Browse Profiles Page (profiles/page.tsx)

Loads all profiles on mount via `profileApi.getAll()`. Includes a search form with:
- Gender dropdown filter
- City text input filter
- Age number input filter

Calls `profileApi.search()` with the filter params. Results are displayed as a grid of `ProfileCard` components.

### Profile Detail Page (profiles/[id]/page.tsx)

Loads a single profile by ID from the URL parameters (dynamic route `[id]`). The page displays:

- A large avatar using `PixelAvatar` with `size="large"`
- Full profile information (name, age, gender, city, education, occupation, about)
- A "Send Interest" button that calls `interestApi.send(currentUser.userId, profile.user.userId)`
- On successful interest send, triggers a `canvas-confetti` celebration animation
- A chroma-key video effect (green screen background with overlay)

The `useParams()` hook from Next.js extracts the profile ID from the URL.

### Interests Page (interests/page.tsx)

A tabbed interface for managing romantic interests:

- **Received Requests Tab** — Shows interests sent to the current user. For each pending interest, displays accept and reject buttons.
- **Sent Requests Tab** — Shows interests sent by the current user. Displays the status of each sent interest.

For admin users (userId 60), the page loads ALL interests across all users using `Promise.allSettled()` to handle partial failures gracefully.

Key actions:
- **Accept** — Calls `interestApi.accept(interestId)`
- **Reject** — Calls `interestApi.reject(interestId)`
- **Delete** — Calls `interestApi.delete(interestId)` (admin and sent interests)

### Admin Page (admin/page.tsx)

An admin-only dashboard that provides quick access to CRUD operations:

1. **User CRUD** — Links to `/register` for creating new users
2. **Profile CRUD** — Links to `/edit-profile` for managing profiles
3. **Interest CRUD** — Links to `/interests` for managing interests

The page checks admin status on mount and redirects non-admin users to `/`.

---

## 11. Pixel Art UI Components

### PixelButton

A reusable button component with pixel-art styling:

```tsx
interface PixelButtonProps extends ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: "primary" | "secondary" | "danger";
}
```

**Props:**
- `variant` — Controls the color scheme: "primary" (default pink), "secondary" (neutral), "danger" (red for destructive actions)
- All standard HTML button attributes are forwarded via `...props`

**Implementation:**
- Uses `forwardRef` to allow ref forwarding (important for form libraries and focus management)
- Applies base class `pixel-button` plus variant-specific class
- Sets minimum height of 46px for touch-friendly tap targets
- Uses Press Start 2P font at 0.6rem size for pixel-art text
- `displayName` is set for React DevTools debugging

### PixelInput

A text input component with pixel-art styling:

```tsx
interface PixelInputProps extends InputHTMLAttributes<HTMLInputElement> {
  label?: string;
}
```

**Props:**
- `label` — Optional label text displayed above the input
- All standard HTML input attributes are forwarded

**Implementation:**
- Uses `forwardRef` for ref forwarding
- Wraps the input in a `pixel-field` container
- Optionally renders a `pixel-field-label` for the label
- Applies `pixel-input` class for the pixel-art border and background styling

### PixelTextarea

A textarea component with pixel-art styling:

```tsx
interface PixelTextareaProps extends TextareaHTMLAttributes<HTMLTextAreaElement> {
  label?: string;
}
```

**Props:**
- `label` — Optional label text displayed above the textarea
- All standard HTML textarea attributes are forwarded

**Implementation:**
- Identical pattern to PixelInput but for multi-line text
- Uses `pixel-textarea` class for styling
- Typically used for "About" sections and longer text inputs

### PixelDropdown

A custom dropdown select component that replaces the native HTML `<select>` element:

```tsx
interface PixelDropdownProps {
  value: string;
  onChange: (value: string) => void;
  options: { value: string; label: string }[];
  placeholder?: string;
}
```

**Props:**
- `value` — The currently selected value
- `onChange` — Callback function when a new option is selected
- `options` — Array of value/label pairs for dropdown items
- `placeholder` — Text shown when no option is selected (default: "Select")

**Implementation:**
- Maintains internal `open` state for dropdown visibility
- Uses a `ref` on the container for click-outside detection
- `useEffect` adds a mousedown event listener to close the dropdown when clicking outside
- Displays a trigger button showing the selected label or placeholder
- Renders a dropdown arrow that rotates when open
- The menu is a `<ul>` with `<li>` items that call `onChange` on click
- Selected item is highlighted with a "selected" CSS class

### PixelAvatar

A gender-based avatar component:

```tsx
interface PixelAvatarProps {
  gender: string;
  size?: "card" | "large";
}
```

**Props:**
- `gender` — The gender string ("Male" or "Female")
- `size` — Controls the avatar size: "card" (small, for grid display) or "large" (for detail pages)

**Implementation:**
- Checks if gender is "female" (case-insensitive)
- Renders 👩 emoji for female, 👨 emoji for male
- Applies size-specific CSS class (`pixel-avatar-card` or `pixel-avatar-large`)
- The avatar container has pixel-art border styling

### AuthForm (Login/Register Sliding Panel)

A dual-purpose authentication form with a sliding panel animation:

```tsx
interface AuthFormProps {
  initialMode?: "login" | "signup";
}
```

**Props:**
- `initialMode` — Determines which form is shown first (default: "login")

**State Variables:**
- `mode` — Current form mode (login or signup)
- `name`, `email`, `password`, `confirmPassword` — Form field values
- `showPassword` — Toggle for password visibility
- `rememberMe` — Remember me checkbox state
- `error` — Error message display
- `loading` — Submission in progress flag

**Implementation:**
- Renders a container with two side-by-side panels
- CSS class `mode-login` or `mode-signup` controls which panel is visible via CSS transform
- Login panel has email and password fields
- Signup panel has name, email, password, and confirm password fields
- Both forms have submit handlers that call the appropriate AuthContext function
- Error messages are extracted from `err.response?.data?.message` for backend validation errors
- Loading state disables buttons during submission

---

## 12. Feature Components

### ProfileCard

Displays a profile in a compact card format for grid layouts:

```tsx
interface ProfileCardProps {
  profile: Profile;
}
```

**Props:**
- `profile` — The Profile object to display

**Implementation:**
- Renders a `pixel-border pixel-card` container
- Shows a `PixelAvatar` with the profile's gender
- Displays the user's name (from `profile.user?.name`)
- Shows a metadata line with age, gender, city, education, and occupation
- Displays the "about" text if available
- Includes a "View Profile →" button that navigates to `/profiles/{profileId}`

The component extracts the user's name with a fallback: `profile.user?.name || "Unknown"`. This handles cases where the profile object might not include the nested user object.

### UserMenu (Floating User Menu)

A floating dropdown menu positioned in the top-right corner:

**State Variables:**
- `open` — Controls dropdown visibility

**Implementation:**
- Renders a user icon button (👤 emoji) that toggles the dropdown
- The dropdown contains navigation links:
  - 🏠 Home → `/`
  - 🔍 Browse → `/profiles`
  - ✏️ Edit Profile → `/edit-profile`
  - 💌 Interests → `/interests`
  - 👑 Admin → `/admin` (only visible for admin users)
- A LOGOUT button at the bottom calls the `logout` function from AuthContext
- The dropdown uses `position: fixed` and high `z-index` to float above all content
- Each menu item closes the dropdown after navigation
- The user's name and email are displayed at the top of the dropdown in pixel-art font

### PhysicsCupid

A decorative floating cupid element:

```tsx
interface PhysicsCupidProps {
  side: "left" | "right";
}
```

**Props:**
- `side` — Determines which side of the screen the cupid appears on

**Implementation:**
- Uses a `ref` for potential future physics animations
- Renders a container with class `cupid-wrapper cupid-{side}`
- Displays a bow and arrow emoji (🏹) as the cupid icon
- Includes a decorative string element
- The floating animation is handled by the `cupid-hover` CSS keyframe animation defined in globals.css
- No real physics simulation is used — the animation is pure CSS

### Profile Detail Page Components

The profile detail page (`/profiles/[id]`) includes several notable features:

1. **Large Avatar** — Uses `PixelAvatar` with `size="large"` for a prominent display
2. **Full Profile Info** — Displays all profile fields in a structured layout
3. **Send Interest Button** — Triggers `interestApi.send()` and confetti animation
4. **Canvas Confetti** — Uses the `canvas-confetti` library for celebration effects
5. **Chroma-Key Video** — A green-screen style video effect with overlay

The confetti effect is triggered on successful interest send:
```tsx
import confetti from "canvas-confetti";
// After successful API call:
confetti({ particleCount: 100, spread: 70, origin: { y: 0.6 } });
```

---

## 13. Sarvam AI Chat Integration

### Architecture

The chat system follows a proxy pattern:

1. **Frontend (PhoneChatMockup)** → Sends messages to `/api/chat`
2. **API Route (/api/chat/route.ts)** → Proxies to Sarvam AI API
3. **Sarvam AI API** → Returns AI-generated response

This proxy pattern is necessary because:
- The Sarvam API key must be kept secret (not exposed to the browser)
- CORS restrictions prevent direct browser-to-API communication
- The API route can add custom system prompts and formatting

### API Route Implementation

The `/api/chat/route.ts` file:

1. Extracts `messages`, `partnerName`, and `partnerGender` from the request body
2. Reads the `SARVAM_API_KEY` from environment variables
3. Constructs a system prompt that defines the AI's persona:
   ```
   You are ${partnerName || "Priya Patel"}, a 25-year-old female Financial Analyst 
   from Mumbai who just matched with Sriyaan on HeartMate. You are sweet, charming, 
   playful, and slightly flirtatious. Speak naturally in modern Hinglish (a mix of 
   Hindi and English). Use emojis appropriately. Keep your responses short (1-3 sentences). 
   Never act like a robotic AI assistant.
   ```
4. Sends the conversation to `https://api.sarvam.ai/v1/chat/completions`
5. Uses the model `sarvam-105b-conversations`
6. Sets temperature to 0.7 for balanced creativity/consistency
7. Limits responses to 200 tokens for short, chat-appropriate replies
8. Returns the AI's response to the frontend

### PhoneChatMockup UI

The chat widget provides:

1. **Toggle Button** — Heart icon in bottom-right corner
2. **Phone Container** — Phone-shaped frame with pixel-art borders
3. **Message Bubbles** — Alternating user (right) and AI (left) messages
4. **Input Field** — Text input with send button
5. **Auto-Scroll** — Messages container scrolls to bottom on new messages
6. **Loading State** — Shows loading indicator during AI response
7. **Error Handling** — Friendly error message if AI is unavailable

The chat uses the standard chat completion format with role-based messages:
- `"user"` role for user messages
- `"assistant"` role for AI responses
- `"system"` role for the persona prompt (added by the API route)

---

## 14. React 19 & Next.js 16 Patterns

### Client Components

Every page and component in the application uses the `"use client"` directive. This is because:

1. All pages use React hooks (`useState`, `useEffect`, `useContext`)
2. Authentication state requires client-side session checking
3. Form interactions need real-time state updates
4. Navigation uses `useRouter` which is a client-side hook
5. The Sarvam AI chat requires client-side message management

While Next.js 16 supports Server Components by default, this application opted for client-side rendering throughout because the content is dynamic and user-specific.

### React Hooks Used

- `useState` — Local state management in every component
- `useEffect` — Side effects (API calls, event listeners, DOM manipulation)
- `useContext` — Accessing the AuthContext in all authenticated components
- `useRef` — DOM element references (PhysicsCupid, chat messages scroll, dropdown click-outside)
- `useRouter` — Navigation between pages
- `useParams` — Extracting dynamic route parameters (profile ID)

### Next.js App Router Features

- **File-system routing** — Each `page.tsx` file creates a route
- **Dynamic routes** — `[id]` folder creates `/profiles/{id}` routes
- **Layout nesting** — `layout.tsx` wraps all child pages
- **API routes** — `api/chat/route.ts` creates a server-side API endpoint
- **Metadata export** — `export const metadata` provides SEO information

---

## 15. TypeScript Usage Throughout

### Interface Definitions

The codebase uses TypeScript interfaces extensively:

- `User` — Backend user entity
- `Profile` — Dating profile entity
- `Interest` — Romantic interest entity
- `AuthContextType` — Shape of the authentication context
- `PixelButtonProps` — Button component props
- `PixelInputProps` — Input component props
- `PixelTextareaProps` — Textarea component props
- `PixelDropdownProps` — Dropdown component props
- `PixelAvatarProps` — Avatar component props
- `AuthFormProps` — Auth form component props
- `ProfileCardProps` — Profile card component props
- `PhysicsCupidProps` — Physics cupid component props

### Type Assertions

Type assertions are used in error handling:
```tsx
} catch (err: any) {
  setError(err.response?.data?.message || "Login failed");
}
```

The `any` type is used for caught errors because Axios errors have a complex type structure. The optional chaining (`?.`) safely accesses nested properties.

### Generics

Axios uses generics for typed responses:
```tsx
api.get<User>("/auth/me")
api.post<User>("/auth/login", data)
api.get<Profile[]>("/profiles")
```

This ensures that `res.data` is properly typed, catching type errors at compile time.

### Utility Types

- `Record<string, string>` — Used for mapping objects (e.g., variant classes in PixelButton)
- `React.FormEvent` — Event type for form submissions
- `ReactNode` — Type for React children in the AuthProvider

---

## 16. Error Handling Patterns

### API Error Handling

Every API call follows this pattern:
```tsx
try {
  const res = await someApiCall();
  // Process res.data
} catch (err: any) {
  setError(err.response?.data?.message || "Default error message");
} finally {
  setLoading(false);
}
```

The error message extraction uses optional chaining to handle cases where:
- The backend returns a JSON error with a `message` field
- The backend returns a non-JSON error (e.g., network timeout)
- The error is thrown by Axios before reaching the server

### Form Validation

Form validation happens at two levels:

1. **Client-side** — Required fields are enforced by HTML `required` attribute
2. **Server-side** — Backend validates data and returns error messages

The frontend displays server-side validation errors in the `error` state variable.

### Network Errors

When the backend is unreachable:
- Axios throws a network error
- The catch block sets a user-friendly error message
- Example: "Could not connect to backend. Make sure it's running on localhost:8080"

### Auth Errors

The AuthContext's `checkAuth` function silently catches errors when the session is invalid:
```tsx
try {
  const res = await authApi.me();
  setUser(res.data);
} catch {
  setUser(null);
  setProfile(null);
} finally {
  setLoading(false);
}
```

This prevents the error from propagating and ensures the UI shows the unauthenticated state.

---

## 17. Responsive Design Strategy

### Mobile-First Approach

The design starts with mobile styles and adds complexity for larger screens:

1. **Base styles** — Optimized for mobile phones (320px and up)
2. **Small tablets** — `@media (min-width: 640px)` adds two-column layouts
3. **Tablets** — `@media (min-width: 768px)` adjusts spacing and typography
4. **Desktop** — `@media (min-width: 1024px)` enables full multi-column layouts
5. **Large desktop** — `@media (min-width: 1280px)` maximizes content width

### Fluid Typography

The `clamp()` function is used for typography that scales smoothly:
```css
font-size: clamp(0.5rem, 2vw, 0.8rem);
```

This ensures text is readable on all screen sizes without abrupt jumps at breakpoints.

### Touch-Friendly Design

- Button minimum height: 46px (meets WCAG touch target guidelines)
- Dropdown items have sufficient padding for tap targets
- The floating chat widget is large enough to tap easily

### Layout Adaptations

- Profile card grid adjusts columns based on screen width
- The AuthForm sliding panel stacks vertically on mobile
- The search form adapts from single-column to multi-column layouts
- The user menu dropdown adjusts positioning for different screen sizes

---

## 18. State Management Architecture

### Global State (AuthContext)

The `AuthContext` provides global state for:
- Current user data (`user`)
- Current user's profile (`profile`)
- Authentication loading state (`loading`)
- Authentication actions (`login`, `register`, `logout`, `refreshUser`)

This is the only global state in the application. All other state is local to individual components.

### Local Component State

Each page and component manages its own local state:

- **Form pages** — Track form field values, submission status, and error messages
- **List pages** — Track search filters, results, loading states
- **Detail pages** — Track the loaded entity and related actions
- **Interactive components** — Track open/close states, animations

### State Lifting

Some state is lifted to parent components:
- The `AuthForm` manages both login and signup modes internally
- The `UserMenu` manages its own open/close state
- The `PixelDropdown` manages its own open/close state

### Context Propagation

The AuthContext wraps the entire application at the layout level, making auth state available everywhere without prop drilling. This is essential because:
- The UserMenu needs to know if the user is logged in and their role
- Protected pages need to check authentication
- The chat widget can access user information
- Profile pages can check if the user owns the profile

---

## 19. Performance Considerations

### Client-Side Rendering

Since all pages use `"use client"`, the initial HTML is minimal and JavaScript handles rendering. This means:
- Fast initial page load (small HTML payload)
- Slower time-to-interactive (JavaScript must load and execute)
- Good for dynamic, user-specific content
- Less effective for SEO (though matrimonial platforms typically don't need SEO for authenticated pages)

### Code Splitting

Next.js automatically code-splits by route. Each page loads only the JavaScript it needs:
- The home page doesn't load the admin page code
- The chat widget code is loaded with the layout but is lazy-rendered
- Component code is shared across pages that use the same components

### Image Optimization

The project uses the `next/image` component for optimized images:
- Automatic WebP conversion
- Responsive image sizes
- Lazy loading by default
- Blur placeholder support

### Bundle Size

The dependencies are carefully chosen to minimize bundle size:
- No heavy UI framework (Material UI, Ant Design)
- No state management library (Redux, Zustand)
- No form library (React Hook Form, Formik)
- Lightweight alternatives (canvas-confetti instead of full animation libraries)

### Caching

- Session cookies provide implicit caching for authenticated requests
- The browser caches static assets automatically
- No explicit cache headers are set in the Axios configuration

---

## 20. Development Workflow

### Getting Started

```bash
# Install dependencies
npm install

# Start development server
npm run dev

# The app runs on http://localhost:3000
# The backend must be running on http://localhost:8080
```

### Development Server

The Next.js development server provides:
- Hot Module Replacement (HMR) for instant updates
- Fast Refresh for React component state preservation
- Automatic TypeScript compilation and type checking
- ESLint integration for code quality

### Building for Production

```bash
npm run build
npm start
```

The production build:
- Optimizes JavaScript bundles
- Pre-renders static pages
- Compresses assets
- Generates source maps for debugging

### Code Quality

- **TypeScript** — Catches type errors at compile time
- **ESLint** — Enforces code style and catches common mistakes
- **React DevTools** — Available for component inspection in development

### Debugging

- Console logs for API responses and state changes
- React DevTools for component tree inspection
- Network tab for API request/response debugging
- Next.js error overlay for development errors

### File Naming Conventions

- **Pages** — `page.tsx` in route folders
- **Components** — PascalCase filenames (e.g., `PixelButton.tsx`)
- **Contexts** — PascalCase with "Context" suffix (e.g., `AuthContext.tsx`)
- **API** — camelCase filenames (e.g., `api.ts`)
- **Styles** — kebab-case for CSS files (e.g., `globals.css`)

---

## Appendix A: Complete API Reference

### Authentication API

| Method | Endpoint | Request Body | Response | Description |
|--------|----------|--------------|----------|-------------|
| POST | `/api/auth/login` | `{ email, password }` | `User` | Login with credentials |
| POST | `/api/auth/register` | `{ name, email, password, confirmPassword }` | `User` | Create new account |
| POST | `/api/auth/logout` | — | — | End session |
| GET | `/api/auth/me` | — | `User` | Get current user |

### User API

| Method | Endpoint | Request Body | Response | Description |
|--------|----------|--------------|----------|-------------|
| GET | `/api/users` | — | `User[]` | Get all users |
| GET | `/api/users/{id}` | — | `User` | Get user by ID |
| POST | `/api/users` | `User` | `User` | Create user |
| PUT | `/api/users/{id}` | `User` | `User` | Update user |
| DELETE | `/api/users/{id}` | — | — | Delete user |

### Profile API

| Method | Endpoint | Request Body | Response | Description |
|--------|----------|--------------|----------|-------------|
| GET | `/api/profiles` | — | `Profile[]` | Get all profiles |
| GET | `/api/profiles/{id}` | — | `Profile` | Get profile by ID |
| GET | `/api/profiles/user/{userId}` | — | `Profile` | Get profile by user ID |
| GET | `/api/profiles/search?gender=&city=&age=` | — | `Profile[]` | Search profiles |
| POST | `/api/profiles/user/{userId}` | `Profile` | `Profile` | Create profile |
| PUT | `/api/profiles/{id}` | `Profile` | `Profile` | Update profile |
| DELETE | `/api/profiles/{id}` | — | — | Delete profile |

### Interest API

| Method | Endpoint | Request Body | Response | Description |
|--------|----------|--------------|----------|-------------|
| POST | `/api/interests/send` | `{ senderId, receiverId }` | — | Send interest |
| GET | `/api/interests/sent/{senderId}` | — | `Interest[]` | Get sent interests |
| GET | `/api/interests/received/{receiverId}` | — | `Interest[]` | Get received interests |
| PUT | `/api/interests/{id}/accept` | — | — | Accept interest |
| PUT | `/api/interests/{id}/reject` | — | — | Reject interest |
| DELETE | `/api/interests/{id}` | — | — | Delete interest |

### Chat API (Internal)

| Method | Endpoint | Request Body | Response | Description |
|--------|----------|--------------|----------|-------------|
| POST | `/api/chat` | `{ messages, partnerName, partnerGender }` | `{ reply: string }` | Send message to AI |

---

## Appendix B: CSS Class Reference

### Layout Classes
- `pixel-border` — Applies pixel-art border treatment
- `pixel-card` — Card container with pixel styling
- `pixel-button` — Base button style
- `pixel-button-secondary` — Secondary button variant
- `pixel-button-danger` — Danger button variant
- `pixel-input` — Text input field
- `pixel-textarea` — Textarea field
- `pixel-dropdown` — Custom dropdown container
- `pixel-dropdown-trigger` — Dropdown trigger button
- `pixel-dropdown-menu` — Dropdown menu list
- `pixel-dropdown-item` — Individual dropdown item
- `pixel-dropdown-value` — Selected value display
- `pixel-dropdown-arrow` — Dropdown arrow indicator
- `pixel-avatar` — Avatar container
- `pixel-avatar-card` — Small avatar for cards
- `pixel-avatar-large` — Large avatar for detail pages
- `pixel-field` — Form field wrapper
- `pixel-field-label` — Form field label

### Profile Classes
- `profile-card` — Profile card container
- `profile-card-content` — Card content wrapper
- `profile-card-name` — Profile name text
- `profile-card-meta` — Metadata line (age, gender, city)
- `profile-card-about` — About text
- `profile-card-action` — Action button container

### Navigation Classes
- `user-menu-wrap` — User menu container
- `user-menu-icon` — Toggle button
- `user-menu-dropdown` — Dropdown panel
- `user-menu-item` — Navigation item
- `user-menu-close` — Logout button

### Animation Classes
- `cupid-wrapper` — PhysicsCupid container
- `cupid-left` / `cupid-right` — Position classes
- `cupid-string` — Decorative string
- `pixel-cupid` — Cupid icon container
- `pixel-sky-object` — Floating object base class

---

## Appendix C: Environment Variables

| Variable | Description | Required |
|----------|-------------|----------|
| `SARVAM_API_KEY` | API key for Sarvam AI chat service | Yes (for chat feature) |

---

## Appendix D: Key Dependencies

| Package | Version | Purpose |
|---------|---------|---------|
| `next` | 16.3.4 | React framework |
| `react` | 19.2.8 | UI library |
| `react-dom` | 19.2.8 | DOM renderer |
| `axios` | 1.20.0 | HTTP client |
| `canvas-confetti` | 1.9.4 | Confetti animations |
| `lucide-react` | 1.40.0 | Icon library |
| `tailwindcss` | ^4 | CSS framework |
| `typescript` | ^5 | Type system |

---

## Appendix E: TypeScript Interfaces Reference

### User
```typescript
interface User {
  userId: number;
  name: string;
  email: string;
  hasProfile: boolean;
  profileId: number | null;
}
```

### Profile
```typescript
interface Profile {
  profileId: number;
  user?: User;
  age: number;
  gender: string;
  city: string;
  education?: string;
  occupation?: string;
  about?: string;
}
```

### Interest
```typescript
interface Interest {
  interestId: number | null;
  sender?: User;
  receiver?: User;
  status: string;
}
```

### AuthContextType
```typescript
interface AuthContextType {
  user: User | null;
  profile: Profile | null;
  loading: boolean;
  login: (email: string, password: string) => Promise<void>;
  register: (name: string, email: string, password: string, confirmPassword: string) => Promise<void>;
  logout: () => Promise<void>;
  refreshUser: () => Promise<void>;
}
```

---

## Appendix F: Detailed Component Lifecycle Analysis

### Component Mounting and Unmounting

Understanding how components mount and unmount in the HeartMate application is crucial for debugging and performance optimization.

#### Root Layout Component

The root layout (`layout.tsx`) mounts once when the application loads and never unmounts. It serves as the persistent shell for the entire application. The `AuthProvider` and `PhoneChatMockup` components within it remain mounted across all page navigations.

When a user navigates from one page to another:
1. The current page component unmounts
2. The new page component mounts
3. The root layout, AuthProvider, and PhoneChatMockup remain mounted

This behavior is fundamental to React's reconciliation process and ensures that:
- Authentication state persists across navigations
- The chat widget maintains its conversation history
- No unnecessary re-initialization of global providers occurs

#### Page Component Lifecycle

Each page component follows this lifecycle:

1. **Initial Render** — The component renders with initial state values (loading: true, data: null)
2. **useEffect Execution** — Side effects run after the DOM is painted:
   - API calls are initiated
   - Event listeners are attached
   - Subscriptions are set up
3. **State Updates** — As API responses arrive, `setState` calls trigger re-renders
4. **Final Render** — The component re-renders with loaded data and updated state
5. **Cleanup** — When navigating away, cleanup functions from `useEffect` run (removing event listeners, aborting pending requests)

#### Example: Profile Detail Page Lifecycle

```tsx
// 1. Component mounts with initial state
const [profile, setProfile] = useState<Profile | null>(null);
const [loading, setLoading] = useState(true);

// 2. useEffect runs after first render
useEffect(() => {
  const loadProfile = async () => {
    try {
      const res = await profileApi.getById(parseInt(id));
      setProfile(res.data); // 3. State update triggers re-render
    } catch (err) {
      setError("Failed to load profile");
    } finally {
      setLoading(false); // 3. Another state update
    }
  };
  loadProfile();
}, [id]); // Dependency array ensures effect runs when id changes

// 4. Component re-renders with profile data
// 5. Cleanup: no explicit cleanup needed (Axios handles request cancellation)
```

### Component Communication Patterns

#### Props Down, Actions Up

The HeartMate application follows the classic React data flow pattern:

- **Parent → Child**: Data flows down through props (e.g., `ProfileCard` receives `profile` prop)
- **Child → Parent**: Actions flow up through callback functions (e.g., `PixelDropdown` calls `onChange` callback)

#### Context-Based Communication

For cross-cutting concerns like authentication, the application uses React Context:

- `AuthContext` provides authentication state and actions to any component in the tree
- Components access context via the `useAuth()` hook
- No prop drilling is required for authentication-related data

#### Event-Based Communication

Some components communicate through DOM events:

- The `PixelDropdown` component listens for `mousedown` events on the document to detect clicks outside
- The `PhoneChatMockup` uses `useRef` to scroll the message container to the bottom
- These patterns avoid tight coupling between components

### Component Optimization Opportunities

While the current implementation is functional, several optimization opportunities exist:

#### Memoization

Components that render frequently with the same props could benefit from `React.memo`:

```tsx
// Potential optimization for ProfileCard
const ProfileCard = React.memo(({ profile }: ProfileCardProps) => {
  // Component implementation
});
```

This would prevent re-renders when the parent component re-renders but the profile data hasn't changed.

#### useCallback for Event Handlers

Event handlers passed as props could be wrapped in `useCallback`:

```tsx
const handleViewProfile = useCallback(() => {
  router.push(`/profiles/${profile.profileId}`);
}, [router, profile.profileId]);
```

#### useMemo for Expensive Computations

The home page's search filtering could benefit from `useMemo`:

```tsx
const filteredProfiles = useMemo(() => {
  return profiles.filter(p => {
    if (ageFrom && p.age < parseInt(ageFrom)) return false;
    if (ageTo && p.age > parseInt(ageTo)) return false;
    return true;
  });
}, [profiles, ageFrom, ageTo]);
```

---

## Appendix G: Form Handling Deep Dive

### Controlled Components Pattern

Every form in HeartMate uses the controlled components pattern, where React state is the single source of truth for form field values.

#### Basic Controlled Input

```tsx
const [email, setEmail] = useState("");

<input
  type="email"
  value={email}
  onChange={(e) => setEmail(e.target.value)}
  className="pixel-input"
/>
```

This pattern ensures:
- The input value is always synchronized with React state
- The state can be validated before submission
- The state can be reset programmatically
- The state can be sent to the backend on submission

#### Multi-Field Form State

Forms with multiple fields use either individual `useState` calls or a single state object:

**Individual State (Used in AuthForm):**
```tsx
const [name, setName] = useState("");
const [email, setEmail] = useState("");
const [password, setPassword] = useState("");
const [confirmPassword, setConfirmPassword] = useState("");
```

**Object State (Alternative approach):**
```tsx
const [formData, setFormData] = useState({
  name: "",
  email: "",
  password: "",
  confirmPassword: ""
});

const handleChange = (field: string, value: string) => {
  setFormData(prev => ({ ...prev, [field]: value }));
};
```

The individual state approach is simpler for small forms, while the object state approach scales better for forms with many fields.

### Form Submission Patterns

#### Preventing Default Behavior

Every form submission starts with preventing the default HTML form behavior:

```tsx
const handleSubmit = async (e: React.FormEvent) => {
  e.preventDefault(); // Prevent page reload
  // ... rest of submission logic
};
```

#### Loading State Management

Forms track submission state to prevent duplicate submissions and show loading indicators:

```tsx
const [loading, setLoading] = useState(false);

const handleSubmit = async (e: React.FormEvent) => {
  e.preventDefault();
  if (loading) return; // Prevent double submission
  setLoading(true);
  try {
    await submitForm();
  } finally {
    setLoading(false);
  }
};
```

#### Error State Management

Forms track error messages to display to users:

```tsx
const [error, setError] = useState("");

const handleSubmit = async (e: React.FormEvent) => {
  e.preventDefault();
  setError(""); // Clear previous errors
  try {
    await submitForm();
  } catch (err: any) {
    setError(err.response?.data?.message || "An error occurred");
  }
};
```

### Form Validation Strategies

#### Client-Side Validation

The application uses minimal client-side validation:

1. **HTML5 Required Attribute** — Ensures fields are not empty
2. **Type Attributes** — `type="email"`, `type="number"` for basic format validation
3. **Pattern Attributes** — Could be added for more complex validation

#### Server-Side Validation

The primary validation happens on the backend:

1. **Email Format** — Backend validates email format
2. **Password Strength** — Backend enforces password requirements
3. **Unique Constraints** — Backend checks for duplicate emails
4. **Data Integrity** — Backend validates all required fields

The frontend displays server-side validation errors by extracting the `message` field from the error response.

### Form Reset Patterns

After successful submission, forms reset their state:

```tsx
const handleSuccess = () => {
  setName("");
  setEmail("");
  setPassword("");
  setConfirmPassword("");
  setError("");
};
```

Some forms redirect after success instead of resetting:

```tsx
const handleSuccess = () => {
  router.push("/create-profile");
};
```

### Dynamic Form Fields

The profile creation form demonstrates dynamic form behavior:

1. **Conditional Fields** — Some fields might be conditionally shown based on other field values
2. **Dependent Dropdowns** — The gender dropdown affects the avatar display
3. **Character Counters** — The "About" textarea could show character count

---

## Appendix H: Navigation and Routing Deep Dive

### Next.js App Router Mechanics

The HeartMate application uses Next.js 16's App Router, which is based on the File System API. Every folder under `src/app/` represents a route segment, and special files within these folders define the UI and behavior.

#### Route Segments

- `/` → `src/app/page.tsx` — Home page
- `/login` → `src/app/login/page.tsx` — Login page
- `/register` → `src/app/register/page.tsx` — Registration page
- `/create-profile` → `src/app/create-profile/page.tsx` — Profile creation
- `/edit-profile` → `src/app/edit-profile/page.tsx` — Profile editing
- `/profiles` → `src/app/profiles/page.tsx` — Browse profiles
- `/profiles/[id]` → `src/app/profiles/[id]/page.tsx` — Profile detail (dynamic route)
- `/interests` → `src/app/interests/page.tsx` — Interest management
- `/admin` → `src/app/admin/page.tsx` — Admin dashboard
- `/api/chat` → `src/app/api/chat/route.ts` — Chat API endpoint

#### Dynamic Routes

The `[id]` folder creates a dynamic route segment. The `id` parameter is extracted using `useParams()`:

```tsx
// In profiles/[id]/page.tsx
import { useParams } from "next/navigation";

export default function ProfileDetail() {
  const params = useParams();
  const id = params.id; // This is the dynamic segment value
  // Use id to fetch profile data
}
```

#### Layout Nesting

The root `layout.tsx` wraps all pages. If a sub-route had its own `layout.tsx`, it would wrap only that route's children. Since HeartMate only has a root layout, all pages are wrapped by the same layout.

### Navigation Methods

#### Programmatic Navigation

The application uses `useRouter` for programmatic navigation:

```tsx
import { useRouter } from "next/navigation";

const router = useRouter();

// Navigate to a route
router.push("/profiles");

// Replace current route (no back button)
router.replace("/login");

// Go back
router.back();
```

#### Link-Based Navigation

For static links, Next.js `Link` component could be used (though the current codebase primarily uses programmatic navigation):

```tsx
import Link from "next/link";

<Link href="/profiles">Browse Profiles</Link>
```

### Route Guards

Route guards protect pages from unauthorized access:

#### Authentication Guard

```tsx
// In edit-profile/page.tsx
const { user, loading } = useAuth();

if (loading) return <div>Loading...</div>;
if (!user) {
  router.push("/login");
  return null;
}
```

#### Admin Guard

```tsx
// In admin/page.tsx
const { user } = useAuth();

useEffect(() => {
  if (user && !isAdmin) {
    router.push("/");
  }
}, [user, isAdmin, router]);
```

### URL Parameters and Query Strings

The application uses URL parameters for:

1. **Dynamic Route Segments** — Profile ID in `/profiles/[id]`
2. **Query Parameters** — Search filters in profile search (though currently handled via form state)

#### Reading URL Parameters

```tsx
const params = useParams(); // For route segments
const searchParams = useSearchParams(); // For query strings
const city = searchParams.get("city"); // For ?city=Mumbai
```

#### Updating Query Parameters

```tsx
const router = useRouter();
const searchParams = useSearchParams();

const updateFilter = (key: string, value: string) => {
  const params = new URLSearchParams(searchParams.toString());
  params.set(key, value);
  router.push(`?${params.toString()}`);
};
```

### Navigation Side Effects

When navigating between pages, several side effects occur:

1. **Component Unmounting** — Current page component unmounts, running cleanup functions
2. **State Reset** — Local state of the current page is lost (unless lifted to context)
3. **Context Persistence** — AuthContext state persists across navigations
4. **Scroll Position** — Browser scrolls to top by default
5. **History Entry** — New entry is added to browser history

---

## Appendix I: Event Handling Patterns

### Synthetic Events

React wraps native DOM events in Synthetic Events for cross-browser compatibility:

```tsx
const handleSubmit = (e: React.FormEvent) => {
  e.preventDefault(); // Synthetic event method
  // Access native event if needed: e.nativeEvent
};
```

### Event Handler Patterns

#### Inline Event Handlers

Simple handlers are defined inline:

```tsx
<button onClick={() => setOpen(!open)}>Toggle</button>
```

#### Named Event Handlers

Complex handlers are defined as named functions:

```tsx
const handleSearch = async (e: React.FormEvent) => {
  e.preventDefault();
  // Complex logic
};

<form onSubmit={handleSearch}>
```

#### Event Handler with Parameters

To pass parameters to event handlers:

```tsx
// Using arrow function
<button onClick={() => handleDelete(interest.interestId)}>Delete</button>

// Using bind (less common)
<button onClick={handleDelete.bind(null, interest.interestId)}>Delete</button>
```

### Event Bubbling and Capture

React follows the W3C event model:

1. **Capture Phase** — Event travels from root to target
2. **Target Phase** — Event reaches the target element
3. **Bubble Phase** — Event travels from target back to root

The `PixelDropdown` component uses event bubbling to detect clicks outside:

```tsx
useEffect(() => {
  const handler = (e: MouseEvent) => {
    if (ref.current && !ref.current.contains(e.target as Node)) {
      setOpen(false);
    }
  };
  document.addEventListener("mousedown", handler);
  return () => document.removeEventListener("mousedown", handler);
}, []);
```

### Keyboard Events

For accessibility and user experience, keyboard events are handled:

```tsx
const handleKeyDown = (e: React.KeyboardEvent) => {
  if (e.key === "Enter") {
    sendMessage();
  }
  if (e.key === "Escape") {
    setOpen(false);
  }
};

<input onKeyDown={handleKeyDown} />
```

### Form Events

Form-specific events:

- `onSubmit` — Form submission
- `onChange` — Input value changes
- `onFocus` — Input receives focus
- `onBlur` — Input loses focus

```tsx
<input
  value={email}
  onChange={(e) => setEmail(e.target.value)}
  onFocus={() => setFocusedField("email")}
  onBlur={() => setFocusedField(null)}
/>
```

### Touch Events

For mobile interactions, touch events could be added:

```tsx
const handleTouchStart = (e: React.TouchEvent) => {
  // Handle touch start
};

<div onTouchStart={handleTouchStart}>
```

---

## Appendix J: Accessibility Considerations

### Current Accessibility Features

The HeartMate application includes several accessibility features:

#### Semantic HTML

- Forms use `<form>` elements with `onSubmit` handlers
- Buttons use `<button>` elements instead of divs with click handlers
- Headings follow hierarchical structure (h1, h2, h3)
- Lists use `<ul>` and `<li>` elements

#### Keyboard Navigation

- All interactive elements are focusable
- The `PixelDropdown` can be operated with keyboard (Enter to select, Escape to close)
- The chat widget input is focusable and submittable with Enter

#### Screen Reader Support

- Form fields have labels (using the `label` prop on PixelInput)
- Button text is descriptive ("View Profile →", "Send Interest")
- Error messages are displayed in the DOM for screen readers

### Accessibility Improvements

Several improvements could be made:

#### ARIA Attributes

```tsx
// Adding ARIA labels to icon buttons
<button aria-label="Toggle chat" onClick={() => setOpen(!open)}>
  💕
</button>

// Adding ARIA live regions for dynamic content
<div aria-live="polite" aria-atomic="true">
  {error && <p className="error">{error}</p>}
</div>

// Adding ARIA expanded to dropdowns
<button aria-expanded={open} onClick={() => setOpen(!open)}>
  Select ▼
</button>
```

#### Focus Management

```tsx
// Managing focus after modal open
useEffect(() => {
  if (open) {
    inputRef.current?.focus();
  }
}, [open]);

// Returning focus after modal close
const handleClose = () => {
  setOpen(false);
  triggerButtonRef.current?.focus();
};
```

#### Color Contrast

The pixel-art color scheme should be tested for WCAG AA contrast ratios:
- Pink on dark background
- Dark text on light backgrounds
- Light text on dark backgrounds

#### Reduced Motion

Users who prefer reduced motion should have animations disabled:

```tsx
const prefersReducedMotion = window.matchMedia("(prefers-reduced-motion: reduce)").matches;

// Disable animations if preferred
if (prefersReducedMotion) {
  // Skip confetti animation
}
```

---

## Appendix K: Security Considerations

### Authentication Security

#### Session Cookies

The application uses HttpOnly session cookies:

- Cookies are set by the Spring Boot backend
- The `withCredentials: true` flag ensures cookies are sent with requests
- HttpOnly flag prevents JavaScript access to cookies (XSS protection)
- Secure flag should be set in production (HTTPS only)
- SameSite attribute prevents CSRF attacks

#### Password Handling

- Passwords are never stored in localStorage or sessionStorage
- Passwords are sent over HTTPS (in production)
- The confirm password field ensures user intent
- Server-side validation enforces password strength

#### Token Management

The application does not use JWT tokens on the client side. Instead, it relies on:
- Session cookies for authentication
- Server-side session storage
- Automatic session expiration

### XSS Prevention

#### Content Security Policy

The application should implement a Content Security Policy:

```
Content-Security-Policy: default-src 'self'; script-src 'self' 'unsafe-eval'; style-src 'self' 'unsafe-inline' https://fonts.googleapis.com; font-src 'self' https://fonts.gstatic.com;
```

#### Input Sanitization

User inputs are sanitized on the backend before storage. The frontend should also sanitize:
- Profile "about" text before display
- User names in the chat widget
- Error messages from the backend

#### Output Encoding

React automatically escapes JSX content, preventing most XSS attacks:
```tsx
// This is safe - React escapes the content
<p>{userInput}</p>

// This is dangerous - only use with trusted content
<p dangerouslySetInnerHTML={{ __html: userInput }}></p>
```

### CSRF Protection

Session cookies with SameSite attribute provide CSRF protection. Additional measures:
- The backend should validate the Origin header
- Critical operations should require confirmation

### Rate Limiting

The frontend should implement:
- Debouncing on search inputs
- Throttling on API calls
- Loading states to prevent rapid re-submissions

---

## Appendix L: Performance Optimization Guide

### Bundle Analysis

To analyze the bundle size:

```bash
# Install the bundle analyzer
npm install @next/bundle-analyzer

# Update next.config.ts
const withBundleAnalyzer = require('@next/bundle-analyzer')({
  enabled: process.env.ANALYZE === 'true',
});
module.exports = withBundleAnalyzer(nextConfig);

# Run the analysis
ANALYZE=true npm run build
```

### Code Splitting

Next.js automatically code-splits by route. To add manual code splitting:

```tsx
import dynamic from "next/dynamic";

// Dynamically import heavy components
const PhoneChatMockup = dynamic(() => import("@/components/PhoneChatMockup"), {
  loading: () => <p>Loading chat...</p>,
  ssr: false, // Disable SSR for client-only components
});
```

### Image Optimization

Use Next.js Image component for optimized images:

```tsx
import Image from "next/image";

<Image
  src="/profile-photos/user1.jpg"
  alt="Profile photo"
  width={200}
  height={200}
  placeholder="blur"
  blurDataURL="data:image/jpeg;base64,..."
/>
```

### Font Optimization

The Press Start 2P font is loaded from Google Fonts. For better performance:

```tsx
// Use next/font for automatic optimization
import { Press_Start_2P } from "next/font/google";

const pressStart = Press_Start_2P({
  weight: "400",
  subsets: ["latin"],
  display: "swap",
});
```

### Caching Strategies

#### Client-Side Caching

Implement SWR or React Query for data fetching:

```tsx
import useSWR from "swr";

const fetcher = (url: string) => fetch(url).then(r => r.json());

function ProfileList() {
  const { data, error, isLoading } = useSWR("/api/profiles", fetcher);
  // ...
}
```

#### HTTP Caching

The backend should set appropriate cache headers:

```
Cache-Control: public, max-age=3600
ETag: "abc123"
```

### Lazy Loading

Implement lazy loading for below-the-fold content:

```tsx
import { Suspense, lazy } from "react";

const HeavyComponent = lazy(() => import("./HeavyComponent"));

<Suspense fallback={<Loading />}>
  <HeavyComponent />
</Suspense>
```

### Virtual Scrolling

For long lists of profiles, implement virtual scrolling:

```tsx
import { FixedSizeList } from "react-window";

<FixedSizeList
  height={600}
  itemCount={profiles.length}
  itemSize={200}
  width="100%"
>
  {({ index, style }) => (
    <div style={style}>
      <ProfileCard profile={profiles[index]} />
    </div>
  )}
</FixedSizeList>
```

---

## Appendix M: Testing Strategies

### Unit Testing

#### Component Testing

Use React Testing Library for component tests:

```tsx
import { render, screen, fireEvent } from "@testing-library/react";
import { PixelButton } from "./PixelButton";

describe("PixelButton", () => {
  it("renders with correct text", () => {
    render(<PixelButton>Click me</PixelButton>);
    expect(screen.getByText("Click me")).toBeInTheDocument();
  });

  it("calls onClick when clicked", () => {
    const handleClick = jest.fn();
    render(<PixelButton onClick={handleClick}>Click</PixelButton>);
    fireEvent.click(screen.getByText("Click"));
    expect(handleClick).toHaveBeenCalledTimes(1);
  });
});
```

#### Hook Testing

Test custom hooks with `@testing-library/react-hooks`:

```tsx
import { renderHook, act } from "@testing-library/react-hooks";
import { useAuth } from "./AuthContext";

test("useAuth returns auth context", () => {
  const { result } = renderHook(() => useAuth(), {
    wrapper: AuthProvider,
  });
  expect(result.current.user).toBeNull();
});
```

### Integration Testing

Test complete user flows:

```tsx
test("user can register and create profile", async () => {
  render(<App />, { wrapper: BrowserRouter });
  
  // Navigate to register
  fireEvent.click(screen.getByText("Register"));
  
  // Fill registration form
  fireEvent.change(screen.getByLabelText("Name"), {
    target: { value: "Test User" },
  });
  fireEvent.change(screen.getByLabelText("Email"), {
    target: { value: "test@example.com" });
  });
  
  // Submit registration
  fireEvent.click(screen.getByText("Register"));
  
  // Should redirect to create profile
  await waitFor(() => {
    expect(screen.getByText("Create Profile")).toBeInTheDocument();
  });
});
```

### E2E Testing

Use Cypress or Playwright for end-to-end tests:

```tsx
describe("HeartMate E2E", () => {
  it("completes registration flow", () => {
    cy.visit("/");
    cy.contains("Register").click();
    cy.get("input[name='name']").type("Test User");
    cy.get("input[name='email']").type("test@example.com");
    cy.get("input[name='password']").type("password123");
    cy.get("input[name='confirmPassword']").type("password123");
    cy.contains("button", "Register").click();
    cy.url().should("include", "/create-profile");
  });
});
```

### Test Coverage

Aim for:
- 80%+ line coverage for components
- 90%+ coverage for utility functions
- 100% coverage for critical paths (authentication, payment)

---

## Appendix N: Deployment Guide

### Environment Variables

For production deployment, set these environment variables:

```bash
# Required
SARVAM_API_KEY=your_production_api_key

# Optional
NEXT_PUBLIC_API_URL=https://api.yourdomain.com
```

### Build Optimization

```bash
# Create production build
npm run build

# Analyze bundle size
ANALYZE=true npm run build

# Start production server
npm start
```

### Docker Deployment

Create a `Dockerfile`:

```dockerfile
FROM node:18-alpine AS builder
WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
RUN npm run build

FROM node:18-alpine AS runner
WORKDIR /app
COPY --from=builder /app/.next/standalone ./
COPY --from=builder /app/.next/static ./.next/static
COPY --from=builder /app/public ./public

EXPOSE 3000
ENV PORT=3000
CMD ["node", "server.js"]
```

### Vercel Deployment

For Vercel deployment:

1. Connect your GitHub repository
2. Set environment variables in Vercel dashboard
3. Vercel automatically detects Next.js and configures the build
4. Deploy with `git push`

### AWS Deployment

For AWS deployment:

1. Build the Docker image
2. Push to Amazon ECR
3. Deploy to Amazon ECS or Elastic Beanstalk
4. Configure CloudFront for CDN

### Monitoring

Implement monitoring for:
- Error tracking (Sentry)
- Performance monitoring (Vercel Analytics)
- Uptime monitoring (Pingdom)
- User analytics (Google Analytics)

---

This comprehensive documentation provides a complete understanding of the HeartMate frontend application, from architecture and design patterns to implementation details and deployment strategies. Every aspect is covered to enable developers to maintain, debug, and extend the application effectively.

---

## Appendix F: Sarvam AI Configuration

### API Endpoint
```
POST https://api.sarvam.ai/v1/chat/completions
```

### Request Headers
```
Content-Type: application/json
api-subscription-key: {SARVAM_API_KEY}
```

### Request Body
```json
{
  "model": "sarvam-105b-conversations",
  "messages": [
    { "role": "system", "content": "..." },
    { "role": "user", "content": "..." },
    { "role": "assistant", "content": "..." }
  ],
  "temperature": 0.7,
  "max_tokens": 200
}
```

### System Prompt Template
```
You are ${partnerName || "Priya Patel"}, a 25-year-old female Financial Analyst 
from Mumbai who just matched with Sriyaan on HeartMate. You are sweet, charming, 
playful, and slightly flirtatious. Speak naturally in modern Hinglish (a mix of 
Hindi and English). Use emojis appropriately. Keep your responses short (1-3 sentences). 
Never act like a robotic AI assistant.
```

---

This documentation provides a comprehensive understanding of the HeartMate frontend architecture, from configuration files to individual components, from CSS design patterns to TypeScript type definitions, and from authentication flows to AI integration. Every aspect of the codebase is covered to enable developers to understand, maintain, and extend the application.
