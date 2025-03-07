'use client';

import { useState, useEffect } from 'react';
import Link from 'next/link';
import { getPolls } from '@/services/polls';
import { Poll } from '@/types';

export default function PollsList() {
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
        <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-indigo-500"></div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded mb-4" role="alert">
        <span className="block sm:inline">{error}</span>
      </div>
    );
  }

  return (
    <div>
      <div className="flex justify-between items-center mb-6">
        <h2 className="text-2xl font-bold">Active Polls</h2>
        <Link
          href="/polls/create"
          className="bg-indigo-600 hover:bg-indigo-700 text-white font-bold py-2 px-4 rounded"
        >
          Create New Poll
        </Link>
      </div>

      {polls.length === 0 ? (
        <div className="bg-white shadow rounded-lg p-6 text-center">
          <p className="text-gray-500">No active polls found</p>
        </div>
      ) : (
        <div className="grid gap-6 md:grid-cols-2 lg:grid-cols-3">
          {polls.map((poll) => (
            <Link key={poll.id} href={`/polls/${poll.id}`}>
              <div className="bg-white shadow rounded-lg p-6 hover:shadow-lg transition-shadow cursor-pointer">
                <h3 className="text-lg font-semibold mb-2">{poll.title}</h3>
                <p className="text-gray-600 mb-4 line-clamp-2">{poll.description}</p>
                <div className="flex justify-between text-sm text-gray-500">
                  <span>
                    {poll.options.length} option{poll.options.length !== 1 ? 's' : ''}
                  </span>
                  <span>
                    Expires: {new Date(poll.expiresAt).toLocaleDateString()}
                  </span>
                </div>
              </div>
            </Link>
          ))}
        </div>
      )}
    </div>
  );
} 