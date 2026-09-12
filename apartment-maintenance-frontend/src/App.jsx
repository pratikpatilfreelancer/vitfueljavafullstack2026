import { BrowserRouter, Routes, Route } from "react-router-dom";
import { RoleProvider } from "./context/RoleContext";
import Navbar from "./components/Navbar";
import RoleSelect from "./pages/RoleSelect";
import ResidentDashboard from "./pages/ResidentDashboard";
import StaffDashboard from "./pages/StaffDashboard";
import AdminDashboard from "./pages/AdminDashboard";
import ComplaintList from "./pages/ComplaintList";
import ComplaintForm from "./pages/ComplaintForm";
import ResidentList from "./pages/ResidentList";
import StaffList from "./pages/StaffList";

function App() {
  return (
    <RoleProvider>
      <BrowserRouter>
        <Navbar />
        <Routes>
          <Route path="/" element={<RoleSelect />} />
          <Route path="/resident" element={<ResidentDashboard />} />
          <Route path="/staff" element={<StaffDashboard />} />
          <Route path="/admin" element={<AdminDashboard />} />
          <Route path="/complaints" element={<ComplaintList />} />
          <Route path="/complaints/new" element={<ComplaintForm />} />
          <Route path="/residents" element={<ResidentList />} />
          <Route path="/staff-management" element={<StaffList />} />
        </Routes>
      </BrowserRouter>
    </RoleProvider>
  );
}

export default App;