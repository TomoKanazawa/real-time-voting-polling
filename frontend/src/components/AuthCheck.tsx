'use client';

import { useEffect, useState } from 'react';
import { useRouter, usePathname } from 'next/navigation';
import { isAuthenticated } from '@/services/auth';
import Link from 'next/link';

interface AuthCheckProps {
  children: React.ReactNode;
  redirectTo?: string;
}

export default function AuthCheck({ children, redirectTo = '/auth/login' }: AuthCheckProps) {
  const router = useRouter();
  const pathname = usePathname() || '/';
  const [isChecking, setIsChecking] = useState(true);

  useEffect(() => {
    // Short delay to prevent flash of content
    const timer = setTimeout(() => {
      setIsChecking(false);
      if (!isAuthenticated()) {
        // Add the current path as a redirect parameter
        const returnUrl = encodeURIComponent(pathname);
        router.push(`${redirectTo}?returnUrl=${returnUrl}`);
      }
    }, 500);

    return () => clearTimeout(timer);
  }, [redirectTo, router, pathname]);

  // While checking, show a loading state
  if (isChecking) {
    return (
      <div className="flex items-center justify-center min-h-[60vh]">
        <div className="animate-spin rounded-full h-10 w-10 border-b-2 border-blue-500"></div>
      </div>
    );
  }

  // If not authenticated, show a message
  if (!isAuthenticated()) {
    return (
      <div className="flex items-center justify-center min-h-[60vh]">
        <div className="text-center bg-white dark:bg-gray-800 shadow-md rounded-lg p-8 max-w-md">
          <svg xmlns="http://www.w3.org/2000/svg" className="h-12 w-12 text-blue-500 mx-auto mb-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z" />
          </svg>
          <h2 className="text-xl font-semibold mb-2 text-gray-900 dark:text-white">Authentication Required</h2>
          <p className="text-gray-600 dark:text-gray-300 mb-6">You need to be logged in to access this page.</p>
          <div className="flex flex-col sm:flex-row gap-4 justify-center">
            <Link href={`${redirectTo}?returnUrl=${encodeURIComponent(pathname)}`} className="btn-primary">
              Log In
            </Link>
            <Link href="/" className="btn-secondary">
              Return to Home
            </Link>
          </div>
        </div>
      </div>
    );
  }

  // If authenticated, render children
  return <>{children}</>;
} 