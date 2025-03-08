'use client';

import { useState, useEffect } from 'react';
import Link from 'next/link';
import { getPolls } from '@/services/polls';
import { Poll } from '@/types';
import AuthCheck from '@/components/AuthCheck';

export default function PollsListPage() {
  return (
    <AuthCheck>
      <PollsList />
    </AuthCheck>
  );
}

function PollsList() {
  const [polls, setPolls] = useState<Poll[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    const fetchPolls = async () => {
      try {
        const data = await getPolls();
        setPolls(data);
      } catch (err: any) {
        setError(err.response?.data?.message || 'Failed to fetch polls');
      } finally {
        setLoading(false);
      }
    };

    fetchPolls();
  }, []);

  if (loading) {
    return (
      <div className="flex justify-center items-center h-64">
        <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-blue-500"></div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="bg-red-100 border border-red-400 text-red-700 px-6 py-4 rounded-lg mb-6" role="alert">
        <div className="flex items-center">
          <svg className="w-6 h-6 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path>
          </svg>
          <span className="font-medium">{error}</span>
        </div>
      </div>
    );
  }

  return (
    <div className="animate-fade-in">
      <div className="bg-white dark:bg-gray-800 rounded-lg shadow-md p-6 mb-8">
        <div className="flex flex-col md:flex-row justify-between items-center mb-6">
          <div>
            <h1 className="text-2xl font-bold text-gray-900 dark:text-white mb-2">Active Polls</h1>
            <p className="text-gray-600 dark:text-gray-400">Participate in polls and see real-time results</p>
          </div>
          <Link
            href="/polls/create"
            className="btn-primary mt-4 md:mt-0 inline-flex items-center justify-center"
          >
            <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5 mr-2" viewBox="0 0 20 20" fill="currentColor">
              <path fillRule="evenodd" d="M10 3a1 1 0 011 1v5h5a1 1 0 110 2h-5v5a1 1 0 11-2 0v-5H4a1 1 0 110-2h5V4a1 1 0 011-1z" clipRule="evenodd" />
            </svg>
            Create New Poll
          </Link>
        </div>

        <div className="mb-4 p-4 bg-blue-50 dark:bg-blue-900/20 rounded-lg">
          <h2 className="text-lg font-semibold text-gray-900 dark:text-white mb-2">System Information</h2>
          <p className="text-gray-600 dark:text-gray-300 text-sm">
            This page demonstrates real-time data synchronization across distributed nodes. 
            When you vote on a poll, your vote is processed by the leader node and propagated to all follower nodes 
            to maintain consistency across the system.
          </p>
        </div>

        {polls.length === 0 ? (
          <div className="bg-white dark:bg-gray-800 border border-gray-200 dark:border-gray-700 rounded-lg p-8 text-center">
            <div className="inline-flex items-center justify-center w-16 h-16 rounded-full bg-blue-100 dark:bg-blue-900 mb-4">
              <svg xmlns="http://www.w3.org/2000/svg" className="h-8 w-8 text-blue-500 dark:text-blue-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2" />
              </svg>
            </div>
            <h3 className="text-xl font-semibold mb-2 text-gray-900 dark:text-white">No active polls found</h3>
            <p className="text-gray-600 dark:text-gray-400 mb-6">Create a new poll to test the distributed voting system</p>
            <Link
              href="/polls/create"
              className="btn-primary inline-flex items-center justify-center"
            >
              <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5 mr-2" viewBox="0 0 20 20" fill="currentColor">
                <path fillRule="evenodd" d="M10 3a1 1 0 011 1v5h5a1 1 0 110 2h-5v5a1 1 0 11-2 0v-5H4a1 1 0 110-2h5V4a1 1 0 011-1z" clipRule="evenodd" />
              </svg>
              Create New Poll
            </Link>
          </div>
        ) : (
          <div className="grid gap-6 md:grid-cols-2 lg:grid-cols-3">
            {polls.map((poll) => (
              <Link key={poll.id} href={`/polls/${poll.id}`} className="block group">
                <div className="card h-full p-6 flex flex-col transition-all duration-300 hover:border-blue-300 dark:hover:border-blue-700">
                  <div className="flex justify-between items-start mb-4">
                    <h3 className="text-lg font-semibold text-gray-900 dark:text-white group-hover:text-blue-500 dark:group-hover:text-blue-400 transition-colors">
                      {poll.title}
                    </h3>
                    <span className="bg-blue-100 dark:bg-blue-900 text-blue-800 dark:text-blue-200 text-xs font-medium px-2.5 py-0.5 rounded-full">
                      {poll.options.length} option{poll.options.length !== 1 ? 's' : ''}
                    </span>
                  </div>
                  <p className="text-gray-600 dark:text-gray-300 mb-4 line-clamp-2 flex-grow">{poll.description}</p>
                  <div className="flex justify-between items-center text-sm text-gray-500 dark:text-gray-400 mt-auto pt-4 border-t border-gray-100 dark:border-gray-700">
                    <span className="flex items-center">
                      <svg xmlns="http://www.w3.org/2000/svg" className="h-4 w-4 mr-1" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
                      </svg>
                      {poll.createdBy?.username || 'Anonymous'}
                    </span>
                    <span className="flex items-center">
                      <svg xmlns="http://www.w3.org/2000/svg" className="h-4 w-4 mr-1" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
                      </svg>
                      {new Date(poll.expiresAt).toLocaleDateString()}
                    </span>
                  </div>
                </div>
              </Link>
            ))}
          </div>
        )}
      </div>

      <div className="bg-white dark:bg-gray-800 rounded-lg shadow-md p-6">
        <h2 className="text-xl font-semibold text-gray-900 dark:text-white mb-4">About This Implementation</h2>
        <p className="text-gray-600 dark:text-gray-300 mb-4">
          This voting system demonstrates several key distributed systems concepts:
        </p>
        <ul className="list-disc list-inside text-gray-600 dark:text-gray-300 space-y-2 mb-4">
          <li><span className="font-medium text-gray-900 dark:text-white">Leader Election:</span> The system uses a consensus algorithm to elect a leader node that coordinates voting operations.</li>
          <li><span className="font-medium text-gray-900 dark:text-white">Fault Tolerance:</span> If a node fails, the system automatically recovers and continues operation.</li>
          <li><span className="font-medium text-gray-900 dark:text-white">Consistency:</span> All nodes maintain a consistent view of the voting data.</li>
          <li><span className="font-medium text-gray-900 dark:text-white">Real-time Updates:</span> Vote results are propagated in real-time to all clients.</li>
        </ul>
        <p className="text-gray-600 dark:text-gray-300 text-sm">
          This implementation is part of a distributed systems academic project that explores the challenges of building reliable, 
          scalable systems in the presence of network partitions and node failures.
        </p>
      </div>
    </div>
  );
} 