import { useState, useEffect } from 'react';
import axios from 'axios';

export default function TestCors() {
  const [getMessage, setGetMessage] = useState('');
  const [postMessage, setPostMessage] = useState('');
  const [error, setError] = useState('');

  useEffect(() => {
    // Test GET request
    axios.get('http://localhost:8080/api/test')
      .then(response => {
        setGetMessage(JSON.stringify(response.data));
      })
      .catch(err => {
        console.error('GET Error:', err);
        setError(`GET Error: ${err.message}`);
      });
  }, []);

  const handleTestPost = () => {
    // Test POST request
    axios.post('http://localhost:8080/api/test', { test: 'data' })
      .then(response => {
        setPostMessage(JSON.stringify(response.data));
      })
      .catch(err => {
        console.error('POST Error:', err);
        setError(`POST Error: ${err.message}`);
      });
  };

  const handleTestAuth = () => {
    // Test auth endpoint
    axios.post('http://localhost:8080/api/auth/register', {
      username: 'testuser',
      email: 'test@example.com',
      password: 'password123'
    })
      .then(response => {
        setPostMessage(JSON.stringify(response.data));
      })
      .catch(err => {
        console.error('Auth Error:', err);
        setError(`Auth Error: ${err.message}`);
      });
  };

  return (
    <div className="container mx-auto p-4">
      <h1 className="text-2xl font-bold mb-4">CORS Test Page</h1>
      
      <div className="mb-4">
        <h2 className="text-xl font-semibold">GET Test Result:</h2>
        <pre className="bg-gray-100 p-2 rounded">{getMessage || 'No response yet'}</pre>
      </div>
      
      <div className="mb-4">
        <h2 className="text-xl font-semibold">POST Test Result:</h2>
        <pre className="bg-gray-100 p-2 rounded">{postMessage || 'No response yet'}</pre>
      </div>
      
      {error && (
        <div className="mb-4 text-red-500">
          <h2 className="text-xl font-semibold">Error:</h2>
          <pre className="bg-red-100 p-2 rounded">{error}</pre>
        </div>
      )}
      
      <div className="flex space-x-4">
        <button 
          onClick={handleTestPost}
          className="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded"
        >
          Test POST
        </button>
        
        <button 
          onClick={handleTestAuth}
          className="bg-green-500 hover:bg-green-700 text-white font-bold py-2 px-4 rounded"
        >
          Test Register
        </button>
      </div>
    </div>
  );
} 