import apiClient from "./axiosConfig";

const complaintService = {
  getAll: () => apiClient.get("/complaints"),
  getById: (id) => apiClient.get(`/complaints/${id}`),

  // Create only takes description, residentId, categoryId — no status, no staffId
  create: (data) => apiClient.post("/complaints", data),

  getByResident: (residentId) => apiClient.get(`/complaints/resident/${residentId}`),

  // NOTE: requires backend addition below — not in your controller yet
  getByStaff: (staffId) => apiClient.get(`/complaints/staff/${staffId}`),

  getByStatus: (status) => apiClient.get(`/complaints/status/${status}`),

  assignStaff: (complaintId, staffId) =>
    apiClient.put(`/complaints/${complaintId}/assign/${staffId}`),

  // body shape assumes ComplaintStatusUpdateDTO has a "status" field — verify this
  updateStatus: (id, status) => apiClient.put(`/complaints/${id}/status`, { status }),

  delete: (id) => apiClient.delete(`/complaints/${id}`),
};

export default complaintService;