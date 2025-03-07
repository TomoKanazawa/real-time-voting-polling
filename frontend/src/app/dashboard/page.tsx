'use client';

import { useState, useEffect } from 'react';
import { useRouter } from 'next/navigation';
import Link from 'next/link';
import { getPolls } from '@/services/polls';
import { isAuthenticated, getCurrentUser, logout } from '@/services/auth';
import { Poll } from '@/types';

export default function Dashboard() {
  const [myPolls, setMyPolls] = useState<Poll[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const router = useRouter();

  useEffect(() => {
    if (!isAuthenticated()) {
      router.push('/auth/login?redirect=/dashboard');
      return;
    }

    const fetchPolls = async () => {
      try {
        const data = await getPolls();
        const currentUser = getCurrentUser();
        
        if (currentUser) {
          // Filter polls created by the current user
          const userPolls = data.filter(poll => 
            poll.createdBy && poll.createdBy.id === currentUser.id
          );
          setMyPolls(userPolls);
        }
      } catch (err: any) {
        setError(err.response?.data?.message || 'Failed to fetch polls');
      } finally {
        setLoading(false);
      }
    };

    fetchPolls();
  }, [router]);

  const handleLogout = () => {
    logout();
    router.push('/');
  };

  if (loading) {
    return (
      <div className="flex justify-center items-center h-64">
        <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-indigo-500"></div>
      </div>
    );
  }

  const currentUser = getCurrentUser();

  return (
    <div>
      <div className="bg-white shadow rounded-lg p-6 mb-6">
        <div className="flex justify-between items-center">
          <div>
            <h2 className="text-2xl font-bold">Dashboard</h2>
            <p className="text-gray-600">Welcome, {currentUser?.username || 'User'}</p>
          </div>
          <button
            onClick={handleLogout}
            className="bg-red-600 hover:bg-red-700 text-white font-bold py-2 px-4 rounded"
          >
            Logout
          </button>
        </div>
      </div>

      <div className="bg-white shadow rounded-lg p-6 mb-6">
        <div className="flex justify-between items-center mb-4">
          <h3 className="text-xl font-semibold">My Polls</h3>
          <Link
            href="/polls/create"
            className="bg-indigo-600 hover:bg-indigo-700 text-white font-bold py-2 px-4 rounded"
          >
            Create New Poll
          </Link>
        </div>

        {error && (
          <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded mb-4" role="alert">
            <span className="block sm:inline">{error}</span>
          </div>
        )}

        {myPolls.length === 0 ? (
          <div className="text-center py-4">
            <p className="text-gray-500">You haven't created any polls yet</p>
          </div>
        ) : (
          <div className="overflow-x-auto">
            <table className="min-w-full divide-y divide-gray-200">
              <thead className="bg-gray-50">
                <tr>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Title
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Created
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Expires
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Status
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Actions
                  </th>
                </tr>
              </thead>
              <tbody className="bg-white divide-y divide-gray-200">
                {myPolls.map((poll) => (
                  <tr key={poll.id}>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <div className="text-sm font-medium text-gray-900">{poll.title}</div>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <div className="text-sm text-gray-500">
                        {new Date(poll.createdAt).toLocaleDateString()}
                      </div>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <div className="text-sm text-gray-500">
                        {new Date(poll.expiresAt).toLocaleDateString()}
                      </div>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <span className={`px-2 inline-flex text-xs leading-5 font-semibold rounded-full ${
                        poll.active ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'
                      }`}>
                        {poll.active ? 'Active' : 'Inactive'}
                      </span>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm font-medium">
                      <Link href={`/polls/${poll.id}`} className="text-indigo-600 hover:text-indigo-900 mr-4">
                        View
                      </Link>
                      <Link href={`/polls/${poll.id}/edit`} className="text-indigo-600 hover:text-indigo-900">
                        Edit
                      </Link>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  );
} 