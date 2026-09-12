// categoryService.js
import apiClient from "./axiosConfig";

const categoryService = {
  getAll: () => apiClient.get("/categories"), // verify actual path
  create: (data) => apiClient.post("/categories", data),
};

export default categoryService;