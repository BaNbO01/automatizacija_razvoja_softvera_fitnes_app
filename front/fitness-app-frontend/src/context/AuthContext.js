import React, { createContext, useState, useEffect, useContext } from 'react';
import axios from 'axios'; 

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
  const [token, setToken] = useState(sessionStorage.getItem('accessToken'));
  const [userRole, setUserRole] = useState(sessionStorage.getItem('userRole'));
  const [isAuthenticated, setIsAuthenticated] = useState(!!token);

  useEffect(() => {
    const storedToken = sessionStorage.getItem('accessToken');
    const storedRole = sessionStorage.getItem('userRole');
    if (storedToken) {
      setToken(storedToken);
      setUserRole(storedRole);
      setIsAuthenticated(true);
    }
  }, []);

  const login = async (email, password) => {
    try {
      const response = await axios.post('http://localhost:8080/api/auth/login', { email, password });
      const { accessToken, role } = response.data;
      sessionStorage.setItem('accessToken', accessToken);
      sessionStorage.setItem('userRole', role);
      setToken(accessToken);
      setUserRole(role);
      setIsAuthenticated(true);
      return { success: true, role };
    } catch (error) {
      console.error('Login failed:', error);
      setIsAuthenticated(false);
      return { success: false, message: "Prijava na sistem neuspesna! Proverite email i password i pokusajte ponovo." || 'Login failed' };
    }
  };

 const register = async (username, email, password, role) => {
    try {
    
      await axios.post('http://localhost:8080/api/auth/register', { username, email, password, role });

     
      const loginResult = await login(email, password);

      if (loginResult.success) {
        return { success: true, role: loginResult.role };
      } else {
     
        return { success: false, message: loginResult.message || 'Registracija uspešna, ali prijava neuspešna.' };
      }

    } catch (error) {
      console.error('Registration failed:', error);
      setIsAuthenticated(false);
      return { success: false, message: error.response?.data?.message || 'Registracija neuspešna. Pokušajte ponovo.' };
    }
  };

  const logout = () => {
    sessionStorage.removeItem('accessToken');
    sessionStorage.removeItem('userRole');
    setToken(null);
    setUserRole(null);
    setIsAuthenticated(false);
    axios.post('http://localhost:8080/api/auth/logout', {}, {
      headers: { Authorization: `Bearer ${token}` }
    }).catch(err => console.error('Logout backend call failed:', err));
  };

  return (
    <AuthContext.Provider value={{ token, userRole, isAuthenticated, login, register, logout }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => useContext(AuthContext);
