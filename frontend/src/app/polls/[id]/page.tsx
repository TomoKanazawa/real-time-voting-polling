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

  if (!poll) {
    return (
      <div className="bg-yellow-100 border border-yellow-400 text-yellow-700 px-6 py-4 rounded-lg mb-6" role="alert">
        <div className="flex items-center">
          <svg className="w-6 h-6 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z"></path>
          </svg>
          <span className="font-medium">Poll not found</span>
        </div>
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
  const [showSystemInfo, setShowSystemInfo] = useState(false);
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
    <div className="animate-fade-in space-y-6">
      <div className="bg-white dark:bg-gray-800 shadow-md rounded-lg overflow-hidden">
        <div className="bg-blue-500 px-6 py-4">
          <div className="flex items-center justify-between">
            <h1 className="text-2xl font-bold text-white">{poll.title}</h1>
            <div className="bg-white bg-opacity-20 text-white text-sm font-medium px-3 py-1 rounded-full">
              {new Date(poll.expiresAt).toLocaleDateString()}
            </div>
          </div>
        </div>
        
        <div className="p-6">
          <p className="text-gray-700 dark:text-gray-300 text-lg mb-6">{poll.description}</p>
          
          {error && (
            <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded-lg mb-6" role="alert">
              <div className="flex items-center">
                <svg className="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path>
                </svg>
                <span className="font-medium">{error}</span>
              </div>
            </div>
          )}

          {!showResults ? (
            <div className="bg-gray-50 dark:bg-gray-900 rounded-lg p-6 mb-6">
              <h2 className="text-xl font-semibold text-gray-900 dark:text-white mb-4">Cast Your Vote</h2>
              <div className="space-y-3 mb-6">
                {poll.options.map((option) => (
                  <div 
                    key={option.id} 
                    className={`border rounded-lg p-4 transition-all cursor-pointer ${
                      selectedOption === option.id 
                        ? 'border-blue-500 bg-blue-50 dark:bg-blue-900/30 dark:border-blue-400' 
                        : 'border-gray-200 dark:border-gray-700 hover:border-blue-300 dark:hover:border-blue-700'
                    }`}
                    onClick={() => setSelectedOption(option.id)}
                  >
                    <label className="flex items-center cursor-pointer">
                      <input
                        type={poll.multipleChoiceAllowed ? 'checkbox' : 'radio'}
                        name="poll-option"
                        value={option.id}
                        checked={selectedOption === option.id}
                        onChange={() => setSelectedOption(option.id)}
                        className="form-radio h-5 w-5 text-blue-600 focus:ring-blue-500"
                      />
                      <span className="ml-3 text-gray-800 dark:text-gray-200">{option.text}</span>
                    </label>
                  </div>
                ))}
              </div>

              {poll.anonymousVotingAllowed && (
                <div className="mb-6">
                  <label className="flex items-center space-x-3 cursor-pointer">
                    <input
                      type="checkbox"
                      checked={anonymous}
                      onChange={() => setAnonymous(!anonymous)}
                      className="form-checkbox h-5 w-5 text-blue-600 rounded focus:ring-blue-500"
                    />
                    <span className="text-gray-700 dark:text-gray-300">Vote anonymously</span>
                  </label>
                </div>
              )}

              <div className="flex flex-col sm:flex-row gap-4">
                <button
                  onClick={handleVote}
                  disabled={voting || !selectedOption}
                  className="btn-primary inline-flex items-center justify-center disabled:opacity-50 disabled:cursor-not-allowed"
                >
                  {voting ? (
                    <>
                      <svg className="animate-spin -ml-1 mr-2 h-4 w-4 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                        <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
                        <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                      </svg>
                      Submitting...
                    </>
                  ) : (
                    <>
                      <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5 mr-2" viewBox="0 0 20 20" fill="currentColor">
                        <path fillRule="evenodd" d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z" clipRule="evenodd" />
                      </svg>
                      Submit Vote
                    </>
                  )}
                </button>
                <button
                  onClick={() => setShowResults(true)}
                  className="btn-secondary inline-flex items-center justify-center"
                >
                  <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5 mr-2" viewBox="0 0 20 20" fill="currentColor">
                    <path d="M2 11a1 1 0 011-1h2a1 1 0 011 1v5a1 1 0 01-1 1H3a1 1 0 01-1-1v-5zM8 7a1 1 0 011-1h2a1 1 0 011 1v9a1 1 0 01-1 1H9a1 1 0 01-1-1V7zM14 4a1 1 0 011-1h2a1 1 0 011 1v12a1 1 0 01-1 1h-2a1 1 0 01-1-1V4z" />
                  </svg>
                  View Results
                </button>
              </div>
            </div>
          ) : (
            <div className="bg-gray-50 dark:bg-gray-900 rounded-lg p-6 mb-6">
              {results ? (
                <div>
                  <div className="flex justify-between items-center mb-6">
                    <h2 className="text-xl font-semibold text-gray-900 dark:text-white">Results</h2>
                    <div className="bg-blue-100 dark:bg-blue-900 text-blue-800 dark:text-blue-200 text-sm font-medium px-3 py-1 rounded-full">
                      Total votes: {results.totalVotes}
                    </div>
                  </div>
                  
                  <div className="space-y-6">
                    {results.options.map((option) => (
                      <div key={option.optionId} className="mb-4">
                        <div className="flex justify-between mb-2">
                          <span className="text-gray-800 dark:text-gray-200 font-medium">{option.optionText}</span>
                          <span className="text-gray-600 dark:text-gray-400">
                            {option.voteCount} vote{option.voteCount !== 1 ? 's' : ''} ({option.percentage.toFixed(1)}%)
                          </span>
                        </div>
                        <div className="w-full bg-gray-200 dark:bg-gray-700 rounded-full h-4 overflow-hidden">
                          <div 
                            className={`h-4 rounded-full ${getColorClass(option.percentage)}`}
                            style={{ width: `${option.percentage}%`, transition: 'width 0.5s ease-in-out' }}
                          ></div>
                        </div>
                      </div>
                    ))}
                  </div>
                  
                  <button
                    onClick={() => setShowResults(false)}
                    className="btn-secondary mt-6 inline-flex items-center justify-center"
                  >
                    <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5 mr-2" viewBox="0 0 20 20" fill="currentColor">
                      <path fillRule="evenodd" d="M9.707 14.707a1 1 0 01-1.414 0l-4-4a1 1 0 010-1.414l4-4a1 1 0 011.414 1.414L7.414 9H15a1 1 0 110 2H7.414l2.293 2.293a1 1 0 010 1.414z" clipRule="evenodd" />
                    </svg>
                    Back to Voting
                  </button>
                </div>
              ) : (
                <div className="flex justify-center items-center h-32">
                  <div className="animate-spin rounded-full h-8 w-8 border-t-2 border-b-2 border-blue-500"></div>
                </div>
              )}
            </div>
          )}

          <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center text-sm text-gray-500 dark:text-gray-400 pt-4 border-t border-gray-200 dark:border-gray-700">
            <div className="flex items-center mb-2 sm:mb-0">
              <svg xmlns="http://www.w3.org/2000/svg" className="h-4 w-4 mr-1" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
              </svg>
              Created by: {poll.createdBy?.username || 'Anonymous'}
            </div>
            <div className="flex items-center">
              <svg xmlns="http://www.w3.org/2000/svg" className="h-4 w-4 mr-1" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
              Expires: {new Date(poll.expiresAt).toLocaleString()}
            </div>
          </div>
        </div>
      </div>

      <div className="bg-white dark:bg-gray-800 shadow-md rounded-lg p-6">
        <div className="flex justify-between items-center mb-4">
          <h2 className="text-xl font-semibold text-gray-900 dark:text-white">System Information</h2>
          <button 
            onClick={() => setShowSystemInfo(!showSystemInfo)}
            className="text-blue-500 hover:text-blue-700 dark:text-blue-400 dark:hover:text-blue-300 text-sm font-medium"
          >
            {showSystemInfo ? 'Hide Details' : 'Show Details'}
          </button>
        </div>
        
        {showSystemInfo && (
          <div className="bg-gray-50 dark:bg-gray-900 rounded-lg p-4 text-sm">
            <p className="text-gray-600 dark:text-gray-300 mb-4">
              This poll demonstrates several distributed systems concepts in action:
            </p>
            
            <div className="space-y-4">
              <div>
                <h3 className="font-medium text-gray-900 dark:text-white mb-1">Vote Processing Flow</h3>
                <ol className="list-decimal list-inside text-gray-600 dark:text-gray-300 space-y-1 ml-2">
                  <li>Your vote is sent to the API gateway</li>
                  <li>The request is forwarded to the current leader node</li>
                  <li>The leader validates the vote and writes it to its local state</li>
                  <li>The vote is published to a Kafka topic for durability</li>
                  <li>Follower nodes consume the message and update their local state</li>
                  <li>Real-time updates are pushed to all connected clients via WebSockets</li>
                </ol>
              </div>
              
              <div>
                <h3 className="font-medium text-gray-900 dark:text-white mb-1">Fault Tolerance</h3>
                <p className="text-gray-600 dark:text-gray-300 ml-2">
                  If the leader node fails, a new leader is automatically elected using a consensus algorithm.
                  The system continues to function without interruption, demonstrating the resilience of distributed architectures.
                </p>
              </div>
              
              <div>
                <h3 className="font-medium text-gray-900 dark:text-white mb-1">Consistency Model</h3>
                <p className="text-gray-600 dark:text-gray-300 ml-2">
                  This system implements eventual consistency, where all nodes will eventually have the same data
                  once all updates have been propagated. This approach prioritizes availability and partition tolerance
                  over strong consistency, following the CAP theorem principles.
                </p>
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}

// Helper function to get color class based on percentage
function getColorClass(percentage: number): string {
  if (percentage >= 75) return 'bg-green-500 dark:bg-green-600';
  if (percentage >= 50) return 'bg-blue-500 dark:bg-blue-600';
  if (percentage >= 25) return 'bg-yellow-500 dark:bg-yellow-600';
  return 'bg-red-500 dark:bg-red-600';
} 