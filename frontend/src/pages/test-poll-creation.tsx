'use client';

import { useState, useEffect } from 'react';
import { getCurrentUser } from '@/services/auth';
import axios from 'axios';
import AuthCheck from '@/components/AuthCheck';

export default function TestPollCreationPage() {
  return (
    <AuthCheck>
      <TestPollCreationContent />
    </AuthCheck>
  );
}

function TestPollCreationContent() {
  const [result, setResult] = useState('');
  const [error, setError] = useState('');
  const [userId, setUserId] = useState('');

  useEffect(() => {
    const user = getCurrentUser();
    if (user) {
      setUserId(user.id);
    }
  }, []);

  const testCreatePoll = async () => {
    try {
      const pollData = {
        title: "Test Poll " + new Date().toISOString(),
        description: "This is a test poll",
        expiresAt: new Date(Date.now() + 24 * 60 * 60 * 1000).toISOString(),
        active: true,
        multipleChoiceAllowed: false,
        anonymousVotingAllowed: true,
        options: [
          { text: "Option 1" },
          { text: "Option 2" }
        ],
        userId: userId
      };

      console.log('Sending poll creation request:', JSON.stringify(pollData, null, 2));
      
      const response = await axios.post('http://localhost:8080/api/polls', pollData, {
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${localStorage.getItem('token')}`
        }
      });
      
      console.log('Poll creation response:', response.data);
      setResult(JSON.stringify(response.data, null, 2));
      setError('');
    } catch (err: any) {
      console.error('Poll creation error:', err.response?.data || err.message);
      setError(JSON.stringify(err.response?.data || err.message, null, 2));
    }
  };

  return (
    <div className="p-4">
      <h1 className="text-2xl font-bold mb-4">Test Poll Creation</h1>
      
      <div className="mb-4">
        <p>Current User ID: {userId || 'Not logged in'}</p>
      </div>
      
      <div className="flex space-x-2 mb-4">
        <button 
          onClick={testCreatePoll}
          className="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded"
          disabled={!userId}
        >
          Test Create Poll
        </button>
      </div>
      
      {error && (
        <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded mb-4">
          <h2 className="text-lg font-semibold mb-2">Error:</h2>
          <pre>{error}</pre>
        </div>
      )}
      
      {result && (
        <div className="bg-gray-100 p-4 rounded">
          <h2 className="text-lg font-semibold mb-2">Result:</h2>
          <pre>{result}</pre>
        </div>
      )}
    </div>
  );
} 