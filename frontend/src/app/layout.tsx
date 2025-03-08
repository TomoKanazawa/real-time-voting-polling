import type { Metadata } from "next";
import { Inter } from "next/font/google";
import "./globals.css";
import Link from "next/link";

const inter = Inter({ subsets: ["latin"], variable: "--font-inter" });

export const metadata: Metadata = {
  title: "Distributed Real-Time Voting System",
  description: "A scalable, fault-tolerant, real-time voting and polling system",
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="en" className={inter.variable}>
      <body className={inter.className}>
        <div className="min-h-screen flex flex-col bg-gray-50 dark:bg-gray-900">
          <header className="bg-white dark:bg-gray-800 shadow-sm">
            <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
              <div className="flex justify-between items-center py-4">
                <div className="flex items-center">
                  <Link href="/" className="flex items-center space-x-2">
                    <svg xmlns="http://www.w3.org/2000/svg" className="h-8 w-8 text-blue-500" viewBox="0 0 20 20" fill="currentColor">
                      <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clipRule="evenodd" />
                    </svg>
                    <div>
                      <span className="text-xl font-bold text-gray-900 dark:text-white">Distributed Voting System</span>
                      <p className="text-xs text-gray-500 dark:text-gray-400">Real-time, Scalable, Fault-tolerant</p>
                    </div>
                  </Link>
                </div>
                <nav className="hidden md:flex space-x-6">
                  <Link href="/" className="text-gray-600 hover:text-blue-500 dark:text-gray-300 dark:hover:text-blue-400 transition-colors">
                    Home
                  </Link>
                  <Link href="/polls" className="text-gray-600 hover:text-blue-500 dark:text-gray-300 dark:hover:text-blue-400 transition-colors">
                    Polls
                  </Link>
                  <Link href="/dashboard" className="text-gray-600 hover:text-blue-500 dark:text-gray-300 dark:hover:text-blue-400 transition-colors">
                    Dashboard
                  </Link>
                  <Link href="/auth/login" className="text-gray-600 hover:text-blue-500 dark:text-gray-300 dark:hover:text-blue-400 transition-colors">
                    Login
                  </Link>
                </nav>
                <div className="md:hidden">
                  <button className="text-gray-600 hover:text-blue-500 dark:text-gray-300 dark:hover:text-blue-400">
                    <svg xmlns="http://www.w3.org/2000/svg" className="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 6h16M4 12h16M4 18h16" />
                    </svg>
                  </button>
                </div>
              </div>
            </div>
          </header>
          <main className="flex-grow">
            <div className="max-w-7xl mx-auto py-8 px-4 sm:px-6 lg:px-8 animate-fade-in">
              {children}
            </div>
          </main>
          <footer className="bg-white dark:bg-gray-800 shadow-inner mt-auto">
            <div className="max-w-7xl mx-auto py-6 px-4 sm:px-6 lg:px-8">
              <div className="flex flex-col md:flex-row justify-between items-center">
                <div className="mb-4 md:mb-0">
                  <p className="text-gray-500 dark:text-gray-400">&copy; {new Date().getFullYear()} Distributed Real-Time Voting System - Academic Project</p>
                </div>
                <div className="flex space-x-6">
                  <a href="#" className="text-gray-500 hover:text-blue-500 dark:text-gray-400 dark:hover:text-blue-400 transition-colors">
                    Documentation
                  </a>
                  <a href="#" className="text-gray-500 hover:text-blue-500 dark:text-gray-400 dark:hover:text-blue-400 transition-colors">
                    GitHub
                  </a>
                  <a href="#" className="text-gray-500 hover:text-blue-500 dark:text-gray-400 dark:hover:text-blue-400 transition-colors">
                    References
                  </a>
                </div>
              </div>
            </div>
          </footer>
        </div>
      </body>
    </html>
  );
}
