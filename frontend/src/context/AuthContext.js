import React, { createContext, useContext, useState, useCallback } from 'react';
import api from '../services/api';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [token, setToken] = useState(() => localStorage.getItem('ss_token'));
  const [user, setUser]   = useState(() => {
    try { return JSON.parse(localStorage.getItem('ss_user')); } catch { return null; }
  });

  const login = useCallback(async (email, password) => {
    const { data } = await api.post('/auth/login', { email, password });
    localStorage.setItem('ss_token', data.token);
    localStorage.setItem('ss_user', JSON.stringify({ name: data.name, email: data.email, userId: data.userId }));
    setToken(data.token);
    setUser({ name: data.name, email: data.email, userId: data.userId });
    return data;
  }, []);

  const register = useCallback(async (name, email, password) => {
    const { data } = await api.post('/auth/register', { name, email, password });
    localStorage.setItem('ss_token', data.token);
    localStorage.setItem('ss_user', JSON.stringify({ name: data.name, email: data.email, userId: data.userId }));
    setToken(data.token);
    setUser({ name: data.name, email: data.email, userId: data.userId });
    return data;
  }, []);

  const logout = useCallback(() => {
    localStorage.removeItem('ss_token');
    localStorage.removeItem('ss_user');
    setToken(null);
    setUser(null);
  }, []);

  return (
    <AuthContext.Provider value={{ token, user, login, register, logout }}>
      {children}
    </AuthContext.Provider>
  );
}

export const useAuth = () => useContext(AuthContext);
