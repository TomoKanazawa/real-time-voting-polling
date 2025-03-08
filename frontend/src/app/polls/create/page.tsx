'use client';

import { useState, FormEvent, useEffect } from 'react';
import { useRouter } from 'next/navigation';
import { createPoll, CreatePollRequest } from '@/services/polls';
import { isAuthenticated, getCurrentUser } from '@/services/auth';
import AuthCheck from '@/components/AuthCheck';

export default function CreatePollPage() {
  return (
    <AuthCheck>
      <CreatePollForm />
    </AuthCheck>
  );
}

function CreatePollForm() {
  const [title, setTitle] = useState('');
  const [description, setDescription] = useState('');
  const [options, setOptions] = useState(['', '']);
  const [expiresAt, setExpiresAt] = useState('');
  const [multipleChoiceAllowed, setMultipleChoiceAllowed] = useState(false);
  const [anonymousVotingAllowed, setAnonymousVotingAllowed] = useState(false);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const router = useRouter();

  // Set default expiration date to 24 hours from now
  useEffect(() => {
    const date = new Date();
    date.setHours(date.getHours() + 24);
    setExpiresAt(date.toISOString().slice(0, 16));
  }, []);

  const addOption = () => {
    setOptions([...options, '']);
  };

  const removeOption = (index: number) => {
    if (options.length <= 2) {
      setError('A poll must have at least 2 options');
      return;
    }
    const newOptions = [...options];
    newOptions.splice(index, 1);
    setOptions(newOptions);
  };

  const handleOptionChange = (index: number, value: string) => {
    const newOptions = [...options];
    newOptions[index] = value;
    setOptions(newOptions);
  };

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    setError('');

    const currentUser = getCurrentUser();
    console.log('Current user:', currentUser);
    
    if (!currentUser) {
      setError('You must be logged in to create a poll');
      return;
    }

    // Validate form
    if (!title.trim()) {
      setError('Title is required');
      return;
    }

    if (options.some(option => !option.trim())) {
      setError('All options must have a value');
      return;
    }

    if (new Set(options.map(o => o.trim())).size !== options.length) {
      setError('All options must be unique');
      return;
    }

    if (!expiresAt) {
      setError('Expiration date is required');
      return;
    }

    setLoading(true);

    try {
      const pollData: CreatePollRequest = {
        title,
        description,
        expiresAt: new Date(expiresAt).toISOString(),
        active: true,
        multipleChoiceAllowed,
        anonymousVotingAllowed,
        options: options.map(text => ({ text: text.trim() })),
        userId: currentUser.id
      };

      console.log('Creating poll with data:', JSON.stringify(pollData, null, 2));
      console.log('User ID type:', typeof currentUser.id);
      
      const createdPoll = await createPoll(pollData);
      console.log('Poll created successfully:', createdPoll);
      router.push(`/polls/${createdPoll.id}`);
    } catch (err: any) {
      console.error('Error creating poll:', err);
      setError(err.response?.data?.error || 'Failed to create poll');
      setLoading(false);
    }
  };

  return (
    <div className="bg-white shadow-md rounded px-8 pt-6 pb-8 mb-4">
      <h2 className="text-2xl font-bold mb-6">Create New Poll</h2>
      
      {error && (
        <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded mb-4" role="alert">
          <span className="block sm:inline">{error}</span>
        </div>
      )}
      
      <form onSubmit={handleSubmit}>
        <div className="mb-4">
          <label className="block text-gray-700 text-sm font-bold mb-2" htmlFor="title">
            Title
          </label>
          <input
            className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
            id="title"
            type="text"
            placeholder="Poll Title"
            value={title}
            onChange={(e) => setTitle(e.target.value)}
            required
          />
        </div>
        
        <div className="mb-4">
          <label className="block text-gray-700 text-sm font-bold mb-2" htmlFor="description">
            Description
          </label>
          <textarea
            className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
            id="description"
            placeholder="Poll Description"
            value={description}
            onChange={(e) => setDescription(e.target.value)}
            rows={3}
          />
        </div>
        
        <div className="mb-4">
          <label className="block text-gray-700 text-sm font-bold mb-2">
            Options
          </label>
          {options.map((option, index) => (
            <div key={index} className="flex mb-2">
              <input
                className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
                type="text"
                placeholder={`Option ${index + 1}`}
                value={option}
                onChange={(e) => handleOptionChange(index, e.target.value)}
                required
              />
              <button
                type="button"
                onClick={() => removeOption(index)}
                className="ml-2 bg-red-500 hover:bg-red-700 text-white font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline"
              >
                X
              </button>
            </div>
          ))}
          <button
            type="button"
            onClick={addOption}
            className="bg-green-500 hover:bg-green-700 text-white font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline"
          >
            Add Option
          </button>
        </div>
        
        <div className="mb-4">
          <label className="block text-gray-700 text-sm font-bold mb-2" htmlFor="expiresAt">
            Expires At
          </label>
          <input
            className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
            id="expiresAt"
            type="datetime-local"
            value={expiresAt}
            onChange={(e) => setExpiresAt(e.target.value)}
            required
          />
        </div>
        
        <div className="mb-4">
          <label className="flex items-center">
            <input
              type="checkbox"
              checked={multipleChoiceAllowed}
              onChange={() => setMultipleChoiceAllowed(!multipleChoiceAllowed)}
              className="mr-2"
            />
            <span className="text-gray-700">Allow multiple choices</span>
          </label>
        </div>
        
        <div className="mb-6">
          <label className="flex items-center">
            <input
              type="checkbox"
              checked={anonymousVotingAllowed}
              onChange={() => setAnonymousVotingAllowed(!anonymousVotingAllowed)}
              className="mr-2"
            />
            <span className="text-gray-700">Allow anonymous voting</span>
          </label>
        </div>
        
        <div className="flex items-center justify-between">
          <button
            className="bg-indigo-600 hover:bg-indigo-700 text-white font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline"
            type="submit"
            disabled={loading}
          >
            {loading ? 'Creating...' : 'Create Poll'}
          </button>
        </div>
      </form>
    </div>
  );
} 