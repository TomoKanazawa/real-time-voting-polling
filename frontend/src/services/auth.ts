import api from './api';
import { AuthResponse, User } from '@/types';

export const login = async (username: string, password: string): Promise<AuthResponse> => {
  const response = await api.post<AuthResponse>('/auth/login', { username, password });
  if (response.data.token) {
    localStorage.setItem('token', response.data.token);
    localStorage.setItem('user', JSON.stringify(response.data.user));
  }
  return response.data;
};

export const register = async (username: string, email: string, password: string): Promise<AuthResponse> => {
  const response = await api.post<AuthResponse>('/auth/register', { username, email, password });
  if (response.data.token) {
    localStorage.setItem('token', response.data.token);
    localStorage.setItem('user', JSON.stringify(response.data.user));
  }
  return response.data;
};

export const logout = (): void => {
  localStorage.removeItem('token');
  localStorage.removeItem('user');
};

export const getCurrentUser = (): User | null => {
  const userStr = typeof window !== 'undefined' ? localStorage.getItem('user') : null;
  if (userStr) {
    return JSON.parse(userStr) as User;
  }
  return null;
};

export const isAuthenticated = (): boolean => {
  return typeof window !== 'undefined' && !!localStorage.getItem('token');
}; 