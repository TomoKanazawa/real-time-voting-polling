import { useState } from 'react';

export default function DirectTest() {
  const [result, setResult] = useState('');
  const [error, setError] = useState('');

  const testRegister = async () => {
    try {
      const response = await fetch('http://localhost:8080/api/auth/register', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          username: 'testuser',
          email: 'test@example.com',
          password: 'password123'
        }),
      });
      
      console.log('Response status:', response.status);
      console.log('Response headers:', response.headers);
      
      const data = await response.json();
      console.log('Response data:', data);
      
      setResult(JSON.stringify(data, null, 2));
      setError('');
    } catch (err: any) {
      console.error('Error:', err);
      setError(`Error: ${err.message}`);
    }
  };

  const testWithCredentials = async () => {
    try {
      const response = await fetch('http://localhost:8080/api/auth/register', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        credentials: 'include',
        body: JSON.stringify({
          username: 'testuser2',
          email: 'test2@example.com',
          password: 'password123'
        }),
      });
      
      console.log('Response status:', response.status);
      console.log('Response headers:', response.headers);
      
      const data = await response.json();
      console.log('Response data:', data);
      
      setResult(JSON.stringify(data, null, 2));
      setError('');
    } catch (err: any) {
      console.error('Error:', err);
      setError(`Error: ${err.message}`);
    }
  };

  return (
    <div className="p-4">
      <h1 className="text-2xl font-bold mb-4">Direct API Test</h1>
      
      <div className="flex space-x-2 mb-4">
        <button 
          onClick={testRegister}
          className="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded"
        >
          Test Register
        </button>
        
        <button 
          onClick={testWithCredentials}
          className="bg-green-500 hover:bg-green-700 text-white font-bold py-2 px-4 rounded"
        >
          Test with Credentials
        </button>
      </div>
      
      {error && (
        <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded mb-4">
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