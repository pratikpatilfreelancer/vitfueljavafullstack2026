import axios from "axios";

// Two microservices, two base URLs - no API Gateway, kept simple on purpose.
const USER_SERVICE_URL = "http://localhost:8081/api";
const LIBRARY_SERVICE_URL = "http://localhost:8082/api";

/**
 * There is no JWT/session in this project: every protected request is sent
 * with an HTTP Basic "Authorization" header built from the credentials the
 * user typed in at login. We keep those credentials (base64-encoded) in
 * localStorage only for the duration of the browser session/tab, purely so
 * subsequent API calls can re-authenticate - a normal trade-off for a minor
 * project that explicitly avoids JWT/OAuth.
 */
function getAuthHeader() {
  const raw = localStorage.getItem("credentials");
  if (!raw) return {};
  return { Authorization: `Basic ${raw}` };
}

function buildClient(baseURL) {
  const instance = axios.create({ baseURL });
  instance.interceptors.request.use((config) => {
    config.headers = { ...config.headers, ...getAuthHeader() };
    return config;
  });
  return instance;
}

export const userApi = buildClient(USER_SERVICE_URL);
export const libraryApi = buildClient(LIBRARY_SERVICE_URL);

export function setStoredCredentials(email, password) {
  const encoded = btoa(`${email}:${password}`);
  localStorage.setItem("credentials", encoded);
}

export function clearStoredCredentials() {
  localStorage.removeItem("credentials");
}

/** Extracts a readable message from an error thrown by axios / our GlobalExceptionHandler. */
export function extractErrorMessage(error) {
  if (error.response && error.response.data && error.response.data.message) {
    return error.response.data.message;
  }
  if (error.message) return error.message;
  return "Something went wrong. Please try again.";
}
