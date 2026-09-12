import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, useAuth } from './context/AuthContext';
import ProtectedRoute, { homeFor } from './components/ProtectedRoute';
import Layout from './components/Layout';
import Login from './pages/Login';
import VoucherDetails from './pages/VoucherDetails';
import VoucherListPage from './pages/VoucherListPage';

import EmployeeDashboard from './pages/employee/EmployeeDashboard';
import CreateVoucher from './pages/employee/CreateVoucher';
import MyVouchers from './pages/employee/MyVouchers';
import EditVoucher from './pages/employee/EditVoucher';

import DirectorDashboard from './pages/director/DirectorDashboard';
import AccountsDashboard from './pages/accounts/AccountsDashboard';

function RootRedirect() {
  const { user } = useAuth();
  return <Navigate to={user ? homeFor(user.role) : '/login'} replace />;
}

export default function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Routes>
          <Route path="/login" element={<Login />} />

          <Route element={<ProtectedRoute><Layout /></ProtectedRoute>}>
            {/* Employee ------------------------------------------------- */}
            <Route path="/employee/dashboard" element={
              <ProtectedRoute roles={['EMPLOYEE']}><EmployeeDashboard /></ProtectedRoute>
            } />
            <Route path="/employee/create" element={
              <ProtectedRoute roles={['EMPLOYEE']}><CreateVoucher /></ProtectedRoute>
            } />
            <Route path="/employee/vouchers" element={
              <ProtectedRoute roles={['EMPLOYEE']}><MyVouchers /></ProtectedRoute>
            } />
            <Route path="/employee/vouchers/:id" element={
              <ProtectedRoute roles={['EMPLOYEE']}><VoucherDetails backPath="/employee/vouchers" /></ProtectedRoute>
            } />
            <Route path="/employee/vouchers/:id/edit" element={
              <ProtectedRoute roles={['EMPLOYEE']}><EditVoucher /></ProtectedRoute>
            } />

            {/* Director --------------------------------------------------*/}
            <Route path="/director/dashboard" element={
              <ProtectedRoute roles={['DIRECTOR']}><DirectorDashboard /></ProtectedRoute>
            } />
            <Route path="/director/pending" element={
              <ProtectedRoute roles={['DIRECTOR']}>
                <VoucherListPage title="Pending Approvals" subtitle="Vouchers awaiting your decision." fixedStatus="SUBMITTED" basePath="/director/pending" />
              </ProtectedRoute>
            } />
            <Route path="/director/pending/:id" element={
              <ProtectedRoute roles={['DIRECTOR']}><VoucherDetails backPath="/director/pending" /></ProtectedRoute>
            } />
            <Route path="/director/vouchers" element={
              <ProtectedRoute roles={['DIRECTOR']}>
                <VoucherListPage title="All Vouchers" subtitle="Every voucher across the organization." basePath="/director/vouchers" />
              </ProtectedRoute>
            } />
            <Route path="/director/vouchers/:id" element={
              <ProtectedRoute roles={['DIRECTOR']}><VoucherDetails backPath="/director/vouchers" /></ProtectedRoute>
            } />

            {/* Accounts --------------------------------------------------*/}
            <Route path="/accounts/dashboard" element={
              <ProtectedRoute roles={['ACCOUNTS']}><AccountsDashboard /></ProtectedRoute>
            } />
            <Route path="/accounts/vouchers" element={
              <ProtectedRoute roles={['ACCOUNTS']}>
                <VoucherListPage title="All Vouchers" subtitle="Organization-wide vouchers for reimbursement processing." basePath="/accounts/vouchers" />
              </ProtectedRoute>
            } />
            <Route path="/accounts/vouchers/:id" element={
              <ProtectedRoute roles={['ACCOUNTS']}><VoucherDetails backPath="/accounts/vouchers" /></ProtectedRoute>
            } />
          </Route>

          <Route path="/" element={<RootRedirect />} />
          <Route path="*" element={<RootRedirect />} />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  );
}
