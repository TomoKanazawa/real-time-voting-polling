'use client';

import Link from 'next/link';
import Image from 'next/image';
import { useEffect, useState } from 'react';
import { isAuthenticated } from '@/services/auth';

export default function Home() {
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  
  useEffect(() => {
    setIsLoggedIn(isAuthenticated());
  }, []);

  return (
    <div className="animate-slide-up">
      {/* Hero Section */}
      <div className="bg-white dark:bg-gray-800 rounded-lg shadow-md overflow-hidden mb-8">
        <div className="px-6 py-10 md:px-10 md:py-12 text-center md:text-left md:flex md:items-center md:justify-between">
          <div className="md:w-1/2 mb-8 md:mb-0">
            <h1 className="text-3xl md:text-4xl font-bold text-gray-900 dark:text-white mb-4 leading-tight">
              Distributed <span className="text-blue-500 dark:text-blue-400">Real-Time</span> Voting System
            </h1>
            <p className="text-lg text-gray-600 dark:text-gray-300 mb-6 max-w-2xl">
              A scalable, fault-tolerant, real-time voting and polling system designed for high performance and reliability.
            </p>
            <div className="flex flex-col sm:flex-row gap-4 justify-center md:justify-start">
              {isLoggedIn ? (
                <>
                  <Link 
                    href="/polls" 
                    className="btn-primary inline-flex items-center justify-center"
                  >
                    <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5 mr-2" viewBox="0 0 20 20" fill="currentColor">
                      <path d="M2 10a8 8 0 018-8v8h8a8 8 0 11-16 0z" />
                      <path d="M12 2.252A8.014 8.014 0 0117.748 8H12V2.252z" />
                    </svg>
                    View Active Polls
                  </Link>
                  <Link 
                    href="/polls/create" 
                    className="btn-outline inline-flex items-center justify-center"
                  >
                    <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5 mr-2" viewBox="0 0 20 20" fill="currentColor">
                      <path fillRule="evenodd" d="M10 3a1 1 0 011 1v5h5a1 1 0 110 2h-5v5a1 1 0 11-2 0v-5H4a1 1 0 110-2h5V4a1 1 0 011-1z" clipRule="evenodd" />
                    </svg>
                    Create New Poll
                  </Link>
                </>
              ) : (
                <>
                  <Link 
                    href="/auth/login" 
                    className="btn-primary inline-flex items-center justify-center"
                  >
                    <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5 mr-2" viewBox="0 0 20 20" fill="currentColor">
                      <path fillRule="evenodd" d="M3 3a1 1 0 011-1h12a1 1 0 011 1v3a1 1 0 01-.293.707L12 11.414V15a1 1 0 01-.293.707l-2 2A1 1 0 018 17v-5.586L3.293 6.707A1 1 0 013 6V3z" clipRule="evenodd" />
                    </svg>
                    Log In
                  </Link>
                  <Link 
                    href="/auth/register" 
                    className="btn-outline inline-flex items-center justify-center"
                  >
                    <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5 mr-2" viewBox="0 0 20 20" fill="currentColor">
                      <path d="M8 9a3 3 0 100-6 3 3 0 000 6zM8 11a6 6 0 016 6H2a6 6 0 016-6zM16 7a1 1 0 10-2 0v1h-1a1 1 0 100 2h1v1a1 1 0 102 0v-1h1a1 1 0 100-2h-1V7z" />
                    </svg>
                    Register
                  </Link>
                </>
              )}
            </div>
          </div>
          <div className="md:w-1/2 flex justify-center md:justify-end">
            <div className="relative w-64 h-64 md:w-72 md:h-72">
              <div className="absolute inset-0 bg-blue-100 dark:bg-blue-900 rounded-full opacity-20"></div>
              <svg className="absolute inset-0 w-full h-full text-blue-500" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M9 12H15M12 9V15M3.6 9H20.4C21.2837 9 22 8.28366 22 7.4V4.6C22 3.71634 21.2837 3 20.4 3H3.6C2.71634 3 2 3.71634 2 4.6V7.4C2 8.28366 2.71634 9 3.6 9ZM3.6 21H20.4C21.2837 21 22 20.2837 22 19.4V16.6C22 15.7163 21.2837 15 20.4 15H3.6C2.71634 15 2 15.7163 2 16.6V19.4C2 20.2837 2.71634 21 3.6 21Z" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
              </svg>
            </div>
          </div>
        </div>
      </div>

      {/* Project Overview */}
      <div className="bg-white dark:bg-gray-800 rounded-lg shadow-md p-6 mb-8">
        <h2 className="text-2xl font-bold text-gray-900 dark:text-white mb-4">Project Overview</h2>
        <p className="text-gray-600 dark:text-gray-300 mb-4">
          This distributed real-time voting system is designed as an academic project to demonstrate principles of distributed systems, 
          fault tolerance, and real-time data processing. The system implements leader election algorithms and uses message queues 
          for reliable communication between components.
        </p>
        <p className="text-gray-600 dark:text-gray-300">
          The architecture follows best practices for scalable systems, allowing it to handle thousands of concurrent votes 
          while maintaining consistency and availability even in the presence of node failures.
        </p>
      </div>

      {/* Features Section */}
      <div className="mb-8">
        <h2 className="text-2xl font-bold text-center mb-6 text-gray-900 dark:text-white">Key Features</h2>
        <div className="grid md:grid-cols-3 gap-6">
          {features.map((feature, index) => (
            <div key={index} className="card p-6 flex flex-col items-center text-center">
              <div className="w-12 h-12 rounded-full bg-blue-100 dark:bg-blue-900 flex items-center justify-center mb-4">
                <span className="text-blue-500 dark:text-blue-400">{feature.icon}</span>
              </div>
              <h3 className="text-xl font-semibold mb-2 text-gray-900 dark:text-white">{feature.title}</h3>
              <p className="text-gray-600 dark:text-gray-300">{feature.description}</p>
            </div>
          ))}
        </div>
      </div>

      {/* Technical Implementation */}
      <div className="bg-white dark:bg-gray-800 rounded-lg shadow-md p-6 mb-8">
        <h2 className="text-2xl font-bold text-gray-900 dark:text-white mb-4">Technical Implementation</h2>
        <div className="grid md:grid-cols-2 gap-6">
          <div>
            <h3 className="text-lg font-semibold text-gray-900 dark:text-white mb-2">Backend Architecture</h3>
            <ul className="list-disc list-inside text-gray-600 dark:text-gray-300 space-y-1">
              <li>Distributed nodes with leader election</li>
              <li>Apache Kafka for message queuing</li>
              <li>RESTful API for client communication</li>
              <li>WebSockets for real-time updates</li>
              <li>Consistent hashing for load distribution</li>
            </ul>
          </div>
          <div>
            <h3 className="text-lg font-semibold text-gray-900 dark:text-white mb-2">Frontend Technologies</h3>
            <ul className="list-disc list-inside text-gray-600 dark:text-gray-300 space-y-1">
              <li>Next.js for server-side rendering</li>
              <li>React for component-based UI</li>
              <li>TailwindCSS for responsive design</li>
              <li>TypeScript for type safety</li>
              <li>Real-time data visualization</li>
            </ul>
          </div>
        </div>
      </div>

      {/* CTA Section */}
      {!isLoggedIn && (
        <div className="bg-blue-50 dark:bg-blue-900/20 rounded-lg shadow-md p-6 text-center">
          <h2 className="text-2xl font-bold text-gray-900 dark:text-white mb-4">Try the System</h2>
          <p className="text-gray-600 dark:text-gray-300 mb-6 max-w-2xl mx-auto">
            Experience the real-time voting system by creating a poll or participating in existing ones.
          </p>
          <div className="flex flex-col sm:flex-row gap-4 justify-center">
            <Link 
              href="/auth/register" 
              className="btn-primary inline-flex items-center justify-center"
            >
              Register Now
            </Link>
            <Link 
              href="/auth/login" 
              className="btn-secondary inline-flex items-center justify-center"
            >
              Login
            </Link>
          </div>
        </div>
      )}
    </div>
  );
}

const features = [
  {
    title: 'Real-Time Results',
    description: 'See voting results update instantly as votes are cast, providing immediate feedback.',
    icon: (
      <svg xmlns="http://www.w3.org/2000/svg" className="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M13 10V3L4 14h7v7l9-11h-7z" />
      </svg>
    ),
  },
  {
    title: 'Fault Tolerance',
    description: 'Built with distributed systems principles to ensure high availability and reliability.',
    icon: (
      <svg xmlns="http://www.w3.org/2000/svg" className="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12l2 2 4-4m5.618-4.016A11.955 11.955 0 0112 2.944a11.955 11.955 0 01-8.618 3.04A12.02 12.02 0 003 9c0 5.591 3.824 10.29 9 11.622 5.176-1.332 9-6.03 9-11.622 0-1.042-.133-2.052-.382-3.016z" />
      </svg>
    ),
  },
  {
    title: 'Scalable Architecture',
    description: 'Designed to handle thousands of concurrent users with minimal latency.',
    icon: (
      <svg xmlns="http://www.w3.org/2000/svg" className="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 11H5m14 0a2 2 0 012 2v6a2 2 0 01-2 2H5a2 2 0 01-2-2v-6a2 2 0 012-2m14 0V9a2 2 0 00-2-2M5 11V9a2 2 0 012-2m0 0V5a2 2 0 012-2h6a2 2 0 012 2v2M7 7h10" />
      </svg>
    ),
  },
];
