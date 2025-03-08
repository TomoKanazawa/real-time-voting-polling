'use client';

import { useEffect } from 'react';
import { useRouter } from 'next/navigation';
import { isAuthenticated } from '@/services/auth';

interface AuthCheckProps {
  children: React.ReactNode;
  redirectTo?: string;
}

export default function AuthCheck({ children, redirectTo = '/' }: AuthCheckProps) {
  const router = useRouter();

  useEffect(() => {
    if (!isAuthenticated()) {
      console.log('User not authenticated, redirecting to', redirectTo);
      router.push(redirectTo);
    }
  }, [redirectTo, router]);

  // If not authenticated, render nothing while redirecting
  if (!isAuthenticated()) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <div className="text-center">
          <h2 className="text-xl font-semibold mb-2">Authentication Required</h2>
          <p className="text-gray-600 mb-4">Please log in to access this page.</p>
          <div className="animate-spin rounded-full h-10 w-10 border-b-2 border-indigo-600 mx-auto"></div>
        </div>
      </div>
    );
  }

  // If authenticated, render children
  return <>{children}</>;
} 