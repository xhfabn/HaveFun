import React from 'react';
import { Navigate } from 'react-router-dom';

const ProtectedRoute = ({ tokenKey, redirectTo, children }) => {
  const token = localStorage.getItem(tokenKey);
  if (!token) {
    return <Navigate to={redirectTo} replace />;
  }
  return children;
};

export default ProtectedRoute;
