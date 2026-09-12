// residentService.js
import apiClient from "./axiosConfig";

const residentService = {
  getAll: () => apiClient.get("/residents"),
  getById: (id) => apiClient.get(`/residents/${id}`),
  create: (data) => apiClient.post("/residents", data),
  update: (id, data) => apiClient.put(`/residents/${id}`, data),
  delete: (id) => apiClient.delete(`/residents/${id}`),
};

export default residentService;