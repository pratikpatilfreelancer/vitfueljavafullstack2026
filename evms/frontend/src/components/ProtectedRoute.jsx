import { Navigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export default function ProtectedRoute({ roles, children }) {
  const { user } = useAuth();

  if (!user) return <Navigate to="/login" replace />;
  if (roles && !roles.includes(user.role)) {
    return <Navigate to={homeFor(user.role)} replace />;
  }
  return children;
}

export function homeFor(role) {
  if (role === 'EMPLOYEE') return '/employee/dashboard';
  if (role === 'DIRECTOR') return '/director/dashboard';
  if (role === 'ACCOUNTS') return '/accounts/dashboard';
  return '/login';
}
