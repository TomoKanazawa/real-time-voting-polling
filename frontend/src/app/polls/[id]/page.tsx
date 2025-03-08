'use client';

import { useState, useEffect } from 'react';
import { useRouter, useParams } from 'next/navigation';
import { getPoll, vote, getPollResults, connectToVoteUpdates } from '@/services/polls';
import { isAuthenticated } from '@/services/auth';
import { Poll, VoteResult, ApiErrorResponse } from '@/types';
import AuthCheck from '@/components/AuthCheck';

export default function PollDetailPage() {
  const params = useParams();
  const pollId = params?.id as string;
  const [poll, setPoll] = useState<Poll | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  
  // Fetch the poll first to determine if authentication is required
  useEffect(() => {
    const fetchPoll = async () => {
      try {
        const data = await getPoll(pollId);
        setPoll(data);
      } catch (err: unknown) {
        const apiError = err as ApiErrorResponse;
        setError(apiError.response?.data?.message || 'Failed to fetch poll');
      } finally {
        setLoading(false);
      }
    };

    if (pollId) {
      fetchPoll();
    }
  }, [pollId]);

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

  if (!poll) {
    return (
      <div className="bg-yellow-100 border border-yellow-400 text-yellow-700 px-4 py-3 rounded mb-4" role="alert">
        <span className="block sm:inline">Poll not found</span>
      </div>
    );
  }

  // If anonymous voting is allowed, no need for authentication
  if (poll.anonymousVotingAllowed) {
    return <PollDetail poll={poll} pollId={pollId} />;
  }

  // If anonymous voting is not allowed, require authentication
  return (
    <AuthCheck>
      <PollDetail poll={poll} pollId={pollId} />
    </AuthCheck>
  );
}

interface PollDetailProps {
  poll: Poll;
  pollId: string;
}

function PollDetail({ poll, pollId }: PollDetailProps) {
  const [selectedOption, setSelectedOption] = useState<string | null>(null);
  const [anonymous, setAnonymous] = useState(false);
  const [voting, setVoting] = useState(false);
  const [error, setError] = useState('');
  const [results, setResults] = useState<VoteResult | null>(null);
  const [showResults, setShowResults] = useState(false);
  const router = useRouter();

  useEffect(() => {
    // Fetch initial results
    const fetchResults = async () => {
      try {
        const resultsData = await getPollResults(pollId);
        setResults(resultsData);
      } catch (err) {
        console.error('Failed to fetch results:', err);
        // Create default results if the endpoint is not available yet
        if (!results) {
          const defaultResults: VoteResult = {
            pollId: pollId,
            pollTitle: poll.title,
            totalVotes: 0,
            options: poll.options.map(option => ({
              optionId: option.id,
              optionText: option.text,
              voteCount: 0,
              percentage: 0
            })),
            timestamp: Date.now()
          };
          setResults(defaultResults);
        }
      }
    };

    fetchResults();

    // Connect to real-time updates
    try {
      const disconnect = connectToVoteUpdates(pollId, (updatedResults) => {
        setResults(updatedResults);
      });

      return () => {
        disconnect();
      };
    } catch (err) {
      console.error('Failed to connect to real-time updates:', err);
      return () => {};
    }
  }, [pollId, poll, results]);

  const handleVote = async () => {
    if (!selectedOption) {
      setError('Please select an option');
      return;
    }

    if (!isAuthenticated() && !poll.anonymousVotingAllowed) {
      router.push(`/auth/login?redirect=/polls/${pollId}`);
      return;
    }

    setVoting(true);
    setError('');

    try {
      await vote(pollId, selectedOption, anonymous);
      setShowResults(true);
      
      // Update results after voting
      try {
        const resultsData = await getPollResults(pollId);
        setResults(resultsData);
      } catch (err) {
        console.error('Failed to fetch updated results:', err);
      }
    } catch (err: unknown) {
      const apiError = err as ApiErrorResponse;
      setError(apiError.response?.data?.message || 'Failed to submit vote');
    } finally {
      setVoting(false);
    }
  };

  return (
    <div className="bg-white shadow-md rounded-lg p-6">
      <h2 className="text-2xl font-bold mb-2">{poll.title}</h2>
      <p className="text-gray-600 mb-6">{poll.description}</p>
      
      {error && (
        <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded mb-4" role="alert">
          <span className="block sm:inline">{error}</span>
        </div>
      )}

      {!showResults ? (
        <div>
          <div className="mb-6">
            {poll.options.map((option) => (
              <div key={option.id} className="mb-2">
                <label className="flex items-center space-x-3">
                  <input
                    type={poll.multipleChoiceAllowed ? 'checkbox' : 'radio'}
                    name="poll-option"
                    value={option.id}
                    checked={selectedOption === option.id}
                    onChange={() => setSelectedOption(option.id)}
                    className="form-radio h-5 w-5 text-indigo-600"
                  />
                  <span className="text-gray-700">{option.text}</span>
                </label>
              </div>
            ))}
          </div>

          {poll.anonymousVotingAllowed && (
            <div className="mb-4">
              <label className="flex items-center space-x-3">
                <input
                  type="checkbox"
                  checked={anonymous}
                  onChange={() => setAnonymous(!anonymous)}
                  className="form-checkbox h-5 w-5 text-indigo-600"
                />
                <span className="text-gray-700">Vote anonymously</span>
              </label>
            </div>
          )}

          <div className="flex space-x-4">
            <button
              onClick={handleVote}
              disabled={voting || !selectedOption}
              className="bg-indigo-600 hover:bg-indigo-700 text-white font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline disabled:opacity-50"
            >
              {voting ? 'Submitting...' : 'Submit Vote'}
            </button>
            <button
              onClick={() => setShowResults(true)}
              className="bg-gray-200 hover:bg-gray-300 text-gray-800 font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline"
            >
              View Results
            </button>
          </div>
        </div>
      ) : (
        <div>
          {results ? (
            <div>
              <h3 className="text-xl font-semibold mb-4">Results</h3>
              <p className="text-gray-600 mb-4">Total votes: {results.totalVotes}</p>
              
              <div className="space-y-4">
                {results.options.map((option) => (
                  <div key={option.optionId} className="mb-2">
                    <div className="flex justify-between mb-1">
                      <span className="text-gray-700">{option.optionText}</span>
                      <span className="text-gray-700">{option.voteCount} votes ({option.percentage.toFixed(1)}%)</span>
                    </div>
                    <div className="w-full bg-gray-200 rounded-full h-2.5">
                      <div 
                        className="bg-indigo-600 h-2.5 rounded-full" 
                        style={{ width: `${option.percentage}%` }}
                      ></div>
                    </div>
                  </div>
                ))}
              </div>
              
              <button
                onClick={() => setShowResults(false)}
                className="mt-6 bg-gray-200 hover:bg-gray-300 text-gray-800 font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline"
              >
                Back to Voting
              </button>
            </div>
          ) : (
            <div className="flex justify-center items-center h-32">
              <div className="animate-spin rounded-full h-8 w-8 border-t-2 border-b-2 border-indigo-500"></div>
            </div>
          )}
        </div>
      )}

      <div className="mt-6 text-sm text-gray-500">
        <p>Created by: {poll.createdBy?.username || 'Anonymous'}</p>
        <p>Expires: {new Date(poll.expiresAt).toLocaleString()}</p>
      </div>
    </div>
  );
} 